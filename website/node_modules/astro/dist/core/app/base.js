import {
  prependForwardSlash,
  removeTrailingForwardSlash,
  stripRequestBase
} from "@astrojs/internal-helpers/path";
import { matchPattern } from "@astrojs/internal-helpers/remote";
import { computePathnameFromDomain } from "../i18n/domain.js";
import { ASTRO_ERROR_HEADER, clientAddressSymbol } from "../constants.js";
import { getSetCookiesFromResponse } from "../cookies/index.js";
import { AstroError, AstroErrorData } from "../errors/index.js";
import { AstroIntegrationLogger } from "../logger/core.js";
import { DefaultFetchHandler } from "../fetch/default-handler.js";
import { getUsedFeatures, FetchFeatures } from "../fetch/features.js";
import { FetchState } from "../fetch/fetch-state.js";
import { renderErrorPage } from "../errors/handler.js";
import { getLogger, getResolvedLogger } from "../logger/manifest-logger.js";
import { handleRequest } from "../routing/handler.js";
import { getDefaultStatusCode } from "../routing/helpers.js";
import { matchRequest } from "../routing/match-request.js";
import { getRouteTable, matchRoute, updateRouteTable } from "../routing/route-table.js";
import { validateAndDecodePathname } from "../util/pathname.js";
import { setRenderOptions } from "./render-options.js";
class BaseApp {
  manifest;
  #adapterLogger;
  baseWithoutTrailingSlash;
  /**
   * The streaming flag passed to the constructor, surfaced through the
   * protected `resolveStreaming()` hook and fed into the internal
   * `FetchState` facade hooks on the fast path.
   */
  #streaming;
  /**
   * The handler that turns incoming `Request` objects into `Response`s.
   * Defaults to a `DefaultFetchHandler` pinned to this app and can be
   * overridden via `setFetchHandler` — typically by the bundled
   * entrypoint after importing `virtual:astro:fetchable`.
   */
  #fetchHandler;
  #errorHandler;
  /**
   * Whether a custom fetch handler (from `src/fetch.ts`) has been set
   * via `setFetchHandler`. When false, the `DefaultFetchHandler` is
   * in use and all features are implicitly active.
   */
  #hasCustomFetchHandler = false;
  /**
   * Whether the missing-feature check has already run. We only want
   * to warn once — after the first request in dev, or at build end.
   */
  #featureCheckDone = false;
  get logger() {
    return getLogger(this.manifest);
  }
  /**
   * Route data derived from the manifest, used for route matching. Reads and
   * writes go through the single per-manifest route table, so HMR updates are
   * visible to every consumer at once.
   */
  get manifestData() {
    return getRouteTable(this.manifest);
  }
  set manifestData(routesList) {
    updateRouteTable(this.manifest, routesList.routes);
  }
  get adapterLogger() {
    const currentOptions = this.logger.options;
    if (!this.#adapterLogger || this.#adapterLogger.options !== currentOptions) {
      this.#adapterLogger = new AstroIntegrationLogger(currentOptions, this.manifest.adapterName);
    }
    return this.#adapterLogger;
  }
  constructor(manifest, streaming = true) {
    this.manifest = manifest;
    this.baseWithoutTrailingSlash = removeTrailingForwardSlash(manifest.base);
    this.#streaming = streaming;
    getRouteTable(manifest);
    getLogger(manifest);
    this.#fetchHandler = new DefaultFetchHandler(this);
    this.#errorHandler = this.createErrorHandler();
  }
  /**
   * Resolves the user-configured logger destination from the manifest and
   * returns the logger. Lazy and only resolves once; safe to call before
   * the first render (adapters use this to log startup messages through
   * the configured destination).
   */
  getLogger() {
    return getResolvedLogger(this.manifest);
  }
  /**
   * The streaming flag fed into the internal `FetchState` facade hooks on
   * the fast path. Returns the constructor flag by
   * default; `BuildApp` overrides this to return `undefined` so streaming
   * falls through to the environment default (`manifest.serverLike`).
   */
  resolveStreaming() {
    return this.#streaming;
  }
  /**
   * Override the fetch handler used to dispatch requests. Entrypoints
   * call this with the default export of `virtual:astro:fetchable` to
   * plug in a user-authored handler from `src/fetch.ts`.
   */
  setFetchHandler(handler) {
    this.#fetchHandler = handler;
    this.#hasCustomFetchHandler = !(handler instanceof DefaultFetchHandler);
  }
  /**
   * Returns the error handler used by this app. The default is a thin
   * bridge over the functional error API — strategy selection (production
   * default / dev / build) is environment-driven inside `renderErrorPage`.
   * External subclasses can override this to customize error rendering.
   */
  createErrorHandler() {
    return {
      renderError: (request, options) => renderErrorPage(this.manifest, request, options)
    };
  }
  /**
   * Resets the cached adapter logger so it picks up a new logger instance.
   * Used by BuildApp when the logger is replaced via setOptions().
   */
  resetAdapterLogger() {
    this.#adapterLogger = void 0;
  }
  getAllowedDomains() {
    return this.manifest.allowedDomains;
  }
  matchesAllowedDomains(forwardedHost, protocol) {
    return BaseApp.validateForwardedHost(forwardedHost, this.manifest.allowedDomains, protocol);
  }
  static validateForwardedHost(forwardedHost, allowedDomains, protocol) {
    if (!allowedDomains || allowedDomains.length === 0) {
      return false;
    }
    try {
      const testUrl = new URL(`${protocol || "https"}://${forwardedHost}`);
      return allowedDomains.some((pattern) => {
        return matchPattern(testUrl, pattern);
      });
    } catch {
      return false;
    }
  }
  set setManifestData(newManifestData) {
    updateRouteTable(this.manifest, newManifestData.routes);
  }
  removeBase(pathname) {
    return stripRequestBase(pathname, this.manifest.base);
  }
  /**
   * Fully decodes a pathname, falling back to a single decode and then the raw pathname
   * when validation fails. Adapter matching runs before `render()`, so it must not throw
   * for request input that render-time validation handles.
   */
  safeDecodePathname(pathname) {
    try {
      return validateAndDecodePathname(pathname);
    } catch (e) {
      this.adapterLogger.debug(e.toString());
      try {
        return decodeURI(pathname);
      } catch {
        return pathname;
      }
    }
  }
  /**
   * Extracts the base-stripped, decoded pathname from a request.
   * Used by adapters to compute the pathname for dev-mode route matching.
   */
  getPathnameFromRequest(request) {
    const url = new URL(request.url);
    const pathname = prependForwardSlash(this.removeBase(url.pathname));
    return this.safeDecodePathname(pathname);
  }
  /**
   * Given a `Request`, it returns the `RouteData` that matches its `pathname`. By default, prerendered
   * routes aren't returned, even if they are matched.
   *
   * When `allowPrerenderedRoutes` is `true`, the function returns matched prerendered routes too.
   * @param request
   * @param allowPrerenderedRoutes
   */
  match(request, allowPrerenderedRoutes = false) {
    return matchRequest(this.manifest, request, allowPrerenderedRoutes);
  }
  /**
   * A matching route function to use in the development server.
   * Contrary to the `.match` function, this function resolves props and params, returning the correct
   * route based on the priority, segments. It also returns the correct, resolved pathname.
   * @param pathname
   */
  devMatch(pathname) {
    pathname;
    return void 0;
  }
  computePathnameFromDomain(request) {
    return computePathnameFromDomain(
      request,
      new URL(request.url),
      this.manifest.i18n,
      this.manifest.base,
      this.manifest.trailingSlash,
      this.logger
    );
  }
  async render(request, {
    addCookieHeader = false,
    clientAddress = Reflect.get(request, clientAddressSymbol),
    locals,
    prerenderedErrorPageFetch = fetch,
    routeData,
    waitUntil
  } = {}) {
    await getResolvedLogger(this.manifest);
    if (routeData) {
      this.logger.debug(
        "router",
        "The adapter " + this.manifest.adapterName + " provided a custom RouteData for ",
        request.url
      );
      this.logger.debug("router", "RouteData");
      this.logger.debug("router", routeData);
    }
    if (locals) {
      if (typeof locals !== "object") {
        const error = new AstroError(AstroErrorData.LocalsNotAnObject);
        this.logger.error(null, error.stack);
        return this.renderError(request, {
          addCookieHeader,
          clientAddress,
          prerenderedErrorPageFetch,
          // If locals are invalid, we don't want to include them when
          // rendering the error page
          locals: void 0,
          routeData,
          waitUntil,
          status: 500,
          error
        });
      }
    }
    if (!routeData) {
      const domainPathname = this.computePathnameFromDomain(request);
      if (domainPathname) {
        routeData = matchRoute(this.manifest, this.safeDecodePathname(domainPathname));
      }
    }
    const resolvedOptions = {
      addCookieHeader,
      clientAddress,
      prerenderedErrorPageFetch,
      locals,
      routeData,
      waitUntil
    };
    let response;
    if (this.#fetchHandler instanceof DefaultFetchHandler) {
      response = await handleRequest(
        new FetchState(this.manifest, request, resolvedOptions, {
          streaming: this.resolveStreaming(),
          renderError: (req, opts) => this.renderError(req, opts),
          logRequest: (payload) => this.logThisRequest(payload)
        })
      );
    } else {
      setRenderOptions(request, resolvedOptions);
      response = await this.#fetchHandler.fetch(request);
    }
    this.#warnMissingFeatures();
    if (response.headers.get(ASTRO_ERROR_HEADER)) {
      response.headers.delete(ASTRO_ERROR_HEADER);
      return this.renderError(request, {
        addCookieHeader,
        clientAddress,
        prerenderedErrorPageFetch,
        locals,
        routeData,
        waitUntil,
        response,
        status: response.status,
        error: response.status === 500 ? null : void 0
      });
    }
    return response;
  }
  setCookieHeaders(response) {
    return getSetCookiesFromResponse(response);
  }
  /**
   * Reads all the cookies written by `Astro.cookie.set()` onto the passed response.
   * For example,
   * ```ts
   * for (const cookie_ of App.getSetCookieFromResponse(response)) {
   *     const cookie: string = cookie_
   * }
   * ```
   * @param response The response to read cookies from.
   * @returns An iterator that yields key-value pairs as equal-sign-separated strings.
   */
  static getSetCookieFromResponse = getSetCookiesFromResponse;
  /**
   * If it is a known error code, try sending the according page (e.g. 404.astro / 500.astro).
   * This also handles pre-rendered /404 or /500 routes.
   *
   * Delegates to the app's configured `ErrorHandler`. To customize behavior
   * for a specific environment, override `createErrorHandler()` rather than
   * this method.
   */
  async renderError(request, options) {
    return this.#errorHandler.renderError(request, options);
  }
  /**
   * One-shot check: after the first request with a custom `src/fetch.ts`,
   * compare `usedFeatures` against the manifest and warn about any
   * configured features the user's pipeline doesn't call.
   */
  #warnMissingFeatures() {
    if (this.#featureCheckDone || !this.#hasCustomFetchHandler) return;
    this.#featureCheckDone = true;
    const manifest = this.manifest;
    const missing = [];
    const used = getUsedFeatures(this.manifest);
    if (manifest.routes.some((r) => r.routeData.type === "redirect") && !(used & FetchFeatures.redirects)) {
      missing.push("redirects");
    }
    if (manifest.sessionConfig && !(used & FetchFeatures.sessions)) {
      missing.push("sessions");
    }
    if (manifest.actions && !(used & FetchFeatures.actions)) {
      missing.push("actions");
    }
    if (manifest.middleware && !(used & FetchFeatures.middleware)) {
      missing.push("middleware");
    }
    if (manifest.i18n && manifest.i18n.strategy !== "manual" && !(used & FetchFeatures.i18n)) {
      missing.push("i18n");
    }
    if (manifest.cacheConfig && !(used & FetchFeatures.cache)) {
      missing.push("cache");
    }
    for (const feature of missing) {
      this.logger.warn(
        "router",
        `Your project uses ${feature}, but your custom src/fetch.ts does not call the ${feature}() handler. This feature will not work unless your fetch handler calls it.`
      );
    }
  }
  getDefaultStatusCode(routeData, pathname) {
    return getDefaultStatusCode(this.manifest, routeData, pathname);
  }
  getManifest() {
    return this.manifest;
  }
  logThisRequest({
    pathname,
    method,
    statusCode,
    isRewrite,
    timeStart
  }) {
    const timeEnd = performance.now();
    this.logRequest({
      pathname,
      method,
      statusCode,
      isRewrite,
      reqTime: timeEnd - timeStart
    });
  }
}
export {
  BaseApp
};
