/**
 * Little-endian primitives for reading the walk/snapshot wire buffers. Shared by
 * the hand-written decoders and the generated layout decoder so both interpret
 * the bytes identically.
 */
import type { Position } from "unist";
/** Read a u16 (LE) at `off`. */
export declare function ru16(view: DataView, off: number): number;
/** Read a u32 (LE) at `off`. */
export declare function ru32(view: DataView, off: number): number;
/** Read a UTF-8 string of `len` bytes at `off`. Very short ASCII runs decode
 *  with a charCode loop — TextDecoder's per-call overhead dominates there. The
 *  threshold is lower than the encode-side `INLINE_STR_MAX`: decoding builds a
 *  JS string by concatenation, which costs more per char than storing bytes.
 *  Measured crossover ~8-12 bytes (Node 24: 8B 30 vs 57 ns, 16B 64 vs 57). */
export declare function rstr(buf: Uint8Array, off: number, len: number): string;
/**
 * Decode the 24-byte position block ([startOffset, endOffset, startLine,
 * startColumn, endLine, endColumn], u32 LE each) shared by the walk prefixes
 * and the snapshot node structs. A zero start line is the sentinel for
 * synthesized nodes with no source range (e.g. GFM autolink-literal nodes, or a
 * plugin-built replacement spliced by the rebuild): unist lines are 1-based, so
 * line 0 always means "no source position" — even when the rebuild has rebased
 * the (otherwise zero) offset by the spliced subtree's source base. Surfaced as
 * `undefined` so a node reads the same however it's reached.
 */
export declare function readPosition(view: DataView, off: number): Position | undefined;
