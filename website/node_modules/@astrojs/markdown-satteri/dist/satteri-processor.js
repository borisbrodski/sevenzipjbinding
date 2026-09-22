import { isRemoteAllowed } from "@astrojs/internal-helpers/remote";
import {
  defaultExcludeLanguages,
  markdownConfigDefaults,
  syntaxHighlightDefaults
} from "@astrojs/internal-helpers/markdown";
import Slugger from "github-slugger";
import { createShikiHighlighter } from "@astrojs/internal-helpers/shiki";
let satteri;
async function loadSatteri() {
  satteri ??= await import("satteri");
  return satteri;
}
function createCollectImagesPlugin(image = {}) {
  const domains = image?.domains ?? [];
  const remotePatterns = image?.remotePatterns ?? [];
  return {
    name: "collect-images",
    image(node, ctx) {
      const url = node.url ? decodeURI(node.url) : void 0;
      if (!url) return;
      const astro = ctx.data.astro;
      if (URL.canParse(url)) {
        if (isRemoteAllowed(url, { domains, remotePatterns })) {
          astro?.remoteImagePaths.add(url);
        }
      } else if (!url.startsWith("/")) {
        astro?.localImagePaths.add(url);
      }
    }
  };
}
function resolveFrontmatterExpression(expr, frontmatter) {
  if (!expr.startsWith("frontmatter.") && !expr.startsWith("frontmatter[")) return void 0;
  const pathStr = expr.slice("frontmatter".length);
  const parts = [];
  const pathRegex = /\.(\w+)|\[(\d+)\]|\["([^"]+)"\]|\['([^']+)'\]/g;
  let match;
  while ((match = pathRegex.exec(pathStr)) !== null) {
    parts.push(match[1] ?? match[2] ?? match[3] ?? match[4]);
  }
  if (parts.length === 0) return void 0;
  let value = frontmatter;
  for (const key of parts) {
    if (value == null || typeof value !== "object") return void 0;
    value = value[key];
  }
  return typeof value === "string" ? value : void 0;
}
function collectHastText(node, frontmatter) {
  let text = "";
  if (node.type === "mdxFlowExpression" || node.type === "mdxTextExpression") {
    if (node.value != null && frontmatter) {
      const resolved = resolveFrontmatterExpression(node.value.trim(), frontmatter);
      text += resolved ?? node.value;
    }
  } else if ("value" in node && node.value != null) {
    text += node.value;
  }
  if ("children" in node && node.children) {
    for (const child of node.children) {
      text += collectHastText(child, frontmatter);
    }
  }
  return text;
}
function createHeadingIdsPlugin() {
  const slugger = new Slugger();
  const headings = [];
  return {
    name: "heading-ids",
    element: {
      filter: ["h1", "h2", "h3", "h4", "h5", "h6"],
      visit(node, ctx) {
        const astro = ctx.data.astro;
        const rawText = ctx.textContent(node);
        const text = rawText.includes("frontmatter") ? collectHastText(node, astro?.frontmatter ?? {}) : rawText;
        const existingId = node.properties?.id;
        const slug = typeof existingId === "string" ? existingId : slugger.slug(text);
        const depth = Number.parseInt(node.tagName[1], 10);
        headings.push({ depth, slug, text });
        if (astro) astro.headings = headings;
        if (typeof existingId !== "string") {
          ctx.setProperty(node, "id", slug);
        }
      }
    }
  };
}
const HAST_PRESERVED_PROPERTIES = /* @__PURE__ */ new Set(["className", "htmlFor"]);
function createImageMarkerPlugin() {
  const indexBySrc = /* @__PURE__ */ new Map();
  return {
    name: "image-marker",
    element: {
      filter: ["img"],
      visit(node, ctx) {
        const props = node.properties ?? {};
        const rawSrc = typeof props.src === "string" ? props.src : void 0;
        if (!rawSrc) return;
        const src = decodeURI(rawSrc);
        const astro = ctx.data.astro;
        const isLocal = astro?.localImagePaths.has(src) ?? false;
        const isRemote = !isLocal && (astro?.remoteImagePaths.has(src) ?? false);
        if (!isLocal && !isRemote) return;
        const { src: _src, ...rest } = props;
        const index = indexBySrc.get(rawSrc) ?? 0;
        indexBySrc.set(rawSrc, index + 1);
        const imageProperties = { ...rest, src, index };
        if (isRemote && !("width" in props) && !("height" in props)) {
          imageProperties.inferSize = true;
        }
        ctx.setProperty(node, "__ASTRO_IMAGE_", JSON.stringify(imageProperties));
        for (const key of Object.keys(rest)) {
          if (!HAST_PRESERVED_PROPERTIES.has(key)) {
            ctx.setProperty(node, key, null);
          }
        }
        ctx.setProperty(node, "src", null);
      }
    }
  };
}
function createHighlightPlugin(highlight, excludeLangs) {
  return {
    name: "highlight",
    element: {
      filter: ["pre"],
      visit(node, ctx) {
        const codeChild = node.children?.find(
          (c) => c.type === "element" && c.tagName === "code"
        );
        if (!codeChild || codeChild.type !== "element") return;
        const lang = codeChild.data?.lang ?? "plaintext";
        const meta = codeChild.data?.meta ?? void 0;
        if (excludeLangs && excludeLangs.includes(lang) || defaultExcludeLanguages.includes(lang)) {
          return;
        }
        const code = ctx.textContent(codeChild).replace(/\n$/, "");
        return highlight(code, lang, meta);
      }
    }
  };
}
async function createHighlightFn(syntaxHighlight, shikiConfig) {
  const syntaxHighlightType = typeof syntaxHighlight === "string" ? syntaxHighlight : syntaxHighlight ? syntaxHighlight.type : void 0;
  if (syntaxHighlightType === "shiki") {
    const hl = await createShikiHighlighter({
      langs: shikiConfig?.langs,
      theme: shikiConfig?.theme,
      themes: shikiConfig?.themes,
      langAlias: shikiConfig?.langAlias
    });
    return (code, lang, meta) => hl.codeToHast(code, lang, {
      meta,
      wrap: shikiConfig?.wrap,
      defaultColor: shikiConfig?.defaultColor,
      transformers: shikiConfig?.transformers
    }).then((root) => root.children[0]);
  }
  if (syntaxHighlightType === "prism") {
    const { runHighlighterWithAstro } = await import("@astrojs/prism/dist/highlighter");
    const { htmlToHast } = await loadSatteri();
    return async (code, lang) => {
      const { html, classLanguage } = await runHighlighterWithAstro(lang, code);
      const pre = `<pre class="${classLanguage}" data-language="${lang}"><code class="${classLanguage}">${html}</code></pre>`;
      const tree = htmlToHast(pre, { fragment: true });
      return "children" in tree ? tree.children[0] : void 0;
    };
  }
  return void 0;
}
async function createSatteriMarkdownProcessor(opts) {
  const s = await loadSatteri();
  const {
    syntaxHighlight = syntaxHighlightDefaults,
    shikiConfig = markdownConfigDefaults.shikiConfig,
    gfm = markdownConfigDefaults.gfm,
    smartypants = markdownConfigDefaults.smartypants,
    mdastPlugins: userMdastPlugins = [],
    hastPlugins: userHastPlugins = [],
    features: userFeatures
  } = opts ?? {};
  const highlightFn = await createHighlightFn(syntaxHighlight, shikiConfig);
  const syntaxHighlightExcludeLangs = typeof syntaxHighlight === "object" ? syntaxHighlight.excludeLangs : void 0;
  return {
    async render(content, renderOpts) {
      const astro = {
        frontmatter: renderOpts?.frontmatter ?? {},
        headings: [],
        localImagePaths: /* @__PURE__ */ new Set(),
        remoteImagePaths: /* @__PURE__ */ new Set()
      };
      const allMdastPlugins = [
        ...userMdastPlugins,
        createCollectImagesPlugin(opts?.image)
      ];
      const hastPlugins = [];
      if (highlightFn) {
        hastPlugins.push(createHighlightPlugin(highlightFn, syntaxHighlightExcludeLangs));
      }
      hastPlugins.push(...userHastPlugins);
      hastPlugins.push(createImageMarkerPlugin());
      hastPlugins.push(createHeadingIdsPlugin());
      const { html, data } = await s.markdownToHtml(content, {
        mdastPlugins: allMdastPlugins,
        hastPlugins,
        features: {
          gfm: gfm !== false,
          smartPunctuation: smartypants !== false,
          ...userFeatures
        },
        fileURL: renderOpts?.fileURL,
        data: { astro }
      });
      const result = data.astro;
      return {
        code: html,
        metadata: {
          headings: result?.headings ?? [],
          localImagePaths: result ? Array.from(result.localImagePaths) : [],
          remoteImagePaths: result ? Array.from(result.remoteImagePaths) : [],
          frontmatter: result?.frontmatter ?? {}
        }
      };
    }
  };
}
export {
  collectHastText,
  createCollectImagesPlugin,
  createHeadingIdsPlugin,
  createHighlightFn,
  createHighlightPlugin,
  createImageMarkerPlugin,
  createSatteriMarkdownProcessor
};
