import type { Plugin as VitePlugin } from 'vite';
import type { RoutesList } from '../types/astro.js';
import type { RouteData } from '../types/public/internal.js';
export declare const VIRTUAL_PAGES_MODULE_ID = "virtual:astro:pages";
interface PagesPluginOptions {
    routesList: RoutesList;
}
/**
 * Filters routes for a specific build environment.
 *
 * Redirect target routes do not need special handling here: they already
 * appear in the routes list with their own `prerender` flag and are
 * included when it matches `isPrerender`. At SSR runtime, redirect
 * responses are generated from route metadata alone (no component is
 * loaded), so the target's component is never needed in the SSR page map.
 */
export declare function getRoutesForEnvironment(routes: RouteData[], isPrerender: boolean): Set<RouteData>;
export declare function pluginPages({ routesList }: PagesPluginOptions): VitePlugin;
export {};
