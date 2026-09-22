import fetchable, { isDefaultFetchHandler } from "virtual:astro:fetchable";
import { manifest } from "virtual:astro:manifest";
import { clearActions } from "../../../../actions/load.js";
import { createNonRunnableEnvironment } from "../../../environment/dev-nonrunnable.js";
import { setEnvironment } from "../../../environment/index.js";
import { createConsoleLogger } from "../../../logger/impls/console.js";
import { getLogger, setLogger } from "../../../logger/manifest-logger.js";
import { clearMiddleware } from "../../../middleware/load.js";
import { getRouteCache } from "../../../render/route-cache.js";
import { updateRouteTable } from "../../../routing/route-table.js";
import { DevFacadeApp } from "../../dev-facade.js";
let hmrWired = false;
const createApp = ({ streaming } = {}) => {
  setLogger(manifest, createConsoleLogger({ level: manifest.logLevel }));
  setEnvironment(manifest, createNonRunnableEnvironment());
  const app = new DevFacadeApp(manifest, streaming);
  if (!isDefaultFetchHandler) {
    app.setFetchHandler(fetchable);
  }
  if (import.meta.hot && !hmrWired) {
    hmrWired = true;
    import.meta.hot.on("astro:routes-updated", async () => {
      try {
        const { routes: newRoutes } = await import("virtual:astro:routes");
        updateRouteTable(
          manifest,
          newRoutes.map((route) => route.routeData)
        );
      } catch (e) {
        getLogger(manifest).error("router", `Failed to update routes via HMR:
 ${e}`);
      }
    });
    import.meta.hot.on("astro:content-changed", () => {
      getRouteCache(manifest).clearAll();
    });
    import.meta.hot.on("astro:middleware-updated", () => {
      clearMiddleware(manifest);
    });
    import.meta.hot.on("astro:actions-updated", () => {
      clearActions(manifest);
    });
  }
  return app;
};
export {
  createApp
};
