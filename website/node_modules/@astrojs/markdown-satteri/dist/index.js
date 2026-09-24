import {
  createCollectImagesPlugin,
  createHeadingIdsPlugin,
  createImageMarkerPlugin,
  createHighlightPlugin,
  createHighlightPlugin as createHighlightPlugin2,
  createHighlightFn,
  collectHastText,
  createSatteriMarkdownProcessor
} from "./satteri-processor.js";
import {
  isSatteriProcessor,
  satteri
} from "./processor.js";
export {
  createSatteriMarkdownProcessor,
  isSatteriProcessor,
  satteri,
  collectHastText as satteriCollectHastText,
  createCollectImagesPlugin as satteriCollectImagesPlugin,
  createHighlightFn as satteriCreateHighlightFn,
  createHeadingIdsPlugin as satteriHeadingIdsPlugin,
  createHighlightPlugin as satteriHighlightPlugin,
  createImageMarkerPlugin as satteriImageMarkerPlugin,
  createHighlightPlugin2 as satteriShikiPlugin
};
