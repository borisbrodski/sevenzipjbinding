import type { ComponentInstance } from '../../types/astro.js';
import type { GetStaticPathsItem, GetStaticPathsResultKeyed, Params } from '../../types/public/common.js';
import type { AstroConfig, RuntimeMode } from '../../types/public/config.js';
import type { RouteData, SSRManifest } from '../../types/public/internal.js';
import type { AstroLogger } from '../logger/core.js';
interface CallGetStaticPathsOptions {
    mod: ComponentInstance | undefined;
    route: RouteData;
    routeCache: RouteCache;
    ssr: boolean;
    base: AstroConfig['base'];
    trailingSlash: AstroConfig['trailingSlash'];
}
export declare function callGetStaticPaths({ mod, route, routeCache, ssr, base, trailingSlash, }: CallGetStaticPathsOptions): Promise<GetStaticPathsResultKeyed>;
interface RouteCacheEntry {
    mod: ComponentInstance;
    staticPaths: GetStaticPathsResultKeyed;
}
/**
 * Manage the route cache, responsible for caching data related to each route,
 * including the result of calling getStaticPaths(). This gives route matching,
 * params/props resolution, and prerender generation a shared static-path table.
 *
 * In dev, this cache intentionally survives requests. It is invalidated by route
 * module identity changes after HMR or by explicit cache clears from content data
 * changes, not by each request. Dev route matching can call getStaticPaths()
 * before rendering, and render-time props resolution may ask for it again.
 */
export declare class RouteCache {
    private logger;
    private cache;
    private runtimeMode;
    constructor(logger: AstroLogger, runtimeMode?: RuntimeMode);
    /** Clear the cache. */
    clearAll(): void;
    set(route: RouteData, entry: RouteCacheEntry): void;
    get(route: RouteData): RouteCacheEntry | undefined;
    key(route: RouteData): string;
}
/**
 * The `RouteCache` for a manifest. Cleared via
 * `getRouteCache(manifest).clearAll()` on dev `astro:content-changed`.
 */
export declare function getRouteCache(manifest: SSRManifest): RouteCache;
export declare function findPathItemByKey(staticPaths: GetStaticPathsResultKeyed, params: Params, route: RouteData, logger: AstroLogger, trailingSlash: AstroConfig['trailingSlash']): GetStaticPathsItem | undefined;
export {};
