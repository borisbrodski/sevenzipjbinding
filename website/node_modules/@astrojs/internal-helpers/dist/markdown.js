const defaultExcludeLanguages = ["math"];
const syntaxHighlightDefaults = {
  type: "shiki",
  excludeLangs: defaultExcludeLanguages
};
const markdownConfigDefaults = {
  syntaxHighlight: syntaxHighlightDefaults,
  shikiConfig: {
    langs: [],
    theme: "github-dark",
    themes: {},
    wrap: false,
    transformers: [],
    langAlias: {}
  },
  gfm: true,
  smartypants: true,
  remarkPlugins: [],
  rehypePlugins: [],
  remarkRehype: {}
};
export {
  defaultExcludeLanguages,
  markdownConfigDefaults,
  syntaxHighlightDefaults
};
