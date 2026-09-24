// src/jsx-runtime/index.ts
import { TEXT_NODE } from "../index.js";
import { ELEMENT_NODE, Fragment, __unsafeRenderFn } from "../index.js";
function createVNode(type, { children, ...attributes }, key, __self, __source) {
  const vnode = {
    type: ELEMENT_NODE,
    name: typeof type === "function" ? type.name : type,
    attributes,
    children: (Array.isArray(children) ? children : [children]).map((child) => {
      if (typeof child === "string") {
        return {
          type: TEXT_NODE,
          value: child
        };
      }
      return child;
    }),
    parent: void 0,
    loc: []
  };
  if (typeof type === "function") {
    __unsafeRenderFn(vnode, type);
  }
  return vnode;
}
export {
  Fragment,
  createVNode as jsx,
  createVNode as jsxDEV,
  createVNode as jsxs
};
