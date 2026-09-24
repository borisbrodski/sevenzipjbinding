import type { ComponentInstance } from '../../types/astro.js';
import type { SSRManifest } from '../../core/app/types.js';
import type { PathWithRoute } from '../../types/public/integrations.js';
import type { RouteData } from '../../types/public/internal.js';
import type { RouteCache } from '../../core/render/route-cache.js';
export type { PathWithRoute } from '../../types/public/integrations.js';
/**
 * Minimal interface for what StaticPaths needs from an App.
 * This allows adapters to pass any App-like object (BuildApp, NodeApp, etc).
 * Only the manifest is required: the route cache and the component loader can
 * be reached through the manifest-keyed functional core.
 *
 * When the caller's app provides `routeCache` / `getComponentByRoute`
 * (`BuildApp` does), those are preferred: they execute inside the app's own
 * module graph, which matters when `StaticPaths` and the app come from
 * different bundles (the default prerenderer imports the prerender bundle's
 * `BuildApp`, whose per-manifest state lives in the bundle's copies of the
 * core modules).
 */
export interface StaticPathsApp {
    manifest: SSRManifest;
    routeCache?: RouteCache;
    getComponentByRoute?(route: RouteData): Promise<ComponentInstance>;
}
/**
 * Collects all static paths for prerendering.
 * Handles calling getStaticPaths on each route and populating the route cache.
 */
export declare class StaticPaths {
    #private;
    constructor(app: StaticPathsApp);
    /**
     * Get all static paths for prerendering with their associated routes.
     * This avoids needing to re-match routes later, which can be incorrect due to route priority.
     */
    getAll(): Promise<PathWithRoute[]>;
}
