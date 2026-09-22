import { createAsyncManifestMemo } from "../manifest/memo.js";
import { createConsoleLogger } from "./impls/console.js";
const loggers = /* @__PURE__ */ new WeakMap();
function getLogger(manifest) {
  let logger = loggers.get(manifest);
  if (!logger) {
    logger = createConsoleLogger({ level: manifest.logLevel });
    loggers.set(manifest, logger);
  }
  return logger;
}
function setLogger(manifest, logger) {
  loggers.set(manifest, logger);
}
const resolvedLogger = createAsyncManifestMemo(async (manifest) => {
  const logger = getLogger(manifest);
  try {
    const destination = (await manifest.logger?.())?.default;
    if (destination) {
      logger.setDestination(destination);
    }
  } catch (error) {
    logger.error(
      "config",
      "Failed to load the configured logger destination; continuing with the console logger.\n" + (error instanceof Error ? error.stack ?? error.message : String(error))
    );
  }
  return logger;
});
function getResolvedLogger(manifest) {
  return resolvedLogger.get(manifest);
}
export {
  getLogger,
  getResolvedLogger,
  setLogger
};
