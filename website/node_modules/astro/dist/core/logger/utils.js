import { resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { isRelativePath } from "../path.js";
const COMPOSE_LOGGER_ENTRYPOINT = "astro/logger/compose";
function normalizeLoggerConfig(logger, root) {
  const entrypoint = normalizeEntrypoint(logger.entrypoint, root);
  if (entrypoint === COMPOSE_LOGGER_ENTRYPOINT) {
    const loggers = logger.config?.loggers ?? [];
    return {
      entrypoint,
      loggers: loggers.map((nested) => normalizeLoggerConfig(nested, root))
    };
  }
  return { entrypoint, config: logger.config };
}
function normalizeEntrypoint(entrypoint, root) {
  if (entrypoint instanceof URL) {
    return fileURLToPath(entrypoint);
  }
  if (isRelativePath(entrypoint)) {
    return resolve(fileURLToPath(root), entrypoint);
  }
  return entrypoint;
}
export {
  COMPOSE_LOGGER_ENTRYPOINT,
  normalizeLoggerConfig
};
