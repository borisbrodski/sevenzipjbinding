import type { SerializedStaticImage } from '../../assets/types.js';
/**
 * Records that a content entry was rendered, keyed by its root-relative
 * `filePath`. No-op when no scope is installed (dev, production SSR,
 * non-incremental builds) or no render is in scope (`getStaticPaths`, module
 * top-level).
 */
export declare function recordContentEntryRender(filePath: string | undefined): void;
/**
 * Records a resolved image transform, dedup hits included, preserving
 * duplicates (array push, not a set — replay depends on every record
 * arriving).
 */
export declare function recordStaticImage(image: SerializedStaticImage): void;
/** Records an untransformed image reference against the active render. */
export declare function recordReferencedImage(fsPath: string): void;
