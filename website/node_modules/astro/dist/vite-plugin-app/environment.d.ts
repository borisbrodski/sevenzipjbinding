import type { RenderEnvironment } from '../core/environment/index.js';
import type { ModuleLoader } from '../core/module-loader/index.js';
import type { AstroSettings } from '../types/astro.js';
import type { SSRLoadedRenderer, SSRManifest } from '../types/public/internal.js';
export declare function getDevRenderers(manifest: SSRManifest): SSRLoadedRenderer[];
export declare function setDevRenderers(manifest: SSRManifest, renderers: SSRLoadedRenderer[]): void;
export interface RunnableEnvironmentOptions {
    loader: ModuleLoader;
    settings: AstroSettings;
    getDebugInfo: () => Promise<string>;
}
/**
 * The runnable dev environment (the Vite SSR environment can load modules at
 * runtime). The ModuleLoader and AstroSettings are captured in this closure at
 * composition time (`createAstroServerApp`) and are unreachable from
 * requests/states by design — only the environment functions close over them.
 */
export declare function createRunnableEnvironment({ loader, settings, getDebugInfo, }: RunnableEnvironmentOptions): RenderEnvironment;
