import nodeFs from "node:fs";
import { fileURLToPath } from "node:url";
import { isRunnableDevEnvironment, normalizePath } from "vite";
import { createContentDataIncrementalMetadata } from "../core/build/incremental-metadata.js";
import { ASTRO_VITE_ENVIRONMENT_NAMES } from "../core/constants.js";
import { AstroError, AstroErrorData } from "../core/errors/index.js";
import { rootRelativePath } from "../core/viteUtils.js";
import { isAstroClientEnvironment } from "../environments.js";
import { createDefaultAstroMetadata } from "../vite-plugin-astro/metadata.js";
import {
  ASSET_IMPORTS_FILE,
  ASSET_IMPORTS_RESOLVED_STUB_ID,
  ASSET_IMPORTS_VIRTUAL_ID,
  CONTENT_MODULE_FLAG,
  CONTENT_RENDER_FLAG,
  DATA_STORE_CHUNK_FILE_NAME_PATTERN,
  DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX,
  DATA_STORE_MANIFEST_FILE,
  DATA_STORE_VIRTUAL_ID,
  MODULES_IMPORTS_FILE,
  MODULES_MJS_ID,
  MODULES_MJS_VIRTUAL_ID,
  RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX,
  RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_SUFFIX,
  RESOLVED_DATA_STORE_VIRTUAL_ID,
  RESOLVED_VIRTUAL_MODULE_ID,
  VIRTUAL_MODULE_ID
} from "./consts.js";
import { getDataStoreChunkSize, getDataStoreDir, getDataStoreFile } from "./paths.js";
import { getContentPaths, isDeferredModule } from "./utils.js";
function invalidateAssetImports(viteServer, filePath) {
  const timestamp = Date.now();
  for (const environment of Object.values(viteServer.environments)) {
    const modules = environment.moduleGraph.getModulesByFile(filePath);
    if (modules) {
      for (const module of modules) {
        environment.moduleGraph.invalidateModule(module, void 0, timestamp, true);
      }
    }
    if (isRunnableDevEnvironment(environment)) {
      const runnerModules = environment.runner.evaluatedModules.getModulesByFile(filePath);
      if (runnerModules) {
        for (const runnerModule of runnerModules) {
          environment.runner.evaluatedModules.invalidateModule(runnerModule);
        }
      }
    }
  }
}
function invalidateDataStore(viteServer, { notifyClient = true } = {}) {
  const timestamp = Date.now();
  for (const name of [
    ASTRO_VITE_ENVIRONMENT_NAMES.ssr,
    ASTRO_VITE_ENVIRONMENT_NAMES.prerender
  ]) {
    const environment = viteServer.environments[name];
    if (!environment) continue;
    const module = environment.moduleGraph.getModuleById(RESOLVED_DATA_STORE_VIRTUAL_ID);
    if (module) {
      environment.moduleGraph.invalidateModule(module, void 0, timestamp, true);
    }
    if (isRunnableDevEnvironment(environment)) {
      const runnerModule = environment.runner.evaluatedModules.getModuleById(
        RESOLVED_DATA_STORE_VIRTUAL_ID
      );
      if (runnerModule) {
        environment.runner.evaluatedModules.invalidateModule(runnerModule);
      }
    }
    environment.hot.send("astro:content-changed", {});
  }
  if (notifyClient) {
    viteServer.environments.client.hot.send({
      type: "full-reload",
      path: "*"
    });
  }
}
const directInvalidations = /* @__PURE__ */ new Map();
const DIRECT_INVALIDATION_ECHO_MS = 1e3;
function markDirectInvalidation(path) {
  directInvalidations.set(path, Date.now());
}
function isDirectInvalidationEcho(path) {
  const time = directInvalidations.get(path);
  return time !== void 0 && Date.now() - time < DIRECT_INVALIDATION_ECHO_MS;
}
function getDevDataStoreFile(settings) {
  if (getDataStoreChunkSize(settings) !== void 0) {
    return new URL(DATA_STORE_MANIFEST_FILE, getDataStoreDir(settings, true));
  }
  return getDataStoreFile(settings, true);
}
function attachDataStoreInvalidation(store, server, settings) {
  const dataStorePath = fileURLToPath(getDevDataStoreFile(settings));
  const assetImportsPath = fileURLToPath(new URL(ASSET_IMPORTS_FILE, settings.dotAstroDir));
  store.onFileWritten((path) => {
    if (path === dataStorePath) {
      markDirectInvalidation(dataStorePath);
      invalidateDataStore(server);
      invalidateAssetImports(server, assetImportsPath);
    } else if (path === assetImportsPath) {
      markDirectInvalidation(assetImportsPath);
      invalidateAssetImports(server, assetImportsPath);
    }
  });
}
function astroContentVirtualModPlugin({
  settings,
  fs
}) {
  let dataStoreDir;
  let dataStoreFile;
  let devServer;
  let liveConfig;
  let isDev = false;
  return {
    name: "astro-content-virtual-mod-plugin",
    enforce: "pre",
    config(_, env) {
      isDev = env.command === "serve";
      dataStoreDir = getDataStoreDir(settings, isDev);
      if (getDataStoreChunkSize(settings) !== void 0) {
        dataStoreFile = new URL(DATA_STORE_MANIFEST_FILE, dataStoreDir);
      } else {
        dataStoreFile = getDataStoreFile(settings, isDev);
      }
      const contentPaths = getContentPaths(
        settings.config,
        void 0,
        settings.config.legacy?.collectionsBackwardsCompat
      );
      if (contentPaths.liveConfig.exists) {
        liveConfig = normalizePath(fileURLToPath(contentPaths.liveConfig.url));
      }
    },
    buildStart() {
      if (devServer) {
        const assetImportsPath = fileURLToPath(new URL(ASSET_IMPORTS_FILE, settings.dotAstroDir));
        devServer.watcher.add(fileURLToPath(dataStoreFile));
        devServer.watcher.add(assetImportsPath);
        invalidateDataStore(devServer, { notifyClient: false });
        invalidateAssetImports(devServer, assetImportsPath);
      }
    },
    resolveId: {
      filter: {
        id: new RegExp(
          `^(${VIRTUAL_MODULE_ID}|${DATA_STORE_VIRTUAL_ID}|${MODULES_MJS_ID}|${ASSET_IMPORTS_VIRTUAL_ID})$|^${DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX}|(?:\\?|&)${CONTENT_MODULE_FLAG}(?:&|=|$)`
        )
      },
      async handler(id, importer) {
        if (id === VIRTUAL_MODULE_ID) {
          if (liveConfig && importer && liveConfig === normalizePath(importer)) {
            return this.resolve("astro/virtual-modules/live-config", importer, {
              skipSelf: true
            });
          }
          return RESOLVED_VIRTUAL_MODULE_ID;
        }
        if (id === DATA_STORE_VIRTUAL_ID) {
          return RESOLVED_DATA_STORE_VIRTUAL_ID;
        }
        if (id.startsWith(DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX)) {
          const fileName = id.slice(DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX.length);
          if (!DATA_STORE_CHUNK_FILE_NAME_PATTERN.test(fileName)) {
            this.error(`Invalid data-store chunk: ${fileName}`);
          }
          return `${RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX}${fileName}${RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_SUFFIX}`;
        }
        if (isDeferredModule(id)) {
          const [, query] = id.split("?");
          const params = new URLSearchParams(query);
          const fileName = params.get("fileName");
          let importPath = void 0;
          if (fileName && URL.canParse(fileName, settings.config.root.toString())) {
            importPath = fileURLToPath(new URL(fileName, settings.config.root));
          }
          if (importPath) {
            return await this.resolve(`${importPath}?${CONTENT_RENDER_FLAG}`);
          }
        }
        if (id === MODULES_MJS_ID) {
          const modules = new URL(MODULES_IMPORTS_FILE, settings.dotAstroDir);
          if (fs.existsSync(modules)) {
            return {
              id: fileURLToPath(modules),
              meta: createContentDataIncrementalMetadata()
            };
          }
          return MODULES_MJS_VIRTUAL_ID;
        }
        if (id === ASSET_IMPORTS_VIRTUAL_ID) {
          const assetImportsFile = new URL(ASSET_IMPORTS_FILE, settings.dotAstroDir);
          if (fs.existsSync(assetImportsFile)) {
            return {
              id: fileURLToPath(assetImportsFile),
              meta: createContentDataIncrementalMetadata()
            };
          }
          return ASSET_IMPORTS_RESOLVED_STUB_ID;
        }
      }
    },
    load: {
      filter: {
        id: new RegExp(
          `^(${RESOLVED_VIRTUAL_MODULE_ID}|${RESOLVED_DATA_STORE_VIRTUAL_ID}|${ASSET_IMPORTS_RESOLVED_STUB_ID}|${MODULES_MJS_VIRTUAL_ID})$|^${RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX}`
        )
      },
      async handler(id) {
        if (id.startsWith(RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX)) {
          const resolvedFileName = id.slice(RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX.length);
          if (!resolvedFileName.endsWith(RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_SUFFIX)) {
            this.error(`Invalid data-store chunk: ${resolvedFileName}`);
          }
          const fileName = resolvedFileName.slice(
            0,
            -RESOLVED_DATA_STORE_CHUNK_VIRTUAL_ID_SUFFIX.length
          );
          if (!DATA_STORE_CHUNK_FILE_NAME_PATTERN.test(fileName)) {
            this.error(`Invalid data-store chunk: ${fileName}`);
          }
          const contents = await fs.promises.readFile(
            new URL(`./${fileName}`, dataStoreDir),
            "utf-8"
          );
          return {
            code: `export default ${JSON.stringify(contents)}`,
            map: { mappings: "" }
          };
        }
        if (id === RESOLVED_VIRTUAL_MODULE_ID) {
          const isClient = isAstroClientEnvironment(this.environment);
          const code = await generateContentEntryFile({
            settings,
            fs,
            isClient
          });
          const astro = createDefaultAstroMetadata();
          astro.propagation = "in-tree";
          return {
            code,
            meta: {
              astro
            }
          };
        }
        if (id === RESOLVED_DATA_STORE_VIRTUAL_ID) {
          if (!fs.existsSync(dataStoreFile)) {
            return {
              code: "export default new Map()",
              meta: createContentDataIncrementalMetadata()
            };
          }
          const jsonData = await fs.promises.readFile(dataStoreFile, "utf-8");
          if (getDataStoreChunkSize(settings) !== void 0) {
            try {
              const manifest = JSON.parse(jsonData);
              const chunkImport = (fileName) => `(await import(${JSON.stringify(`${DATA_STORE_CHUNK_VIRTUAL_ID_PREFIX}${fileName}`)}))`;
              const entries = Object.entries(manifest).map(
                ([collection, parts]) => `${JSON.stringify(collection)}:[${parts.map(chunkImport).join(",")}]`
              );
              const code = `export default{${entries.join(",")}}`;
              return {
                code,
                map: { mappings: "" },
                meta: createContentDataIncrementalMetadata()
              };
            } catch (err) {
              const message = "Could not parse data store manifest JSON file";
              this.error({ message, id, cause: err });
            }
          }
          try {
            JSON.parse(jsonData);
          } catch (err) {
            const message = "Could not parse JSON file";
            this.error({ message, id, cause: err });
          }
          return {
            code: `export default JSON.parse(${JSON.stringify(jsonData)})`,
            map: { mappings: "" },
            meta: createContentDataIncrementalMetadata()
          };
        }
        if (id === ASSET_IMPORTS_RESOLVED_STUB_ID) {
          const assetImportsFile = new URL(ASSET_IMPORTS_FILE, settings.dotAstroDir);
          return {
            code: fs.existsSync(assetImportsFile) ? fs.readFileSync(assetImportsFile, "utf-8") : "export default new Map()",
            meta: createContentDataIncrementalMetadata()
          };
        }
        if (id === MODULES_MJS_VIRTUAL_ID) {
          const modules = new URL(MODULES_IMPORTS_FILE, settings.dotAstroDir);
          return {
            code: fs.existsSync(modules) ? fs.readFileSync(modules, "utf-8") : "export default new Map()",
            meta: createContentDataIncrementalMetadata()
          };
        }
      }
    },
    configureServer(server) {
      devServer = server;
      const dataStorePath = fileURLToPath(dataStoreFile);
      const assetImportsPath = fileURLToPath(new URL(ASSET_IMPORTS_FILE, settings.dotAstroDir));
      server.watcher.on("add", (addedPath) => {
        if (addedPath === dataStorePath && !isDirectInvalidationEcho(dataStorePath)) {
          invalidateDataStore(server);
          invalidateAssetImports(server, assetImportsPath);
        }
      });
      server.watcher.on("change", (changedPath) => {
        if (changedPath === dataStorePath) {
          if (isDirectInvalidationEcho(dataStorePath)) {
            return;
          }
          invalidateDataStore(server);
          invalidateAssetImports(server, assetImportsPath);
        } else if (changedPath === assetImportsPath && !isDirectInvalidationEcho(assetImportsPath)) {
          invalidateAssetImports(server, assetImportsPath);
        }
      });
    }
  };
}
async function generateContentEntryFile({
  settings,
  isClient
}) {
  const contentPaths = getContentPaths(
    settings.config,
    void 0,
    settings.config.legacy?.collectionsBackwardsCompat
  );
  const relContentDir = rootRelativePath(settings.config.root, contentPaths.contentDir);
  let virtualModContents;
  if (isClient) {
    throw new AstroError({
      ...AstroErrorData.ServerOnlyModule,
      message: AstroErrorData.ServerOnlyModule.message("astro:content")
    });
  } else {
    virtualModContents = nodeFs.readFileSync(contentPaths.virtualModTemplate, "utf-8").replace("@@CONTENT_DIR@@", relContentDir).replace(
      "/* @@LIVE_CONTENT_CONFIG@@ */",
      contentPaths.liveConfig.exists ? (
        // Dynamic import so it extracts the chunk and avoids a circular import
        `const liveCollections = (await import(${JSON.stringify(fileURLToPath(contentPaths.liveConfig.url))})).collections;`
      ) : "const liveCollections = {};"
    );
  }
  return virtualModContents;
}
export {
  astroContentVirtualModPlugin,
  attachDataStoreInvalidation
};
