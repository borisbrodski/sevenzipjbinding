import type { BuildEnvironment, Plugin as VitePlugin } from 'vite';
import type { AstroPluginOptions } from '../../types/astro.js';
import type { ServerIslandsState } from './shared-state.js';
export declare const SERVER_ISLAND_MANIFEST = "virtual:astro:server-island-manifest";
export declare const SERVER_ISLAND_MAP_MARKER = "$$server-islands-map$$";
export declare function vitePluginServerIslands({ settings, serverIslandsState, }: AstroPluginOptions & {
    serverIslandsState: ServerIslandsState;
}): VitePlugin;
/**
 * Checks if the prerender environment discovered any server islands during the build.
 * This encapsulates the logic of finding the server islands plugin and querying its state.
 */
export declare function hasServerIslands(environment: BuildEnvironment): boolean;
