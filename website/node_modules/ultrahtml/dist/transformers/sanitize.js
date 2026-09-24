// src/transformers/sanitize.ts
import { ELEMENT_NODE, walkSync } from "../index.js";
function resolveSantizeOptions(sanitize2) {
  if (sanitize2 === void 0) {
    return {
      allowElements: [],
      dropElements: ["script"],
      allowComponents: false,
      allowCustomElements: false,
      allowComments: false
    };
  } else {
    const dropElements = /* @__PURE__ */ new Set([]);
    if (!sanitize2.allowElements?.includes("script")) {
      dropElements.add("script");
    }
    for (const dropElement of sanitize2.dropElements ?? []) {
      dropElements.add(dropElement);
    }
    return {
      allowComponents: false,
      allowCustomElements: false,
      allowComments: false,
      ...sanitize2,
      dropElements: Array.from(dropElements)
    };
  }
}
function getNodeKind(node) {
  if (node.name.includes("-")) return "custom-element";
  if (/[\_\$A-Z]/.test(node.name[0]) || node.name.includes("."))
    return "component";
  return "element";
}
function getAction(name, kind, sanitize2) {
  if (sanitize2.allowElements?.length > 0) {
    if (sanitize2.allowElements.includes(name)) return "allow";
  }
  if (sanitize2.blockElements?.length > 0) {
    if (sanitize2.blockElements.includes(name)) return "block";
  }
  if (sanitize2.dropElements?.length > 0) {
    if (sanitize2.dropElements.find((n) => n === name)) return "drop";
  }
  if (kind === "component" && !sanitize2.allowComponents) return "drop";
  if (kind === "custom-element" && !sanitize2.allowCustomElements) return "drop";
  if (sanitize2.unblockElements) {
    return sanitize2.unblockElements.some((n) => n === name) ? "allow" : "block";
  }
  return sanitize2.allowElements?.length > 0 ? "drop" : "allow";
}
function sanitizeAttributes(node, sanitize2) {
  const attrs = node.attributes;
  for (const key of Object.keys(node.attributes)) {
    if (sanitize2.allowAttributes?.[key] && sanitize2.allowAttributes?.[key].includes(node.name) || sanitize2.allowAttributes?.[key]?.includes("*")) {
      continue;
    }
    if (sanitize2.dropAttributes?.[key] && sanitize2.dropAttributes?.[key].includes(node.name) || sanitize2.dropAttributes?.[key]?.includes("*")) {
      delete attrs[key];
    }
  }
  return attrs;
}
function sanitizeElement(opts, node, parent) {
  const kind = getNodeKind(node);
  const { name } = node;
  const action = getAction(name, kind, opts);
  if (action === "drop")
    return () => {
      parent.children = parent.children.filter(
        (child) => child !== node
      );
    };
  if (action === "block")
    return () => {
      parent.children = parent.children.map((child) => child === node ? child.children : child).flat(1);
    };
  return () => {
    node.attributes = sanitizeAttributes(node, opts);
  };
}
function sanitize(opts) {
  const sanitize2 = resolveSantizeOptions(opts);
  return (doc) => {
    let actions = [];
    walkSync(doc, (node, parent) => {
      switch (node.type) {
        case ELEMENT_NODE: {
          actions.push(sanitizeElement(sanitize2, node, parent));
          return;
        }
        default:
          return;
      }
    });
    for (let i = actions.length - 1; i >= 0; i--) {
      actions[i]();
    }
    return doc;
  };
}
export {
  sanitize as default
};
