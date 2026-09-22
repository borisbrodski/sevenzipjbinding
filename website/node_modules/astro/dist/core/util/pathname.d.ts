/**
 * Thrown when a URL path is encoded so many times that we give up decoding it
 * (see {@link validateAndDecodePathname}). When this happens we reject the
 * request with a `400` instead of guessing the path. If we let a half-decoded
 * path through, your middleware might check one path while Astro routes to a
 * different one.
 */
export declare class MultiLevelEncodingError extends Error {
    constructor();
}
/**
 * Decodes a URL path over and over until it stops changing, so a path that was
 * encoded several times ends up as a single, final path. This stops someone
 * from sneaking a path like `/admin` past middleware by encoding it multiple
 * times — middleware always sees the real, decoded path.
 *
 * @param pathname - The path to decode
 * @returns The final, fully decoded path
 * @throws Error if the path has broken encoding that can't be decoded at all
 *   (for example a lone `%` that isn't followed by two hex digits)
 * @throws MultiLevelEncodingError if the path is still changing after
 *   {@link MAX_DECODE_ITERATIONS} tries (it was encoded too many times).
 *   Handing back a half-decoded path here would bring back the security hole
 *   this function exists to close.
 */
export declare function validateAndDecodePathname(pathname: string): string;
