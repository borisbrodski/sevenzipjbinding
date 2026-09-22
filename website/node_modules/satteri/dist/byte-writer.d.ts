/**
 * Growable little-endian byte buffer shared by the op-stream and command-buffer
 * encoders — the two hottest write paths in the package.
 *
 * Subclasses write single bytes directly through the protected `buf`/`n`
 * fields after an `ensure` for the whole record (one bounds check per record,
 * not per byte, and no per-byte call in unoptimized tiers — CodSpeed's
 * Simulation mode runs too few iterations for V8 to inline method calls).
 * Only the non-trivial logic lives here: growth, u32 writes, and UTF-8
 * string encoding.
 */
export declare class ByteWriter {
    #private;
    protected buf: Uint8Array;
    protected n: number;
    constructor(initialSize: number);
    /** Number of bytes written so far. */
    get length(): number;
    get capacity(): number;
    /** Reset for reuse; the grown buffer is retained so steady state is alloc-free. */
    reset(): void;
    /** View of the bytes written so far (no copy; valid until the next write or reset). */
    take(): Uint8Array;
    /** Release the backing (handed-out views stay intact); next write re-allocates. */
    protected release(): void;
    /** Grow (doubling) so `extra` more bytes fit; required before unchecked writes. */
    ensure(extra: number): void;
    /** Write a u32 (LE) at the cursor; caller must have `ensure`d 4 bytes. */
    protected writeU32(v: number): void;
    /** u32 byte length + UTF-8 bytes (self-ensuring). */
    protected utf8WithU32Len(s: string): void;
    /** Backpatch a u32 (LE) at `pos` in the already-written region; the cursor
     *  is left untouched. */
    protected patchU32(pos: number, v: number): void;
}
