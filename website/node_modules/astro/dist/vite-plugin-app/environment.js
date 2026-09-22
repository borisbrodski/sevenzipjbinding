import { fileURLToPath } from "node:url";
import { ASTRO_VERSION } from "../core/constants.js";
import { enhanceViteSSRError } from "../core/errors/dev/index.js";
import { AggregateError, CSSError, MarkdownError } from "../core/errors/index.js";
import { getLogger } from "../core/logger/manifest-logger.js";
import { req } from "../core/messages/runtime.js";
import {
  RedirectComponentInstance,
  RedirectSinglePageBuiltModule
} from "../core/redirects/index.js";
import { loadRenderer } from "../core/render/index.js";
import { getDefaultRoutes } from "../core/routing/default.js";
import { routeIsRedirect } from "../core/routing/helpers.js";
import { findRouteToRewrite } from "../core/routing/rewrite.js";
import { getRouteTable } from "../core/routing/route-table.js";
import { isPage } from "../core/util.js";
import { resolveIdToUrl } from "../core/viteUtils.js";
import { stringifyForScript } from "../runtime/server/escape.js";
import { getComponentMetadata } from "../vite-plugin-astro-server/metadata.js";
import { PAGE_SCRIPT_ID } from "../vite-plugin-scripts/index.js";
const devRenderers = /* @__PURE__ */ new WeakMap();
function getDevRenderers(manifest) {
  return devRenderers.get(manifest) ?? [];
}
function setDevRenderers(manifest, renderers) {
  devRenderers.set(manifest, renderers);
}
async function getModuleForRoute(manifest, route) {
  for (const defaultRoute of getDefaultRoutes(manifest)) {
    if (route.component === defaultRoute.component) {
      return {
        page: () => Promise.resolve(defaultRoute.instance)
      };
    }
  }
  if (route.type === "redirect") {
    return RedirectSinglePageBuiltModule;
  } else {
    if (manifest.pageMap) {
      const importComponentInstance = manifest.pageMap.get(route.component);
      if (!importComponentInstance) {
        throw new Error(
          `Unexpectedly unable to find a component instance for route ${route.route}`
        );
      }
      return await importComponentInstance();
    } else if (manifest.pageModule) {
      return manifest.pageModule;
    }
    throw new Error(
      "Astro couldn't find the correct page to render, probably because it wasn't correctly mapped for SSR usage. This is an internal error, please file an issue."
    );
  }
}
function createRunnableEnvironment({
  loader,
  settings,
  getDebugInfo
}) {
  async function getComponentByRoute(manifest, routeData) {
    if (routeIsRedirect(routeData)) {
      return RedirectComponentInstance;
    }
    const filePath = new URL(`${routeData.component}`, manifest.rootDir);
    for (const route of getDefaultRoutes(manifest)) {
      if (route.matchesComponent(filePath)) {
        return route.instance;
      }
    }
    if (settings) {
      const renderers__ = settings.renderers.map((r) => loadRenderer(r, loader));
      const renderers_ = await Promise.all(renderers__);
      setDevRenderers(
        manifest,
        renderers_.filter((r) => Boolean(r))
      );
    }
    try {
      return await loader.import(filePath.toString());
    } catch (error) {
      if (MarkdownError.is(error) || CSSError.is(error) || AggregateError.is(error)) {
        throw error;
      }
      throw enhanceViteSSRError({ error, filePath, loader });
    }
  }
  return {
    name: "dev-runnable",
    runtimeMode: "development",
    // Dev always streams.
    defaultStreaming: () => true,
    resolve(manifest, specifier) {
      return resolveIdToUrl(loader, specifier, manifest.rootDir);
    },
    async headElements(manifest, routeData) {
      const filePath = new URL(`${routeData.component}`, manifest.rootDir);
      const scripts = /* @__PURE__ */ new Set();
      if (settings) {
        if (isPage(filePath, settings)) {
          scripts.add({
            props: { type: "module", src: "/@vite/client" },
            children: ""
          });
          if (manifest.devToolbar.enabled) {
            scripts.add({
              props: {
                type: "module",
                src: "/@id/astro/runtime/client/dev-toolbar/entrypoint.js"
              },
              children: ""
            });
            const additionalMetadata = {
              root: fileURLToPath(settings.config.root),
              version: ASTRO_VERSION,
              latestAstroVersion: settings.latestAstroVersion,
              // TODO: Currently the debug info is always fetched, which slows things down.
              // We should look into not loading it if the dev toolbar is disabled. And when
              // enabled, it would nice to request the debug info through import.meta.hot
              // when the button is click to defer execution as much as possible
              debugInfo: await getDebugInfo(),
              placement: settings.config.devToolbar.placement
            };
            const children = `window.__astro_dev_toolbar__ = ${stringifyForScript(additionalMetadata)}`;
            scripts.add({ props: {}, children });
          }
        }
        for (const script of settings.scripts) {
          if (script.stage === "head-inline") {
            scripts.add({
              props: {},
              children: script.content
            });
          } else if (script.stage === "page" && isPage(filePath, settings)) {
            scripts.add({
              props: { type: "module", src: `/@id/${PAGE_SCRIPT_ID}` },
              children: ""
            });
          }
        }
      }
      const { devCSSMap } = await import("virtual:astro:dev-css-all");
      const importer = devCSSMap.get(routeData.component);
      let css = /* @__PURE__ */ new Set();
      if (importer) {
        const cssModule = await importer();
        css = cssModule.css;
      } else {
        getLogger(manifest).warn(
          "assets",
          `Unable to find CSS for ${routeData.component}. This is likely a bug in Astro.`
        );
      }
      const links = /* @__PURE__ */ new Set();
      const styles = /* @__PURE__ */ new Set();
      for (const { id, url: src, content } of css) {
        scripts.add({ props: { type: "module", src }, children: "" });
        styles.add({ props: { "data-vite-dev-id": id }, children: content });
      }
      return { scripts, styles, links };
    },
    componentMetadata(manifest, routeData) {
      const filePath = new URL(`${routeData.component}`, manifest.rootDir);
      return getComponentMetadata(filePath, loader);
    },
    getComponentByRoute,
    getModuleForRoute,
    async tryRewrite(manifest, payload, request) {
      const { routeData, pathname, newUrl } = findRouteToRewrite({
        payload,
        request,
        // The single fresh route table: HMR route updates are visible
        // to rewrites at the same instant as every other consumer.
        routes: getRouteTable(manifest).routes,
        trailingSlash: manifest.trailingSlash,
        buildFormat: manifest.buildFormat,
        base: manifest.base,
        outDir: manifest.outDir
      });
      const componentInstance = await getComponentByRoute(manifest, routeData);
      return { newUrl, pathname, componentInstance, routeData };
    },
    getRenderers(manifest) {
      return getDevRenderers(manifest);
    },
    errorStrategy: "dev",
    injectCspMetaTagsOnErrorPages: true,
    logRequest(manifest, payload) {
      const { pathname, method, statusCode, isRewrite, timeStart } = payload;
      if (pathname === "/favicon.ico") {
        return;
      }
      const reqTime = performance.now() - timeStart;
      getLogger(manifest).info(
        null,
        req({
          url: pathname,
          method,
          statusCode,
          isRewrite,
          reqTime
        })
      );
    }
  };
}
export {
  createRunnableEnvironment,
  getDevRenderers,
  setDevRenderers
};
