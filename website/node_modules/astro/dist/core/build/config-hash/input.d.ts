import type { AstroConfig } from '../../../types/public/config.js';
/**
 * Projection of {@link AstroConfig} limited to values that can change the bytes
 * or paths of a prerendered page. A change to any of these must invalidate the
 * incremental build cache: the per-route dependency hash only sees module
 * *source*, so it misses config baked into compiled output by the compiler
 * (`compressHTML`, `site`, `scopedStyleStrategy`), inlined via Vite `define`
 * (`base`, `build.assetsPrefix`), or serialized into virtual modules (`image`,
 * `env.schema`, `i18n`).
 *
 * Only serializable values are included. Function-valued config (integrations,
 * vite/remark/rehype plugins, image services) is omitted because it cannot be
 * serialized. `outDir`/`publicDir` are omitted too, since they move where files
 * are written without changing a page's bytes.
 *
 * The whole `vite` config cannot be hashed: it carries plugins and other
 * function-valued, per-run state that churns between builds and would sink the
 * cache hit rate. Instead a curated allowlist of serializable Vite options that
 * change emitted bytes or asset paths is included (e.g. `build.assetsInlineLimit`
 * flips an asset between an inlined data URI and a separate hashed file). This
 * allowlist is best-effort, so an obscure output-affecting Vite option outside
 * it will not invalidate the cache; `--force` is the escape hatch.
 *
 * The set of fields is asserted against the full config schema in
 * `test/units/build/config-hash.test.ts`, which fails when a new top-level
 * config key appears so it gets classified here or explicitly excluded.
 */
