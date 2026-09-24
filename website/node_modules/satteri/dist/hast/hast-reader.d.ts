import type { BufferHeader } from "../types.js";
import type { MdxJsxAttribute, MdxJsxExpressionAttribute } from "../mdx-types.js";
import type { Position } from "unist";
export type { MdxJsxAttribute, MdxJsxExpressionAttribute };
export declare const HAST_ROOT: number;
export declare const HAST_ELEMENT: number;
export declare const HAST_TEXT: number;
export declare const HAST_COMMENT: number;
export declare const HAST_RAW: number;
export declare const HAST_MDX_JSX_ELEMENT: number;
export declare const HAST_MDX_JSX_TEXT_ELEMENT: number;
export declare const HAST_MDX_FLOW_EXPRESSION: number;
export declare const HAST_MDX_ESM: number;
export declare const HAST_MDX_TEXT_EXPRESSION: number;
export interface HastProperty {
    name: string;
    value: string | number | boolean | (string | number)[];
}
export declare class HastReader {
    #private;
    constructor(buffer: ArrayBuffer | Uint8Array);
    /**
     * Per-node JSON `data` blob (set via `Arena::set_node_data` on the Rust
     * side). Returns `null` when the node has no entry. Lazy-builds a
     * `Map<id, string>` on first call so a materialization pass on a
     * data-heavy tree stays O(nodes) rather than O(nodes × entries).
     */
    getNodeData(nodeId: number): string | null;
    get nodeCount(): number;
    get header(): BufferHeader;
    /** The full string pool (original input + interning heap). Not the document
     * source as written; for that, read `ctx.source` from a plugin. */
    getStringPool(): string;
    /** Read a substring from the string pool by byte offset and length. */
    getString(offset: number, len: number): string;
    /** Get position data for a node. */
    getPosition(nodeId: number): Position | undefined;
    /** Get the node_type byte for a given node ID. */
    getNodeType(nodeId: number): number;
    /** Get the parent id for a given node (0xffffffff at the root). */
    getParentId(nodeId: number): number;
    /** Get child node IDs for a given node. */
    getChildIds(nodeId: number): number[];
    /** Push child node IDs directly onto a stack array (reverse order for depth-first). */
    pushChildIds(nodeId: number, stack: number[]): void;
    /** Get the raw type_data bytes for a node. */
    getTypeData(nodeId: number): Uint8Array;
    /** Element `tagName`, `properties` count, and the i-th property, read without
     *  building the intermediate views and records `getElementData` allocates. */
    getElementTagName(nodeId: number): string;
    getElementPropCount(nodeId: number): number;
    getElementPropName(nodeId: number, index: number): string;
    getElementPropValue(nodeId: number, index: number): HastProperty["value"];
    getElementData(nodeId: number): {
        tagName: string;
        properties: HastProperty[];
    };
    /**
     * Get MDX JSX element data: name and attributes.
     *
     * MDX JSX element type_data layout:
     *   [name: StringRef(8B)][attr_count: u32(4B)][_pad: u32(4B)] = 16-byte header
     *   then attr_count * 20 bytes:
     *     [kind: u8(1B)][_pad: [u8;3](3B)][name: StringRef(8B)][value: StringRef(8B)]
     */
    getMdxJsxElementData(nodeId: number): {
        name: string | null;
        attributes: (MdxJsxAttribute | MdxJsxExpressionAttribute)[];
    };
    /**
     * Get the string value for HAST_TEXT, HAST_COMMENT, or HAST_RAW nodes.
     * These store a single StringRef (8 bytes) as their type_data.
     */
    getTextValue(nodeId: number): string;
}
