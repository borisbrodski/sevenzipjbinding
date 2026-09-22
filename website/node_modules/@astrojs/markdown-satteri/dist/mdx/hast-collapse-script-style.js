import {
  defineHastPlugin
} from "satteri";
function isScriptOrStyle(node) {
  if (!node) return false;
  if (node.type === "element") return node.tagName === "script" || node.tagName === "style";
  if (node.type === "mdxJsxFlowElement" || node.type === "mdxJsxTextElement") {
    return node.name === "script" || node.name === "style";
  }
  return false;
}
function hasSetDirective(node) {
  if (node.type === "element") {
    return Boolean(
      node.properties && ("set:html" in node.properties || "set:text" in node.properties)
    );
  }
  return node.attributes.some(
    (attr) => attr.type === "mdxJsxAttribute" && (attr.name === "set:html" || attr.name === "set:text")
  );
}
function literalExpressionValue(program) {
  const statement = program?.body[0];
  if (statement?.type !== "ExpressionStatement") return void 0;
  const expression = statement.expression;
  if (expression.type === "Literal" && typeof expression.value === "string") {
    return expression.value;
  }
  if (expression.type === "TemplateLiteral" && expression.expressions.length === 0) {
    return expression.quasis[0]?.value.cooked ?? void 0;
  }
  return void 0;
}
const literalizeScriptStyleExpression = defineHastPlugin({
  name: "literalize-script-style-expression",
  mdxFlowExpression: (node, ctx) => {
    if (!isScriptOrStyle(ctx.parent(node))) return;
    const value = literalExpressionValue(node.parseExpression());
    if (value === void 0) return;
    return { type: "text", value };
  },
  mdxTextExpression: (node, ctx) => {
    if (!isScriptOrStyle(ctx.parent(node))) return;
    const value = literalExpressionValue(node.parseExpression());
    if (value === void 0) return;
    return { type: "text", value };
  }
});
const collapseScriptStyleText = defineHastPlugin({
  name: "collapse-script-style-text",
  element: { filter: ["script", "style"], visit: collapseIfAllText },
  mdxJsxFlowElement: { filter: ["script", "style"], visit: collapseIfAllText },
  mdxJsxTextElement: { filter: ["script", "style"], visit: collapseIfAllText }
});
function collapseIfAllText(node, ctx) {
  if (node.children.length === 0 || hasSetDirective(node)) return;
  let value = "";
  for (const child of node.children) {
    if (child.type !== "text") return;
    value += child.value;
  }
  ctx.setProperty(node, "set:html", value);
  for (let i = node.children.length - 1; i >= 0; i--) {
    ctx.removeChildAt(node, i);
  }
}
export {
  collapseScriptStyleText,
  literalizeScriptStyleExpression
};