export declare function getConfigHashInput(config: AstroConfig): {
    site: string | undefined;
    base: string;
    trailingSlash: "never" | "ignore" | "always";
    output: "server" | "static";
    compressHTML: boolean | "jsx";
    scopedStyleStrategy: "where" | "class" | "attribute";
    build: {
        format: "file" | "directory" | "preserve";
        assets: string;
        assetsPrefix: string | ({
            fallback: string;
        } & Record<string, string>) | undefined;
        inlineStylesheets: "never" | "always" | "auto";
        redirects: boolean;
    };
    redirects: Record<string, string | {
        status: 301 | 302 | 303 | 307 | 308 | 300 | 304;
        destination: string;
    }>;
    i18n: {
        defaultLocale: string;
        locales: (string | {
            path: string;
            codes: [string, ...string[]];
        })[];
        routing: "manual" | {
            prefixDefaultLocale: boolean;
            redirectToDefaultLocale: boolean;
            fallbackType: "redirect" | "rewrite";
        };
        domains?: Record<string, string> | undefined;
        fallback?: Record<string, string> | undefined;
    } | undefined;
    image: {
        endpoint: {
            route: string;
            entrypoint?: string | undefined;
        };
        service: {
            entrypoint: string;
            config: Record<string, any>;
        };
        dangerouslyProcessSVG: boolean;
        domains: string[];
        remotePatterns: {
            protocol?: string | undefined;
            hostname?: string | undefined;
            port?: string | undefined;
            pathname?: string | undefined;
        }[];
        responsiveStyles: boolean;
        layout?: "fixed" | "constrained" | "full-width" | "none" | undefined;
        objectFit?: string | undefined;
        objectPosition?: string | undefined;
        breakpoints?: number[] | undefined;
    };
    markdown: {
        syntaxHighlight: false | "shiki" | "prism" | {
            type: "shiki" | "prism";
            excludeLangs?: string[] | undefined;
        };
        shikiConfig: {
            langs: (import("shiki").LanguageRegistration & import("../../config/schemas/base.js").ComplexifyUnionObj)[];
            langAlias: Record<string, string>;
            theme: "andromeeda" | "aurora-x" | "ayu-dark" | "ayu-light" | "ayu-mirage" | "catppuccin-frappe" | "catppuccin-latte" | "catppuccin-macchiato" | "catppuccin-mocha" | "dark-plus" | "dracula" | "dracula-soft" | "everforest-dark" | "everforest-light" | "github-dark" | "github-dark-default" | "github-dark-dimmed" | "github-dark-high-contrast" | "github-light" | "github-light-default" | "github-light-high-contrast" | "gruvbox-dark-hard" | "gruvbox-dark-medium" | "gruvbox-dark-soft" | "gruvbox-light-hard" | "gruvbox-light-medium" | "gruvbox-light-soft" | "horizon" | "horizon-bright" | "houston" | "kanagawa-dragon" | "kanagawa-lotus" | "kanagawa-wave" | "laserwave" | "light-plus" | "material-theme" | "material-theme-darker" | "material-theme-lighter" | "material-theme-ocean" | "material-theme-palenight" | "min-dark" | "min-light" | "monokai" | "night-owl" | "night-owl-light" | "nord" | "one-dark-pro" | "one-light" | "plastic" | "poimandres" | "red" | "rose-pine" | "rose-pine-dawn" | "rose-pine-moon" | "slack-dark" | "slack-ochin" | "snazzy-light" | "solarized-dark" | "solarized-light" | "synthwave-84" | "tokyo-night" | "vesper" | "vitesse-black" | "vitesse-dark" | "vitesse-light" | (NonNullable<(import("shiki").BundledTheme | "css-variables") | import("shiki").ThemeRegistration | import("shiki").ThemeRegistrationRaw | undefined> & import("../../config/schemas/base.js").ComplexifyUnionObj);
            themes: Record<string, "andromeeda" | "aurora-x" | "ayu-dark" | "ayu-light" | "ayu-mirage" | "catppuccin-frappe" | "catppuccin-latte" | "catppuccin-macchiato" | "catppuccin-mocha" | "dark-plus" | "dracula" | "dracula-soft" | "everforest-dark" | "everforest-light" | "github-dark" | "github-dark-default" | "github-dark-dimmed" | "github-dark-high-contrast" | "github-light" | "github-light-default" | "github-light-high-contrast" | "gruvbox-dark-hard" | "gruvbox-dark-medium" | "gruvbox-dark-soft" | "gruvbox-light-hard" | "gruvbox-light-medium" | "gruvbox-light-soft" | "horizon" | "horizon-bright" | "houston" | "kanagawa-dragon" | "kanagawa-lotus" | "kanagawa-wave" | "laserwave" | "light-plus" | "material-theme" | "material-theme-darker" | "material-theme-lighter" | "material-theme-ocean" | "material-theme-palenight" | "min-dark" | "min-light" | "monokai" | "night-owl" | "night-owl-light" | "nord" | "one-dark-pro" | "one-light" | "plastic" | "poimandres" | "red" | "rose-pine" | "rose-pine-dawn" | "rose-pine-moon" | "slack-dark" | "slack-ochin" | "snazzy-light" | "solarized-dark" | "solarized-light" | "synthwave-84" | "tokyo-night" | "vesper" | "vitesse-black" | "vitesse-dark" | "vitesse-light" | (NonNullable<(import("shiki").BundledTheme | "css-variables") | import("shiki").ThemeRegistration | import("shiki").ThemeRegistrationRaw | undefined> & import("../../config/schemas/base.js").ComplexifyUnionObj)>;
            wrap: boolean | null;
            transformers: (import("shiki").ShikiTransformer & import("../../config/schemas/base.js").ComplexifyUnionObj)[];
            defaultColor?: string | false | undefined;
        };
        remarkPlugins: (string | [string, any] | (import("@astrojs/markdown-remark").RemarkPlugin & import("../../config/schemas/base.js").ComplexifyUnionObj) | [import("@astrojs/markdown-remark").RemarkPlugin & import("../../config/schemas/base.js").ComplexifyUnionObj, any])[];
        rehypePlugins: (string | [string, any] | (import("@astrojs/markdown-remark").RehypePlugin & import("../../config/schemas/base.js").ComplexifyUnionObj) | [import("@astrojs/markdown-remark").RehypePlugin & import("../../config/schemas/base.js").ComplexifyUnionObj, any])[];
        remarkRehype: import("../../config/schemas/base.js").RemarkRehype;
        processor: {
            name: string;
            options: object;
            createRenderer: (shared: import("@astrojs/markdown-remark").AstroMarkdownOptions) => Promise<import("@astrojs/markdown-remark").MarkdownRenderer>;
            createMdxRenderer?: ((shared: import("@astrojs/markdown-remark").AstroMarkdownOptions, mdx: import("@astrojs/internal-helpers/markdown").MdxRendererOptions) => Promise<import("@astrojs/internal-helpers/markdown").MdxRenderer>) | undefined;
        };
        gfm?: boolean | undefined;
        smartypants?: false | import("../../config/schemas/base.js").Smartypants | undefined;
    };
    env: {
        schema: Record<string, ({
            context: "client";
            access: "public";
        } | {
            context: "server";
            access: "public";
        } | {
            context: "server";
            access: "secret";
        }) & ({
            type: "enum";
            values: string[];
            optional?: boolean | undefined;
            default?: string | undefined;
        } | {
            type: "string";
            optional?: boolean | undefined;
            default?: string | undefined;
            max?: number | undefined;
            min?: number | undefined;
            length?: number | undefined;
            url?: boolean | undefined;
            includes?: string | undefined;
            startsWith?: string | undefined;
            endsWith?: string | undefined;
        } | {
            type: "number";
            optional?: boolean | undefined;
            default?: number | undefined;
            gt?: number | undefined;
            min?: number | undefined;
            lt?: number | undefined;
            max?: number | undefined;
            int?: boolean | undefined;
        } | {
            type: "boolean";
            optional?: boolean | undefined;
            default?: boolean | undefined;
        })>;
    };
    security: {
        csp: boolean | {
            algorithm: "SHA-256" | "SHA-384" | "SHA-512";
            directives?: (`base-uri${string}` | `child-src${string}` | `connect-src${string}` | `default-src${string}` | `fenced-frame-src${string}` | `font-src${string}` | `form-action${string}` | `frame-ancestors${string}` | `frame-src${string}` | `img-src${string}` | `manifest-src${string}` | `media-src${string}` | `object-src${string}` | `referrer${string}` | `report-to${string}` | `report-uri${string}` | `require-trusted-types-for${string}` | `sandbox${string}` | `trusted-types${string}` | `upgrade-insecure-requests${string}` | `worker-src${string}`)[] | undefined;
            styleDirective?: {
                resources?: (string | {
                    resource: string;
                    kind: "default" | "attribute" | "element";
                })[] | undefined;
                hashes?: (`sha256-${string}` | `sha384-${string}` | `sha512-${string}` | {
                    hash: `sha256-${string}` | `sha384-${string}` | `sha512-${string}`;
                    kind: "default" | "attribute" | "element";
                })[] | undefined;
            } | undefined;
            scriptDirective?: {
                resources?: (string | {
                    resource: string;
                    kind: "default" | "attribute" | "element";
                })[] | undefined;
                hashes?: (`sha256-${string}` | `sha384-${string}` | `sha512-${string}` | {
                    hash: `sha256-${string}` | `sha384-${string}` | `sha512-${string}`;
                    kind: "default" | "attribute" | "element";
                })[] | undefined;
                strictDynamic?: boolean | undefined;
            } | undefined;
        };
    };
    prefetch: boolean | {
        prefetchAll?: boolean | undefined;
        defaultStrategy?: "tap" | "hover" | "viewport" | "load" | undefined;
    } | undefined;
    vite: {
        build: {
            assetsInlineLimit: number | ((filePath: string, content: Buffer) => boolean | undefined) | undefined;
            minify: boolean | "esbuild" | "oxc" | "terser" | undefined;
            cssMinify: boolean | "esbuild" | "lightningcss" | undefined;
            cssTarget: false | import("vite/types/internal/esbuildOptions.js").EsbuildTarget | undefined;
            target: false | import("vite/types/internal/esbuildOptions.js").EsbuildTarget | undefined;
        };
        css: import("vite").CSSOptions | undefined;
        json: import("vite").JsonOptions | undefined;
        define: Record<string, any> | undefined;
        oxc: false | import("vite").OxcOptions | undefined;
    };
    experimental: {
        clientPrerender: boolean;
    };
};
