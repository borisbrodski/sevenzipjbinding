import type { AstroSettings } from '../types/astro.js';
/**
 * Get the path to the data store file.
 * During development, this is in the `.astro` directory so that the Vite watcher can see it.
 * In production, it's in the cache directory so that it's preserved between builds.
 */
export declare function getDataStoreFile(settings: AstroSettings, isDev: boolean): URL;
export declare function getDataStoreChunkSize(settings: AstroSettings): number | undefined;
/**
 * Get the path to the data store directory, used when the store is split across
 * multiple files.
 * During development, this is in the `.astro` directory so that the Vite watcher can see it.
 * In production, it's in the cache directory so that it's preserved between builds.
 */
export declare function getDataStoreDir(settings: AstroSettings, isDev: boolean): URL;
