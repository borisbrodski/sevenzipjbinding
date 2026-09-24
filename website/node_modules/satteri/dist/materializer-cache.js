/**
 * Shared materializer machinery for the HAST and MDAST flavors: per-reader
 * node memo, lazy `children` descriptors, and the frozen-mode (plugin walk
 * path) freeze rules.
 */
import { deepFreeze } from "./freeze.js";
/**
 * Build a memoizing materializer, memoized per `(reader, id)`.
 *
 * `node` materializes one node with lazy `children`; `frozen` (the plugin walk
 * path) deep-freezes every node at construction so plugins cannot corrupt the
 * shared cache. `tree` materializes a whole tree eagerly, which is what the
 * step-by-step API wants: it asked for the tree, so laziness would only add
 * per-node accessor overhead.
 */
export function createMaterializer(spec) {
    const readerCaches = new WeakMap();
    function materialize(reader, nodeId, frozen = false, refs) {
        const cache = readerCache(reader, frozen, refs);
        let node = cache.nodes.get(nodeId);
        if (node === undefined) {
            node = buildNode(reader, cache, nodeId, reader.getNodeType(nodeId));
            cache.nodes.set(nodeId, node);
        }
        return node;
    }
    /** Frozen-mode `children`: memoized in `cache.childLists` because the node
     *  is frozen, so the accessor cannot self-replace with a data property. */
    function frozenChildrenDescriptor(reader, cache) {
        return {
            get() {
                const nodeId = this._nodeId;
                let value = cache.childLists.get(nodeId);
                if (value === undefined) {
                    const ids = reader.getChildIds(nodeId);
                    const built = new Array(ids.length);
                    let i = 0;
                    for (const childId of ids)
                        built[i++] = materialize(reader, childId, true);
                    value = Object.freeze(built);
                    cache.childLists.set(nodeId, value);
                }
                return value;
            },
            configurable: true,
            enumerable: true,
        };
    }
    /** Mutable-mode `children`: self-replacing with a plain writable array on
     *  first read. The id is captured rather than stored on the node, so a
     *  materialized tree carries no marker for `toEqual` or a spread to find. */
    function mutableChildrenDescriptor(reader, nodeId) {
        return {
            get() {
                const ids = reader.getChildIds(nodeId);
                const value = new Array(ids.length);
                let i = 0;
                for (const childId of ids)
                    value[i++] = materialize(reader, childId);
                Object.defineProperty(this, "children", {
                    value,
                    writable: true,
                    configurable: true,
                    enumerable: true,
                });
                return value;
            },
            configurable: true,
            enumerable: true,
        };
    }
    function readerCache(reader, frozen, refs) {
        let cache = readerCaches.get(reader);
        if (cache === undefined) {
            cache = {
                nodes: new Map(),
                childLists: new Map(),
                children: undefined,
                frozen,
                refs,
            };
            if (frozen)
                cache.children = frozenChildrenDescriptor(reader, cache);
            readerCaches.set(reader, cache);
        }
        if (cache.frozen !== frozen) {
            throw new Error(`${spec.label}: a reader cannot mix frozen and mutable materialization`);
        }
        return cache;
    }
    function buildNode(reader, cache, nodeId, nodeType, eager = false) {
        const typeName = spec.typeNames[nodeType] ?? `unknown(${nodeType})`;
        // Plain object, not a class: unified's `assertNode` rejects any other prototype.
        const node = { type: typeName };
        const position = reader.getPosition(nodeId);
        if (position !== undefined) {
            node.position = position;
        }
        if (cache.frozen) {
            // Non-enumerable so `nid()` never trusts an id that a spread copied.
            cache.refs?.set(node, nodeId);
            Object.defineProperty(node, "_nodeId", {
                value: nodeId,
                writable: false,
                configurable: true,
                enumerable: false,
            });
        }
        spec.populate(node, reader, nodeId, nodeType);
        // Plugins can set `data` on any node type, so rehydrate generically
        // (see website/content/docs/divergences.md for the code-block case).
        const rawData = reader.getNodeData(nodeId);
        if (rawData !== null) {
            try {
                const parsed = JSON.parse(rawData);
                if (parsed && typeof parsed === "object" && Object.keys(parsed).length > 0) {
                    Object.defineProperty(node, "data", {
                        value: parsed,
                        writable: true,
                        configurable: true,
                        enumerable: true,
                    });
                }
            }
            catch (err) {
                if (process.env.NODE_ENV !== "production") {
                    console.warn(`${spec.label}: malformed node_data for nodeId=${nodeId}`, err);
                }
            }
        }
        if (!eager && spec.hasChildren(nodeType, node, reader, nodeId)) {
            Object.defineProperty(node, "children", cache.children ?? mutableChildrenDescriptor(reader, nodeId));
        }
        if (cache.frozen) {
            // Deep-freeze the eager own values but not the lazy `children` accessor;
            // freeze eagerly even for containers so nothing is writable while cached.
            const descriptors = Object.getOwnPropertyDescriptors(node);
            for (const key of Object.keys(descriptors)) {
                const desc = descriptors[key];
                if (desc !== undefined && "value" in desc) {
                    deepFreeze(desc.value);
                }
            }
            Object.freeze(node);
        }
        return node;
    }
    /**
     * Iterative on purpose: recursing to full document depth overflows on deeply
     * nested input, and nothing else here descends more than one level.
     *
     * Breadth-first over the parents still to fill, so every node is built and
     * every child list decoded exactly once, and a child is wired into its
     * parent's array as it is built rather than in a second pass.
     */
    function fillTree(reader, cache, rootId) {
        const rootType = reader.getNodeType(rootId);
        const root = buildNode(reader, cache, rootId, rootType, true);
        if (!spec.hasChildren(rootType, root, reader, rootId))
            return root;
        const parents = [root];
        const parentIds = [rootId];
        for (let p = 0; p < parentIds.length; p++) {
            const parent = parents[p];
            const parentId = parentIds[p];
            if (parent === undefined || parentId === undefined)
                continue;
            const ids = reader.getChildIds(parentId);
            const kids = new Array(ids.length);
            for (let i = 0; i < ids.length; i++) {
                const childId = ids[i];
                if (childId === undefined)
                    continue;
                const childType = reader.getNodeType(childId);
                const child = buildNode(reader, cache, childId, childType, true);
                kids[i] = child;
                if (spec.hasChildren(childType, child, reader, childId)) {
                    parents.push(child);
                    parentIds.push(childId);
                }
            }
            // Assignment beats defineProperty here; `eager` left `children` uninstalled.
            parent.children = kids;
        }
        return root;
    }
    /** Whole tree at once: the caller will walk it, so lazy accessors cost more than they defer. */
    function materializeTree(reader, rootId) {
        return fillTree(reader, readerCache(reader, false, undefined), rootId);
    }
    return { node: materialize, tree: materializeTree };
}
