import { createRequire } from "node:module";
const BROWSER_MAP = {
  chrome: "chrome",
  edge: "edge",
  firefox: "firefox",
  hermes: false,
  ie: "ie",
  ios: "ios_saf",
  node: false,
  opera: "opera",
  rhino: false,
  safari: "safari"
};
const VERSION_RE = /\d/;
function convertTargets(esbuildTarget) {
  if (!esbuildTarget) return {};
  const targets = {};
  const entries = Array.isArray(esbuildTarget) ? esbuildTarget : [esbuildTarget];
  for (const entry of entries) {
    if (entry === "esnext") continue;
    const index = entry.search(VERSION_RE);
    if (index >= 0) {
      const browserName = entry.slice(0, index);
      const browser = BROWSER_MAP[browserName];
      if (browser === false) continue;
      if (browser) {
        const [major, minor = 0] = entry.slice(index).split(".").map((v) => Number.parseInt(v, 10));
        if (!isNaN(major) && !isNaN(minor)) {
          const version = major << 16 | minor << 8;
          if (!targets[browser] || version < targets[browser]) {
            targets[browser] = version;
          }
        }
      }
    }
  }
  return targets;
}
function pluginCssTargetLowering() {
  let resolvedConfig;
  return {
    name: "astro:css-target-lowering",
    enforce: "post",
    configResolved(config) {
      resolvedConfig = config;
    },
    async generateBundle(_outputOptions, bundle) {
      if (resolvedConfig.build.cssMinify) return;
      const cssTarget = resolvedConfig.build.cssTarget;
      if (!cssTarget) return;
      const targets = convertTargets(cssTarget);
      if (Object.keys(targets).length === 0) return;
      let lcssTransform;
      try {
        const requireFromVite = createRequire(import.meta.resolve("vite"));
        lcssTransform = requireFromVite("lightningcss").transform;
      } catch {
        return;
      }
      for (const [, asset] of Object.entries(bundle)) {
        if (asset.type !== "asset") continue;
        if (!asset.fileName.endsWith(".css")) continue;
        if (typeof asset.source !== "string") continue;
        try {
          const result = lcssTransform({
            ...resolvedConfig.css?.lightningcss,
            targets,
            cssModules: void 0,
            filename: asset.fileName,
            code: Buffer.from(asset.source),
            minify: false
          });
          asset.source = new TextDecoder().decode(result.code);
        } catch {
        }
      }
    }
  };
}
export {
  pluginCssTargetLowering
};
