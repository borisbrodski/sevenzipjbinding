import type { AstroConfig } from '../../types/public/index.js';
import type { CacheOptions, CacheProviderConfig, NormalizedCacheProviderConfig, SSRManifestCache } from './types.js';
export declare function normalizeCacheProviderConfig(provider: CacheProviderConfig): NormalizedCacheProviderConfig;
/**
 * Normalize a route rule to extract cache options.
 */
export declare function normalizeRouteRuleCacheOptions(rule: {
    maxAge?: number;
    swr?: number;
    tags?: string[];
} | undefined): CacheOptions | undefined;
/**
 * Extract cache routes from the `routeRules` config.
 */
export declare function extractCacheRoutesFromRouteRules(routeRules: AstroConfig['routeRules']): Record<string, CacheOptions> | undefined;
export declare function cacheConfigToManifest(cacheConfig: AstroConfig['cache'], routeRulesConfig: AstroConfig['routeRules']): SSRManifestCache | undefined;
