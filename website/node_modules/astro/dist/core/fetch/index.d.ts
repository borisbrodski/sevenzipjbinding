import { FetchState as BaseFetchState } from './fetch-state.js';
import type { AstroFetchState } from './fetch-state.js';
export type { AstroFetchState };
/**
 * The public per-request state, constructible from a bare `Request`.
 * Static, build-time data comes from the ambient manifest — the manifest
 * module bundled into every Astro-built server — so no app, pipeline, or
 * request-attached handle is needed.
 */
export declare class FetchState extends BaseFetchState {
    constructor(request: Request);
}
export declare function astro(state: FetchState): Promise<Response>;
/**
 * Checks if the request pathname needs trailing-slash normalization and
 * returns a redirect `Response` if so. Returns `undefined` when no
 * redirect is needed and the caller should continue processing.
 */
export declare function trailingSlash(state: FetchState): Response | undefined;
/**
 * Runs Astro's middleware chain for the given state, calling `next` at
 * the bottom of the chain to produce the response. Lazily creates the
 * render context if needed. Unmatched routes render the 404 error page;
 * errors thrown by user middleware are logged and render the 500 error
 * page; errors surfaced through `next` (the host framework's downstream
 * chain) propagate to the host instead.
 */
export declare function middleware(state: FetchState, next: (state: FetchState) => Promise<Response>): Promise<Response>;
/**
 * Dispatches the request to the matched route (endpoint, page, redirect,
 * or fallback). Lazily creates the render context if needed. Unmatched
 * routes render the 404 error page; render-time errors are logged and
 * render the 500 error page.
 */
export declare function pages(state: FetchState): Promise<Response>;
/**
 * Registers the session provider on the state. The session is created
 * lazily when user code accesses `ctx.session`, and persisted when
 * `state.finalizeAll()` is called. No-op if sessions are not configured.
 *
 * Call this early (before middleware runs). Call `state.finalizeAll()`
 * in a `finally` block after the response is produced to persist
 * any session mutations.
 */
export declare function sessions(state: FetchState): Promise<void> | void;
/**
 * Checks if the matched route is a redirect and returns the redirect
 * `Response` if so. Returns `undefined` when the route is not a
 * redirect and the caller should continue processing.
 * `state.routeData` must be set before calling this.
 */
export declare function redirects(state: FetchState): Promise<Response> | undefined;
/**
 * Handles Astro Action requests (RPC + form). Returns a `Response` for
 * RPC actions, or `undefined` for form actions / non-action requests
 * (the caller should continue to page rendering). Lazily creates
 * the render context if needed.
 */
export declare function actions(state: FetchState): Promise<Response | undefined> | undefined;
/**
 * Post-processes a response against the manifest's i18n configuration.
 * Handles locale redirects, 404s for invalid locales, and fallback
 * routing. Returns the response unmodified if i18n is not configured
 * (or the routing strategy is `manual`).
 */
export declare function i18n(state: FetchState, response: Response): Promise<Response>;
/**
 * Wraps a render callback with cache provider logic. Handles runtime
 * caching (onRequest), CDN-based providers (headers only), and the
 * no-cache case transparently. Cache headers are applied and stripped
 * internally.
 */
export declare function cache(state: FetchState, next: () => Promise<Response>): Promise<Response>;
