import { appendForwardSlash } from "@astrojs/internal-helpers/path";
import { computeFallbackRoute } from "../../i18n/fallback.js";
import { I18nRouter } from "../../i18n/router.js";
import { markFeatureUsed, FetchFeatures } from "../fetch/features.js";
import { shouldAppendForwardSlash } from "../build/util.js";
import { createManifestMemo } from "../manifest/memo.js";
function compileI18n(i18n, base, trailingSlash, format) {
  const router = new I18nRouter({
    strategy: i18n.strategy,
    defaultLocale: i18n.defaultLocale,
    locales: i18n.locales,
    base,
    domains: i18n.domainLookupTable ? Object.keys(i18n.domainLookupTable).reduce(
      (acc, domain) => {
        const locale = i18n.domainLookupTable[domain];
        if (!acc[domain]) {
          acc[domain] = [];
        }
        acc[domain].push(locale);
        return acc;
      },
      {}
    ) : void 0
  });
  return { config: i18n, base, trailingSlash, format, router };
}
const i18nMemo = createManifestMemo((manifest) => {
  const config = manifest.i18n;
  return config && config.strategy !== "manual" ? compileI18n(config, manifest.base, manifest.trailingSlash, manifest.buildFormat) : null;
});
function getI18n(manifest) {
  return i18nMemo.get(manifest);
}
async function finalizeI18n(compiled, state, response) {
  markFeatureUsed(state.manifest, FetchFeatures.i18n);
  const i18n = compiled.config;
  if (state.skipErrorReroute && typeof i18n.fallback === "undefined") {
    return response;
  }
  if (state.responseRouteType !== "page" && state.responseRouteType !== "fallback") {
    return response;
  }
  const url = state.url;
  const currentLocale = state.computeCurrentLocale();
  const isPrerendered = state.routeData.prerender;
  const routerContext = {
    currentLocale,
    currentDomain: url.hostname,
    routeType: state.responseRouteType,
    isReroute: false
  };
  const routeDecision = compiled.router.match(url.pathname, routerContext);
  switch (routeDecision.type) {
    case "redirect": {
      let location = routeDecision.location;
      if (shouldAppendForwardSlash(compiled.trailingSlash, compiled.format)) {
        location = appendForwardSlash(location);
      }
      return new Response(null, {
        status: routeDecision.status ?? 302,
        headers: { Location: location }
      });
    }
    case "notFound": {
      if (isPrerendered) {
        const prerenderedRes = new Response(response.body, {
          status: 404,
          headers: response.headers
        });
        state.skipErrorReroute = true;
        if (routeDecision.location) {
          prerenderedRes.headers.set("Location", routeDecision.location);
        }
        return prerenderedRes;
      }
      const headers = new Headers();
      if (routeDecision.location) {
        headers.set("Location", routeDecision.location);
      }
      return new Response(null, { status: 404, headers });
    }
    case "continue":
      break;
  }
  if (i18n.fallback && i18n.fallbackType) {
    const effectiveStatus = state.responseRouteType === "fallback" ? 404 : response.status;
    const fallbackDecision = computeFallbackRoute({
      pathname: url.pathname,
      responseStatus: effectiveStatus,
      currentLocale,
      fallback: i18n.fallback,
      fallbackType: i18n.fallbackType,
      locales: i18n.locales,
      defaultLocale: i18n.defaultLocale,
      strategy: i18n.strategy,
      base: compiled.base
    });
    switch (fallbackDecision.type) {
      case "redirect":
        return new Response(null, {
          status: 302,
          headers: { Location: fallbackDecision.pathname + url.search }
        });
      case "rewrite":
        try {
          return await state.rewrite(fallbackDecision.pathname + url.search);
        } catch {
          break;
        }
      case "none":
        break;
    }
  }
  return response;
}
export {
  compileI18n,
  finalizeI18n,
  getI18n
};
