import { getFetchStateFromAPIContext } from "../core/fetch/fetch-state.js";
import { compileI18n, finalizeI18n } from "../core/i18n/handler.js";
function createI18nMiddleware(i18n, base, trailingSlash, format) {
  if (!i18n) return (_, next) => next();
  const compiled = compileI18n(i18n, base, trailingSlash, format);
  return async (context, next) => {
    const response = await next();
    return finalizeI18n(compiled, getFetchStateFromAPIContext(context), response);
  };
}
export {
  createI18nMiddleware
};
