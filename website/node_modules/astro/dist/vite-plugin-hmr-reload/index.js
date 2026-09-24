import { isRunnableDevEnvironment } from "vite";
import { VIRTUAL_PAGE_RESOLVED_MODULE_ID } from "../vite-plugin-pages/const.js";
import { RESOLVED_MODULE_DEV_CSS_PREFIX } from "../vite-plugin-css/const.js";
import { getDevCssModuleNameFromPageVirtualModuleName } from "../vite-plugin-css/util.js";
import { isAstroServerEnvironment } from "../environments.js";
const STYLE_EXT_REGEX = /\.(?:css|less|sass|scss|styl|stylus|pcss|postcss|sss)$/i;
const STYLE_COMPONENT_EXT_REGEX = /\.(astro|svelte|vue)$/i;
const RAW_QUERY_REGEX = /(?:\?|&)raw(?:&|$)/;
function hasStyleExtension(id) {
  return STYLE_EXT_REGEX.test(id.split("?")[0]);
}
function isComponentStyleModule(id) {
  const [filename, rawQuery] = id.split("?", 2);
  const extensionMatch = STYLE_COMPONENT_EXT_REGEX.exec(filename);
  if (!rawQuery || !extensionMatch) return false;
  const query = new URLSearchParams(rawQuery);
  return query.get("type") === "style" && query.has(extensionMatch[1].toLowerCase());
}
function getStyleModuleType(mod) {
  if (mod.id && RAW_QUERY_REGEX.test(mod.id) && hasStyleExtension(mod.id)) return;
  if (mod.id && isComponentStyleModule(mod.id)) return "component";
  if (mod.file && hasStyleExtension(mod.file)) return "file";
  return mod.id && hasStyleExtension(mod.id) ? "file" : void 0;
}
function hasClientStyleModuleByFile(moduleGraph, mod, styleType) {
  if (mod.file == null || moduleGraph.getModulesByFile == null) return false;
  const fileModules = moduleGraph.getModulesByFile(mod.file);
  if (fileModules == null) return false;
  for (const fileMod of fileModules) {
    if (fileMod.id == null) continue;
    if (styleType === "component") {
      if (isComponentStyleModule(fileMod.id)) return true;
    } else if (!RAW_QUERY_REGEX.test(fileMod.id) && hasStyleExtension(fileMod.id)) {
      return true;
    }
  }
  return false;
}
function hmrReload() {
  return {
    name: "astro:hmr-reload",
    enforce: "post",
    hotUpdate: {
      order: "post",
      handler({ modules, server, timestamp, file }) {
        if (!isAstroServerEnvironment(this.environment)) return;
        let hasSsrOnlyModules = false;
        let hasStyleModules = false;
        let hasSkippedStyleModules = false;
        const invalidatedModules = /* @__PURE__ */ new Set();
        for (const mod of modules) {
          if (mod.id == null) continue;
          const styleType = getStyleModuleType(mod);
          const clientModule = server.environments.client.moduleGraph.getModuleById(mod.id);
          if (styleType) {
            hasStyleModules = true;
            if (clientModule == null && !hasClientStyleModuleByFile(server.environments.client.moduleGraph, mod, styleType)) {
              this.environment.moduleGraph.invalidateModule(
                mod,
                invalidatedModules,
                timestamp,
                true
              );
              hasSsrOnlyModules = true;
              continue;
            }
            hasSkippedStyleModules = true;
            continue;
          }
          if (clientModule != null) continue;
          this.environment.moduleGraph.invalidateModule(mod, invalidatedModules, timestamp, true);
          hasSsrOnlyModules = true;
        }
        const invalidateDevCssModules = () => {
          for (const [id, mod] of this.environment.moduleGraph.idToModuleMap) {
            if (id.startsWith(RESOLVED_MODULE_DEV_CSS_PREFIX)) {
              this.environment.moduleGraph.invalidateModule(mod, void 0, timestamp, true);
              if (isRunnableDevEnvironment(this.environment)) {
                const runnerMod = this.environment.runner.evaluatedModules.getModuleById(id);
                if (runnerMod) {
                  this.environment.runner.evaluatedModules.invalidateModule(runnerMod);
                }
              }
            }
          }
        };
        for (const invalidatedModule of invalidatedModules) {
          if (invalidatedModule.id?.startsWith(VIRTUAL_PAGE_RESOLVED_MODULE_ID)) {
            const cssMod = this.environment.moduleGraph.getModuleById(
              getDevCssModuleNameFromPageVirtualModuleName(invalidatedModule.id)
            );
            if (!cssMod || cssMod.id == null) continue;
            this.environment.moduleGraph.invalidateModule(cssMod, void 0, timestamp, true);
          }
        }
        if (hasSsrOnlyModules) {
          if (hasStyleModules) {
            invalidateDevCssModules();
          }
          if (isRunnableDevEnvironment(this.environment)) {
            for (const invalidated of invalidatedModules) {
              if (invalidated.id == null) continue;
              const runnerModule = this.environment.runner.evaluatedModules.getModuleById(
                invalidated.id
              );
              if (runnerModule) {
                this.environment.runner.evaluatedModules.invalidateModule(runnerModule);
              }
            }
          }
          server.ws.send({ type: "full-reload" });
          if (!isRunnableDevEnvironment(this.environment)) {
            this.environment.hot.send({
              type: "full-reload",
              triggeredBy: file,
              path: "*"
            });
          }
          return [];
        }
        if (hasSkippedStyleModules) {
          invalidateDevCssModules();
          return [];
        }
        if (modules.length > 0) {
          return [];
        }
      }
    }
  };
}
export {
  hmrReload as default
};
