import type { SSRManifest } from '../app/types.js';
/** The manifest's `site` as a `URL` (used for `Astro.site`). */
export declare function getSite(manifest: SSRManifest): URL | undefined;
