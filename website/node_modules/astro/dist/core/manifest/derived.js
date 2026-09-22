import { createManifestMemo } from "./memo.js";
const sites = createManifestMemo(
  (manifest) => manifest.site ? new URL(manifest.site) : void 0
);
function getSite(manifest) {
  return sites.get(manifest);
}
export {
  getSite
};
