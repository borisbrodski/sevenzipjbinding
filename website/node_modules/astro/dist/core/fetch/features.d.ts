import type { SSRManifest } from '../app/types.js';
/**
 * Bit flags for features that handler functions register as "used" when a
 * custom `src/fetch.ts` fetch handler is in play. After the first request
 * (dev) or at runtime (prod SSR), we compare against the manifest to warn
 * about features the user configured but forgot to include in their custom
 * fetch handler.
 */
export declare const FetchFeatures: {
    readonly redirects: number;
    readonly sessions: number;
    readonly actions: number;
    readonly middleware: number;
    readonly i18n: number;
    readonly cache: number;
};
/** All feature bits ORed together. Keep next to `FetchFeatures` so
 *  new flags are hard to forget. */
export declare const ALL_FETCH_FEATURES: number;
/** ORs a feature bit into the manifest's used-features bitmask. */
export declare function markFeatureUsed(manifest: SSRManifest, feature: number): void;
/** The used-features bitmask for a manifest; `0` when nothing was marked. */
export declare function getUsedFeatures(manifest: SSRManifest): number;
