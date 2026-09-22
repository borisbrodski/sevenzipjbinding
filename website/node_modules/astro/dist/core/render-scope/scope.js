const SCOPE_KEY = /* @__PURE__ */ Symbol.for("astro:render-scope");
function installRenderScope(scope) {
  const host = globalThis;
  const existing = host[SCOPE_KEY];
  if (existing) return existing;
  Object.defineProperty(host, SCOPE_KEY, {
    value: scope,
    configurable: true,
    writable: false,
    enumerable: false
  });
  return scope;
}
function getInstalledRenderScope() {
  return globalThis[SCOPE_KEY];
}
function uninstallRenderScope() {
  delete globalThis[SCOPE_KEY];
}
function getRenderCollectors() {
  return getInstalledRenderScope()?.getStore();
}
export {
  getInstalledRenderScope,
  getRenderCollectors,
  installRenderScope,
  uninstallRenderScope
};
