import { RedirectSinglePageBuiltModule } from "../core/redirects/index.js";
import {
  createModuleScriptElement,
  createStylesheetElementSet
} from "../core/render/ssr-element.js";
import { getDefaultRoutes } from "../core/routing/default.js";
import { findRouteToRewrite } from "../core/routing/rewrite.js";
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
function createContainerEnvironment({
  interner,
  resolve,
  renderers,
  streaming
}) {
  async function getComponentByRoute(_manifest, routeData) {
    const page = interner.get(routeData);
    if (page) {
      return page.page();
    }
    throw new Error("Couldn't find component for route " + routeData.pathname);
  }
  return {
    name: "container",
    runtimeMode: "development",
    defaultStreaming: () => streaming,
    async resolve(_manifest, specifier) {
      return resolve(specifier);
    },
    headElements(manifest, routeData) {
      const routeInfo = manifest.routes.find((route) => route.routeData === routeData);
      const links = /* @__PURE__ */ new Set();
      const scripts = /* @__PURE__ */ new Set();
      const styles = createStylesheetElementSet(routeInfo?.styles ?? []);
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
      return { links, styles, scripts };
    },
    componentMetadata() {
    },
    getComponentByRoute,
    getModuleForRoute,
    async tryRewrite(manifest, payload, request) {
      const { newUrl, pathname, routeData } = findRouteToRewrite({
        payload,
        request,
        // PER-CALL scan of the live manifest routes: the container inserts
        // routes at runtime (`insertRoute` pushes into `manifest.routes`),
        // and an uncompiled scan sees them immediately. Reading the derived
        // route table here would miss them.
        routes: manifest.routes.map((r) => r.routeData),
        trailingSlash: manifest.trailingSlash,
        buildFormat: manifest.buildFormat,
        base: manifest.base,
        outDir: manifest.outDir
      });
      const componentInstance = await getComponentByRoute(manifest, routeData);
      return { componentInstance, routeData, newUrl, pathname };
    },
    getRenderers() {
      return renderers;
    },
    errorStrategy: "default",
    injectCspMetaTagsOnErrorPages: false,
    logRequest() {
    }
  };
}
export {
  createContainerEnvironment
};
