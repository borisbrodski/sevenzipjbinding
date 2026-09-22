import type { RenderErrorOptions } from '../app/base.js';
import type { SSRManifest } from '../app/types.js';
export interface DevErrorHandlerOptions {
    /**
     * Whether to inject CSP meta tags into the rendered error page response.
     * The Vite dev server injects them; the non-runnable dev pipeline does not.
     */
    shouldInjectCspMetaTags: boolean;
}
/**
 * The dev-server error strategy. Renders custom 404/500 routes if the user
 * has them, otherwise throws so Vite's dev overlay is shown. Shared between
 * the Vite dev server and the non-runnable dev pipeline; only
 * `shouldInjectCspMetaTags` differs between them (carried by the
 * environment record's `injectCspMetaTagsOnErrorPages` static).
 */
export declare function renderDevError(manifest: SSRManifest, request: Request, { skipMiddleware, error, status, response: _response, pathname, ...resolvedRenderOptions }: RenderErrorOptions, { shouldInjectCspMetaTags }: DevErrorHandlerOptions): Promise<Response>;
