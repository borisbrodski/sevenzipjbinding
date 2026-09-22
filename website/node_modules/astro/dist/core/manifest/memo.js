function createManifestMemo(derive) {
  const cache = /* @__PURE__ */ new WeakMap();
  return {
    get(manifest) {
      if (cache.has(manifest)) {
        return cache.get(manifest);
      }
      const value = derive(manifest);
      cache.set(manifest, value);
      return value;
    },
    has(manifest) {
      return cache.has(manifest);
    },
    set(manifest, value) {
      cache.set(manifest, value);
    },
    invalidate(manifest) {
      cache.delete(manifest);
    }
  };
}
function createAsyncManifestMemo(derive) {
  const cache = /* @__PURE__ */ new WeakMap();
  return {
    get(manifest) {
      let promise = cache.get(manifest);
      if (!promise) {
        promise = derive(manifest);
        cache.set(manifest, promise);
        promise.catch(() => {
          if (cache.get(manifest) === promise) {
            cache.delete(manifest);
          }
        });
      }
      return promise;
    },
    invalidate(manifest) {
      cache.delete(manifest);
    }
  };
}
export {
  createAsyncManifestMemo,
  createManifestMemo
};
