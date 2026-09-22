import { renderEndpoint } from "../../runtime/server/endpoint.js";
import { renderPage } from "../../runtime/server/index.js";
import { ASTRO_ERROR_HEADER } from "../constants.js";
import {
  createCrossOriginForbiddenResponse,
  isForbiddenCrossOriginRequest
} from "../app/origin-check.js";
import { getCookiesFromResponse } from "../cookies/response.js";
import { renderErrorFromState } from "../errors/handler.js";
const EMPTY_SLOTS = Object.freeze({});
async function handlePages(state, ctx) {
  const { logger, streaming } = state;
  state.resetResponseMetadata();
  let response;
  const componentInstance = await state.loadComponentInstance();
  switch (state.routeData.type) {
    case "endpoint": {
      response = await renderEndpoint(
        componentInstance,
        ctx,
        state.routeData.prerender,
        logger,
        state
      );
      break;
    }
    case "page": {
      const props = await state.getProps();
      const actionApiContext = state.getActionAPIContext();
      const result = await state.createResult(componentInstance, actionApiContext);
      try {
        response = await renderPage(
          result,
          componentInstance?.default,
          props,
          state.slots ?? EMPTY_SLOTS,
          streaming,
          state.routeData
        );
      } catch (e) {
        result.cancelled = true;
        throw e;
      }
      state.responseRouteType = "page";
      if (state.routeData.route === "/404" || state.routeData.route === "/500") {
        state.skipErrorReroute = true;
      }
      break;
    }
    case "redirect": {
      return new Response(null, { status: 404, headers: { [ASTRO_ERROR_HEADER]: "true" } });
    }
    case "fallback": {
      state.responseRouteType = "fallback";
      return new Response(null, { status: 500 });
    }
  }
  const responseCookies = getCookiesFromResponse(response);
  if (responseCookies) {
    state.cookies.merge(responseCookies);
  }
  state.response = response;
  return response;
}
async function handlePagesWithErrorFallback(state) {
  if (!state.routeData) {
    return new Response(null, { status: 404, headers: { [ASTRO_ERROR_HEADER]: "true" } });
  }
  const ctx = state.getAPIContext();
  if (state.manifest.checkOrigin && isForbiddenCrossOriginRequest(ctx.request, ctx.url, ctx.isPrerendered)) {
    return createCrossOriginForbiddenResponse(ctx.request);
  }
  try {
    return await handlePages(state, ctx);
  } catch (err) {
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
  handlePages,
  handlePagesWithErrorFallback
};
