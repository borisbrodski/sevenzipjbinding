import type { APIContext } from '../../types/public/context.js';
import type { FetchState } from '../fetch/fetch-state.js';
/**
 * Handles dispatch of a matched route (endpoint / redirect / page / fallback)
 * at the bottom of the middleware chain. This is a pure dispatch layer — it
 * renders whatever route the `FetchState` currently points to without any
 * rewrite logic. Rewrites are handled upstream: `executeRewrite()` for
 * `Astro.rewrite()` and `handleMiddleware` for `next(payload)`.
 *
 * `handlePages` is the `next` callback that `handleMiddleware` invokes at
 * the end of the middleware chain. Error handlers and the container also
 * use it directly for the same dispatch behavior.
 */
export declare function handlePages(state: FetchState, ctx: APIContext): Promise<Response>;
/**
 * Like `handlePages`, but mirrors the app-level error handling that
 * `handleRequest` provides on the standard path: unmatched routes
 * return a 404 marked with `X-Astro-Error` for the app's post-check
 * to render the 404 error page, and render-time errors are logged
 * and render the 500 error page instead of propagating to the host
 * framework.
 *
 * Used by the composable `astro/fetch` `pages()` entry point, where
 * there is no surrounding `handleRequest` to supply this fallback.
 */
export declare function handlePagesWithErrorFallback(state: FetchState): Promise<Response>;
