import { createAsyncManifestMemo } from "../manifest/memo.js";
const sessionDriverMemo = createAsyncManifestMemo(async (manifest) => {
  if (manifest.sessionDriver) {
    const driverModule = await manifest.sessionDriver();
    return driverModule?.default || null;
  }
  return null;
});
function getSessionDriver(manifest) {
  return sessionDriverMemo.get(manifest);
}
export {
  getSessionDriver
};
