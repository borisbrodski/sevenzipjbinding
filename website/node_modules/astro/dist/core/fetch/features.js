const FetchFeatures = {
  redirects: 1 << 0,
  sessions: 1 << 1,
  actions: 1 << 2,
  middleware: 1 << 3,
  i18n: 1 << 4,
  cache: 1 << 5
};
const ALL_FETCH_FEATURES = FetchFeatures.redirects | FetchFeatures.sessions | FetchFeatures.actions | FetchFeatures.middleware | FetchFeatures.i18n | FetchFeatures.cache;
const usedFeatures = /* @__PURE__ */ new WeakMap();
function markFeatureUsed(manifest, feature) {
  const entry = usedFeatures.get(manifest);
  if (entry) {
    entry.bits |= feature;
  } else {
    usedFeatures.set(manifest, { bits: feature });
  }
}
function getUsedFeatures(manifest) {
  return usedFeatures.get(manifest)?.bits ?? 0;
}
export {
  ALL_FETCH_FEATURES,
  FetchFeatures,
  getUsedFeatures,
  markFeatureUsed
};
