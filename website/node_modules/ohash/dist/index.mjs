import { isEqual, serialize } from "./_chunks/is-equal.mjs";
import { digest, digest as digest$1 } from "ohash/crypto";
function hash(input) {
	return digest$1(serialize(input));
}
export { digest, hash, isEqual, serialize };
