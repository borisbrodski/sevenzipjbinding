/**
 * Byte-offset to UTF-16-offset remap for a wire string pool holding multibyte
 * sequences, so the once-decoded pool can still be sliced with `substring`
 * instead of a `TextDecoder` call per string.
 */
export declare class PoolOffsets {
    #private;
    /** `extraBytes` is the pool's byte length minus its UTF-16 length, which
     *  bounds the multibyte character count: each one costs at least one byte. */
    constructor(bytes: Uint8Array, extraBytes: number);
    /** Both ends of a string ref sit on character boundaries, so the remap is exact. */
    slice(text: string, offset: number, len: number): string;
}
