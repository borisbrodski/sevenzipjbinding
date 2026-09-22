import type { SSRManifest } from '../core/app/types.js';
import type { Locales } from '../types/public/config.js';
export declare function isLocalizedErrorRoute(route: string, status: 404 | 500, locales: Locales | undefined): boolean;
export declare function getErrorRoutePath(pathname: string, status: 404 | 500, routes: Pick<SSRManifest['routes'][number]['routeData'], 'route'>[], locales: Locales | undefined, appendTrailingSlash?: boolean): string;
