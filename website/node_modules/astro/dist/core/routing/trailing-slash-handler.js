import {
  appendForwardSlash,
  collapseDuplicateTrailingSlashes,
  hasFileExtension,
  isInternalPath,
  removeTrailingForwardSlash
} from "@astrojs/internal-helpers/path";
import { prepareResponse } from "../app/prepare-response.js";
import { redirectTemplate } from "./3xx.js";
function handleTrailingSlash(state) {
  const url = new URL(state.request.url);
  const redirect = redirectTrailingSlash(state.manifest.trailingSlash, url.pathname);
  if (redirect === url.pathname) {
    return void 0;
  }
  const addCookieHeader = state.renderOptions.addCookieHeader;
  const status = state.request.method === "GET" ? 301 : 308;
  const location = redirect + url.search;
  const response = new Response(
    redirectTemplate({
      status,
      relativeLocation: location,
      absoluteLocation: location,
      from: state.request.url
    }),
    {
      status,
      headers: {
        location
      }
    }
  );
  prepareResponse(response, { addCookieHeader });
  return response;
}
function redirectTrailingSlash(trailingSlash, pathname) {
  if (pathname === "/" || isInternalPath(pathname)) {
    return pathname;
  }
  const path = collapseDuplicateTrailingSlashes(pathname, trailingSlash !== "never");
  if (path !== pathname) {
    return path;
  }
  if (trailingSlash === "ignore") {
    return pathname;
  }
  if (trailingSlash === "always" && !hasFileExtension(pathname)) {
    return appendForwardSlash(pathname);
  }
  if (trailingSlash === "never") {
    return removeTrailingForwardSlash(pathname);
  }
  return pathname;
}
export {
  handleTrailingSlash
};
