import type http from 'node:http';
import type { DevFacadeApp } from '../core/app/dev-facade.js';
import type { ModuleLoader } from '../core/module-loader/index.js';
import type { AstroSettings } from '../types/astro.js';
import type { DevServerController } from '../vite-plugin-astro-server/controller.js';
/** Composition-time dependencies closed over by `createAstroServerApp`. */
export interface DevRequestDeps {
    loader: ModuleLoader;
    settings: AstroSettings;
    controller: DevServerController;
}
export interface DevRequestIO {
    incomingRequest: http.IncomingMessage;
    incomingResponse: http.ServerResponse;
    isHttps: boolean;
    /** When true, only handle prerendered routes. Returns false for SSR routes. */
    prerenderOnly?: boolean;
}
/**
 * Handle a dev-server request: the HTTP glue that drives the `DevFacadeApp`
 * the way an adapter does — it sits outside the functional core, like the
 * node adapter's `serve-app.ts`. The ModuleLoader and AstroSettings are
 * closed over at composition time in `createAstroServerApp` and passed as
 * `deps`.
 *
 * @returns Whether or not the request was handled by this handler. If the
 * result is not `true`, then the request has not been handled yet and other
 * handlers can be run.
 */
export declare function handleDevRequest(app: DevFacadeApp, deps: DevRequestDeps, { incomingRequest, incomingResponse, isHttps, prerenderOnly }: DevRequestIO): Promise<boolean>;
