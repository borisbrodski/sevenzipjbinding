import * as z from 'zod/v4';
/**
 * Cache provider configuration (`cache`).
 * Provider only - routes are configured via `routeRules`.
 */
export declare const CacheSchema: z.ZodObject<{
    provider: z.ZodOptional<z.ZodObject<{
        config: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodAny>>;
        entrypoint: z.ZodUnion<readonly [z.ZodString, z.ZodCustom<URL, URL>]>;
        name: z.ZodOptional<z.ZodString>;
    }, z.core.$strip>>;
}, z.core.$strip>;
/**
 * Route rules configuration (`routeRules`).
 * Maps route patterns to route rules. Patterns use the same `[param]` and
 * `[...rest]` syntax as file-based routing; glob wildcards (`*`) are not supported.
 *
 * Example:
 * ```ts
 * routeRules: {
 *   '/api/[...path]': { swr: 600 },
 *   '/products/[...slug]': { maxAge: 3600, tags: ['products'] },
 * }
 * ```
 */
export declare const RouteRulesSchema: z.ZodRecord<z.ZodString, z.ZodObject<{
    maxAge: z.ZodOptional<z.ZodNumber>;
    swr: z.ZodOptional<z.ZodNumber>;
    tags: z.ZodOptional<z.ZodArray<z.ZodString>>;
}, z.core.$strip>>;
