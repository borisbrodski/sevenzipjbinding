import { serializeHandle } from "#binding";
/** Rebuild count per handle: bumped whenever a command buffer lands and
 *  renumbers the arena, invalidating ids captured before it. */
const HANDLE_EPOCHS = new WeakMap();
/** Per-flavor `(handle → cache)` slots, registered so a mutation can evict a snapshot the handle would otherwise pin. */
const EPOCH_CACHE_SLOTS = [];
export function registerEpochCacheSlot(slot) {
    EPOCH_CACHE_SLOTS.push(slot);
    return slot;
}
/** Per handle, not per epoch: an id stale within its own handle is the epoch machinery's to drop with a warning. */
const HANDLE_NODE_REFS = new WeakMap();
function nodeRefsOfHandle(handle) {
    let refs = HANDLE_NODE_REFS.get(handle);
    if (refs === undefined) {
        refs = new WeakMap();
        HANDLE_NODE_REFS.set(handle, refs);
    }
    return refs;
}
/** Record that `handle`'s arena was rebuilt. Resolvers created before the bump
 *  refuse to take a fresh snapshot afterwards (their ids are stale). */
export function markHandleMutated(handle) {
    HANDLE_EPOCHS.set(handle, (HANDLE_EPOCHS.get(handle) ?? 0) + 1);
    // Safe to evict: a stale-epoch entry can never be adopted, only pinned by existing resolvers
    for (const slot of EPOCH_CACHE_SLOTS) {
        slot.delete(handle);
    }
}
/** Arena sentinel in the node struct's parent field: the node has no parent. */
const NO_PARENT = 0xffffffff;
/**
 * Lazy node materializer for the walk paths: serializes the handle's arena
 * once, on the first stub materialization, then materializes nodes from that
 * snapshot. Subclasses supply reader construction and per-node materialization
 * so the hot path stays free of per-node closures.
 */
export class LazyChildResolver {
    #handle;
    #epoch;
    refs;
    /** Strong pin: retained nodes keep their pass snapshot alive after later epochs evict the slot. */
    #cache;
    constructor(handle) {
        this.#handle = handle;
        this.#epoch = HANDLE_EPOCHS.get(handle) ?? 0;
        this.refs = nodeRefsOfHandle(handle);
    }
    #ensureCache() {
        let cache = this.#cache;
        if (cache !== undefined)
            return cache;
        const slot = this.cacheSlot();
        cache = slot.get(this.#handle);
        if (cache !== undefined && cache.epoch === this.#epoch) {
            this.#cache = cache;
            return cache;
        }
        // A node id proves the tree was read in-pass, so a deferred snapshot is
        // still faithful as long as no command buffer mutated the arena since
        // match time. An existing same-epoch cache is always safe: the snapshot
        // is an immutable copy.
        if ((HANDLE_EPOCHS.get(this.#handle) ?? 0) !== this.#epoch) {
            throw new Error("Cannot read node content: this node was retained past its visitor pass " +
                "and the tree has changed since. Reading a child node's field (or calling " +
                "ctx.parent()) during the pass pins the pass snapshot; eager fields like " +
                "tagName or properties do not. Or copy the data you need before the pass ends.");
        }
        // The serialized buffer already carries each node's `data` blob (read
        // eagerly by the materializer), and the arena isn't mutated mid-visit,
        // so no separate lazy NAPI fetch is needed. This also keeps walk-path
        // children consistent with the fully materialized tree (no `data` key
        // when a node has none).
        cache = {
            epoch: this.#epoch,
            reader: this.createReader(serializeHandle(this.#handle)),
        };
        this.#cache = cache;
        slot.set(this.#handle, cache);
        return cache;
    }
    #ensureReader() {
        return this.#ensureCache().reader;
    }
    /** Whether this pass's snapshot already exists; never takes one itself. */
    hasHotSnapshot() {
        if (this.#cache !== undefined)
            return true;
        const cache = this.cacheSlot().get(this.#handle);
        return cache !== undefined && cache.epoch === this.#epoch;
    }
    /** Materialize one node for a child stub's first real-field read. The
     *  materializers memoize per `(reader, id)`, so overlapping subtrees and
     *  later passes share the same materialized objects. */
    materializeOne(nodeId) {
        return this.materializeNode(this.#ensureCache().reader, nodeId, this.refs);
    }
    /** Arena id of `nodeId`'s parent in the pass snapshot, or undefined at the root. */
    parentIdOf(nodeId) {
        const parentId = this.readParentId(this.#ensureReader(), nodeId);
        return parentId === NO_PARENT ? undefined : parentId;
    }
    /** Per-parent child-id→index maps, built lazily: null until a plugin calls
     *  `indexInParent` (most never do). Cache-safe because the snapshot is immutable. */
    #childIndexByParent = null;
    /** Index of `nodeId` within its parent's children in the pass snapshot,
     *  or undefined at the root. */
    indexInParent(nodeId) {
        const reader = this.#ensureReader();
        const parentId = this.readParentId(reader, nodeId);
        if (parentId === NO_PARENT)
            return undefined;
        const byParent = (this.#childIndexByParent ??= new Map());
        let indexById = byParent.get(parentId);
        if (indexById === undefined) {
            const map = new Map();
            this.readChildIds(reader, parentId).forEach((id, i) => map.set(id, i));
            byParent.set(parentId, map);
            indexById = map;
        }
        return indexById.get(nodeId);
    }
}
