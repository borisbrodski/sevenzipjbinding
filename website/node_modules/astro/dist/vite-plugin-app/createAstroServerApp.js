import { manifest } from "virtual:astro:manifest";
import { clearActions } from "../actions/load.js";
import { getPackageManager } from "../cli/info/core/get-package-manager.js";
import { DevDebugInfoProvider } from "../cli/info/infra/dev-debug-info-provider.js";
import { ProcessNodeVersionProvider } from "../cli/info/infra/process-node-version-provider.js";
import { ProcessPackageManagerUserAgentProvider } from "../cli/info/infra/process-package-manager-user-agent-provider.js";
import { StyledDebugInfoFormatter } from "../cli/info/infra/styled-debug-info-formatter.js";
import { BuildTimeAstroVersionProvider } from "../cli/infra/build-time-astro-version-provider.js";
import { PassthroughTextStyler } from "../cli/infra/passthrough-text-styler.js";
import { ProcessOperatingSystemProvider } from "../cli/infra/process-operating-system-provider.js";
import { TinyexecCommandExecutor } from "../cli/infra/tinyexec-command-executor.js";
import { DevFacadeApp } from "../core/app/dev-facade.js";
import { setEnvironment } from "../core/environment/index.js";
import { createNodeLoggerFromFlags } from "../core/logger/impls/node.js";
import { setLogger } from "../core/logger/manifest-logger.js";
import { clearMiddleware } from "../core/middleware/load.js";
import { getRouteCache } from "../core/render/route-cache.js";
import { updateRouteTable } from "../core/routing/route-table.js";
import { createRunnableEnvironment } from "./environment.js";
import { handleDevRequest } from "./handle-request.js";
async function createAstroServerApp(controller, settings, loader, logger) {
  const actualLogger = logger ?? createNodeLoggerFromFlags({});
  const debugInfoProvider = new DevDebugInfoProvider({
    config: settings.config,
    astroVersionProvider: new BuildTimeAstroVersionProvider(),
    operatingSystemProvider: new ProcessOperatingSystemProvider(),
    packageManager: await getPackageManager({
      packageManagerUserAgentProvider: new ProcessPackageManagerUserAgentProvider(),
      commandExecutor: new TinyexecCommandExecutor()
    }),
    nodeVersionProvider: new ProcessNodeVersionProvider()
  });
  const debugInfoFormatter = new StyledDebugInfoFormatter({
    textStyler: new PassthroughTextStyler()
  });
  const debugInfo = debugInfoFormatter.format(await debugInfoProvider.get());
  setLogger(manifest, actualLogger);
  setEnvironment(
    manifest,
    createRunnableEnvironment({ loader, settings, getDebugInfo: async () => debugInfo })
  );
  const app = new DevFacadeApp(manifest, true);
  const deps = { loader, settings, controller };
  if (import.meta.hot) {
    import.meta.hot.on("astro:routes-updated", async () => {
      try {
        const { routes: newRoutes } = await import("virtual:astro:routes");
        updateRouteTable(
          manifest,
          newRoutes.map((route) => route.routeData)
        );
        actualLogger.debug("router", "Routes updated via HMR");
      } catch (e) {
        actualLogger.error("router", `Failed to update routes via HMR:
 ${e}`);
      }
    });
    import.meta.hot.on("astro:content-changed", () => {
      getRouteCache(manifest).clearAll();
      actualLogger.debug("router", "Route cache cleared due to content change");
    });
    import.meta.hot.on("astro:middleware-updated", () => {
      clearMiddleware(manifest);
      actualLogger.debug("router", "Middleware cache cleared due to file change");
    });
    import.meta.hot.on("astro:actions-updated", () => {
      clearActions(manifest);
      actualLogger.debug("router", "Actions cache cleared due to file change");
    });
  }
  return {
    handler(incomingRequest, incomingResponse, options) {
      return handleDevRequest(app, deps, {
        incomingRequest,
        incomingResponse,
        isHttps: loader?.isHttps() ?? false,
        prerenderOnly: options?.prerenderOnly
      });
    }
  };
}
export {
  createAstroServerApp as default
};
