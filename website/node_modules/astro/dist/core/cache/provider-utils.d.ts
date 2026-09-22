/**
 * Shared utilities for CDN cache providers.
 *
 * These helpers are used by first-party adapter cache providers
 * (@astrojs/netlify/cache, @astrojs/vercel/cache, @astrojs/cloudflare/cache)
 * to implement common patterns like cache-control header generation,
 * path-based invalidation via tags, and tag normalization.
 */
import type { CacheOptions, InvalidateOptions } from './types.js';
/**
 * Generate a cache tag for a given path.
 * Used by Netlify and Vercel providers to support `invalidate({ path })`.
 */
export declare function pathTag(path: string): string;
/**
 * Build cache-control directives from CacheOptions.
 * Returns the directive string (e.g. `"public, max-age=300, stale-while-revalidate=60"`)
 * without the header name, so each provider can use its own header
 * (`Netlify-CDN-Cache-Control`, `Vercel-CDN-Cache-Control`, `Cloudflare-CDN-Cache-Control`).
 *
 * Returns `undefined` if no caching directives are present.
 */
export declare function buildCacheControlDirectives(options: CacheOptions, extraDirectives?: string[]): string | undefined;
/**
 * Set common conditional headers (Last-Modified, ETag) on a Headers object.
 */
export declare function setConditionalHeaders(headers: Headers, options: CacheOptions): void;
/**
 * Normalize `InvalidateOptions.tags` to a flat string array.
 */
export declare function normalizeTags(tags: string | string[] | undefined): string[];
/**
 * Collect all tags needed to invalidate the given options,
 * including the path tag if `options.path` is set.
 * Used by providers that implement path invalidation via tags
 * (Netlify, Vercel) rather than native path purge (Cloudflare).
 */
export declare function collectInvalidationTags(options: InvalidateOptions): string[];
