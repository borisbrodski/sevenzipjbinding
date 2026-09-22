import type { FetchState } from '../fetch/fetch-state.js';
import type { APIContext } from '../../types/public/context.js';
/**
 * Callback invoked at the bottom of the middleware chain to dispatch the
 * request to the matched route (endpoint / redirect / page / fallback).
 *
 * Callers of `handleMiddleware` pass `handlePages` (or a wrapper around it)
 * so route dispatch logic stays out of the middleware layer.
 */
export type RenderRouteCallback = (state: FetchState, ctx: APIContext) => Promise<Response>;
/**
 * Runs Astro's middleware chain (origin check + user `onRequest`) for a
 * single render, reading the composed middleware from the manifest and
 * per-request data (componentInstance, slots, props, API contexts) off the
 * supplied `FetchState`. The actual route dispatch (endpoint / redirect /
 * page / fallback) is supplied by the caller as `renderRouteCallback` —
 * typically `handlePages`.
 */
export declare function handleMiddleware(state: FetchState, renderRouteCallback: RenderRouteCallback): Promise<Response>;
/**
 * Like `handleMiddleware`, but mirrors the app-level error handling that
 * `handleRequest` provides on the standard path, the same way
 * `handlePagesWithErrorFallback` does for `pages()`. When no route matched
 * it returns a 404 marked with `X-Astro-Error` for the app's post-check;
 * when Astro's own middleware chain throws it logs the error and renders
 * the custom `500.astro`.
 *
 * Errors surfaced through `renderRouteCallback` (the host framework's
 * `next`, e.g. host middleware mounted below `middleware()`) are re-thrown
 * instead, so the host's own error handling still runs rather than being
 * swallowed into Astro's 500 page. A sentinel tells the two apart.
 *
 * Used by the composable `astro/fetch` `middleware()` entry point, where
 * there is no surrounding `handleRequest` to supply this fallback.
 */
export declare function handleMiddlewareWithErrorFallback(state: FetchState, renderRouteCallback: RenderRouteCallback): Promise<Response>;
