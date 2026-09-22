/**
 * Low-level op-stream writer shared by the MDAST/HAST declarative compilers.
 *
 * Emits the compact OPEN/CLOSE/field/REF/KEEP_CHILDREN/PROP stream that Rust
 * replays straight into the arena (`replay_opstream` in js_commands.rs; byte
 * values in generated/wire-constants.ts) — the only structural encoding for
 * plugin-built content. Strings ride ByteWriter's zero-alloc path (inline
 * char codes when short ASCII, `encodeInto` otherwise). `CommandBuffer`
 * extends this class so payloads are emitted directly into the command
 * bytes, with no intermediate opstream buffer or copy.
 */
import { ByteWriter } from "./byte-writer.js";
export { OF_VALUE, OF_URL, OF_TITLE, OF_ALT, OF_LANG, OF_META, OF_IDENTIFIER, OF_LABEL, OF_NAME, OF_REFERENCE_TYPE, OF_DEPTH, OF_CHECKED, OF_START, OF_ORDERED, OF_SPREAD, OF_TAGNAME, OF_EXPLICIT, PROP_STRING, PROP_BOOL_TRUE, PROP_BOOL_FALSE, PROP_SPACE_SEP, PROP_INT, PROP_NULL, MDX_ATTR_BOOLEAN_PROP, MDX_ATTR_LITERAL_PROP, MDX_ATTR_EXPRESSION_PROP, MDX_ATTR_SPREAD, } from "./generated/wire-constants.js";
export declare class OpWriter extends ByteWriter {
    constructor(initialSize?: number);
    open(type: number): void;
    close(): void;
    str(field: number, s: string): void;
    u8(field: number, v: number): void;
    u32(field: number, v: number): void;
    bool(field: number, v: boolean): void;
    data(value: unknown): void;
    prop(name: string, kind: number, value: string): void;
    ref(id: number): void;
    /** Table column-alignment codes (0=none, 1=left, 2=right, 3=center). */
    align(codes: readonly number[]): void;
    keepChildren(): void;
}
/** Emit one MDX JSX attribute as an OP_PROP, mirroring `encode_js_jsx_attrs`:
 *  null→boolean, string→literal, `{ value }` object→expression, else→boolean. */
export declare function emitMdxAttr(w: OpWriter, a: Record<string, unknown>): void;
