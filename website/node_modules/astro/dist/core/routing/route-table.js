import { createManifestMemo } from "../manifest/memo.js";
import { ensure404Route } from "./astro-designed-error-pages.js";
import { Router } from "./router.js";
function compileRouteTable(manifest, routes) {
  const routesList = ensure404Route({ routes });
  const router = new Router(routesList.routes, {
    base: manifest.base,
    trailingSlash: manifest.trailingSlash,
    buildFormat: manifest.buildFormat
  });
  return { routes: routesList.routes, router };
}
const routeTables = createManifestMemo(
  (manifest) => (
    // Fresh array: `ensure404Route` mutates the DERIVED list, never `manifest.routes`.
    compileRouteTable(
      manifest,
      (manifest.routes ?? []).map((route) => route.routeData)
    )
  )
);
function getRouteTable(manifest) {
  return routeTables.get(manifest);
}
function updateRouteTable(manifest, routes) {
  routeTables.set(manifest, compileRouteTable(manifest, [...routes]));
}
function matchRoute(manifest, pathname) {
  const match = getRouteTable(manifest).router.match(pathname, { allowWithoutBase: true });
  if (match.type !== "match") return void 0;
  return match.route;
}
function matchAllRoutes(manifest, pathname) {
  return getRouteTable(manifest).router.matchAll(pathname, { allowWithoutBase: true });
}
export {
  getRouteTable,
  matchAllRoutes,
  matchRoute,
  updateRouteTable
};
