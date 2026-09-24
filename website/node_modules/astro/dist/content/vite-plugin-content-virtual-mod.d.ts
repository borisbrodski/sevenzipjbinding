import nodeFs from 'node:fs';
import { type Plugin, type ViteDevServer } from 'vite';
import type { AstroSettings } from '../types/astro.js';
import type { MutableDataStore } from './mutable-data-store.js';
interface AstroContentVirtualModPluginParams {
    settings: AstroSettings;
    fs: typeof nodeFs;
}
/**
 * Invalidates the content virtual modules directly whenever the given store
 * writes to disk. The watcher listeners in `configureServer` cover writes from
 * other processes, but the watcher can miss the atomic rename that commits a
 * write on some platforms (notably Windows, see #17335), leaving dev serving
 * stale content until a restart. Subscribing to the store's own write
 * notifications makes invalidation of this process's writes deterministic.
 */
export declare function attachDataStoreInvalidation(store: MutableDataStore, server: ViteDevServer, settings: AstroSettings): void;
export declare function astroContentVirtualModPlugin({ settings, fs, }: AstroContentVirtualModPluginParams): Plugin;
export {};
