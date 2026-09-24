import { getContentPaths, reloadContentConfigObserver } from "./utils.js";
let inFlight;
function getContentConfigLoadPromise() {
  return inFlight;
}
function kickOffContentConfigLoad({
  settings,
  fs,
  logger,
  environment
}) {
  if (inFlight) {
    return inFlight;
  }
  const contentPaths = getContentPaths(
    settings.config,
    fs,
    settings.config.legacy?.collectionsBackwardsCompat
  );
  if (!contentPaths.config.exists) {
    return Promise.resolve();
  }
  inFlight = reloadContentConfigObserver({
    fs,
    settings,
    environment,
    logger
  }).finally(() => {
    inFlight = void 0;
  });
  return inFlight;
}
export {
  getContentConfigLoadPromise,
  kickOffContentConfigLoad
};
