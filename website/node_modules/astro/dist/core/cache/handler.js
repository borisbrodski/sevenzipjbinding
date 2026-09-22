import { markFeatureUsed, FetchFeatures } from "../fetch/features.js";
import { getEnvironment } from "../environment/index.js";
import { createManifestMemo } from "../manifest/memo.js";
import { getCacheProvider } from "./provider.js";
import { AstroCache, applyCacheHeaders } from "./runtime/cache.js";
import { NoopAstroCache, DisabledAstroCache } from "./runtime/noop.js";
import {
  compileCacheRoutes,
  matchCacheRoute
} from "./runtime/route-matching.js";
const CACHE_KEY = "cache";
function provideCache(state) {
  const manifest = state.manifest;
  if (!manifest.cacheConfig) {
    state.provide(CACHE_KEY, {
      create: () => new DisabledAstroCache(state.logger)
    });
    return;
  }
  if (getEnvironment(manifest).runtimeMode === "development") {
    state.provide(CACHE_KEY, {
      create: () => new NoopAstroCache()
    });
    return;
  }
  return provideCacheAsync(state, manifest);
}
async function provideCacheAsync(state, manifest) {
  const cacheProvider = await getCacheProvider(manifest);
  state.provide(CACHE_KEY, {
    create() {
      const cache = new AstroCache(cacheProvider);
      if (manifest.cacheConfig?.routes) {
        const matched = matchCacheRoute(state.pathname, getCompiledCacheRoutes(manifest));
        if (matched) {
          cache.set(matched);
        }
      }
      return cache;
    }
  });
}
async function handleCache(state, next) {
  markFeatureUsed(state.manifest, FetchFeatures.cache);
  if (!state.manifest.cacheProvider) {
    return next();
  }
  const cache = state.resolve(CACHE_KEY);
  const cacheProvider = await getCacheProvider(state.manifest);
  if (cacheProvider?.onRequest) {
    const response2 = await cacheProvider.onRequest(
      {
        request: state.request,
        url: new URL(state.request.url),
        waitUntil: state.renderOptions.waitUntil,
        logger: {
          info(msg) {
            state.logger.info("cache", msg);
          },
          warn(msg) {
            state.logger.warn("cache", msg);
          },
          error(msg) {
            state.logger.error("cache", msg);
          }
        }
      },
      async () => {
        const res = await next();
        applyCacheHeaders(cache, res, state.request);
        return res;
      }
    );
    response2.headers.delete("CDN-Cache-Control");
    response2.headers.delete("Cache-Tag");
    return response2;
  }
  const response = await next();
  applyCacheHeaders(cache, response, state.request);
  return response;
}
const compiledCacheRoutesMemo = createManifestMemo(
  (manifest) => manifest.cacheConfig?.routes ? compileCacheRoutes(manifest.cacheConfig.routes, manifest.base, manifest.trailingSlash) : []
);
function getCompiledCacheRoutes(manifest) {
  return compiledCacheRoutesMemo.get(manifest);
}
function setCompiledCacheRoutes(manifest, routes) {
  compiledCacheRoutesMemo.set(manifest, routes);
}
export {
  getCompiledCacheRoutes,
  handleCache,
  provideCache,
  setCompiledCacheRoutes
};
