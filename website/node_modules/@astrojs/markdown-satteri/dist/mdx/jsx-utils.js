const nonAlphaRe = /[^a-zA-Z]/;
function isComponent(tagName) {
  return tagName[0] && tagName[0].toLowerCase() !== tagName[0] || tagName.includes(".") || nonAlphaRe.test(tagName[0]);
}
function hasDirective(node, prefix) {
  for (const a of node.attributes) {
    if (a.type === "mdxJsxAttribute" && a.name.startsWith(prefix)) return true;
  }
  return false;
}
function findAttrValue(node, name) {
  for (const a of node.attributes) {
    if (a.type === "mdxJsxAttribute" && a.name === name) {
      return typeof a.value === "string" ? a.value : null;
    }
  }
  return null;
}
function makeJsxAttr(name, value) {
  return { type: "mdxJsxAttribute", name, value };
}
function makeJsxExprAttr(name, expression) {
  return {
    type: "mdxJsxAttribute",
    name,
    value: { type: "mdxJsxAttributeValueExpression", value: expression }
  };
}
export {
  findAttrValue,
  hasDirective,
  isComponent,
  makeJsxAttr,
  makeJsxExprAttr
};
