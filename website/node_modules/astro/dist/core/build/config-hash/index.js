import { encodeHexLowerCase } from "@oslojs/encoding";
import { getConfigHashInput } from "./input.js";
import { getConfigHashInput as getConfigHashInput2 } from "./input.js";
function stableStringify(value) {
  return JSON.stringify(value, (_key, val) => {
    if (val && typeof val === "object" && !Array.isArray(val)) {
      return Object.keys(val).sort().reduce((acc, key) => {
        acc[key] = val[key];
        return acc;
      }, {});
    }
    return val;
  });
}
async function computeConfigHash(config) {
  const input = stableStringify(getConfigHashInput(config));
  const digest = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(input));
  return encodeHexLowerCase(new Uint8Array(digest));
}
export {
  computeConfigHash,
  getConfigHashInput2 as getConfigHashInput
};
