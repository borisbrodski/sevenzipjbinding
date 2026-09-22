import type { AstroMetadata } from './markdown.js';
export declare const ASTRO_IMAGE_ELEMENT = "astro-image";
export declare const ASTRO_IMAGE_IMPORT = "__AstroImage__";
export declare const USES_ASTRO_IMAGE_FLAG = "__usesAstroImage";
export declare function createDefaultAstroMetadata(): AstroMetadata;
/**
 * Resolve island component specifiers to stable paths for hydration metadata.
 *
 * Examples:
 * - `./components/Button.jsx` from `/app/src/pages/index.astro`
 *   -> `/app/src/pages/components/Button.tsx` (when `.tsx` exists)
 * - `../components/Counter` from `/app/src/pages/index.astro`
 *   -> `/app/src/components/Counter.tsx` (extensionless imports probe Vite's
 *   default extension order, then directory `index` files)
 * - `#components/react/Counter.tsx`
 *   -> `/app/src/components/react/Counter.tsx` via package `imports`
 */
export declare function resolvePath(specifier: string, importer: string): string;
