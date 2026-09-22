import type { SSRManifest } from '../app/types.js';
import type { FetchHandler } from './types.js';
/**
 * The default request handler for `BaseApp`. Stateless: builds the
 * per-request `FetchState` from the manifest and delegates to
 * `handleRequest`.
 *
 * The export path (`astro/app/fetch/default-handler`), the class name, and
 * no-arg constructibility are baked into generated builds
 * (`core/fetch/vite-plugin.ts` emits `new DefaultFetchHandler()`), so all
 * three survive.
 */
export declare class DefaultFetchHandler {
    #private;
    /**
     * `BaseApp` passes itself so states resolve that app's manifest ahead of
     * the ambient one; generated builds construct the handler with no
     * arguments and use the ambient manifest.
     */
    constructor(app?: {
        manifest: SSRManifest;
    });
    fetch: FetchHandler;
}
