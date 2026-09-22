import type { MdastReader } from "../mdast-reader.js";
/** Materialized property names per tag (the non-skip `js` names above),
 *  exported for the child-stub field tables. */
export declare const MDAST_LAYOUT_KEYS: Readonly<Record<number, readonly string[]>>;
/**
 * Decode a node's type-specific `type_data` from the walk buffer onto `node`,
 * driven by `MDAST_LAYOUTS` (fixed-field types) and `MDAST_TAILS` (counted
 * attribute lists). Returns `false` for tags in neither, so the caller falls
 * through to the remaining hand-written cases (list, listItem).
 */
export declare function decodeMdastTypeData(view: DataView, buf: Uint8Array, start: number, nodeType: number, node: Record<string, unknown>): boolean;
/**
 * Attach a node's fixed `type_data` fields, materialized from the arena
 * snapshot, driven by `MDAST_LAYOUTS`. Returns `false` for tags with no entry,
 * so the materializer falls through to its hand-written cases (list, table,
 * directives, MDX JSX). Fields are eager plain stores.
 */
export declare function materializeMdastFields(reader: MdastReader, node: object, nodeId: number, nodeType: number): boolean;
