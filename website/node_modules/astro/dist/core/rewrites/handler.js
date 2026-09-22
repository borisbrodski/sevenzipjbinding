import { AstroCookies } from "../cookies/index.js";
import { getEnvironment } from "../environment/index.js";
import { ForbiddenRewrite } from "../errors/errors-data.js";
import { AstroError } from "../errors/errors.js";
import { handleMiddleware } from "../middleware/astro-middleware.js";
import { handlePages } from "../pages/handler.js";
import { getParams } from "../render/params-and-props.js";
import { copyRequest, setOriginPathname } from "../routing/rewrite.js";
import { createNormalizedUrl } from "../util/normalized-url.js";
function applyRewriteToState(state, payload, { routeData, componentInstance, newUrl, pathname }, { mergeCookies = false } = {}) {
  const oldPathname = state.pathname;
  const isI18nFallback = routeData.fallbackRoutes && routeData.fallbackRoutes.length > 0;
  if (state.manifest.serverLike && !state.routeData.prerender && routeData.prerender && !isI18nFallback) {
    throw new AstroError({
      ...ForbiddenRewrite,
      message: ForbiddenRewrite.message(state.pathname, pathname, routeData.component),
      hint: ForbiddenRewrite.hint(routeData.component)
    });
  }
  state.routeData = routeData;
  state.componentInstance = componentInstance;
  if (payload instanceof Request) {
    state.request = payload;
  } else {
    state.request = copyRequest(
      newUrl,
      state.request,
      routeData.prerender,
      state.logger,
      state.routeData.route
    );
  }
  state.url = createNormalizedUrl(state.request.url);
  if (mergeCookies) {
    const newCookies = new AstroCookies(state.request, state.logger);
    if (state.cookies) {
      newCookies.merge(state.cookies);
    }
    state.cookies = newCookies;
  }
  state.params = getParams(routeData, pathname);
  state.pathname = pathname;
  state.isRewriting = true;
  state.status = 200;
  setOriginPathname(
    state.request,
    oldPathname,
    state.manifest.trailingSlash,
    state.manifest.buildFormat
  );
  state.invalidateContexts();
}
async function executeRewrite(state, payload) {
  state.logger.debug("router", "Calling rewrite: ", payload);
  const result = await getEnvironment(state.manifest).tryRewrite(
    state.manifest,
    payload,
    state.request
  );
  applyRewriteToState(state, payload, result, { mergeCookies: true });
  return handleMiddleware(state, handlePages);
}
export {
  applyRewriteToState,
  executeRewrite
};
