/**
 * Cold/shape-stable helpers shared by the mdast and hast visitors. The
 * per-node hot decoders stay duplicated in each visitor on purpose: sharing
 * them would feed differently-shaped arguments through one call site and turn
 * it polymorphic.
 */
import type { CommandBuffer } from "./command-buffer.js";
/** Plugin-level configuration, set via `options` on a plugin definition. */
export interface PluginOptions {
    /**
     * Set to `true` if any visitor reads `node.position`. When no plugin in the
     * pipeline opts in, source-position tracking is skipped during parsing
     * (~15% faster parse), and `node.position` is `undefined`.
     */
    position?: boolean;
}
export declare const ROOT_NODE_ID = 0;
export declare function rootReplacementError(content: unknown): Error;
/** Hooks subscribe to node 0 by node type, so a document left headed by anything
 *  but a `root` silently stops firing them, in this phase and every later one. */
export declare function requireRootReplacement<T>(content: T): T;
export declare function asArray<T>(value: T | T[]): T[];
/** Thrown when declarative replacement content can't be compiled to the
 *  structural op-stream — an unsupported node type (e.g. a bare `root`/`doctype`
 *  handed in as content) or an out-of-range numeric field. The op-stream is the
 *  only structural encoding, so this is a hard error rather than a fallback. */
export declare function unencodableContentError(content: unknown): Error;
/** Per handle, so membership alone proves ownership and the read path needs no marker of its own. */
export type NodeRefs = WeakMap<object, number>;
/** Separates "belongs to another tree" from "never had an id"; arena ids are never negative. */
export declare const FOREIGN_REF = -1;
/** `_refs` rides on the prototype, surviving neither a spread copy nor an object literal. */
export declare function crossPipelineForeign(node: object): number | undefined;
/**
 * Arena id for a node passed to a context method, via a per-kind `nid` lookup
 * (closure keeps each kind's call site monomorphic) resolved against the
 * edited tree's own refs. Plugin-built nodes have no id; without this check
 * the id would coerce to 0 in the command buffer and the mutation would
 * silently target the document root. A node read from another tree has an id
 * that is meaningless here, and one that happens to be in range would edit an
 * unrelated node.
 */
export declare function makeRequireNid<TNode>(nid: (node: TNode, refs: NodeRefs) => number | undefined): (node: TNode, method: string, refs: NodeRefs) => number;
/** Concatenate the return-value and context command buffers for one pass,
 *  resetting both for reuse. */
export declare function mergeAndReset(returnBuffer: CommandBuffer, ctx: {
    getCommandBuffer(): CommandBuffer;
}): {
    merged: Uint8Array;
    hasMutations: boolean;
};
