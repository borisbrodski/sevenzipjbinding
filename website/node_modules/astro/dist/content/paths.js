import { DATA_STORE_DIR, DATA_STORE_FILE } from "./consts.js";
function getDataStoreFile(settings, isDev) {
  return new URL(DATA_STORE_FILE, isDev ? settings.dotAstroDir : settings.config.cacheDir);
}
function getDataStoreChunkSize(settings) {
  const storage = settings.config.experimental.collectionStorage;
  if (storage === void 0 || storage === "single-file") {
    return void 0;
  }
  if (storage === "chunked") {
    return 20 * 1024 * 1024;
  }
  return storage.chunkSize;
}
function getDataStoreDir(settings, isDev) {
  return new URL(DATA_STORE_DIR, isDev ? settings.dotAstroDir : settings.config.cacheDir);
}
export {
  getDataStoreChunkSize,
  getDataStoreDir,
  getDataStoreFile
};
