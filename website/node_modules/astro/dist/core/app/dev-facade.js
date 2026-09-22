import { req } from "../messages/runtime.js";
import { matchRoute as devMatchRoute } from "../routing/dev.js";
import { BaseApp } from "./base.js";
class DevFacadeApp extends BaseApp {
  constructor(manifest, streaming = true) {
    super(manifest, streaming);
  }
  isDev() {
    return true;
  }
  /** Dev always allows prerendered routes to match. */
  match(request) {
    return super.match(request, true);
  }
  /**
   * A matching route function for the development server. Contrary to
   * `.match`, this resolves props and params, returning the correct route
   * based on priority and segments, plus the resolved pathname.
   */
  async devMatch(pathname, { prerenderOnly } = {}) {
    if (pathname === void 0) {
      return void 0;
    }
    const matchedRoute = await devMatchRoute(this.manifest, pathname, { prerenderOnly });
    if (!matchedRoute) {
      return void 0;
    }
    return {
      routeData: matchedRoute.route,
      resolvedPathname: matchedRoute.resolvedPathname
    };
  }
  logRequest({ pathname, method, statusCode, isRewrite, reqTime }) {
    if (pathname === "/favicon.ico") {
      return;
    }
    this.logger.info(
      null,
      req({
        url: pathname,
        method,
        statusCode,
        isRewrite,
        reqTime
      })
    );
  }
}
export {
  DevFacadeApp
};
