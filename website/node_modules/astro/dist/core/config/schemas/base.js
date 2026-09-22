import { syntaxHighlightDefaults } from "@astrojs/internal-helpers/markdown";
import { satteri } from "@astrojs/markdown-satteri";
import { bundledThemes } from "shiki";
import * as z from "zod/v4";
import { FontFamilySchema } from "../../../assets/fonts/config.js";
import { SvgOptimizerSchema } from "../../../assets/svg/config.js";
import { EnvSchema } from "../../../env/schema.js";
import { CacheSchema, RouteRulesSchema } from "../../cache/config.js";
import {
  allowedDirectivesSchema,
  cspAlgorithmSchema,
  cspHashEntrySchema,
  cspResourceEntrySchema
} from "../../csp/config.js";
import { SessionSchema } from "../../session/config.js";
import { ASTRO_CONFIG_DEFAULTS } from "./defaults.js";
const highlighterTypesSchema = z.union([z.literal("shiki"), z.literal("prism")]).default(syntaxHighlightDefaults.type);
const quoteCharacterMapSchema = z.object({
  double: z.string(),
  single: z.string()
});
const smartypantsOptionsSchema = z.object({
  backticks: z.union([z.boolean(), z.literal("all")]).default(true),
  closingQuotes: quoteCharacterMapSchema.default({
    double: "\u201D",
    single: "\u2019"
  }),
  dashes: z.union([z.boolean(), z.literal("inverted"), z.literal("oldschool")]).default(true),
  ellipses: z.union([z.boolean(), z.literal("spaced"), z.literal("unspaced")]).default(true),
  openingQuotes: quoteCharacterMapSchema.default({
    double: "\u201C",
    single: "\u2018"
  }),
  quotes: z.boolean().default(true)
});
const AstroConfigSchema = z.object({
  root: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.root).transform((val) => new URL(val)),
  srcDir: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.srcDir).transform((val) => new URL(val)),
  publicDir: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.publicDir).transform((val) => new URL(val)),
  outDir: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.outDir).transform((val) => new URL(val)),
  cacheDir: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.cacheDir).transform((val) => new URL(val)),
  site: z.string().url().optional(),
  compressHTML: z.union([z.boolean(), z.literal("jsx")]).optional().default(ASTRO_CONFIG_DEFAULTS.compressHTML),
  base: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.base),
  trailingSlash: z.union([z.literal("always"), z.literal("never"), z.literal("ignore")]).optional().default(ASTRO_CONFIG_DEFAULTS.trailingSlash),
  output: z.union([z.literal("static"), z.literal("server"), z.literal("hybrid")]).optional().default("static").refine((val) => val !== "hybrid", {
    message: 'The `output: "hybrid"` option has been removed. Use `output: "static"` (the default) instead, which now behaves the same way.'
  }),
  scopedStyleStrategy: z.union([z.literal("where"), z.literal("class"), z.literal("attribute")]).optional().default("attribute"),
  adapter: z.object({ name: z.string(), hooks: z.object({}).loose().default({}) }).optional(),
  integrations: z.preprocess(
    // preprocess
    (val) => Array.isArray(val) ? val.flat(Number.POSITIVE_INFINITY).filter(Boolean) : val,
    // validate
    z.array(z.object({ name: z.string(), hooks: z.object({}).loose().default({}) }))
  ).optional().default(ASTRO_CONFIG_DEFAULTS.integrations),
  build: z.object({
    format: z.union([z.literal("file"), z.literal("directory"), z.literal("preserve")]).optional().default(ASTRO_CONFIG_DEFAULTS.build.format),
    client: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.build.client).transform((val) => new URL(val)),
    server: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.build.server).transform((val) => new URL(val)),
    assets: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.build.assets),
    assetsPrefix: z.string().optional().or(z.object({ fallback: z.string() }).and(z.record(z.string(), z.string()))).optional(),
    serverEntry: z.string().optional().default(ASTRO_CONFIG_DEFAULTS.build.serverEntry),
    redirects: z.boolean().optional().default(ASTRO_CONFIG_DEFAULTS.build.redirects),
    inlineStylesheets: z.enum(["always", "auto", "never"]).optional().default(ASTRO_CONFIG_DEFAULTS.build.inlineStylesheets),
    concurrency: z.number().min(1).optional().default(ASTRO_CONFIG_DEFAULTS.build.concurrency)
  }).prefault({}),
  server: z.preprocess(
    // preprocess
    // NOTE: Uses the "error" command here because this is overwritten by the
    // individualized schema parser with the correct command.
    (val) => typeof val === "function" ? val({ command: "error" }) : val,
    // validate
    z.object({
      open: z.union([z.string(), z.boolean()]).optional().default(ASTRO_CONFIG_DEFAULTS.server.open),
      host: z.union([z.string(), z.boolean()]).optional().default(ASTRO_CONFIG_DEFAULTS.server.host),
      port: z.number().optional().default(ASTRO_CONFIG_DEFAULTS.server.port),
      headers: z.custom().optional(),
      allowedHosts: z.union([z.array(z.string()), z.literal(true)]).optional().default(ASTRO_CONFIG_DEFAULTS.server.allowedHosts)
    })
  ).prefault({}),
  redirects: z.record(
    z.string(),
    z.union([
      z.string(),
      z.object({
        status: z.union([
          z.literal(300),
          z.literal(301),
          z.literal(302),
          z.literal(303),
          z.literal(304),
          z.literal(307),
          z.literal(308)
        ]),
        destination: z.string()
      })
    ])
  ).default(ASTRO_CONFIG_DEFAULTS.redirects),
  prefetch: z.union([
    z.boolean(),
    z.object({
      prefetchAll: z.boolean().optional(),
      defaultStrategy: z.enum(["tap", "hover", "viewport", "load"]).optional()
    })
  ]).optional(),
  image: z.object({
    endpoint: z.object({
      route: z.literal("/_image").or(z.string()).default(ASTRO_CONFIG_DEFAULTS.image.endpoint.route),
      entrypoint: z.string().optional()
    }).default(ASTRO_CONFIG_DEFAULTS.image.endpoint),
    service: z.object({
      entrypoint: z.union([z.literal("astro/assets/services/sharp"), z.string()]).default(ASTRO_CONFIG_DEFAULTS.image.service.entrypoint),
      config: z.record(z.string(), z.any()).default({})
    }).default(ASTRO_CONFIG_DEFAULTS.image.service),
    dangerouslyProcessSVG: z.boolean().default(ASTRO_CONFIG_DEFAULTS.image.dangerouslyProcessSVG),
    domains: z.array(z.string()).default([]),
    remotePatterns: z.array(
      z.object({
        protocol: z.string().optional(),
        hostname: z.string().optional(),
        port: z.string().optional(),
        pathname: z.string().optional()
      })
    ).default([]),
    layout: z.enum(["constrained", "fixed", "full-width", "none"]).optional(),
    objectFit: z.string().optional(),
    objectPosition: z.string().optional(),
    breakpoints: z.array(z.number()).optional(),
    responsiveStyles: z.boolean().default(ASTRO_CONFIG_DEFAULTS.image.responsiveStyles)
  }).prefault(ASTRO_CONFIG_DEFAULTS.image),
  devToolbar: z.object({
    enabled: z.boolean().default(ASTRO_CONFIG_DEFAULTS.devToolbar.enabled),
    placement: z.enum(["bottom-left", "bottom-center", "bottom-right"]).optional()
  }).default(ASTRO_CONFIG_DEFAULTS.devToolbar),
  markdown: z.object({
    syntaxHighlight: z.union([
      z.object({
        type: highlighterTypesSchema,
        excludeLangs: z.array(z.string()).optional()
      }).default(syntaxHighlightDefaults),
      highlighterTypesSchema,
      z.literal(false)
    ]).default(ASTRO_CONFIG_DEFAULTS.markdown.syntaxHighlight),
    shikiConfig: z.object({
      langs: z.custom().array().transform((langs) => {
        for (const lang of langs) {
          if (typeof lang === "object") {
            if (lang.id) {
              lang.name = lang.id;
            }
            if (lang.grammar) {
              Object.assign(lang, lang.grammar);
            }
          }
        }
        return langs;
      }).default([]),
      langAlias: z.record(z.string(), z.string()).optional().default(ASTRO_CONFIG_DEFAULTS.markdown.shikiConfig.langAlias),
      theme: z.enum(Object.keys(bundledThemes)).or(z.custom()).default(ASTRO_CONFIG_DEFAULTS.markdown.shikiConfig.theme),
      themes: z.record(
        z.string(),
        z.enum(Object.keys(bundledThemes)).or(z.custom())
      ).optional().default(ASTRO_CONFIG_DEFAULTS.markdown.shikiConfig.themes),
      defaultColor: z.union([z.literal("light"), z.literal("dark"), z.string(), z.literal(false)]).optional(),
      wrap: z.boolean().or(z.null()).default(ASTRO_CONFIG_DEFAULTS.markdown.shikiConfig.wrap),
      transformers: z.custom().array().default(ASTRO_CONFIG_DEFAULTS.markdown.shikiConfig.transformers)
    }).prefault({}),
    remarkPlugins: z.union([
      z.string(),
      z.tuple([z.string(), z.any()]),
      z.custom((data) => typeof data === "function"),
      z.tuple([z.custom((data) => typeof data === "function"), z.any()])
    ]).array().default(ASTRO_CONFIG_DEFAULTS.markdown.remarkPlugins),
    rehypePlugins: z.union([
      z.string(),
      z.tuple([z.string(), z.any()]),
      z.custom((data) => typeof data === "function"),
      z.tuple([z.custom((data) => typeof data === "function"), z.any()])
    ]).array().default(ASTRO_CONFIG_DEFAULTS.markdown.rehypePlugins),
    remarkRehype: z.custom((data) => data instanceof Object && !Array.isArray(data)).default(ASTRO_CONFIG_DEFAULTS.markdown.remarkRehype),
    // Deprecated: left undefined unless the user explicitly sets them, so the
    // deprecation warning only fires when actually used. The active processor
    // (`satteri()`) supplies the real default (`gfm`/smart punctuation on) when
    // these are absent.
    gfm: z.boolean().optional(),
    smartypants: z.union([z.boolean(), smartypantsOptionsSchema]).transform((val) => {
      if (val === true) return smartypantsOptionsSchema.parse({});
      return val;
    }).optional(),
    processor: z.object({
      name: z.string(),
      // `z.custom` preserves reference identity; `z.record` would clone, breaking
      // the closure inside `createRenderer` that reads `processor.options.*`.
      options: z.custom((v) => typeof v === "object" && v !== null && !Array.isArray(v)).default(() => ({})),
      createRenderer: z.custom(
        (v) => typeof v === "function"
      ),
      createMdxRenderer: z.custom(
        (v) => v === void 0 || typeof v === "function"
      ).optional()
    }).default(() => satteri())
  }).prefault({}),
  vite: z.custom((data) => data instanceof Object && !Array.isArray(data)).default(ASTRO_CONFIG_DEFAULTS.vite),
  i18n: z.optional(
    z.object({
      defaultLocale: z.string(),
      locales: z.array(
        z.union([
          z.string(),
          z.object({
            path: z.string(),
            codes: z.tuple([z.string()], z.string())
          })
        ])
      ),
      domains: z.record(
        z.string(),
        z.string().url(
          "The domain value must be a valid URL, and it has to start with 'https' or 'http'."
        )
      ).optional(),
      fallback: z.record(z.string(), z.string()).optional(),
      routing: z.literal("manual").or(
        z.object({
          prefixDefaultLocale: z.boolean().optional().default(false),
          redirectToDefaultLocale: z.boolean().optional().default(false),
          fallbackType: z.enum(["redirect", "rewrite"]).optional().default("redirect")
        })
      ).optional().prefault({})
    }).optional()
  ),
  security: z.object({
    checkOrigin: z.boolean().default(ASTRO_CONFIG_DEFAULTS.security.checkOrigin),
    allowedDomains: z.array(
      z.object({
        hostname: z.string().optional(),
        protocol: z.string().optional(),
        port: z.string().optional()
      })
    ).optional().default(ASTRO_CONFIG_DEFAULTS.security.allowedDomains),
    actionBodySizeLimit: z.number().optional().default(ASTRO_CONFIG_DEFAULTS.security.actionBodySizeLimit),
    serverIslandBodySizeLimit: z.number().optional().default(ASTRO_CONFIG_DEFAULTS.security.serverIslandBodySizeLimit),
    csp: z.union([
      z.boolean().optional().default(ASTRO_CONFIG_DEFAULTS.security.csp),
      z.object({
        algorithm: cspAlgorithmSchema,
        directives: z.array(allowedDirectivesSchema).optional(),
        styleDirective: z.object({
          resources: z.array(cspResourceEntrySchema).optional(),
          hashes: z.array(cspHashEntrySchema).optional()
        }).optional(),
        scriptDirective: z.object({
          resources: z.array(cspResourceEntrySchema).optional(),
          hashes: z.array(cspHashEntrySchema).optional(),
          strictDynamic: z.boolean().optional()
        }).optional()
      })
    ]).optional().default(ASTRO_CONFIG_DEFAULTS.security.csp)
  }).optional().default(ASTRO_CONFIG_DEFAULTS.security),
  env: z.object({
    schema: EnvSchema.optional().default(ASTRO_CONFIG_DEFAULTS.env.schema),
    validateSecrets: z.boolean().optional().default(ASTRO_CONFIG_DEFAULTS.env.validateSecrets)
  }).strict().optional().default(ASTRO_CONFIG_DEFAULTS.env),
  session: SessionSchema.optional(),
  prerenderConflictBehavior: z.enum(["error", "warn", "ignore"]).optional().default(ASTRO_CONFIG_DEFAULTS.prerenderConflictBehavior),
  fetchFile: z.string().nullable().optional().default(ASTRO_CONFIG_DEFAULTS.fetchFile),
  logger: z.object({
    entrypoint: z.union([z.string(), z.instanceof(URL)]),
    config: z.record(z.string(), z.any()).optional()
  }).optional(),
  fonts: z.array(FontFamilySchema).optional(),
  cache: CacheSchema.optional(),
  routeRules: RouteRulesSchema.optional(),
  experimental: z.strictObject({
    clientPrerender: z.boolean().optional().default(ASTRO_CONFIG_DEFAULTS.experimental.clientPrerender),
    contentIntellisense: z.boolean().optional().default(ASTRO_CONFIG_DEFAULTS.experimental.contentIntellisense),
    chromeDevtoolsWorkspace: z.boolean().optional().default(ASTRO_CONFIG_DEFAULTS.experimental.chromeDevtoolsWorkspace),
    incrementalBuild: z.boolean().optional().default(ASTRO_CONFIG_DEFAULTS.experimental.incrementalBuild),
    svgOptimizer: SvgOptimizerSchema.optional(),
    collectionStorage: z.union([
      z.literal("single-file"),
      z.literal("chunked"),
      z.strictObject({
        type: z.literal("chunked"),
        chunkSize: z.number().int().positive()
      })
    ]).optional().default(ASTRO_CONFIG_DEFAULTS.experimental.collectionStorage)
  }).prefault({}),
  legacy: z.object({
    collectionsBackwardsCompat: z.boolean().optional().default(false)
  }).prefault({})
});
export {
  ASTRO_CONFIG_DEFAULTS,
  AstroConfigSchema
};
