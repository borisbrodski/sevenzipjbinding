import { handleAction } from "../../actions/handler.js";
import { REROUTABLE_STATUS_CODES } from "../constants.js";
import { handleTrailingSlash } from "./trailing-slash-handler.js";
import { handleCache, provideCache } from "../cache/handler.js";
import { getEnvironment } from "../environment/index.js";
import { renderErrorFromState } from "../errors/handler.js";
import { ALL_FETCH_FEATURES, markFeatureUsed, FetchFeatures } from "../fetch/features.js";
import { finalizeI18n, getI18n } from "../i18n/handler.js";
import { getResolvedLogger } from "../logger/manifest-logger.js";
import { handleMiddleware } from "../middleware/astro-middleware.js";
import { handlePages } from "../pages/handler.js";
import { renderRedirect } from "../redirects/render.js";
import { provideSession } from "../session/provider.js";
import { prepareResponse } from "../app/prepare-response.js";
import { getDefaultStatusCode } from "./helpers.js";
function logRequestFromState(state, payload) {
  if (state.logRequest) {
    state.logRequest(payload);
  } else {
    getEnvironment(state.manifest).logRequest(state.manifest, payload);
  }
}
function actionsAndPages(state, ctx) {
  if (!state.skipMiddleware) {
    const actionResult = handleAction(ctx, state);
    if (actionResult) {
      return actionResult.then((response) => response ?? handlePages(state, ctx));
    }
  }
  return handlePages(state, ctx);
}
async function handleRequest(state) {
  await getResolvedLogger(state.manifest);
  markFeatureUsed(state.manifest, ALL_FETCH_FEATURES);
  if (state.invalidEncoding) {
    return new Response(null, { status: 400, statusText: "Bad Request" });
  }
  const trailingSlashRedirect = handleTrailingSlash(state);
  if (trailingSlashRedirect) {
    return trailingSlashRedirect;
  }
  if (!state.routeData) {
    return renderErrorFromState(state, state.request, {
      ...state.renderOptions,
      status: 404,
      pathname: state.pathname
    });
  }
  return render(state);
}
async function render(state) {
  const routeData = state.routeData;
  const pathname = state.pathname;
  const request = state.request;
  const { addCookieHeader } = state.renderOptions;
  state.status = getDefaultStatusCode(state.manifest, routeData, pathname);
  let response;
  let finalizeError;
  try {
    const sessionP = state.manifest.sessionConfig ? provideSession(state) : void 0;
    const cacheP = provideCache(state);
    if (sessionP || cacheP) await Promise.all([sessionP, cacheP]);
    markFeatureUsed(state.manifest, FetchFeatures.sessions);
    if (routeData.type === "redirect") {
      const redirectResponse = await renderRedirect(state);
      logRequestFromState(state, {
        pathname,
        method: request.method,
        statusCode: redirectResponse.status,
        isRewrite: false,
        timeStart: state.timeStart
      });
      prepareResponse(redirectResponse, { addCookieHeader });
      state.logger.flush();
      return redirectResponse;
    }
    const i18n = getI18n(state.manifest);
    if (!state.manifest.cacheProvider) {
      markFeatureUsed(state.manifest, FetchFeatures.cache);
      response = await handleMiddleware(state, actionsAndPages);
      if (i18n) {
        response = await finalizeI18n(i18n, state, response);
      }
    } else {
      const runPipeline = async () => {
        let res = await handleMiddleware(state, actionsAndPages);
        if (i18n) {
          res = await finalizeI18n(i18n, state, res);
        }
        return res;
      };
      response = await handleCache(state, runPipeline);
    }
    logRequestFromState(state, {
      pathname,
      method: request.method,
      statusCode: response.status,
      isRewrite: state.isRewriting,
      timeStart: state.timeStart
    });
  } catch (err) {
    state.logger.error(null, err.stack || err.message || String(err));
    return renderErrorFromState(state, request, {
      ...state.renderOptions,
      status: 500,
      error: err,
      pathname: state.pathname
    });
  } finally {
    try {
      const finalize = state.finalizeAll();
      if (finalize) await finalize;
    } catch (err) {
      finalizeError = err;
      state.logger.error(null, err.stack || err.message || String(err));
    }
  }
  if (finalizeError) {
    return renderErrorFromState(state, request, {
      ...state.renderOptions,
      status: 500,
      error: finalizeError,
      pathname: state.pathname
    });
  }
  if (REROUTABLE_STATUS_CODES.includes(response.status) && // If the body isn't null, that means the user sets the 404 status
  // but uses the current route to handle the 404
  response.body === null && !state.skipErrorReroute) {
    return renderErrorFromState(state, request, {
      ...state.renderOptions,
      response,
      status: response.status,
      // We don't have an error to report here. Passing null means we pass nothing intentionally
      // while undefined means there's no error
      error: response.status === 500 ? null : void 0,
      pathname: state.pathname
    });
  }
  prepareResponse(response, { addCookieHeader });
  state.logger.flush();
  return response;
}
export {
  handleRequest
};
