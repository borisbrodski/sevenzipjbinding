import { BaseApp } from "../app/entrypoints/index.js";
import { getEnvironment } from "../environment/index.js";
import { getRouteCache } from "../render/route-cache.js";
class BuildApp extends BaseApp {
  #buildEnv;
  constructor(manifest, buildEnv) {
    super(manifest);
    this.#buildEnv = buildEnv;
  }
  isDev() {
    return true;
  }
  /**
   * Streaming falls through to the environment default
   * (`manifest.serverLike` for the build environment) — we can skip
   * streaming in SSG for performance, as writing strings is faster.
   */
  resolveStreaming() {
    return void 0;
  }
  setInternals(internals) {
    this.#buildEnv.setInternals(internals);
  }
  setOptions(options) {
    this.#buildEnv.setOptions(options);
    this.logger.setDestination(options.logger.options.destination);
    this.resetAdapterLogger();
  }
  getOptions() {
    return this.#buildEnv.getOptions();
  }
  getSettings() {
    return this.#buildEnv.getSettings();
  }
  /**
   * Route cache and component loader for `StaticPaths`. Defined on the app
   * (rather than reached through the functional core at the call site) so
   * they execute inside the prerender bundle's module graph: the default
   * prerenderer constructs `StaticPaths` from a different bundle, whose
   * copies of the core modules hold separate per-manifest state.
   */
  get routeCache() {
    return getRouteCache(this.manifest);
  }
  getComponentByRoute(routeData) {
    return getEnvironment(this.manifest).getComponentByRoute(this.manifest, routeData);
  }
  logRequest(_options) {
  }
}
export {
  BuildApp
};
