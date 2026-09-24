import { errorMap } from "../errors/index.js";
import { AstroConfigRefinedSchema, createRelativeSchema } from "./schemas/index.js";
async function validateConfig(userConfig, root, cmd) {
  const AstroConfigRelativeSchema = createRelativeSchema(cmd, root);
  await coerceLegacyMarkdownPlugins(userConfig);
  warnDeprecatedMarkdownOptions(userConfig);
  return await validateConfigRefined(
    await AstroConfigRelativeSchema.parseAsync(userConfig, {
      error(issue) {
        if (issue.path?.[0] === "experimental") {
          return {
            message: `Invalid or outdated experimental feature.
Check for incorrect spelling or outdated Astro version.
See https://docs.astro.build/en/reference/experimental-flags/ for a list of all current experiments.`
          };
        }
        return errorMap(issue);
      }
    })
  );
}
let didWarnAboutDeprecatedMarkdownOptions = false;
function warnDeprecatedMarkdownOptions(config) {
  if (didWarnAboutDeprecatedMarkdownOptions) return;
  const md = config?.markdown;
  if (!md) return;
  const deprecated = ["gfm", "smartypants"].filter((k) => md[k] !== void 0);
  if (deprecated.length === 0) return;
  didWarnAboutDeprecatedMarkdownOptions = true;
  const names = deprecated.map((key) => `\`markdown.${key}\``).join(" and ");
  const isPlural = deprecated.length > 1;
  console.warn(
    `[astro] ${names} ${isPlural ? "are" : "is"} deprecated. Move ${isPlural ? "them" : "it"} onto your processor instead (e.g. \`satteri({ features: { gfm: false, smartPunctuation: false } })\`, or \`unified({ gfm: false, smartypants: false })\` from \`@astrojs/markdown-remark\`). Will be removed in a future major.`
  );
}
let didWarnAboutLegacyMarkdownPlugins = false;
let didWarnAboutProcessorMismatch = false;
const migratedLegacyPluginCounts = /* @__PURE__ */ new WeakMap();
async function coerceLegacyMarkdownPlugins(config) {
  const md = config?.markdown;
  if (!md) return;
  const remarkPlugins = md.remarkPlugins ?? [];
  const rehypePlugins = md.rehypePlugins ?? [];
  const remarkRehype = md.remarkRehype ?? {};
  if (remarkPlugins.length === 0 && rehypePlugins.length === 0 && Object.keys(remarkRehype).length === 0) {
    return;
  }
  let unified;
  let isUnifiedProcessor;
  try {
    ({ unified, isUnifiedProcessor } = await import("@astrojs/markdown-remark"));
  } catch {
    throw new Error(
      "`markdown.remarkPlugins`, `markdown.rehypePlugins`, and `markdown.remarkRehype` run on the `unified` processor from `@astrojs/markdown-remark`, which is no longer installed by default now that S\xE4tteri is the default Markdown processor. Install it with:\n  npm install @astrojs/markdown-remark"
    );
  }
  const current = md.processor;
  if (!current || isUnifiedProcessor(current)) {
    const target = current ?? (md.processor = unified());
    const counts = migratedLegacyPluginCounts.get(target.options) ?? { remark: 0, rehype: 0 };
    if (remarkPlugins.length > counts.remark) {
      target.options.remarkPlugins.push(...remarkPlugins.slice(counts.remark));
    }
    if (rehypePlugins.length > counts.rehype) {
      target.options.rehypePlugins.push(...rehypePlugins.slice(counts.rehype));
    }
    Object.assign(target.options.remarkRehype, remarkRehype);
    migratedLegacyPluginCounts.set(target.options, {
      remark: remarkPlugins.length,
      rehype: rehypePlugins.length
    });
    if (!didWarnAboutLegacyMarkdownPlugins) {
      didWarnAboutLegacyMarkdownPlugins = true;
      console.warn(
        "[astro] `markdown.remarkPlugins`, `markdown.rehypePlugins`, and `markdown.remarkRehype` are deprecated. Pass them to `unified({...})` from `@astrojs/markdown-remark` directly instead."
      );
    }
  } else if (!didWarnAboutProcessorMismatch) {
    didWarnAboutProcessorMismatch = true;
    console.warn(
      `[astro] \`markdown.remarkPlugins\`/\`rehypePlugins\`/\`remarkRehype\` are set, but your \`${current.name}\` processor doesn't run them. Move them to \`unified({...})\` from \`@astrojs/markdown-remark\` and set \`markdown.processor: unified({...})\` if you want them to apply.`
    );
  }
}
async function validateConfigRefined(updatedConfig) {
  await coerceLegacyMarkdownPlugins(updatedConfig);
  warnDeprecatedMarkdownOptions(updatedConfig);
  return await AstroConfigRefinedSchema.parseAsync(updatedConfig, { error: errorMap });
}
export {
  validateConfig,
  validateConfigRefined
};
