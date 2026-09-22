import { pathHasLocale } from "./path.js";
function isLocalizedErrorRoute(route, status, locales) {
  if (!locales) return false;
  const suffix = `/${status}`;
  if (!route.endsWith(suffix)) return false;
  const localeSegment = route.slice(0, -suffix.length);
  if (!localeSegment || localeSegment.includes("/", 1)) return false;
  return pathHasLocale(localeSegment, locales);
}
function getErrorRoutePath(pathname, status, routes, locales, appendTrailingSlash = false) {
  const suffix = appendTrailingSlash ? "/" : "";
  if (locales) {
    const firstSegment = pathname.split("/").find(Boolean);
    if (firstSegment && pathHasLocale(`/${firstSegment}`, locales)) {
      const localized = `/${firstSegment}/${status}`;
      if (routes.some((route) => route.route === localized)) {
        return `${localized}${suffix}`;
      }
    }
  }
  return `/${status}${suffix}`;
}
export {
  getErrorRoutePath,
  isLocalizedErrorRoute
};
