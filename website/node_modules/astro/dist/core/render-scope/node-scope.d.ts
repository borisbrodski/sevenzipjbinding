import type { RenderCollectorScope } from './scope.js';
/**
 * Installs (first-wins) an AsyncLocalStorage-backed render scope and returns
 * the installed scope.
 *
 * This module is the ONE static `node:async_hooks` import in core and must be
 * imported ONLY by `core/build/default-prerenderer.ts` — orchestrator-only
 * code that is never bundled into prerender, server, or adapter output.
 */
export declare function ensureAsyncRenderScope(): RenderCollectorScope;
