import { REROUTABLE_STATUS_CODES } from "../constants.js";
import { getEnvironment } from "../environment/index.js";
import { renderBuildError } from "./build-handler.js";
import { renderDefaultError } from "./default-handler.js";
import { renderDevError } from "./dev-handler.js";
function renderErrorPage(manifest, request, options) {
  const env = getEnvironment(manifest);
  switch (env.errorStrategy) {
    case "dev":
      return renderDevError(manifest, request, options, {
        shouldInjectCspMetaTags: env.injectCspMetaTagsOnErrorPages
      });
    case "build":
      return renderBuildError(manifest, request, options);
    case "default":
      return renderDefaultError(manifest, request, options);
  }
}
function renderErrorFromState(state, request, options) {
  if (state.renderError) {
    return state.renderError(request, options);
  }
  return renderErrorPage(state.manifest, request, options);
}
function rewroteToEmptyErrorResponse(skipMiddleware, errorRouteData, renderedRouteData, response) {
  return skipMiddleware === false && renderedRouteData !== errorRouteData && response.body === null && REROUTABLE_STATUS_CODES.includes(response.status);
}
export {
  renderErrorFromState,
  renderErrorPage,
  rewroteToEmptyErrorResponse
};
