import { HastReader } from "./hast-reader.js";
import type { Root } from "hast";
import type { HastNode } from "../types.js";
export type { HastNode };
/** Container node types (the ones that carry `children`); everything else is a leaf. */
export declare const HAST_CONTAINER_TYPES: ReadonlySet<number>;
/**
 * Materialize a single HAST node; scalars eager, `children` lazy, memoized per
 * `(reader, id)`; `frozen` (the plugin walk path) deep-freezes so plugins
 * cannot corrupt the shared cache.
 */
export declare const materializeHastNode: (reader: HastReader, nodeId: number, frozen?: boolean, refs?: import("../visitor-shared.js").NodeRefs) => import("hast").Nodes;
/**
 * Materialize the full HAST tree from root (nodeId=0).
 */
export declare function materializeHastTree(reader: HastReader): Root;
