import { markFeatureUsed, FetchFeatures } from "../fetch/features.js";
function provideSession(state) {
  markFeatureUsed(state.manifest, FetchFeatures.sessions);
}
export {
  provideSession
};
