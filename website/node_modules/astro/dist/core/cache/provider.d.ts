import type { SSRManifest } from '../app/types.js';
import type { CacheProvider } from './types.js';
/** Resolves the cache provider from the manifest, `null` when none. */
export declare function getCacheProvider(manifest: SSRManifest): Promise<CacheProvider | null>;
