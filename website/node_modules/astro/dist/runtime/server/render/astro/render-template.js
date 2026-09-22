import { markHTMLString } from "../../escape.js";
import { isPromise } from "../../util.js";
import { renderChild } from "../any.js";
import { createBufferedRenderer } from "../util.js";
const renderTemplateResultSym = /* @__PURE__ */ Symbol.for("astro.renderTemplateResult");
const markedHtmlParts = /* @__PURE__ */ new WeakMap();
function markHtmlParts(htmlParts) {
  let marked = markedHtmlParts.get(htmlParts);
  if (marked === void 0) {
    marked = new Array(htmlParts.length);
    for (let i = 0; i < htmlParts.length; i++) {
      marked[i] = htmlParts[i] ? markHTMLString(htmlParts[i]) : htmlParts[i];
    }
    markedHtmlParts.set(htmlParts, marked);
  }
  return marked;
}
class RenderTemplateResult {
  [renderTemplateResultSym] = true;
  htmlParts;
  expressions;
  error;
  wrapped;
  constructor(htmlParts, expressions) {
    this.htmlParts = htmlParts;
    this.error = void 0;
    this.expressions = expressions;
  }
  catchExpressionError(index, expression) {
    if (this.wrapped?.has(index)) return expression;
    const wrapped = Promise.resolve(expression).catch((err) => {
      if (!this.error) {
        this.error = err;
        throw err;
      }
    });
    (this.wrapped ??= /* @__PURE__ */ new Set()).add(index);
    this.expressions[index] = wrapped;
    return wrapped;
  }
  render(destination) {
    const { expressions } = this;
    const htmlParts = markHtmlParts(this.htmlParts);
    for (let i = 0; i < htmlParts.length; i++) {
      const html = htmlParts[i];
      if (html) {
        destination.write(html);
      }
      if (i >= expressions.length) break;
      let exp = expressions[i];
      if (!(exp || exp === 0)) continue;
      if (isPromise(exp)) exp = this.catchExpressionError(i, exp);
      const result = renderChild(destination, exp);
      if (isPromise(result)) {
        const startIdx = i + 1;
        const remaining = expressions.length - startIdx;
        const flushers = new Array(remaining);
        for (let j = 0; j < remaining; j++) {
          let rExp = expressions[startIdx + j];
          if (isPromise(rExp)) rExp = this.catchExpressionError(startIdx + j, rExp);
          flushers[j] = createBufferedRenderer(destination, (bufferDestination) => {
            if (rExp || rExp === 0) {
              return renderChild(bufferDestination, rExp);
            }
          });
        }
        return result.then(() => {
          let k = 0;
          const iterate = () => {
            while (k < flushers.length) {
              const rHtml = htmlParts[startIdx + k];
              if (rHtml) {
                destination.write(rHtml);
              }
              const flushResult = flushers[k++].flush();
              if (isPromise(flushResult)) {
                return flushResult.then(iterate);
              }
            }
            const lastHtml = htmlParts[htmlParts.length - 1];
            if (lastHtml) {
              destination.write(lastHtml);
            }
          };
          return iterate();
        });
      }
    }
  }
}
function isRenderTemplateResult(obj) {
  return typeof obj === "object" && obj !== null && !!obj[renderTemplateResultSym];
}
function renderTemplate(htmlParts, ...expressions) {
  return new RenderTemplateResult(htmlParts, expressions);
}
export {
  RenderTemplateResult,
  isRenderTemplateResult,
  renderTemplate
};
