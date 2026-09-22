async function getServerIslands(manifest) {
  if (manifest.serverIslandMappings) {
    return manifest.serverIslandMappings();
  }
  return {
    serverIslandMap: /* @__PURE__ */ new Map(),
    serverIslandNameMap: /* @__PURE__ */ new Map()
  };
}
export {
  getServerIslands
};
