import { getEnvironment } from "../environment/index.js";
import { markFeatureUsed, FetchFeatures } from "../fetch/features.js";
import { getSessionDriver } from "./driver.js";
import { AstroSession, PERSIST_SYMBOL } from "./runtime.js";
const SESSION_KEY = "session";
function provideSession(state) {
  markFeatureUsed(state.manifest, FetchFeatures.sessions);
  const config = state.manifest.sessionConfig;
  if (!config) return;
  return provideSessionAsync(state, config);
}
async function provideSessionAsync(state, config) {
  const driverFactory = await getSessionDriver(state.manifest);
  if (!driverFactory) return;
  state.provide(SESSION_KEY, {
    create() {
      const cookies = state.cookies;
      return new AstroSession({
        cookies,
        config,
        runtimeMode: getEnvironment(state.manifest).runtimeMode,
        driverFactory,
        mockStorage: null,
        logger: state.logger
      });
    },
    finalize(session) {
      return session[PERSIST_SYMBOL]();
    }
  });
}
export {
  provideSession
};
