import type { SSRManifest } from '../app/types.js';
/**
 * Registers a manifest for environments where `virtual:astro:manifest` cannot
 * resolve (plain Node: unit tests, embedders). Internal API — deliberately not
 * exported from any public entrypoint.
 * Pass `undefined` to clear (test teardown).
 */
export declare function setAmbientManifest(manifest: SSRManifest | undefined): void;
/**
 * The ambient manifest: the explicitly registered one, else the virtual
 * module's. Throws lazily when neither is available, so merely importing a
 * module that uses this never fails — only actually handling a request does.
 */
export declare function getAmbientManifest(): SSRManifest;
/** The ambient manifest if one is available, else `undefined`. Never throws. */
export declare function tryGetAmbientManifest(): SSRManifest | undefined;
