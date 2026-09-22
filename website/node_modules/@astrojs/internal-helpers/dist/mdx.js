import { existsSync, statSync } from "node:fs";
import { createRequire } from "node:module";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";
import { slash } from "./path.js";
const ASTRO_IMAGE_ELEMENT = "astro-image";
const ASTRO_IMAGE_IMPORT = "__AstroImage__";
const USES_ASTRO_IMAGE_FLAG = "__usesAstroImage";
function createDefaultAstroMetadata() {
  return {
    hydratedComponents: [],
    clientOnlyComponents: [],
    serverComponents: [],
    scripts: [],
    propagation: "none",
    containsHead: false,
    pageOptions: {}
  };
}
const isWindows = typeof process !== "undefined" && process.platform === "win32";
function normalizePath(id) {
  return path.posix.normalize(isWindows ? slash(id) : id);
}
function resolveJsToTs(filePath) {
  if (filePath.endsWith(".jsx") && !existsSync(filePath)) {
    const tryPath = filePath.slice(0, -4) + ".tsx";
    if (existsSync(tryPath)) {
      return tryPath;
    }
  }
  return filePath;
}
const VITE_DEFAULT_RESOLVE_EXTENSIONS = [".mjs", ".js", ".mts", ".ts", ".jsx", ".tsx", ".json"];
function resolveExtensionlessPath(filePath) {
  const stat = statSync(filePath, { throwIfNoEntry: false });
  if (stat?.isFile()) {
    return filePath;
  }
  for (const ext of VITE_DEFAULT_RESOLVE_EXTENSIONS) {
    const tryPath = filePath + ext;
    if (existsSync(tryPath)) {
      return tryPath;
    }
  }
  if (stat?.isDirectory()) {
    for (const ext of VITE_DEFAULT_RESOLVE_EXTENSIONS) {
      const tryPath = `${filePath}/index${ext}`;
      if (existsSync(tryPath)) {
        return tryPath;
      }
    }
  }
  return filePath;
}
function resolvePath(specifier, importer) {
  if (specifier.startsWith(".")) {
    const absoluteSpecifier = path.resolve(path.dirname(importer), specifier);
    return resolveExtensionlessPath(resolveJsToTs(normalizePath(absoluteSpecifier)));
  } else if (specifier.startsWith("#")) {
    try {
      const resolved = createRequire(pathToFileURL(importer)).resolve(specifier);
      return resolveJsToTs(normalizePath(resolved));
    } catch {
      try {
        const importerURL = pathToFileURL(importer).toString();
        const resolved = import.meta.resolve(specifier, importerURL);
        const resolvedUrl = new URL(resolved);
        if (resolvedUrl.protocol === "file:") {
          return resolveJsToTs(normalizePath(fileURLToPath(resolvedUrl)));
        }
      } catch {
      }
    }
    return specifier;
  } else {
    return specifier;
  }
}
export {
  ASTRO_IMAGE_ELEMENT,
  ASTRO_IMAGE_IMPORT,
  USES_ASTRO_IMAGE_FLAG,
  createDefaultAstroMetadata,
  resolvePath
};
