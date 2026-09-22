import { prependForwardSlash, stripRequestBase } from "@astrojs/internal-helpers/path";
import { computePathnameFromDomain } from "../i18n/domain.js";
import { AstroIntegrationLogger } from "../logger/core.js";
import { getLogger } from "../logger/manifest-logger.js";
import { validateAndDecodePathname } from "../util/pathname.js";
import { matchAllRoutes, matchRoute } from "./route-table.js";
function safeDecodePathname(manifest, pathname) {
  try {
    return validateAndDecodePathname(pathname);
  } catch (e) {
    new AstroIntegrationLogger(getLogger(manifest).options, manifest.adapterName).debug(
      e.toString()
    );
    try {
      return decodeURI(pathname);
    } catch {
      return pathname;
    }
  }
}
function matchRequest(manifest, request, allowPrerenderedRoutes = false) {
  const url = new URL(request.url);
  if (manifest.assets.has(url.pathname)) return void 0;
  let pathname = computePathnameFromDomain(
    request,
    url,
    manifest.i18n,
    manifest.base,
    manifest.trailingSlash,
    getLogger(manifest)
  );
  if (!pathname) {
    pathname = prependForwardSlash(stripRequestBase(url.pathname, manifest.base));
  }
  pathname = safeDecodePathname(manifest, pathname);
  const routeData = matchRoute(manifest, pathname);
  if (!routeData) return void 0;
  if (allowPrerenderedRoutes) {
    return routeData;
  }
  if (routeData.prerender) {
    if (routeData.params.length > 0) {
      const allMatches = matchAllRoutes(manifest, pathname);
      return allMatches.find((r) => !r.prerender);
    }
    return void 0;
  }
  return routeData;
}
export {
  matchRequest
};
