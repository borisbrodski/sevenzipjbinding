function getConfigHashInput(config) {
  return {
    site: config.site,
    base: config.base,
    trailingSlash: config.trailingSlash,
    output: config.output,
    compressHTML: config.compressHTML,
    scopedStyleStrategy: config.scopedStyleStrategy,
    build: {
      format: config.build.format,
      assets: config.build.assets,
      assetsPrefix: config.build.assetsPrefix,
      inlineStylesheets: config.build.inlineStylesheets,
      redirects: config.build.redirects
    },
    redirects: config.redirects,
    i18n: config.i18n,
    image: config.image,
    markdown: config.markdown,
    env: { schema: config.env?.schema },
    security: { csp: config.security?.csp },
    prefetch: config.prefetch,
    vite: {
      build: {
        assetsInlineLimit: config.vite.build?.assetsInlineLimit,
        minify: config.vite.build?.minify,
        cssMinify: config.vite.build?.cssMinify,
        cssTarget: config.vite.build?.cssTarget,
        target: config.vite.build?.target
      },
      css: config.vite.css,
      json: config.vite.json,
      define: config.vite.define,
      oxc: config.vite.oxc
    },
    experimental: {
      clientPrerender: config.experimental.clientPrerender
    }
  };
}
export {
  getConfigHashInput
};
