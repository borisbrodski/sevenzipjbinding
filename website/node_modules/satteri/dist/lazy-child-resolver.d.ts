import type { NodeRefs } from "./visitor-shared.js";
import type { AnyHandle } from "./handles.js";
export declare function registerEpochCacheSlot<T extends object>(slot: WeakMap<AnyHandle, T>): WeakMap<AnyHandle, T>;
/** Record that `handle`'s arena was rebuilt. Resolvers created before the bump
 *  refuse to take a fresh snapshot afterwards (their ids are stale). */
export declare function markHandleMutated(handle: AnyHandle): void;
/** Snapshot for one `(handle, epoch)`. Immutable once built, so it is shared
 *  by every resolver of the same pass chain: nested matched containers dedup
 *  by id, and later read-only passes reuse the whole cache instead of
 *  re-serializing and re-materializing. */
export interface EpochCache<TReader> {
    epoch: number;
    reader: TReader;
}
/**
 * Lazy node materializer for the walk paths: serializes the handle's arena
 * once, on the first stub materialization, then materializes nodes from that
 * snapshot. Subclasses supply reader construction and per-node materialization
 * so the hot path stays free of per-node closures.
 */
export declare abstract class LazyChildResolver<TReader, TNode> {
    #private;
    readonly refs: NodeRefs;
    constructor(handle: AnyHandle);
    protected abstract createReader(wire: Uint8Array): TReader;
    protected abstract materializeNode(reader: TReader, nodeId: number, refs: NodeRefs): TNode;
    protected abstract readParentId(reader: TReader, nodeId: number): number;
    protected abstract readChildIds(reader: TReader, nodeId: number): number[];
    /** Kind-specific `(handle → cache)` slot, supplied as a module-level WeakMap
     *  by each subclass so MDAST and HAST snapshots never collide. */
    protected abstract cacheSlot(): WeakMap<AnyHandle, EpochCache<TReader>>;
    /** Whether this pass's snapshot already exists; never takes one itself. */
    hasHotSnapshot(): boolean;
    /** Materialize one node for a child stub's first real-field read. The
     *  materializers memoize per `(reader, id)`, so overlapping subtrees and
     *  later passes share the same materialized objects. */
    materializeOne(nodeId: number): TNode;
    /** Arena id of `nodeId`'s parent in the pass snapshot, or undefined at the root. */
    parentIdOf(nodeId: number): number | undefined;
    /** Index of `nodeId` within its parent's children in the pass snapshot,
     *  or undefined at the root. */
    indexInParent(nodeId: number): number | undefined;
}
