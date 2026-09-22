import type { RouteData } from '../../types/public/index.js';
import { BaseApp, type DevMatch, type LogRequestPayload } from './base.js';
import type { SSRManifest } from './types.js';
/**
 * The shared thin dev facade: used by BOTH dev paths —
 * the workerd / non-runnable dev entrypoint (`entrypoints/virtual/dev.ts`)
 * and the runnable dev server (`vite-plugin-app/createAstroServerApp.ts`).
 * Everything environment-specific (module loading, error strategy, request
 * logging behavior) comes from the `RenderEnvironment` record registered on
 * the manifest before construction, and the runnable dev server's HTTP glue
 * lives in `vite-plugin-app/handle-request.ts`.
 */
export declare class DevFacadeApp extends BaseApp {
    constructor(manifest: SSRManifest, streaming?: boolean);
    isDev(): boolean;
    /** Dev always allows prerendered routes to match. */
    match(request: Request): RouteData | undefined;
    /**
     * A matching route function for the development server. Contrary to
     * `.match`, this resolves props and params, returning the correct route
     * based on priority and segments, plus the resolved pathname.
     */
    devMatch(pathname?: string, { prerenderOnly }?: {
        prerenderOnly?: boolean;
    }): Promise<DevMatch | undefined>;
    logRequest({ pathname, method, statusCode, isRewrite, reqTime }: LogRequestPayload): void;
}
