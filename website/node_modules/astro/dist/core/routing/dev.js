import { matchAllRoutes } from "./match.js";
import { getSortedPreloadedMatches } from "../../prerender/routing.js";
import { getProps } from "../render/index.js";
import { getCustom404Route } from "./helpers.js";
import { NoMatchingStaticPathFound } from "../errors/errors-data.js";
import { isAstroError } from "../errors/errors.js";
import { getEnvironment } from "../environment/index.js";
import { getLogger } from "../logger/manifest-logger.js";
import { getRouteCache } from "../render/route-cache.js";
import { getRouteTable } from "./route-table.js";
import { getErrorRoutePath } from "../../i18n/error-routes.js";
async function matchRoute(manifest, pathname, { prerenderOnly } = {}) {
  const logger = getLogger(manifest);
  const routeCache = getRouteCache(manifest);
  const env = getEnvironment(manifest);
  const routesList = getRouteTable(manifest);
  const matches = matchAllRoutes(pathname, routesList);
  const preloadedMatches = getSortedPreloadedMatches({
    matches,
    manifest
  });
  let firstError = null;
  let skippedPrerenderOnly = false;
  for await (const { route: maybeRoute, filePath } of preloadedMatches) {
    if (prerenderOnly && !maybeRoute.prerender) {
      skippedPrerenderOnly = true;
      continue;
    }
    try {
      await getProps({
        mod: await env.getComponentByRoute(manifest, maybeRoute),
        routeData: maybeRoute,
        routeCache,
        pathname,
        logger,
        serverLike: manifest.serverLike,
        base: manifest.base,
        trailingSlash: manifest.trailingSlash
      });
      return {
        route: maybeRoute,
        filePath,
        resolvedPathname: pathname
      };
    } catch (e) {
      if (isAstroError(e) && e.title === NoMatchingStaticPathFound.title) {
        continue;
      }
      firstError ??= e;
      continue;
    }
  }
  if (firstError) {
    throw firstError;
  }
  const altPathname = pathname.replace(/\/index\.html$/, "/").replace(/\.html$/, "");
  if (altPathname !== pathname) {
    return await matchRoute(manifest, altPathname, { prerenderOnly });
  }
  if (skippedPrerenderOnly) {
    return void 0;
  }
  if (matches.length) {
    const possibleRoutes = matches.flatMap((route) => route.component);
    logger.warn(
      "router",
      `${NoMatchingStaticPathFound.message(
        pathname
      )}

${NoMatchingStaticPathFound.hint(possibleRoutes)}`
    );
  }
  const errorRoutePath = getErrorRoutePath(
    pathname,
    404,
    routesList.routes,
    manifest.i18n?.locales,
    manifest.trailingSlash === "always"
  );
  const custom404 = routesList.routes.find((route) => route.route === errorRoutePath) ?? getCustom404Route(routesList);
  if (custom404) {
    const filePath = new URL(`./${custom404.component}`, manifest.rootDir);
    return {
      route: custom404,
      filePath,
      resolvedPathname: pathname
    };
  }
  return void 0;
}
export {
  matchRoute
};
