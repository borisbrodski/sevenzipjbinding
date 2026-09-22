/**
 * Use this module only to have functions needed in development
 */
import type { SSRManifest } from '../app/types.js';
import type { RouteData } from '../../types/public/index.js';
interface MatchedRoute {
    route: RouteData;
    filePath: URL;
    resolvedPathname: string;
}
export declare function matchRoute(manifest: SSRManifest, pathname: string, { prerenderOnly }?: {
    prerenderOnly?: boolean;
}): Promise<MatchedRoute | undefined>;
export {};
