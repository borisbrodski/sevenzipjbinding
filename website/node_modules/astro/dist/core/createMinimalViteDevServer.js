import { createServer } from "vite";
async function createMinimalViteDevServer(plugins = [], root) {
  return await createServer({
    root,
    configFile: false,
    server: { middlewareMode: true, hmr: false, watch: null, ws: false },
    optimizeDeps: { noDiscovery: true },
    clearScreen: false,
    appType: "custom",
    ssr: { external: true },
    resolve: { tsconfigPaths: true },
    plugins
  });
}
export {
  createMinimalViteDevServer
};
