import type { MiddlewareHandler } from '../../types/public/common.js';
import type { SSRManifest } from '../app/types.js';
/**
 * Resolves the middleware from the manifest and returns the `onRequest`
 * function (prefixed with the origin-check middleware when configured). If
 * `onRequest` isn't there, it returns a no-op function.
 */
export declare function getMiddleware(manifest: SSRManifest): Promise<MiddlewareHandler>;
/**
 * The already-resolved middleware for a manifest, or `undefined` when
 * `getMiddleware` has not settled yet. Sync — used where a synchronous
 * snapshot is required (the container's `insertRoute`).
 */
export declare function peekMiddleware(manifest: SSRManifest): MiddlewareHandler | undefined;
/**
 * Clears the cached middleware so it is re-resolved on the next request.
 * Called via HMR when middleware files change during development.
 */
export declare function clearMiddleware(manifest: SSRManifest): void;
