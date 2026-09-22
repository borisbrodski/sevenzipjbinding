import { realpathSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { SessionStorageInitError } from "../errors/errors-data.js";
import { AstroError } from "../errors/index.js";
import { normalizePath } from "../viteUtils.js";
import { normalizeSessionDriverConfig } from "./utils.js";
const VIRTUAL_SESSION_DRIVER_ID = "virtual:astro:session-driver";
const RESOLVED_VIRTUAL_SESSION_DRIVER_ID = "\0" + VIRTUAL_SESSION_DRIVER_ID;
function vitePluginSessionDriver({ settings }) {
  return {
    name: VIRTUAL_SESSION_DRIVER_ID,
    enforce: "pre",
    resolveId: {
      filter: {
        id: new RegExp(`^${VIRTUAL_SESSION_DRIVER_ID}$`)
      },
      handler() {
        return RESOLVED_VIRTUAL_SESSION_DRIVER_ID;
      }
    },
    load: {
      filter: {
        id: new RegExp(`^${RESOLVED_VIRTUAL_SESSION_DRIVER_ID}$`)
      },
      async handler() {
        const session = settings.config.session;
        if (session === false || !session?.driver) {
          return { code: "export default null;" };
        }
        const driver = normalizeSessionDriverConfig(session.driver, session.options);
        const importerPath = fileURLToPath(import.meta.url);
        const resolved = await this.resolve(driver.entrypoint, importerPath);
        if (!resolved) {
          throw new AstroError({
            ...SessionStorageInitError,
            message: SessionStorageInitError.message(
              `Failed to resolve session driver: ${driver.entrypoint}`,
              driver.entrypoint
            )
          });
        }
        return {
          code: `import { default as _default } from '${resolved.id}';
export * from '${resolved.id}';
export default _default;`
        };
      }
    }
  };
}
const PROVIDER_FILENAME = "provider.js";
const DISABLED_PROVIDER_FILENAME = "provider-disabled.js";
function canonicalizePath(filePath) {
  try {
    return normalizePath(realpathSync(filePath));
  } catch {
    return normalizePath(filePath);
  }
}
function vitePluginSessionProvider({ settings }) {
  const providerPath = canonicalizePath(
    fileURLToPath(new URL(`./${PROVIDER_FILENAME}`, import.meta.url))
  );
  const disabledProviderPath = canonicalizePath(
    fileURLToPath(new URL(`./${DISABLED_PROVIDER_FILENAME}`, import.meta.url))
  );
  return {
    name: "astro:session-provider",
    enforce: "pre",
    async resolveId(id, importer) {
      const session = settings.config.session;
      const hasSessionDriver = session !== false && !!session?.driver;
      if (hasSessionDriver) return null;
      if (!normalizePath(id).endsWith(`/session/${PROVIDER_FILENAME}`)) return null;
      if (canonicalizePath(id) === providerPath) return disabledProviderPath;
      if (!importer) return null;
      const resolved = await this.resolve(id, importer, { skipSelf: true });
      if (resolved && canonicalizePath(resolved.id) === providerPath) {
        return disabledProviderPath;
      }
      return null;
    }
  };
}
export {
  VIRTUAL_SESSION_DRIVER_ID,
  vitePluginSessionDriver,
  vitePluginSessionProvider
};
