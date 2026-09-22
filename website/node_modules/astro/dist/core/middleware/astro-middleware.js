import { ASTRO_ERROR_HEADER } from "../constants.js";
import { attachCookiesToResponse } from "../cookies/index.js";
import { getEnvironment } from "../environment/index.js";
import { renderErrorFromState } from "../errors/handler.js";
import { markFeatureUsed, FetchFeatures } from "../fetch/features.js";
import { applyRewriteToState } from "../rewrites/handler.js";
import { callMiddleware } from "./callMiddleware.js";
import { getMiddleware } from "./load.js";
import { sequence } from "./index.js";
async function handleMiddleware(state, renderRouteCallback) {
  markFeatureUsed(state.manifest, FetchFeatures.middleware);
  await state.getProps();
  const apiContext = state.getAPIContext();
  state.counter++;
  if (state.counter === 4) {
    return new Response("Loop Detected", {
      // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/508
      status: 508,
      statusText: "Astro detected a loop where you tried to call the rewriting logic more than four times."
    });
  }
  const next = async (ctx, payload) => {
    if (payload) {
      state.logger.debug("router", "Called rewriting to:", payload);
      const result = await getEnvironment(state.manifest).tryRewrite(
        state.manifest,
        payload,
        state.request
      );
      applyRewriteToState(state, payload, result);
    }
    return renderRouteCallback(state, ctx);
  };
  let response;
  if (state.skipMiddleware) {
    response = await next(apiContext);
  } else {
    const middleware = await getMiddleware(state.manifest);
    const composed = sequence(middleware);
    response = await callMiddleware(composed, apiContext, next);
  }
  attachCookiesToResponse(response, state.cookies);
  state.response = response;
  return response;
}
async function handleMiddlewareWithErrorFallback(state, renderRouteCallback) {
  if (!state.routeData) {
    return new Response(null, { status: 404, headers: { [ASTRO_ERROR_HEADER]: "true" } });
  }
  let nextError;
  try {
    return await handleMiddleware(state, async (s, ctx) => {
      try {
        return await renderRouteCallback(s, ctx);
      } catch (err) {
        nextError = err;
        throw err;
      }
    });
  } catch (err) {
    if (err === nextError) throw err;
    state.logger.error(null, err.stack || err.message || String(err));
    return renderErrorFromState(state, state.request, {
      ...state.renderOptions,
      status: 500,
      error: err,
      pathname: state.pathname
    });
  }
}
export {
  handleMiddleware,
  handleMiddlewareWithErrorFallback
};
