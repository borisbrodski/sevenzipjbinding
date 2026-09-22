import { handleAction } from "../../actions/handler.js";
import { FetchState as BaseFetchState } from "./fetch-state.js";
import { handleCache } from "../cache/handler.js";
import { finalizeI18n, getI18n } from "../i18n/handler.js";
import { getAmbientManifest } from "../manifest/ambient.js";
import { handleMiddlewareWithErrorFallback } from "../middleware/astro-middleware.js";
import { handlePagesWithErrorFallback } from "../pages/handler.js";
import { renderRedirect } from "../redirects/render.js";
import { handleRequest } from "../routing/handler.js";
import { provideSession } from "../session/provider.js";
import { handleTrailingSlash } from "../routing/trailing-slash-handler.js";
class FetchState extends BaseFetchState {
  constructor(request) {
    super(getAmbientManifest(), request);
  }
}
function astro(state) {
  return handleRequest(state);
}
function trailingSlash(state) {
  return handleTrailingSlash(state);
}
function middleware(state, next) {
  return handleMiddlewareWithErrorFallback(state, (s, _ctx) => next(s));
}
function pages(state) {
  return handlePagesWithErrorFallback(state);
}
function sessions(state) {
  return provideSession(state);
}
function redirects(state) {
  if (state.routeData?.type === "redirect") {
    return renderRedirect(state);
  }
  return void 0;
}
function actions(state) {
  return handleAction(state.getAPIContext(), state);
}
function i18n(state, response) {
  const compiled = getI18n(state.manifest);
  if (!compiled) return Promise.resolve(response);
  return finalizeI18n(compiled, state, response);
}
function cache(state, next) {
  return handleCache(state, next);
}
export {
  FetchState,
  actions,
  astro,
  cache,
  i18n,
  middleware,
  pages,
  redirects,
  sessions,
  trailingSlash
};
