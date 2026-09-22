import { createAsyncManifestMemo } from "../manifest/memo.js";
const cacheProviderMemo = createAsyncManifestMemo(async (manifest) => {
  if (manifest.cacheProvider) {
    const mod = await manifest.cacheProvider();
    const factory = mod?.default || null;
    return factory ? factory(manifest.cacheConfig?.options) : null;
  }
  return null;
});
function getCacheProvider(manifest) {
  return cacheProviderMemo.get(manifest);
}
export {
  getCacheProvider
};
