import { clsx } from "clsx";
import { HTMLString, markHTMLString, stringifyForScript } from "../escape.js";
import { isPromise } from "../util.js";
const voidElementNames = /^(area|base|br|col|command|embed|hr|img|input|keygen|link|meta|param|source|track|wbr)$/i;
const htmlBooleanAttributes = /^(?:allowfullscreen|async|autofocus|autoplay|checked|controls|default|defer|disabled|disablepictureinpicture|disableremoteplayback|formnovalidate|inert|loop|muted|nomodule|novalidate|open|playsinline|readonly|required|reversed|scoped|seamless|selected|itemscope)$/i;
const AMPERSAND_REGEX = /&/g;
const DOUBLE_QUOTE_REGEX = /"/g;
const STATIC_DIRECTIVES = /* @__PURE__ */ new Set(["set:html", "set:text"]);
const INVALID_ATTR_NAME_CHAR = /[\s"'>/=]/;
const toIdent = (k) => k.trim().replace(/(?!^)\b\w|\s+|\W+/g, (match, index) => {
  if (/\W/.test(match)) return "";
  return index === 0 ? match : match.toUpperCase();
});
const toAttributeString = (value, shouldEscape = true) => {
  if (!shouldEscape) return value;
  const str = String(value);
  if (!str.includes("&") && !str.includes('"')) return str;
  return str.replace(AMPERSAND_REGEX, "&amp;").replace(DOUBLE_QUOTE_REGEX, "&quot;");
};
const kebab = (k) => k.toLowerCase() === k ? k : k.replace(/[A-Z]/g, (match) => `-${match.toLowerCase()}`);
const toStyleString = (obj) => {
  let output = "";
  for (const key of Object.keys(obj)) {
    const value = obj[key];
    if (!(typeof value === "string" && value.trim() || typeof value === "number")) continue;
    if (output) output += ";";
    output += key[0] !== "-" && key[1] !== "-" ? `${kebab(key)}:${value}` : `${key}:${value}`;
  }
  return output;
};
function defineScriptVars(vars) {
  let output = "";
  for (const [key, value] of Object.entries(vars)) {
    output += `const ${toIdent(key)} = ${stringifyForScript(value)};
`;
  }
  return markHTMLString(output);
}
function formatList(values) {
  if (values.length === 1) {
    return values[0];
  }
  return `${values.slice(0, -1).join(", ")} or ${values[values.length - 1]}`;
}
function isCustomElement(tagName) {
  return tagName.includes("-");
}
function handleBooleanAttribute(key, value, shouldEscape, tagName) {
  if (key === "popover") {
    return markHTMLString(value ? ` ${key}` : "");
  }
  if (tagName && isCustomElement(tagName)) {
    return markHTMLString(` ${key}="${toAttributeString(value, shouldEscape)}"`);
  }
  return markHTMLString(value ? ` ${key}` : "");
}
const ATTRIBUTE_KIND = {
  ORDINARY: 0,
  INVALID_NAME: 1,
  STATIC_DIRECTIVE: 2,
  CLASS_LIST: 3,
  STYLE: 4,
  CLASS_NAME: 5,
  BOOLEAN: 6,
  BOOLEAN_IF_BOOLEAN: 7
};
function classifyAttribute(key) {
  if (INVALID_ATTR_NAME_CHAR.test(key)) return ATTRIBUTE_KIND.INVALID_NAME;
  if (STATIC_DIRECTIVES.has(key)) return ATTRIBUTE_KIND.STATIC_DIRECTIVE;
  if (key === "class:list") return ATTRIBUTE_KIND.CLASS_LIST;
  if (key === "style") return ATTRIBUTE_KIND.STYLE;
  if (key === "className") return ATTRIBUTE_KIND.CLASS_NAME;
  if (htmlBooleanAttributes.test(key)) return ATTRIBUTE_KIND.BOOLEAN;
  if (key === "popover" || key === "download" || key === "hidden") {
    return ATTRIBUTE_KIND.BOOLEAN_IF_BOOLEAN;
  }
  return ATTRIBUTE_KIND.ORDINARY;
}
function addAttribute(value, key, shouldEscape = true, tagName = "") {
  if (value == null) {
    return "";
  }
  const kind = classifyAttribute(key);
  switch (kind) {
    // Reject attribute names with characters that could break out of the attribute context.
    case ATTRIBUTE_KIND.INVALID_NAME:
      return "";
    // compiler directives cannot be applied dynamically, log a warning and ignore.
    case ATTRIBUTE_KIND.STATIC_DIRECTIVE:
      console.warn(`[astro] The "${key}" directive cannot be applied dynamically at runtime. It will not be rendered as an attribute.

Make sure to use the static attribute syntax (\`${key}={value}\`) instead of the dynamic spread syntax (\`{...{ "${key}": value }}\`).`);
      return "";
    // support "class" from an expression passed into an element (#782)
    case ATTRIBUTE_KIND.CLASS_LIST: {
      const listValue = toAttributeString(clsx(value), shouldEscape);
      if (listValue === "") {
        return "";
      }
      return markHTMLString(` class="${listValue}"`);
    }
    // support object styles for better JSX compat
    case ATTRIBUTE_KIND.STYLE:
      if (!(value instanceof HTMLString)) {
        if (Array.isArray(value) && value.length === 2) {
          return markHTMLString(
            ` style="${toAttributeString(`${toStyleString(value[0])};${value[1]}`, shouldEscape)}"`
          );
        }
        if (typeof value === "object") {
          return markHTMLString(
            ` style="${toAttributeString(toStyleString(value), shouldEscape)}"`
          );
        }
      }
      break;
    // support `className` for better JSX compat
    case ATTRIBUTE_KIND.CLASS_NAME:
      return markHTMLString(` class="${toAttributeString(value, shouldEscape)}"`);
    // Boolean values only need the key
    case ATTRIBUTE_KIND.BOOLEAN:
      return handleBooleanAttribute(key, value, shouldEscape, tagName);
    case ATTRIBUTE_KIND.BOOLEAN_IF_BOOLEAN:
      if (typeof value === "boolean") {
        return handleBooleanAttribute(key, value, shouldEscape, tagName);
      }
      break;
    case ATTRIBUTE_KIND.ORDINARY:
      break;
    default:
      kind;
  }
  if (value === "") {
    return markHTMLString(` ${key}`);
  }
  return markHTMLString(` ${key}="${toAttributeString(value, shouldEscape)}"`);
}
function spreadElementAttributes(values) {
  let output = "";
  for (const key of Object.keys(values)) {
    if (key === "children") continue;
    output += addAttribute(values[key], key, true);
  }
  return output;
}
function internalSpreadAttributes(values, shouldEscape = true, tagName) {
  let output = "";
  for (const key of Object.keys(values)) {
    output += addAttribute(values[key], key, shouldEscape, tagName);
  }
  return markHTMLString(output);
}
function renderElement(name, { props: _props, children = "" }, shouldEscape = true) {
  const { lang: _, "data-astro-id": astroId, "define:vars": defineVars, ...props } = _props;
  if (defineVars) {
    if (name === "style") {
      delete props["is:global"];
      delete props["is:scoped"];
    }
    if (name === "script") {
      delete props.hoist;
      children = defineScriptVars(defineVars) + "\n" + children;
    }
  }
  if ((children == null || children === "") && voidElementNames.test(name)) {
    return `<${name}${internalSpreadAttributes(props, shouldEscape, name)}>`;
  }
  return `<${name}${internalSpreadAttributes(props, shouldEscape, name)}>${children}</${name}>`;
}
const noop = () => {
};
class BufferedRenderer {
  chunks = [];
  renderPromise;
  destination;
  /**
   * Determines whether buffer has been flushed
   * to the final destination.
   */
  flushed = false;
  constructor(destination, renderFunction) {
    this.destination = destination;
    this.renderPromise = renderFunction(this);
    if (isPromise(this.renderPromise)) {
      Promise.resolve(this.renderPromise).catch(noop);
    }
  }
  write(chunk) {
    if (this.flushed) {
      this.destination.write(chunk);
    } else {
      this.chunks.push(chunk);
    }
  }
  flush() {
    if (this.flushed) {
      throw new Error("The render buffer has already been flushed.");
    }
    this.flushed = true;
    for (const chunk of this.chunks) {
      this.destination.write(chunk);
    }
    return this.renderPromise;
  }
}
function createBufferedRenderer(destination, renderFunction) {
  return new BufferedRenderer(destination, renderFunction);
}
const isNode = typeof process !== "undefined" && Object.prototype.toString.call(process) === "[object process]";
const isDeno = typeof Deno !== "undefined";
const isWorkerd = typeof navigator !== "undefined" && navigator.userAgent === "Cloudflare-Workers";
function promiseWithResolvers() {
  let resolve, reject;
  const promise = new Promise((_resolve, _reject) => {
    resolve = _resolve;
    reject = _reject;
  });
  return {
    promise,
    resolve,
    reject
  };
}
export {
  INVALID_ATTR_NAME_CHAR,
  addAttribute,
  createBufferedRenderer,
  defineScriptVars,
  formatList,
  internalSpreadAttributes,
  isDeno,
  isNode,
  isWorkerd,
  promiseWithResolvers,
  renderElement,
  spreadElementAttributes,
  toAttributeString,
  toStyleString,
  voidElementNames
};
