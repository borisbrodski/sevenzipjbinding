import { fetchStateSymbol } from "../constants.js";
import { getEnvironment } from "../environment/index.js";
import { ForbiddenRewrite } from "../errors/errors-data.js";
import { AstroError } from "../errors/index.js";
import { getParams } from "../render/params-and-props.js";
import { copyRequest, setOriginPathname } from "../routing/rewrite.js";
import { defineMiddleware } from "./defineMiddleware.js";
function sequence(...handlers) {
  const filtered = handlers.filter((h) => !!h);
  const length = filtered.length;
  if (!length) {
    return defineMiddleware((_context, next) => {
      return next();
    });
  }
  return defineMiddleware((context, next) => {
    let carriedPayload = void 0;
    return applyHandle(0, context);
    function applyHandle(i, handleContext) {
      const handle = filtered[i];
      const result = handle(handleContext, async (payload) => {
        if (i < length - 1) {
          if (payload) {
            const oldPathname = handleContext.url.pathname;
            const state = Reflect.get(handleContext, fetchStateSymbol);
            if (!state) {
              throw new Error(
                "FetchState not found on APIContext. `next(payload)` rewrites require a context created through Astro's request pipeline."
              );
            }
            const manifest = state.manifest;
            const { routeData, pathname } = await getEnvironment(manifest).tryRewrite(
              manifest,
              payload,
              handleContext.request
            );
            let newRequest;
            if (payload instanceof Request) {
              newRequest = payload;
            } else {
              const request = handleContext.request.method === "GET" || handleContext.request.method === "HEAD" ? handleContext.request : handleContext.request.clone();
              const newUrl = payload instanceof URL ? payload : new URL(payload, handleContext.url.origin);
              newRequest = copyRequest(newUrl, request, false, state.logger, routeData.route);
            }
            if (manifest.serverLike === true && handleContext.isPrerendered === false && routeData.prerender === true) {
              throw new AstroError({
                ...ForbiddenRewrite,
                message: ForbiddenRewrite.message(
                  handleContext.url.pathname,
                  pathname,
                  routeData.component
                ),
                hint: ForbiddenRewrite.hint(routeData.component)
              });
            }
            carriedPayload = payload;
            handleContext.request = newRequest;
            handleContext.url = new URL(newRequest.url);
            handleContext.params = getParams(routeData, pathname);
            handleContext.routePattern = routeData.route;
            setOriginPathname(
              handleContext.request,
              oldPathname,
              manifest.trailingSlash,
              manifest.buildFormat
            );
          }
          return applyHandle(i + 1, handleContext);
        } else {
          return next(payload ?? carriedPayload);
        }
      });
      return result;
    }
  });
}
export {
  sequence
};
