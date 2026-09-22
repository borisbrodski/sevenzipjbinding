import { getInstalledRenderScope } from "./scope.js";
let warnedNoScope = false;
async function collectPrerenderMetadata(fn, logger) {
  const scope = getInstalledRenderScope();
  if (!scope) {
    if (!warnedNoScope) {
      warnedNoScope = true;
      logger.warn(
        "build",
        "A prerenderer requested metadata collection but no render scope is installed; install one with `installRenderScope` from `astro/app` \u2014 incremental metadata will not be collected for prerendered paths."
      );
    }
    return { value: await fn(), metadata: void 0 };
  }
  const store = {
    contentEntries: /* @__PURE__ */ new Set(),
    staticImages: [],
    referencedImages: /* @__PURE__ */ new Set()
  };
  const value = await scope.run(store, fn);
  return {
    value,
    metadata: {
      contentEntryKeys: [...store.contentEntries],
      staticImages: [...store.staticImages],
      referencedImages: [...store.referencedImages]
    }
  };
}
export {
  collectPrerenderMetadata
};
