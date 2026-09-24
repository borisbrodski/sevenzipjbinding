import type { RenderErrorOptions } from '../app/base.js';
import type { SSRManifest } from '../app/types.js';
/**
 * The default error strategy used in production SSR. Attempts to render the
 * matching error route (404.astro / 500.astro), falling back to a plain
 * response with the given status. Handles prerendered error pages via
 * `prerenderedErrorPageFetch`.
 */
export declare function renderDefaultError(manifest: SSRManifest, request: Request, { status, response: originalResponse, skipMiddleware, error, pathname, ...resolvedRenderOptions }: RenderErrorOptions): Promise<Response>;
