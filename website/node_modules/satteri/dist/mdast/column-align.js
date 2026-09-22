/** Decode a table column-alignment byte — shared by the generated walk decoder
 *  and the snapshot reader so the mapping lives in one place. */
const ALIGN_NAMES = [null, "left", "right", "center"];
export function decodeColumnAlign(byte) {
    return ALIGN_NAMES[byte] ?? null;
}
