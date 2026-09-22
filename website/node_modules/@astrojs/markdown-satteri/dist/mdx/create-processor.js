import { pathToFileURL } from "node:url";
import { isFrontmatterValid } from "@astrojs/internal-helpers/frontmatter";
import {
  ASTRO_IMAGE_IMPORT,
  createDefaultAstroMetadata,
  USES_ASTRO_IMAGE_FLAG
} from "@astrojs/internal-helpers/mdx";
import {
  mdxToJs
} from "satteri";
import {
  createCollectImagesPlugin,
  createHeadingIdsPlugin,
  createHighlightFn,
  createHighlightPlugin
} from "../satteri-processor.js";
import { shouldAddCharset } from "./charset.js";
import { createAstroMetadataPlugin } from "./hast-astro-metadata.js";
import {
  collapseScriptStyleText,
  literalizeScriptStyleExpression
} from "./hast-collapse-script-style.js";
import { createImageToComponentPlugin } from "./hast-images-to-component.js";
function createSatteriMdxProcessor(shared, mdx, satteriOptions) {
  let highlightFn;
  let initPromise;
  function initHighlighter() {
    initPromise = createHighlightFn(shared.syntaxHighlight, shared.shikiConfig).then((fn) => {
      highlightFn = fn;
    });
  }
  return {
    async process(content, filePath, frontmatter) {
      if (!highlightFn && !initPromise) {
        initHighlighter();
      }
      if (initPromise) await initPromise;
      const astroData = {
        frontmatter,
        headings: [],
        localImagePaths: /* @__PURE__ */ new Set(),
        remoteImagePaths: /* @__PURE__ */ new Set()
      };
      const collectImages = createCollectImagesPlugin();
      const headingIds = createHeadingIdsPlugin();
      const astroMeta = createAstroMetadataPlugin(filePath);
      const imageImportInfo = {
        importedImages: /* @__PURE__ */ new Map(),
        hasImages: false
      };
      const imageToComponent = createImageToComponentPlugin(imageImportInfo);
      const syntaxHighlight = shared.syntaxHighlight;
      const excludeLangs = typeof syntaxHighlight === "object" ? syntaxHighlight.excludeLangs : void 0;
      const allMdastPlugins = [...satteriOptions.mdastPlugins, collectImages];
      const hastPlugins = [];
      if (highlightFn) {
        hastPlugins.push(createHighlightPlugin(highlightFn, excludeLangs));
      }
      if (satteriOptions.hastPlugins.length) {
        hastPlugins.push(...satteriOptions.hastPlugins);
      }
      hastPlugins.push(
        imageToComponent,
        headingIds,
        astroMeta,
        literalizeScriptStyleExpression,
        collapseScriptStyleText
      );
      let optimizeStatic;
      if (mdx.optimize) {
        const ignoreElements = typeof mdx.optimize === "object" ? mdx.optimize.ignoreElementNames : void 0;
        optimizeStatic = {
          component: "Fragment",
          prop: "set:html",
          ...ignoreElements && { ignoreElements }
        };
      }
      const { gfm, smartPunctuation } = satteriOptions.features;
      const mdxResult = await mdxToJs(content, {
        mdastPlugins: allMdastPlugins,
        hastPlugins,
        optimizeStatic,
        // `shared` carries the more specific `mdx({ gfm })` and wins, but only over booleans.
        features: {
          ...satteriOptions.features,
          gfm: typeof gfm === "object" ? gfm : (shared.gfm ?? gfm ?? true) !== false,
          smartPunctuation: typeof smartPunctuation === "object" ? smartPunctuation : (shared.smartypants ?? smartPunctuation ?? true) !== false
        },
        fileURL: pathToFileURL(filePath),
        jsxImportSource: "astro",
        elementAttributeNameCase: "html",
        data: { astro: astroData }
      });
      let compiled = mdxResult.code;
      const astro = mdxResult.data.astro;
      const headings = astro?.headings ?? [];
      const astroMetadata = mdxResult.data.__astroMetadata ?? createDefaultAstroMetadata();
      const resolvedFrontmatter = astro?.frontmatter;
      if (!resolvedFrontmatter || !isFrontmatterValid(resolvedFrontmatter)) {
        throw new Error(
          "[MDX] A S\xE4tteri plugin attempted to inject invalid frontmatter. Ensure `ctx.data.astro.frontmatter` is a valid object that is not `null` or `undefined`."
        );
      }
      compiled = compiled.replace(/^export default MDXContent;\s*$/m, "");
      if (imageImportInfo.hasImages) {
        compiled += `
import { Image as ${ASTRO_IMAGE_IMPORT} } from "astro:assets";`;
        for (const [src, importName] of imageImportInfo.importedImages) {
          compiled += `
import ${importName} from ${JSON.stringify(src)};`;
        }
        compiled += `
export const ${USES_ASTRO_IMAGE_FLAG} = true;`;
      }
      compiled += `
export const frontmatter = ${JSON.stringify(resolvedFrontmatter)};`;
      compiled += `
export function getHeadings() { return ${JSON.stringify(headings)}; }`;
      if (resolvedFrontmatter.layout) {
        compiled = compiled.replace(/^function MDXContent\(/m, "function __OriginalMDXContent__(");
        compiled += `
import { jsx as __astro_layout_jsx__ } from 'astro/jsx-runtime';
import __astro_layout_component__ from ${JSON.stringify(resolvedFrontmatter.layout)};
export default function MDXContent(props) {
	const content = __OriginalMDXContent__(props);
	const { layout, ...frontmatterContent } = frontmatter;
	frontmatterContent.file = file;
	frontmatterContent.url = url;
	return __astro_layout_jsx__(__astro_layout_component__, {
		file,
		url,
		content: frontmatterContent,
		frontmatter: frontmatterContent,
		headings: getHeadings(),
		'server:root': true,
		children: content,
	});
}`;
      } else if (shouldAddCharset(content, filePath, mdx.srcDir)) {
        compiled = compiled.replace(/^function MDXContent\(/m, "function __OriginalMDXContent__(");
        compiled += `
import { jsx as __astro_charset_jsx__, jsxs as __astro_charset_jsxs__, Fragment as __astro_charset_Fragment__ } from 'astro/jsx-runtime';
export default function MDXContent(props) {
	return __astro_charset_jsxs__(__astro_charset_Fragment__, {
		children: [
			__astro_charset_jsx__('meta', { charset: 'utf-8' }),
			__OriginalMDXContent__(props),
		],
	});
}`;
      }
      return {
        code: compiled,
        map: null,
        astroMetadata
      };
    }
  };
}
export {
  createSatteriMdxProcessor
};
