import { isVNode } from "../../../jsx-runtime/index.js";
import {
  escapeHTML,
  escapeStyleText,
  HTMLString,
  isHTMLString,
  markHTMLString
} from "../escape.js";
import { isPromise } from "../util.js";
import { renderJSX } from "../jsx.js";
import { renderChild } from "./any.js";
import { Fragment } from "./common.js";
import {
  createBufferedRenderer,
  spreadElementAttributes,
  voidElementNames
} from "./util.js";
import { isAstroComponentFactory } from "./astro/factory.js";
import { isHeadAndContent } from "./astro/head-and-content.js";
import { createAstroComponentInstance, isAstroComponentInstance } from "./astro/instance.js";
import { isRenderTemplateResult } from "./astro/render-template.js";
import { containsServerDirective, ServerIslandComponent } from "./server-islands.js";
import { SlotRenderInstance, SlotString } from "./slot.js";
const ClientOnlyPlaceholder = "astro-client-only";
class TemplateFrame {
  /** The RenderTemplateResult this frame walks. */
  templateResult;
  /** Resume position: the next `htmlParts`/`expressions` index to process. */
  cursor;
  constructor(templateResult) {
    this.templateResult = templateResult;
    this.cursor = 0;
  }
  storeCursor(index) {
    this.cursor = index;
  }
}
class StreamingRenderState {
  // Streaming-mode buffer (written directly to `destination`).
  pendingStaticOutput = "";
  // Buffered-tail mode (entered on the first async dynamic node).
  isBuffering = false;
  // Static text collected while in buffered-tail mode.
  bufferedStaticOutput = "";
  appendStatic(output) {
    if (!output) return;
    if (this.isBuffering) this.bufferedStaticOutput += output;
    else this.pendingStaticOutput += output;
  }
}
async function renderStreaming(root, result, destination) {
  const stack = [root];
  const openTagCache = /* @__PURE__ */ new Map();
  const closeTagCache = /* @__PURE__ */ new Map();
  const closeTagFor = (type) => {
    let tag = closeTagCache.get(type);
    if (tag === void 0) {
      tag = new HTMLString(`</${type}>`);
      closeTagCache.set(type, tag);
    }
    return tag;
  };
  const streamingState = new StreamingRenderState();
  let firstAsyncRender = null;
  const bufferedTail = [];
  const flushBufferedStaticOutput = () => {
    if (streamingState.bufferedStaticOutput) {
      bufferedTail.push(streamingState.bufferedStaticOutput);
      streamingState.bufferedStaticOutput = "";
    }
  };
  const renderDynamic = (node) => (d) => {
    if (isVNode(node)) {
      return renderJSX(result, node).then((out) => renderChild(d, out));
    }
    return renderChild(d, node);
  };
  const handleVNode = (vnode) => {
    const type = vnode.type;
    if (!type) {
      throw new Error(
        `Unable to render ${result.pathname} because it contains an undefined Component!
Did you forget to import the component or is it possible there is a typo?`
      );
    }
    if (type === Fragment) {
      stack.push(vnode.props?.children);
      return;
    }
    if (isAstroComponentFactory(type)) {
      const props = {};
      const slots = {};
      for (const [key, value] of Object.entries(vnode.props ?? {})) {
        if (key === "children" || value && typeof value === "object" && value["$$slot"]) {
          slots[key === "children" ? "default" : key] = () => renderJSX(result, value);
        } else {
          props[key] = value;
        }
      }
      const displayName = type.name || "Anonymous";
      if (containsServerDirective(props)) {
        const island = new ServerIslandComponent(result, props, slots, displayName);
        result._metadata.propagators.add(island);
        stack.push(island);
        return;
      }
      stack.push(createAstroComponentInstance(result, displayName, type, props, slots));
      return;
    }
    if (typeof type === "string" && type !== ClientOnlyPlaceholder) {
      const props = vnode.props;
      let hasAttrs = false;
      if (props) {
        for (const key in props) {
          if (key !== "children") {
            hasAttrs = true;
            break;
          }
        }
      }
      const children = props?.children;
      const isVoid = (children == null || children === "") && voidElementNames.test(type);
      if (!hasAttrs) {
        const key = isVoid ? `${type}/` : type;
        let openTag = openTagCache.get(key);
        if (openTag === void 0) {
          openTag = isVoid ? `<${type}/>` : `<${type}>`;
          openTagCache.set(key, openTag);
        }
        streamingState.appendStatic(openTag);
        if (!isVoid) {
          stack.push(closeTagFor(type));
        }
      } else {
        const attrs = spreadElementAttributes(props);
        if (isVoid) {
          streamingState.appendStatic(`<${type}${attrs}/>`);
          return;
        }
        streamingState.appendStatic(`<${type}${attrs}>`);
        stack.push(closeTagFor(type));
      }
      if (!isVoid && children != null && children !== "") {
        if (typeof children === "string" && type === "style") {
          stack.push(markHTMLString(escapeStyleText(children)));
        } else {
          stack.push(children);
        }
      }
      return;
    }
    if (typeof type === "function" && vnode.props?.["server:root"]) {
      stack.push(type(vnode.props ?? {}));
      return;
    }
    stack.push(renderJSX(result, vnode));
  };
  while (stack.length > 0) {
    const node = stack.pop();
    if (node == null || node === false) continue;
    if (node instanceof TemplateFrame) {
      const htmlParts = node.templateResult.htmlParts;
      const expressions = node.templateResult.expressions;
      let i = node.cursor;
      while (i < htmlParts.length) {
        if (htmlParts[i]) {
          streamingState.appendStatic(htmlParts[i]);
        }
        if (i >= expressions.length) {
          break;
        }
        const expression = expressions[i];
        i++;
        if (expression == null || expression === false) continue;
        const expressionType = typeof expression;
        if (expressionType === "string") {
          streamingState.appendStatic(escapeHTML(expression));
          continue;
        }
        if (expressionType === "number" || expressionType === "bigint" || expressionType === "boolean") {
          streamingState.appendStatic(String(expression));
          continue;
        }
        if (expression instanceof HTMLString || isHTMLString(expression)) {
          if (!(expression instanceof SlotString)) {
            streamingState.appendStatic(expression.toString());
            continue;
          }
        }
        node.storeCursor(i);
        stack.push(node);
        stack.push(
          isPromise(expression) ? node.templateResult.catchExpressionError(i - 1, expression) : expression
        );
        break;
      }
      continue;
    }
    const nodeType = typeof node;
    if (nodeType === "string") {
      streamingState.appendStatic(escapeHTML(node));
      continue;
    }
    if (nodeType === "number" || nodeType === "bigint" || nodeType === "boolean") {
      streamingState.appendStatic(String(node));
      continue;
    }
    if (node instanceof HTMLString || isHTMLString(node)) {
      if (!(node instanceof SlotString)) {
        streamingState.appendStatic(node.toString());
        continue;
      }
    }
    if (Array.isArray(node)) {
      for (let i = node.length - 1; i >= 0; i--) stack.push(node[i]);
      continue;
    }
    if (isRenderTemplateResult(node)) {
      stack.push(new TemplateFrame(node));
      continue;
    }
    if (isVNode(node)) {
      handleVNode(node);
      continue;
    }
    if (node instanceof SlotRenderInstance) {
      stack.push(node.evaluate());
      continue;
    }
    if (isAstroComponentInstance(node)) {
      const returnValue = node.init(result);
      if (!isPromise(returnValue)) {
        stack.push(isHeadAndContent(returnValue) ? returnValue.content : returnValue);
        continue;
      }
    }
    if (!streamingState.isBuffering) {
      if (streamingState.pendingStaticOutput) {
        destination.write(streamingState.pendingStaticOutput);
        streamingState.pendingStaticOutput = "";
      }
      const rendered = renderDynamic(node)(destination);
      if (isPromise(rendered)) {
        streamingState.isBuffering = true;
        firstAsyncRender = rendered;
      }
    } else {
      flushBufferedStaticOutput();
      bufferedTail.push(createBufferedRenderer(destination, renderDynamic(node)));
    }
  }
  if (!streamingState.isBuffering) {
    if (streamingState.pendingStaticOutput) {
      destination.write(streamingState.pendingStaticOutput);
    }
    return;
  }
  await firstAsyncRender;
  flushBufferedStaticOutput();
  for (const segment of bufferedTail) {
    if (typeof segment === "string") {
      destination.write(segment);
    } else {
      const flushResult = segment.flush();
      if (isPromise(flushResult)) await flushResult;
    }
  }
}
export {
  renderStreaming
};
