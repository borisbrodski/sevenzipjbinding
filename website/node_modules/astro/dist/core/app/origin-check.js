import { defineMiddleware } from "../middleware/defineMiddleware.js";
const FORM_CONTENT_TYPES = [
  "application/x-www-form-urlencoded",
  "multipart/form-data",
  "text/plain"
];
const SAFE_METHODS = ["GET", "HEAD", "OPTIONS"];
function isForbiddenCrossOriginRequest(request, url, isPrerendered) {
  if (isPrerendered) {
    return false;
  }
  if (SAFE_METHODS.includes(request.method)) {
    return false;
  }
  const isSameOrigin = request.headers.get("origin") === url.origin;
  const hasContentType = request.headers.has("content-type");
  if (hasContentType) {
    const formLikeHeader = hasFormLikeHeader(request.headers.get("content-type"));
    return formLikeHeader && !isSameOrigin;
  }
  return !isSameOrigin;
}
function createCrossOriginForbiddenResponse(request) {
  return new Response(`Cross-site ${request.method} form submissions are forbidden`, {
    status: 403
  });
}
function createOriginCheckMiddleware() {
  return defineMiddleware((context, next) => {
    const { request, url, isPrerendered } = context;
    if (isForbiddenCrossOriginRequest(request, url, isPrerendered)) {
      return createCrossOriginForbiddenResponse(request);
    }
    return next();
  });
}
function hasFormLikeHeader(contentType) {
  if (contentType) {
    for (const FORM_CONTENT_TYPE of FORM_CONTENT_TYPES) {
      if (contentType.toLowerCase().includes(FORM_CONTENT_TYPE)) {
        return true;
      }
    }
  }
  return false;
}
export {
  createCrossOriginForbiddenResponse,
  createOriginCheckMiddleware,
  hasFormLikeHeader,
  isForbiddenCrossOriginRequest
};
