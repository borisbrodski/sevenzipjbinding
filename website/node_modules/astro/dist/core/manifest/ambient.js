import { NoManifestAvailable } from "../errors/errors-data.js";
import { AstroError } from "../errors/index.js";
import { manifest as viteManifest } from "#astro-internal/ambient-manifest";
let registered;
function setAmbientManifest(manifest) {
  registered = manifest;
}
function getAmbientManifest() {
  const manifest = registered ?? viteManifest;
  if (!manifest) {
    throw new AstroError(NoManifestAvailable);
  }
  return manifest;
}
function tryGetAmbientManifest() {
  return registered ?? viteManifest;
}
export {
  getAmbientManifest,
  setAmbientManifest,
  tryGetAmbientManifest
};
