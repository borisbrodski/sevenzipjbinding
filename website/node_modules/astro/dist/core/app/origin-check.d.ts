/**
 * Home of the `security.checkOrigin` logic. Exposes the shared predicate and
 * response used to reject cross-site submissions, plus the middleware factory
 * that installs the check in the request pipeline. Consumers (the pipeline
 * middleware and the Astro Actions dispatch) import from here so the check
 * stays consistent regardless of where it runs.
 */
import type { MiddlewareHandler } from '../../types/public/common.js';
/**
 * Determines whether a request should be rejected because it is a cross-site
 * submission for a route rendered on demand.
 *
 * This encapsulates the shared logic used by both the origin-check middleware
 * and the Astro Actions dispatch, so the check is applied consistently
 * regardless of where the request is handled.
 *
 * @private
 */
export declare function isForbiddenCrossOriginRequest(request: Request, url: URL, isPrerendered: boolean): boolean;
/**
 * Builds the 403 response returned when a cross-site submission is rejected.
 *
 * @private
 */
export declare function createCrossOriginForbiddenResponse(request: Request): Response;
/**
 * Returns a middleware function in charge to check the `origin` header.
 *
 * @private
 */
export declare function createOriginCheckMiddleware(): MiddlewareHandler;
export declare function hasFormLikeHeader(contentType: string | null): boolean;
