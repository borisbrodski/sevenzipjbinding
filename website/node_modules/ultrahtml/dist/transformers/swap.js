// src/transformers/swap.ts
import { RenderFn } from "../index.js";
import { __unsafeRenderFn } from "../index.js";
import { querySelectorAll } from "../selector.js";
function swap(components = {}) {
  return (doc) => {
    for (const [selector, component] of Object.entries(components)) {
      for (const node of querySelectorAll(doc, selector)) {
        if (typeof component === "string") {
          node.name = component;
          if (RenderFn in node) {
            delete node[RenderFn];
          }
        } else if (typeof component === "function") {
          __unsafeRenderFn(node, component);
        }
      }
    }
    return doc;
  };
}
export {
  swap as default
};
