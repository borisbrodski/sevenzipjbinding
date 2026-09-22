import { getEnvironment } from "../environment/index.js";
import { FetchState } from "../fetch/fetch-state.js";
import { getLogger } from "../logger/manifest-logger.js";
import { handleMiddleware } from "../middleware/astro-middleware.js";
import { handlePages } from "../pages/handler.js";
import { getCustom404Route, getCustom500Route } from "../routing/helpers.js";
import { getRouteTable } from "../routing/route-table.js";
import { isAstroError } from "./index.js";
import { MiddlewareNoDataOrNextCalled, MiddlewareNotAResponse } from "./errors-data.js";
import { rewroteToEmptyErrorResponse } from "./handler.js";
async function renderDevError(manifest, request, {
  skipMiddleware = false,
  error,
  status,
  response: _response,
  pathname,
  ...resolvedRenderOptions
}, { shouldInjectCspMetaTags }) {
  if (isAstroError(error) && [MiddlewareNoDataOrNextCalled.name, MiddlewareNotAResponse.name].includes(error.name)) {
    throw error;
  }
  const resolvedPathname = pathname ?? new FetchState(manifest, request).pathname;
  const renderRoute = async (routeData) => {
    try {
      const preloadedComponent = await getEnvironment(manifest).getComponentByRoute(
        manifest,
        routeData
      );
      const errorState = new FetchState(manifest, request);
      errorState.skipMiddleware = skipMiddleware;
      errorState.clientAddress = resolvedRenderOptions.clientAddress;
      errorState.shouldInjectCspMetaTags = shouldInjectCspMetaTags ? !!manifest.csp : false;
      errorState.routeData = routeData;
      errorState.pathname = resolvedPathname;
      errorState.status = status;
      errorState.componentInstance = preloadedComponent;
      errorState.locals = resolvedRenderOptions.locals ?? {};
      errorState.initialProps = { error };
      const response = await handleMiddleware(errorState, handlePages);
      if (rewroteToEmptyErrorResponse(skipMiddleware, routeData, errorState.routeData, response)) {
        return renderDevError(
          manifest,
          request,
          {
            ...resolvedRenderOptions,
            status,
            error,
            skipMiddleware: true,
            pathname: resolvedPathname
          },
          { shouldInjectCspMetaTags }
        );
      }
      if (error) {
        getLogger(manifest).error(
          "router",
          error.stack || error.message
        );
      }
      return response;
    } catch (_err) {
      if (skipMiddleware === false) {
        return renderDevError(
          manifest,
          request,
          {
            ...resolvedRenderOptions,
            status: 500,
            skipMiddleware: true,
            error: _err,
            pathname: resolvedPathname
          },
          { shouldInjectCspMetaTags }
        );
      }
      throw _err;
    }
  };
  if (status === 404) {
    const custom404 = getCustom404Route(getRouteTable(manifest));
    if (custom404) {
      return renderRoute(custom404);
    }
  }
  const custom500 = getCustom500Route(getRouteTable(manifest));
  if (!custom500) {
    throw error;
  } else {
    return renderRoute(custom500);
  }
}
export {
  renderDevError
};
