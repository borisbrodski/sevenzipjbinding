import type { ServerIslandMappings, SSRManifest } from '../app/types.js';
/**
 * The server-island mappings for a manifest. Deliberately NOT memoized: the
 * manifest thunk is a module import, which is cheap.
 */
export declare function getServerIslands(manifest: SSRManifest): Promise<ServerIslandMappings>;
