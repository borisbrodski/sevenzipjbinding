import { createOriginCheckMiddleware } from "../app/origin-check.js";
import { createAsyncManifestMemo } from "../manifest/memo.js";
import { NOOP_MIDDLEWARE_FN } from "./noop-middleware.js";
import { sequence } from "./sequence.js";
const resolvedMiddleware = /* @__PURE__ */ new WeakMap();
const middlewareMemo = createAsyncManifestMemo(async (manifest) => {
  let handler;
  if (manifest.middleware) {
    const middlewareInstance = await manifest.middleware();
    const onRequest = middlewareInstance.onRequest ?? NOOP_MIDDLEWARE_FN;
    const internalMiddlewares = [onRequest];
    if (manifest.checkOrigin) {
      internalMiddlewares.unshift(createOriginCheckMiddleware());
    }
    handler = sequence(...internalMiddlewares);
  } else {
    handler = NOOP_MIDDLEWARE_FN;
  }
  resolvedMiddleware.set(manifest, handler);
  return handler;
});
function getMiddleware(manifest) {
  return middlewareMemo.get(manifest);
}
function peekMiddleware(manifest) {
  return resolvedMiddleware.get(manifest);
}
function clearMiddleware(manifest) {
  middlewareMemo.invalidate(manifest);
  resolvedMiddleware.delete(manifest);
}
export {
  clearMiddleware,
  getMiddleware,
  peekMiddleware
};
