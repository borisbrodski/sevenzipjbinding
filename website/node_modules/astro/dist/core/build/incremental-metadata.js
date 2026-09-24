const ASTRO_INCREMENTAL_META_KEY = "astroIncremental";
function createContentDataIncrementalMetadata() {
  return {
    [ASTRO_INCREMENTAL_META_KEY]: {
      kind: "content-data"
    }
  };
}
function isContentDataIncrementalModule(info) {
  const metadata = info?.meta?.[ASTRO_INCREMENTAL_META_KEY];
  return metadata?.kind === "content-data";
}
export {
  ASTRO_INCREMENTAL_META_KEY,
  createContentDataIncrementalMetadata,
  isContentDataIncrementalModule
};
