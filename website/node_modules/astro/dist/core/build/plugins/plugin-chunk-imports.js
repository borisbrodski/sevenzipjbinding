import { init, parse } from "es-module-lexer";
import { ASTRO_VITE_ENVIRONMENT_NAMES } from "../../constants.js";
function getImportSpecifier(code, imp) {
  if (imp.n != null) return imp.n;
  if (imp.d > -1) {
    const raw = code.slice(imp.s, imp.e);
    const quote = raw[0];
    if ((quote === "`" || quote === '"' || quote === "'") && raw.at(-1) === quote) {
      const inner = raw.slice(1, -1);
      if (!inner.includes("${")) return inner;
    }
  }
  return void 0;
}
function pluginChunkImports(options) {
  const assetQueryParams = options.settings.adapter?.client?.assetQueryParams;
  if (!assetQueryParams || assetQueryParams.toString() === "") {
    return void 0;
  }
  const queryString = assetQueryParams.toString();
  return {
    name: "@astro/plugin-chunk-imports",
    enforce: "post",
    applyToEnvironment(environment) {
      return environment.name === ASTRO_VITE_ENVIRONMENT_NAMES.client;
    },
    async generateBundle(_options, bundle) {
      await init;
      for (const [, chunk] of Object.entries(bundle)) {
        if (chunk.type !== "chunk") continue;
        if (!chunk.code.includes("./")) continue;
        const [imports] = parse(chunk.code);
        const relativeImports = imports.filter((imp) => {
          const name = getImportSpecifier(chunk.code, imp);
          return name != null && /^\.\.?\//.test(name) && /\.(?:js|mjs)$/.test(name);
        });
        if (relativeImports.length === 0) continue;
        let rewritten = chunk.code;
        for (let i = relativeImports.length - 1; i >= 0; i--) {
          const imp = relativeImports[i];
          const insertAt = imp.d > -1 ? imp.e - 1 : imp.e;
          rewritten = rewritten.slice(0, insertAt) + "?" + queryString + rewritten.slice(insertAt);
        }
        chunk.code = rewritten;
      }
    }
  };
}
export {
  pluginChunkImports
};
