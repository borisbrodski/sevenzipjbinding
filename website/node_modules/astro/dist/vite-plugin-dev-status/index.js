function vitePluginDevStatus() {
  return {
    name: "astro:dev-status",
    configureServer(viteServer) {
      viteServer.middlewares.use(function devStatusHandler(req, res, next) {
        if (req.url !== "/_astro/status") return next();
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ ok: true }));
      });
    }
  };
}
export {
  vitePluginDevStatus
};
