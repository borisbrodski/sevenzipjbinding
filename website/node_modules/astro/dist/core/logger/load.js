import { isAbsolute } from "node:path";
import { pathToFileURL } from "node:url";
import { AstroLogger } from "./core.js";
import { AstroError } from "../errors/index.js";
import { UnableToLoadLogger } from "../errors/errors-data.js";
import { createNodeLoggerFromFlags } from "./impls/node.js";
import {
  COMPOSE_LOGGER_ENTRYPOINT,
  normalizeLoggerConfig
} from "./utils.js";
async function createDestination(config) {
  const specifier = isAbsolute(config.entrypoint) ? pathToFileURL(config.entrypoint).href : config.entrypoint;
  const logger = await import(
    /* @vite-ignore */
    specifier
  );
  if (config.entrypoint === COMPOSE_LOGGER_ENTRYPOINT) {
    return logger.default(await Promise.all((config.loggers ?? []).map(createDestination)));
  }
  return logger.default(config.config);
}
async function loadLoggerDestination(config, root) {
  const normalized = normalizeLoggerConfig(config, root);
  try {
    return await createDestination(normalized);
  } catch (e) {
    const error = new AstroError({
      ...UnableToLoadLogger,
      message: UnableToLoadLogger.message(normalized.entrypoint)
    });
    if (e instanceof Error) {
      error.cause = e;
    }
    throw error;
  }
}
async function loadOrCreateNodeLogger(astroConfig, inlineAstroConfig) {
  if (inlineAstroConfig._logger) return inlineAstroConfig._logger;
  try {
    if (astroConfig.logger) {
      return new AstroLogger({
        destination: await loadLoggerDestination(astroConfig.logger, astroConfig.root),
        level: inlineAstroConfig.logLevel ?? "info"
      });
    } else {
      return createNodeLoggerFromFlags(inlineAstroConfig);
    }
  } catch {
    return createNodeLoggerFromFlags(inlineAstroConfig);
  }
}
export {
  loadLoggerDestination,
  loadOrCreateNodeLogger
};
