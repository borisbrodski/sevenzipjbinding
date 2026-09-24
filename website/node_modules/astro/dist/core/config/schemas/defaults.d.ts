export declare const ASTRO_CONFIG_DEFAULTS: {
    root: string;
    srcDir: string;
    publicDir: string;
    outDir: string;
    cacheDir: string;
    base: string;
    trailingSlash: "ignore";
    build: {
        format: "directory";
        client: string;
        server: string;
        assets: string;
        serverEntry: string;
        redirects: true;
        inlineStylesheets: "auto";
        concurrency: number;
    };
    image: {
        endpoint: {
            entrypoint: undefined;
            route: "/_image";
        };
        service: {
            entrypoint: "astro/assets/services/sharp";
            config: {};
        };
        dangerouslyProcessSVG: false;
        responsiveStyles: false;
    };
    devToolbar: {
        enabled: true;
    };
    compressHTML: "jsx";
    server: {
        host: false;
        port: number;
        open: false;
        allowedHosts: never[];
    };
    integrations: never[];
    markdown: Required<Omit<import("@astrojs/internal-helpers/markdown").AstroMarkdownOptions, "image">>;
    vite: {};
    legacy: {
        collectionsBackwardsCompat: false;
    };
    redirects: {};
    security: {
        checkOrigin: true;
        allowedDomains: never[];
        csp: false;
        actionBodySizeLimit: number;
        serverIslandBodySizeLimit: number;
    };
    env: {
        schema: {};
        validateSecrets: false;
    };
    prerenderConflictBehavior: "warn";
    fetchFile: string;
    experimental: {
        clientPrerender: false;
        contentIntellisense: false;
        chromeDevtoolsWorkspace: false;
        incrementalBuild: false;
        collectionStorage: "single-file";
    };
};
