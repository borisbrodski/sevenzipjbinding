import type { SerializedStaticImage } from '../../assets/types.js';
import type { AstroLogger } from '../logger/core.js';
export interface CollectedPrerenderMetadata {
    contentEntryKeys: string[];
    staticImages: SerializedStaticImage[];
    referencedImages: string[];
}
/**
 * Runs `fn` inside a fresh per-render collectors store and returns its value
 * together with a snapshot of everything recorded while it ran.
 *
 * When no render scope is installed, collection degrades to *not collecting*:
 * warn once per process, run `fn` bare, and return `metadata: undefined`
 * ("not tracked") — never wrong attribution.
 *
 * Invariant: `fn` must not resolve until all recordable work is done — in
 * practice, until the response body is fully buffered inside `fn`. The
 * snapshot is taken by copy when `fn` resolves, so a late-arriving record (a
 * floating promise carrying the async context past buffering) mutates only the
 * abandoned store, never the returned metadata.
 */
export declare function collectPrerenderMetadata<T>(fn: () => Promise<T>, logger: AstroLogger): Promise<{
    value: T;
    metadata: CollectedPrerenderMetadata | undefined;
}>;
