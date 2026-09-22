import { stringifyForScript } from "../../runtime/server/escape.js";
import { ASTRO_VERSION } from "../constants.js";
import { getLogger } from "../logger/manifest-logger.js";
import { req } from "../messages/runtime.js";
import { RedirectSinglePageBuiltModule } from "../redirects/index.js";
import { createModuleScriptElement, createStylesheetElementSet } from "../render/ssr-element.js";
import { getDefaultRoutes } from "../routing/default.js";
import { findRouteToRewrite } from "../routing/rewrite.js";
import { getRouteTable } from "../routing/route-table.js";
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
async function getComponentByRoute(manifest, routeData) {
  try {
    const module2 = await getModuleForRoute(manifest, routeData);
    return module2.page();
  } catch {
  }
  const url = new URL(routeData.component, manifest.rootDir);
  const module = await import(
    /* @vite-ignore */
    url.toString()
  );
  return module;
}
function createNonRunnableEnvironment() {
  return {
    name: "dev-nonrunnable",
    runtimeMode: "development",
    // Dev always streams.
    defaultStreaming: () => true,
    async resolve(_manifest, specifier) {
      if (specifier.startsWith("/")) {
        return specifier;
      } else {
        return "/@id/" + specifier;
      }
    },
    async headElements(manifest, routeData) {
      const { componentMetadataEntries } = await import("virtual:astro:component-metadata");
      for (const [id, entry] of componentMetadataEntries) {
        manifest.componentMetadata.set(id, entry);
      }
      const { assetsPrefix, base } = manifest;
      const routeInfo = manifest.routes.find((route) => route.routeData === routeData);
      const links = /* @__PURE__ */ new Set();
      const scripts = /* @__PURE__ */ new Set();
      const styles = createStylesheetElementSet(routeInfo?.styles ?? [], base, assetsPrefix);
      for (const script of routeInfo?.scripts ?? []) {
        if ("stage" in script) {
          if (script.stage === "head-inline") {
            scripts.add({
              props: {},
              children: script.children
            });
          }
        } else {
          scripts.add(createModuleScriptElement(script));
        }
      }
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
          root: manifest.rootDir.toString(),
          version: ASTRO_VERSION,
          latestAstroVersion: manifest.devToolbar.latestAstroVersion,
          debugInfo: manifest.devToolbar.debugInfoOutput ?? "",
          placement: manifest.devToolbar.placement
        };
        const children = `window.__astro_dev_toolbar__ = ${stringifyForScript(additionalMetadata)}`;
        scripts.add({ props: {}, children });
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
      for (const { id, url: src, content } of css) {
        scripts.add({ props: { type: "module", src }, children: "" });
        styles.add({ props: { "data-vite-dev-id": id }, children: content });
      }
      return { scripts, styles, links };
    },
    componentMetadata() {
    },
    getComponentByRoute,
    getModuleForRoute,
    async tryRewrite(manifest, payload, request) {
      const { newUrl, pathname, routeData } = findRouteToRewrite({
        payload,
        request,
        // The single fresh route table: HMR route updates are visible
        // to rewrites at the same instant as every other consumer.
        routes: getRouteTable(manifest).routes,
        trailingSlash: manifest.trailingSlash,
        buildFormat: manifest.buildFormat,
        base: manifest.base,
        outDir: manifest.serverLike ? manifest.buildClientDir : manifest.outDir
      });
      const componentInstance = await getComponentByRoute(manifest, routeData);
      return { newUrl, pathname, componentInstance, routeData };
    },
    getRenderers(manifest) {
      return manifest.renderers;
    },
    errorStrategy: "dev",
    injectCspMetaTagsOnErrorPages: false,
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
  createNonRunnableEnvironment
};
