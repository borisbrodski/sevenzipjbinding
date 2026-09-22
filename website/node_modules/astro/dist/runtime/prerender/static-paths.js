import { getEnvironment } from "../../core/environment/index.js";
import { stringifyParams } from "../../core/routing/params.js";
import { getFallbackRoute, routeIsFallback, routeIsRedirect } from "../../core/routing/helpers.js";
import { callGetStaticPaths, getRouteCache } from "../../core/render/route-cache.js";
class StaticPaths {
  #app;
  constructor(app) {
    this.#app = app;
  }
  /**
   * Get all static paths for prerendering with their associated routes.
   * This avoids needing to re-match routes later, which can be incorrect due to route priority.
   */
  async getAll() {
    const allPaths = [];
    const manifest = this.#app.manifest;
    const routesToGenerate = [];
    for (const { routeData } of manifest.routes) {
      if (!routeData.prerender) continue;
      if (routeIsRedirect(routeData)) {
        routesToGenerate.push(routeData);
        continue;
      }
      if (routeIsFallback(routeData) && manifest.i18n?.fallback) {
        routesToGenerate.push(routeData);
        continue;
      }
      routesToGenerate.push(routeData);
    }
    for (const route of routesToGenerate) {
      for (const currentRoute of eachRouteInRouteData(route)) {
        const paths = await this.#getPathsForRoute(currentRoute);
        for (const path of paths) {
          allPaths.push(path);
        }
      }
    }
    return allPaths;
  }
  /**
   * Get paths for a single route.
   * Note: Does not filter duplicates - that's handled by generate.ts with conflict detection.
   */
  async #getPathsForRoute(route) {
    const paths = [];
    const manifest = this.#app.manifest;
    const routeCache = this.#app.routeCache ?? getRouteCache(manifest);
    if (route.pathname) {
      paths.push({ pathname: route.pathname, route });
      return paths;
    }
    const componentInstance = this.#app.getComponentByRoute ? await this.#app.getComponentByRoute(route) : await getEnvironment(manifest).getComponentByRoute(manifest, route);
    const routeToProcess = routeIsRedirect(route) ? route.redirectRoute : routeIsFallback(route) ? getFallbackRoute(route, manifest.routes) : route;
    const actualRoute = routeToProcess ?? route;
    const staticPaths = await callGetStaticPaths({
      mod: componentInstance,
      route: actualRoute,
      routeCache,
      ssr: manifest.serverLike,
      base: manifest.base,
      trailingSlash: manifest.trailingSlash
    });
    for (const staticPath of staticPaths) {
      const pathname = stringifyParams(staticPath.params, route, manifest.trailingSlash);
      paths.push({
        pathname,
        route,
        cacheKey: staticPath.cacheKey === void 0 ? void 0 : String(staticPath.cacheKey)
      });
    }
    return paths;
  }
}
function* eachRouteInRouteData(route) {
  yield route;
  for (const fallbackRoute of route.fallbackRoutes) {
    yield fallbackRoute;
  }
}
export {
  StaticPaths
};
