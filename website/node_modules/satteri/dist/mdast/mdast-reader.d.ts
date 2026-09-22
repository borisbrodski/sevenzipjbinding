import type { MdastNodeRaw, BufferHeader, StringRefRaw, MdxJsxAttributeUnion } from "../types.js";
import { readPosition } from "../wire-read.js";
export { NodeType, NodeTypeName } from "./generated/node-types.js";
export declare class MdastReader {
    #private;
    constructor(buffer: ArrayBuffer | Uint8Array);
    /** Per-node JSON `data` blob (set via `Arena::set_node_data` on the Rust
     * side). Lazy-builds a `Map<id, string>` on first call so materialization
     * of a data-heavy tree stays O(nodes) rather than O(nodes × entries). */
    getNodeData(nodeId: number): string | null;
    get nodeCount(): number;
    get header(): BufferHeader;
    /** The full string pool (original input + interning heap). Not the document
     * source as written; for that, read `ctx.source` from a plugin. */
    getStringPool(): string;
    getString(offset: number, len: number): string;
    getNode(nodeId: number): MdastNodeRaw;
    /** Fast path: read only the type byte for a node. */
    getNodeType(nodeId: number): number;
    getPosition(nodeId: number): ReturnType<typeof readPosition>;
    /** Fast path: read only the parent id for a node (0xffffffff at the root). */
    getParentId(nodeId: number): number;
    getChildIds(nodeId: number): number[];
    /** Push child node IDs directly onto a stack array (reverse order for depth-first). */
    pushChildIds(nodeId: number, stack: number[]): void;
    /** Fixed-layout field reads that touch the buffer directly. The generated
     *  decoder runs once per node, so an intermediate view or ref object here
     *  costs an allocation per field. */
    fieldU8(nodeId: number, offset: number, fallback: number): number;
    /** `""` when the field is absent or empty; callers map that to `null` for
     *  nullable fields, matching the Rust decoders' bounds checks. */
    fieldString(nodeId: number, offset: number): string;
    getTypeData(nodeId: number): Uint8Array;
    /** Read a StringRef (offset: u32 LE, len: u32 LE) from type data. */
    readStringRef(typeData: Uint8Array, byteOffset?: number): StringRefRaw;
    /**
     * StringRef value. Valid for Text, InlineCode, Html, Yaml, Toml nodes.
     * These store a single StringRef as their type data.
     */
    getTextValue(nodeId: number): string;
    /**
     * ListData #[repr(C)]: start(0..4), ordered(4), spread(5), _pad(6..8).
     * Valid for List nodes.
     */
    getListData(nodeId: number): {
        ordered: boolean;
        start: number;
        spread: boolean;
    };
    /**
     * ListItemData #[repr(C)]: checked(0), spread(1).
     * checked: 0=unchecked, 1=checked, 2=not-a-task-item.
     */
    getListItemData(nodeId: number): {
        checked: boolean | null;
        spread: boolean;
    };
    /**
     * DescriptionDetailsData #[repr(C)]: spread(0). Valid for descriptionDetails.
     */
    getDescriptionDetailsData(nodeId: number): {
        spread: boolean;
    };
    /**
     * TableData #[repr(C)]: align_count(0..4), then align_count bytes.
     * Alignment bytes: 0=none, 1=left, 2=right, 3=center.
     */
    getTableAlign(nodeId: number): (string | null)[];
    /**
     * MdxJsxElementData: name StringRef (0..8). len===0 means fragment.
     */
    getMdxJsxElementName(nodeId: number): string | null;
    /**
     * MDX JSX element data: name + attributes.
     *
     * Layout:
     *   [name: StringRef(8B)][attr_count: u32(4B)][_pad: u32(4B)] = 16-byte header
     *   then attr_count * 20 bytes:
     *     [kind: u8(1B)][_pad: [u8;3](3B)][name: StringRef(8B)][value: StringRef(8B)]
     *
     * Attribute kinds: 0=boolean, 1=literal, 2=expression, 3=spread
     */
    getMdxJsxElementData(nodeId: number): {
        name: string | null;
        attributes: MdxJsxAttributeUnion[];
    };
    /**
     * DirectiveData layout:
     *   [name: StringRef(8B)][attr_count: u32(4B)][_pad: u32(4B)] = 16-byte header
     *   then attr_count × 16 bytes:
     *     [key: StringRef(8B)][value: StringRef(8B)]
     */
    getDirectiveData(nodeId: number): {
        name: string;
        attributes: Record<string, string>;
    };
    /**
     * Walk the tree depth-first. Return false from visitor to skip children.
     */
    walk(visitor: (nodeId: number, nodeType: number) => boolean | void, rootId?: number): void;
    /** Walk depth-first with full node objects (slower, but convenient). */
    walkFull(visitor: (node: MdastNodeRaw) => boolean | void, rootId?: number): void;
}
