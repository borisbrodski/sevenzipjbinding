import { markHTMLString } from "../escape.js";
import { renderCspContent } from "./csp.js";
import { createRenderInstruction } from "./instruction.js";
import { renderElement } from "./util.js";
function stablePropsKey(props) {
  const keys = Object.keys(props).sort();
  let result = "{";
  for (let i = 0; i < keys.length; i++) {
    if (i > 0) result += ",";
    result += JSON.stringify(keys[i]) + ":" + JSON.stringify(props[keys[i]]);
  }
  result += "}";
  return result;
}
function deduplicateElements(elements) {
  if (elements.length <= 1) return elements;
  const seen = /* @__PURE__ */ new Set();
  return elements.filter((item) => {
    const key = stablePropsKey(item.props) + item.children;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}
function renderAllHeadContent(result) {
  result._metadata.hasRenderedHead = true;
  let content = "";
  if (result.shouldInjectCspMetaTags && result.cspDestination === "meta") {
    content += renderElement(
      "meta",
      {
        props: {
          "http-equiv": "content-security-policy",
          content: renderCspContent(result)
        },
        children: ""
      },
      false
    );
  }
  const styles = deduplicateElements(Array.from(result.styles)).map(
    (style) => style.props.rel === "stylesheet" ? renderElement("link", style) : renderElement("style", style)
  );
  result.styles.clear();
  const scripts = deduplicateElements(Array.from(result.scripts)).map((script) => {
    if (result.userAssetsBase) {
      script.props.src = (result.base === "/" ? "" : result.base) + result.userAssetsBase + script.props.src;
    }
    return renderElement("script", script, false);
  });
  const links = deduplicateElements(Array.from(result.links)).map(
    (link) => renderElement("link", link, false)
  );
  const sep = result.compressHTML === true || result.compressHTML === "jsx" ? "" : "\n";
  content += styles.join(sep) + links.join(sep) + scripts.join(sep);
  if (result.speculationRulesContent) {
    content += renderElement(
      "script",
      {
        props: { type: "speculationrules" },
        children: result.speculationRulesContent
      },
      false
    );
  }
  content += result._metadata.extraHead.join("");
  return markHTMLString(content);
}
const HEAD_INSTRUCTION = Object.freeze(
  createRenderInstruction({ type: "head" })
);
const MAYBE_HEAD_INSTRUCTION = Object.freeze(
  createRenderInstruction({ type: "maybe-head" })
);
function renderHead() {
  return HEAD_INSTRUCTION;
}
function maybeRenderHead() {
  return MAYBE_HEAD_INSTRUCTION;
}
export {
  maybeRenderHead,
  renderAllHeadContent,
  renderHead
};
