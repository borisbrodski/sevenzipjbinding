import type { RouteData } from '../../types/public/internal.js';
import type { SSRManifest } from '../app/types.js';
/**
 * Given a `Request`, returns the `RouteData` that matches its pathname — the
 * appless, purely functional body of `BaseApp.match()`. By default, prerendered
 * routes aren't returned, even if they are matched; when
 * `allowPrerenderedRoutes` is `true`, matched prerendered routes are returned
 * too.
 */
export declare function matchRequest(manifest: SSRManifest, request: Request, allowPrerenderedRoutes?: boolean): RouteData | undefined;
