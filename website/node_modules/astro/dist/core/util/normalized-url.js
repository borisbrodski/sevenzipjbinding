import { collapseDuplicateSlashes } from "@astrojs/internal-helpers/path";
import { validateAndDecodePathname } from "./pathname.js";
function createNormalizedUrl(requestUrl) {
  return normalizeUrl(new URL(requestUrl));
}
function setPathname(url, pathname) {
  if (url.pathname !== pathname) {
    url.pathname = pathname;
  }
}
function normalizeUrl(url) {
  try {
    setPathname(url, validateAndDecodePathname(url.pathname));
  } catch {
    try {
      setPathname(url, decodeURI(url.pathname));
    } catch {
    }
  }
  setPathname(url, collapseDuplicateSlashes(url.pathname));
  return url;
}
export {
  createNormalizedUrl,
  normalizeUrl,
  setPathname
};
