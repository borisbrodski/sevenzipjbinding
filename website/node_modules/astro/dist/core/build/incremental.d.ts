import type { SerializedStaticImage } from '../../assets/types.js';
import type { AstroSettings } from '../../types/astro.js';
export interface IncrementalPathEntry {
    cacheKey: string;
    outputFile: string;
    /**
     * Render-graph hashes of the content entries this path rendered, keyed by the
     * entry's root-relative `filePath`. A change to any of these invalidates the
     * path even when its template and data are unchanged, since content entries
     * render behind `content-data` bridges the per-route hash cannot cross.
     */
    contentHashes?: Record<string, string>;
    /**
     * Optimized-image transforms this path resolved while rendering. Registered
     * transforms are drained into the output after generation, but `dist/` is
     * emptied each build, so a skipped path replays these into the global list to
     * keep the images its restored HTML references from 404ing.
     */
    staticImages?: SerializedStaticImage[];
    /**
     * Absolute source paths of images this path referenced without a transform.
     * Replaying these for a skipped path prevents image generation from deleting
     * originals referenced by restored HTML.
     */
    referencedImages?: string[];
    /**
     * Response header name/value pairs a `staticHeaders` adapter collected for this
     * path (chiefly the CSP header when delivered as a header rather than a `<meta>`
     * tag). The `astro:build:generated` hook writes these to a static headers file,
     * so a skipped path replays them to keep its route in that file.
     */
    headers?: [string, string][];
}
export interface IncrementalRouteEntry {
    dependencyHash: string;
    paths: Record<string, IncrementalPathEntry>;
}
/**
 * On-disk shape of the incremental build cache.
 */
export interface IncrementalManifest {
    version: number;
    /**
     * Hash of the output-affecting subset of the resolved config. A mismatch
     * invalidates the whole cache, since config baked into compiled output or
     * inlined via Vite cannot be seen by the per-route dependency hash.
     */
    configHash: string;
    /**
     * Hash of the project's lockfiles. Externalized dependencies are leaf nodes
     * in the bundle graph with no code and a versionless id, so the per-route
     * dependency hash cannot see when a (possibly transitive) dependency changes.
     * A mismatch invalidates the whole cache.
     */
    lockfileHash: string;
    /**
     * Hash of the server-island encryption key. A page's island props are baked
     * into its HTML as ciphertext bound to this key, so a restored page whose key
     * has changed would be undecryptable at runtime. Server-island pages are only
     * reused when this matches; it does not affect pages without islands.
     */
    keyDigest: string;
    routes: Record<string, IncrementalRouteEntry>;
}
/**
 * Tracks which prerendered paths can be reused from a previous build.
 *
 * The invalidation logic (`canSkip`, `record`, `findOrphanedFiles`) is pure and
 * operates on the previous and next manifests held in memory. Disk access is
 * confined to `load` and the output-file methods.
 */
export declare class IncrementalBuildCache {
    #private;
    constructor(configHash: string, lockfileHash: string, keyDigest: string, contentEntryHashes?: Map<string, string>, previous?: IncrementalManifest | null);
    /**
     * Load the cache from disk. When no valid manifest exists (missing, wrong
     * version, or a config or lockfile hash mismatch) the returned cache has no
     * previous build, so every path is rendered as a full build.
     *
     * `contentEntryHashes` is this build's map of content-entry render hashes,
     * used to detect when the content a path renders has changed.
     *
     * `force` ignores any existing manifest so every path is rebuilt, while still
     * recording a fresh cache for the next build.
     */
    static load(settings: AstroSettings, configHash: string, lockfileHash: string, keyDigest: string, contentEntryHashes?: Map<string, string>, force?: boolean): IncrementalBuildCache;
    /**
     * Determine if a path can be reused from the previous build. A path is
     * skippable when:
     * 1. It returned a cacheKey in this build.
     * 2. The previous cache has an entry for the route.
     * 3. The route's dependency hash matches the previous build (template code is identical).
     * 4. The previous cache has an entry for this exact path.
     * 5. The path's cacheKey matches the previous build (user data is identical).
     * 6. Every content entry the path rendered last build still has a matching
     *    render hash (imported components inside that content are unchanged).
     * 7. If the path renders a server island, the encryption key is unchanged, so
     *    the ciphertext baked into the restored HTML is still decryptable.
     */
    canSkip(routeComponent: string, pathname: string, dependencyHash: string, cacheKey: string, hasServerIsland?: boolean): boolean;
    /**
     * The content entries a path rendered in the previous build, so a skipped path
     * can carry its content-entry tracking forward without re-rendering.
     */
    previousContentEntryKeys(routeComponent: string, pathname: string): string[] | undefined;
    /**
     * The image transforms a path resolved in the previous build, so a skipped
     * path can replay them and carry them forward without re-rendering.
     */
    previousStaticImages(routeComponent: string, pathname: string): SerializedStaticImage[] | undefined;
    /** The untransformed image references recorded by a path in the previous build. */
    previousReferencedImages(routeComponent: string, pathname: string): string[] | undefined;
    /**
     * The response headers a path collected in the previous build, so a skipped
     * path can replay them into a `staticHeaders` adapter's headers file.
     */
    previousHeaders(routeComponent: string, pathname: string): [string, string][] | undefined;
    /** Record a path in the next manifest so a later build can skip or prune it. */
    record(routeComponent: string, dependencyHash: string, pathname: string, cacheKey: string, outputFile: string, contentEntryKeys?: string[], staticImages?: SerializedStaticImage[], referencedImages?: string[], headers?: [string, string][]): void;
    /**
     * Cache copies recorded in the previous build that are no longer keyed in this
     * one, either because the path was removed from `getStaticPaths()` or dropped
     * its `cacheKey`. Their stored copies are stale and should be pruned. Paths
     * that are still keyed keep their copies, even when the `cacheKey` changed.
     */
    findOrphanedFiles(): string[];
    writeManifest(settings: AstroSettings): void;
    restoreOutputFile(settings: AstroSettings, outputFile: string, destination: URL): Promise<boolean>;
    writeOutputFile(settings: AstroSettings, outputFile: string, body: string | Uint8Array): Promise<void>;
    deleteOutputFile(settings: AstroSettings, outputFile: string): Promise<void>;
}
