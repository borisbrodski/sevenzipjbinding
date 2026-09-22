import type { AssetsPrefix } from '../core/app/types.js';
import { type Plugin } from 'vite';
import type { BuildInternals } from '../core/build/internal.js';
import type { ExtractedChunk } from '../core/build/static-build.js';
import type { AstroSettings } from '../types/astro.js';
export declare function astroContentAssetPropagationPlugin({ settings, cssContentCache, }: {
    settings: AstroSettings;
    /** Shared cache of CSS content populated by the dev-css plugin's transform hook.
     *  Used to retrieve already-processed CSS for CSS modules without re-importing
     *  via `?inline`, which would produce different scoped-name hashes with Lightning CSS. */
    cssContentCache?: Map<string, string>;
}): Plugin;
/**
 * Post-build hook that injects propagated styles into content collection chunks.
 * Finds chunks with LINKS_PLACEHOLDER and STYLES_PLACEHOLDER, and replaces them
 * with actual styles from propagatedStylesMap.
 */
export declare function contentAssetsBuildPostHook(base: string, assetsPrefix: AssetsPrefix | undefined, internals: BuildInternals, { chunks, mutate, }: {
    chunks: ExtractedChunk[];
    mutate: (fileName: string, code: string, prerender: boolean) => void;
}): Promise<void>;
