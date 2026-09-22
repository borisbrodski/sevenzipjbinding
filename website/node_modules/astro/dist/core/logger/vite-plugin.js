import { fileURLToPath } from "node:url";
import { UnableToLoadLogger } from "../errors/errors-data.js";
import { AstroError } from "../errors/index.js";
import { normalizeLoggerConfig } from "./utils.js";
const VIRTUAL_LOGGER_ID = "virtual:astro:logger";
const RESOLVED_VIRTUAL_LOGGER_ID = "\0" + VIRTUAL_LOGGER_ID;
async function emitDestination(config, resolveEntrypoint, nameOffset = 0) {
  let resolved = null;
  let cause;
  try {
    resolved = await resolveEntrypoint(config.entrypoint);
  } catch (e) {
    cause = e;
  }
  if (!resolved) {
    const error = new AstroError({
      ...UnableToLoadLogger,
      message: UnableToLoadLogger.message(config.entrypoint)
    });
    if (cause instanceof Error) {
      error.cause = cause;
    }
    throw error;
  }
  const name = `_logger${nameOffset}`;
  const imports = [`import ${name} from ${JSON.stringify(resolved)};`];
  if (config.loggers) {
    const expressions = [];
    for (const nested of config.loggers) {
      const emitted = await emitDestination(nested, resolveEntrypoint, nameOffset + imports.length);
      imports.push(...emitted.imports);
      expressions.push(emitted.expression);
    }
    return { expression: `${name}([${expressions.join(", ")}])`, imports };
  }
  return { expression: `${name}(${JSON.stringify(config.config) ?? "undefined"})`, imports };
}
function vitePluginLogger({ settings }) {
  const loggerConfig = settings.config.logger;
  return {
    name: VIRTUAL_LOGGER_ID,
    enforce: "pre",
    resolveId: {
      filter: {
        id: new RegExp(`^${VIRTUAL_LOGGER_ID}$`)
      },
      handler() {
        return RESOLVED_VIRTUAL_LOGGER_ID;
      }
    },
    load: {
      filter: {
        id: new RegExp(`^${RESOLVED_VIRTUAL_LOGGER_ID}$`)
      },
      async handler() {
        const level = `export const level = ${JSON.stringify(settings.logLevel)};
`;
        if (!loggerConfig) {
          return { code: `${level}export default null;
` };
        }
        const importerPath = fileURLToPath(new URL("package.json", settings.config.root));
        const { expression, imports } = await emitDestination(
          normalizeLoggerConfig(loggerConfig, settings.config.root),
          async (entrypoint) => (await this.resolve(entrypoint, importerPath))?.id ?? null
        );
        return {
          code: `${imports.join("\n")}
${level}export default ${expression};
`
        };
      }
    }
  };
}
export {
  VIRTUAL_LOGGER_ID,
  emitDestination,
  vitePluginLogger
};
