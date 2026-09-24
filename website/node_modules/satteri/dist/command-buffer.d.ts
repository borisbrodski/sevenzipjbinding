/**
 * Binary command buffer for efficient JS→Rust mutation serialization.
 *
 * Simple mutations (remove, setProperty) are encoded as compact binary commands.
 * Structural mutations (insert, replace, …) carry one of two payload kinds:
 * compiled op-streams (`PAYLOAD_OPSTREAM`, replayed straight into the arena —
 * see op-stream.ts) for declarative content, or raw markdown/HTML strings
 * (re-parsed by Rust) for the `{raw}`/`{rawHtml}` escape hatches.
 *
 * All multi-byte integers are little-endian to match native x86/ARM layout and
 * avoid byte-swapping on the Rust side.
 */
import { OpWriter } from "./op-stream.js";
import type { MdastNode } from "./types.js";
type ReturnClass = "no_change" | "raw_markdown" | "raw_html" | "structured_node";
/** Input to the structural mutators (`replace`, `insertBefore`, …). */
type StructuralContent = MdastNode | {
    raw: string;
    mdxExpressions?: boolean;
} | {
    rawHtml: string;
};
export declare function classifyReturn(value: unknown): ReturnClass;
/** Backings above this are dropped on release so one huge compile does not retain megabytes forever. */
export declare const COMMAND_BUFFER_RETAIN_MAX: number;
export declare function acquireCommandBuffer(): CommandBuffer;
export declare function releaseCommandBuffer(buf: CommandBuffer): void;
/** Structural commands that carry a subtree payload emitted in place via `emitOpstreamCommand`. */
export type StructuralOp = "replace" | "insertBefore" | "insertAfter" | "prependChild" | "appendChild" | "wrapNode";
export declare const STRUCTURAL_CMD: Record<StructuralOp, number>;
export declare class CommandBuffer extends OpWriter {
    #private;
    constructor();
    reset(): void;
    /** Emit a structural command whose opstream payload is written by `emit`
     *  via the inherited op methods. If `emit` returns false or throws, the
     *  buffer is rolled back to the command start so the Rust decoder never
     *  sees a half-written command. Returns `emit`'s verdict. */
    emitOpstreamCommand(cmd: number, nodeId: number, emit: () => boolean): boolean;
    removeNode(nodeId: number): void;
    /** Unified set-property for both MDAST and HAST nodes.
     *
     *  Hot path: uses `encodeInto` to write UTF-8 straight into the buffer (no
     *  per-call `Uint8Array`), reserving the worst-case length up front and
     *  backfilling the length prefix once the byte count is known. */
    setProperty(nodeId: number, key: string, value: unknown): void;
    insertBefore(nodeId: number, newNode: StructuralContent): void;
    insertAfter(nodeId: number, newNode: StructuralContent): void;
    prependChild(nodeId: number, newNode: StructuralContent): void;
    appendChild(nodeId: number, newNode: StructuralContent): void;
    wrapNode(nodeId: number, parentNode: StructuralContent): void;
    replace(nodeId: number, newNode: StructuralContent): void;
    /** Header (cmd + nodeId + PAYLOAD_RAW + flags) followed by a length-prefixed string. */
    private writeRawCommand;
    /** Return a Uint8Array view of the written bytes (no copy). */
    getBuffer(): Uint8Array;
    /** Encode the raw-string escape hatch; declarative nodes use `*Opstream` instead. */
    private writeStructuralCommand;
}
export {};
