import type { Root } from "mdast";
import type { MdastReader } from "./mdast-reader.js";
export declare const LEAF_TYPES: ReadonlySet<number>;
/** A `custom` node is a leaf when it has a non-empty `value` and no children or
 *  `data.h*`. Leafness is per node there, not per type, so the read paths ask
 *  this instead of {@link LEAF_TYPES}.
 *  @see {@link Custom} */
export declare function isCustomLeaf(node: {
    readonly value?: unknown;
    readonly data?: unknown;
}, childCount: number): boolean;
/**
 * Materialize a single MDAST node; scalars eager, `children` lazy, memoized
 * per `(reader, id)`; `frozen` (the plugin walk path) deep-freezes so plugins
 * cannot corrupt the shared cache.
 */
export declare const materializeNode: (reader: MdastReader, nodeId: number, frozen?: boolean, refs?: import("../visitor-shared.js").NodeRefs) => import("mdast").Nodes;
/** Materialize the full tree from root (nodeId=0). */
export declare function materializeMdastTree(reader: MdastReader): Root;
