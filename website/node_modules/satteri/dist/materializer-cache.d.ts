/**
 * Shared materializer machinery for the HAST and MDAST flavors: per-reader
 * node memo, lazy `children` descriptors, and the frozen-mode (plugin walk
 * path) freeze rules.
 */
import type { Position } from "unist";
import type { NodeRefs } from "./visitor-shared.js";
/** The reader surface the shared machinery needs; both `HastReader` and `MdastReader` satisfy it. */
export interface MaterializerReader {
    getNodeType(nodeId: number): number;
    getChildIds(nodeId: number): number[];
    getPosition(nodeId: number): Position | undefined;
    getNodeData(nodeId: number): string | null;
}
export interface MaterializerSpec<TReader extends MaterializerReader, TNode extends object> {
    /** Function name used in error/warning messages (e.g. "materializeHastNode"). */
    label: string;
    /** Node-type tag -> canonical AST name (the generated `TYPE_NAMES`). */
    typeNames: Readonly<Record<number, string>>;
    /** Whether `node` carries `children`. Takes the built node because mdast
     *  `custom` decides leafness per node rather than per type. */
    hasChildren(nodeType: number, node: TNode, reader: TReader, nodeId: number): boolean;
    /**
     * Install the type-specific eager fields on `node`. Must not install
     * `children`, `position`, `data`, or `_nodeId`, and must not freeze:
     * the shared machinery owns all of those.
     */
    populate(node: TNode, reader: TReader, nodeId: number, nodeType: number): void;
}
/**
 * Build a memoizing materializer, memoized per `(reader, id)`.
 *
 * `node` materializes one node with lazy `children`; `frozen` (the plugin walk
 * path) deep-freezes every node at construction so plugins cannot corrupt the
 * shared cache. `tree` materializes a whole tree eagerly, which is what the
 * step-by-step API wants: it asked for the tree, so laziness would only add
 * per-node accessor overhead.
 */
export declare function createMaterializer<TReader extends MaterializerReader, TNode extends object>(spec: MaterializerSpec<TReader, TNode>): {
    node: (reader: TReader, nodeId: number, frozen?: boolean, refs?: NodeRefs) => TNode;
    tree: (reader: TReader, rootId: number) => TNode;
};
