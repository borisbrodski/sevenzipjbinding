import fs from "node:fs";
import path from "node:path";
import { normalizePath } from "vite";
const getConfigAlias = (settings) => {
  const { tsConfig, tsConfigPath } = settings;
  if (!tsConfig || !tsConfigPath || !tsConfig.compilerOptions) return null;
  const { baseUrl, paths } = tsConfig.compilerOptions;
  const effectiveBaseUrl = baseUrl ?? (paths ? "." : void 0);
  if (!effectiveBaseUrl) return null;
  const resolvedBaseUrl = path.resolve(path.dirname(tsConfigPath), effectiveBaseUrl);
  const aliases = [];
  if (paths) {
    for (const [alias, values] of Object.entries(paths)) {
      const find = new RegExp(
        `^${[...alias].map(
          (segment) => segment === "*" ? "(.+)" : segment.replace(/[\\^$*+?.()|[\]{}]/, "\\$&")
        ).join("")}$`
      );
      for (const value of values) {
        let matchId = 0;
        const replacement = [...normalizePath(path.resolve(resolvedBaseUrl, value))].map((segment) => segment === "*" ? `$${++matchId}` : segment === "$" ? "$$" : segment).join("");
        aliases.push({ find, replacement });
      }
    }
  }
  if (baseUrl) {
    aliases.push({
      find: /^(?!\.*\/|\.*$|\w:)(.+)$/,
      replacement: `${[...normalizePath(resolvedBaseUrl)].map((segment) => segment === "$" ? "$$" : segment).join("")}/$1`
    });
  }
  return aliases;
};
function resolveWithAlias(id, configAlias) {
  for (const alias of configAlias) {
    if (alias.find.test(id)) {
      const updatedId = id.replace(alias.find, alias.replacement);
      const stats = fs.statSync(updatedId, { throwIfNoEntry: false });
      if (stats && stats.isFile()) {
        return normalizePath(updatedId);
      }
    }
  }
  return null;
}
const cssImportRE = /@import\s+(?:url\(\s*)?['"]([^'"]+)['"]\s*\)?/g;
const cssUrlRE = /(?<!@import\s+)url\(\s*['"]([^'"]+)['"]\s*\)/g;
function configAliasVitePlugin({
  settings
}) {
  const configAlias = getConfigAlias(settings);
  if (!configAlias) return null;
  return [
    // Deprecated CSS fallback for Vite's transform pipeline. Only supports
    // `@import "..."`, `@import url("...")`, and quoted `url("...")`.
    // Do not add support here for `@use`, `@forward`, `@reference`, `@config`,
    // or other CSS/preprocessor file-reference syntax.
    {
      name: "astro:tsconfig-alias-css",
      enforce: "pre",
      transform: {
        filter: {
          id: {
            include: /\.css$/
          }
        },
        handler(code) {
          let hasReplacement = false;
          const replaceAliases = (match, importId) => {
            if (!importId) return match;
            if (importId.startsWith("data:")) return match;
            const resolved = resolveWithAlias(importId, configAlias);
            if (resolved) {
              hasReplacement = true;
              return match.replace(importId, resolved);
            }
            return match;
          };
          let result = code;
          if (result.includes("@import")) {
            result = result.replace(cssImportRE, replaceAliases);
          }
          if (result.includes("url(")) {
            result = result.replace(cssUrlRE, replaceAliases);
          }
          if (hasReplacement) {
            return { code: result, map: null };
          }
        }
      }
    },
    // Deprecated module fallback for JS, TS, and Astro import specifiers that
    // Vite's native tsconfig path resolution does not currently resolve.
    {
      name: "astro:tsconfig-alias",
      // use post to only resolve ids that all other plugins before it can't
      enforce: "post",
      resolveId: {
        filter: {
          id: {
            include: configAlias.map((alias) => alias.find),
            exclude: /(?:\0|^virtual:|^astro:)/
          }
        },
        async handler(id, importer, options) {
          for (const alias of configAlias) {
            if (alias.find.test(id)) {
              const updatedId = id.replace(alias.find, alias.replacement);
              if (updatedId.includes("*")) {
                return updatedId;
              }
              const resolved = await this.resolve(updatedId, importer, {
                skipSelf: true,
                ...options
              });
              if (resolved) return resolved;
            }
          }
        }
      }
    }
  ];
}
export {
  configAliasVitePlugin as default
};
