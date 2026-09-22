import type { Plugin as VitePlugin } from 'vite';
import type { StaticBuildOptions } from '../types.js';
/**
 * Appends assetQueryParams (e.g., ?dpl=<VERCEL_DEPLOYMENT_ID>) to relative
 * JS import paths inside client chunks. Without this, inter-chunk imports
 * bypass the HTML rendering pipeline and miss skew protection query params.
 *
 * Uses es-module-lexer to reliably parse both static and dynamic imports.
 *
 * This runs in `generateBundle` (not `renderChunk`) so that Vite's CSS plugin
 * can first remove pure-CSS wrapper chunks and replace their imports with
 * `/* empty css * /` comments. If we rewrote imports earlier (in `renderChunk`),
 * the appended query params would break Vite's regex-based CSS chunk cleanup,
 * leaving dangling imports to deleted chunks that 404 at runtime.
 */
export declare function pluginChunkImports(options: StaticBuildOptions): VitePlugin | undefined;
