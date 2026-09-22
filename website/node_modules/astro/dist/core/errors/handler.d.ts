import type { RenderErrorOptions } from '../app/base.js';
import type { RouteData } from '../../types/public/index.js';
import type { SSRManifest } from '../app/types.js';
import type { FetchState } from '../fetch/fetch-state.js';
/**
 * A strategy for rendering error responses (404, 500, etc.).
 *
 * Internal shape of `BaseApp`'s `#errorHandler` (whose default wraps
 * {@link renderErrorPage}); external `BaseApp` subclasses may return their
 * own implementation from the protected `createErrorHandler()`.
 */
export interface ErrorHandler {
    renderError(request: Request, options: RenderErrorOptions): Promise<Response>;
}
/**
 * Renders the error page (404.astro / 500.astro or a plain response) for a
 * request, dispatching to the strategy the manifest's environment selects:
 * production/container default, dev (overlay + custom error routes, with or
 * without CSP meta-tag injection), or build (500s throw so the build fails).
 */
export declare function renderErrorPage(manifest: SSRManifest, request: Request, options: RenderErrorOptions): Promise<Response>;
/**
 * Dispatches an internal error render for a request flowing through the
 * handler chain: through the facade's late-bound `renderError` hook when the
 * state was built by `BaseApp.render`'s fast path (preserving instance
 * overrides/reassignments, e.g. cloudflare's prerender-error propagation),
 * else through the environment's strategy via {@link renderErrorPage}.
 */
export declare function renderErrorFromState(state: FetchState, request: Request, options: RenderErrorOptions): Promise<Response>;
/**
 * Whether a middleware rewrite (`ctx.rewrite()` / `next(payload)`) issued while
 * rendering the error page dead-ended in another empty reroutable (404/500)
 * response. The rewrite swaps the state's routeData away from the error route,
 * so returning that render as-is would produce a blank page; the error handler
 * should instead retry rendering the error page without middleware.
 *
 * @param skipMiddleware Whether middleware was already skipped for this render.
 * @param errorRouteData The error route matched before middleware ran.
 * @param renderedRouteData The route data on the state after middleware ran.
 * @param response The response produced by the middleware-driven render.
 */
export declare function rewroteToEmptyErrorResponse(skipMiddleware: boolean, errorRouteData: RouteData, renderedRouteData: RouteData | undefined, response: Response): boolean;
