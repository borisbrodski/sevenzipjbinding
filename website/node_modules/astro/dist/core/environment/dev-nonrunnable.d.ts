import type { RenderEnvironment } from './index.js';
/**
 * The non-runnable dev environment (workerd and other adapters whose requests
 * run outside Vite's module runner). Registered by the dev virtual entrypoint.
 */
export declare function createNonRunnableEnvironment(): RenderEnvironment;
