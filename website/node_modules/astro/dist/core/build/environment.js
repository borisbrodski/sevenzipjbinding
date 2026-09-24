import { BEFORE_HYDRATION_SCRIPT_ID, PAGE_SCRIPT_ID } from "../../vite-plugin-scripts/index.js";
import { RedirectSinglePageBuiltModule } from "../redirects/index.js";
import { createAssetLink, createStylesheetElementSet } from "../render/ssr-element.js";
import { getDefaultRoutes } from "../routing/default.js";
import { getFallbackRoute, routeIsFallback, routeIsRedirect } from "../routing/helpers.js";
import { findRouteToRewrite } from "../routing/rewrite.js";
import { cssOrder, getPageData, mergeInlineCss } from "./runtime.js";
async function getModuleForRoute(manifest, route) {
  for (const defaultRoute of getDefaultRoutes(manifest)) {
    if (route.component === defaultRoute.component) {
      return {
        page: () => Promise.resolve(defaultRoute.instance)
      };
    }
  }
  let routeToProcess = route;
  if (routeIsRedirect(route)) {
    if (route.redirectRoute) {
      routeToProcess = route.redirectRoute;
    } else {
      return RedirectSinglePageBuiltModule;
    }
  } else if (routeIsFallback(route)) {
    routeToProcess = getFallbackRoute(route, manifest.routes);
  }
  if (manifest.pageMap) {
    const importComponentInstance = manifest.pageMap.get(routeToProcess.component);
    if (!importComponentInstance) {
      throw new Error(`Unexpectedly unable to find a component instance for route ${route.route}`);
    }
    return await importComponentInstance();
  } else if (manifest.pageModule) {
    return manifest.pageModule;
  }
  throw new Error(
    "Astro couldn't find the correct page to render, probably because it wasn't correctly mapped for SSR usage. This is an internal error, please file an issue."
  );
}
async function getComponentByRoute(manifest, routeData) {
  const module = await getModuleForRoute(manifest, routeData);
  return module.page();
}
function createBuildEnvironment() {
  let internals;
  let options;
  function getInternals() {
    if (!internals) {
      throw new Error("No internals defined");
    }
    return internals;
  }
  function getOptions() {
    if (!options) {
      throw new Error("No options defined");
    }
    return options;
  }
  function getSettings() {
    return getOptions().settings;
  }
  const resolveCache = /* @__PURE__ */ new Map();
  const env = {
    name: "build",
    runtimeMode: "production",
    // We can skip streaming in SSG for performance as writing as strings is
    // faster.
    defaultStreaming: (manifest) => manifest.serverLike,
    async resolve(manifest, specifier) {
      if (resolveCache.has(specifier)) {
        return resolveCache.get(specifier);
      }
      const hashedFilePath = manifest.entryModules[specifier];
      if (typeof hashedFilePath !== "string" || hashedFilePath === "") {
        if (specifier === BEFORE_HYDRATION_SCRIPT_ID) {
          resolveCache.set(specifier, "");
          return "";
        }
        throw new Error(`Cannot find the built path for ${specifier}`);
      }
      const assetLink = createAssetLink(hashedFilePath, manifest.base, manifest.assetsPrefix);
      resolveCache.set(specifier, assetLink);
      return assetLink;
    },
    headElements(manifest, routeData) {
      const { assetsPrefix, base } = manifest;
      const settings = getSettings();
      const buildInternals = getInternals();
      const links = /* @__PURE__ */ new Set();
      const pageBuildData = getPageData(buildInternals, routeData.route, routeData.component);
      const scripts = /* @__PURE__ */ new Set();
      const sortedCssAssets = pageBuildData?.styles.sort(cssOrder).map(({ sheet }) => sheet).reduce(mergeInlineCss, []);
      const styles = createStylesheetElementSet(sortedCssAssets ?? [], base, assetsPrefix);
      if (settings.scripts.some((script) => script.stage === "page")) {
        const hashedFilePath = buildInternals.entrySpecifierToBundleMap.get(PAGE_SCRIPT_ID);
        if (typeof hashedFilePath !== "string") {
          throw new Error(`Cannot find the built path for ${PAGE_SCRIPT_ID}`);
        }
        const src = createAssetLink(hashedFilePath, base, assetsPrefix);
        scripts.add({
          props: { type: "module", src },
          children: ""
        });
      }
      for (const script of settings.scripts) {
        if (script.stage === "head-inline") {
          scripts.add({
            props: {},
            children: script.content
          });
        }
      }
      return { scripts, styles, links };
    },
    componentMetadata() {
    },
    getComponentByRoute,
    getModuleForRoute,
    async tryRewrite(manifest, payload, request) {
      const { routeData, pathname, newUrl } = findRouteToRewrite({
        payload,
        request,
        // RAW manifest routes, exactly like `BuildPipeline.tryRewrite` — see
        // the production environment's tryRewrite for why the derived
        // (ensured-404) table is NOT observably identical here.
        routes: manifest.routes.map((r) => r.routeData),
        trailingSlash: manifest.trailingSlash,
        buildFormat: manifest.buildFormat,
        base: manifest.base,
        outDir: manifest.serverLike ? manifest.buildClientDir : manifest.outDir
      });
      const componentInstance = await getComponentByRoute(manifest, routeData);
      return { routeData, componentInstance, newUrl, pathname };
    },
    getRenderers(manifest) {
      return manifest.renderers;
    },
    errorStrategy: "build",
    injectCspMetaTagsOnErrorPages: false,
    logRequest() {
    }
  };
  return {
    env,
    setInternals(value) {
      internals = value;
    },
    setOptions(value) {
      options = value;
    },
    getInternals,
    getOptions,
    getSettings
  };
}
export {
  createBuildEnvironment
};
