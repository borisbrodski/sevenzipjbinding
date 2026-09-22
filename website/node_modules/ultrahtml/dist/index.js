// src/index.ts
var DOCUMENT_NODE = 0;
var ELEMENT_NODE = 1;
var TEXT_NODE = 2;
var COMMENT_NODE = 3;
var DOCTYPE_NODE = 4;
function h(type, props = {}, ...children) {
  const vnode = {
    type: ELEMENT_NODE,
    name: typeof type === "function" ? type.name : type,
    attributes: props || {},
    children: children.map(
      (child) => typeof child === "string" ? { type: TEXT_NODE, value: escapeHTML(String(child)) } : child
    ),
    parent: void 0,
    loc: []
  };
  if (typeof type === "function") {
    __unsafeRenderFn(vnode, type);
  }
  return vnode;
}
var Fragment = Symbol("Fragment");
var VOID_TAGS = /* @__PURE__ */ new Set([
  "area",
  "base",
  "br",
  "col",
  "embed",
  "hr",
  "img",
  "input",
  "keygen",
  "link",
  "meta",
  "param",
  "source",
  "track",
  "wbr"
]);
var RAW_TAGS = /* @__PURE__ */ new Set(["script", "style"]);
var DOM_PARSER_RE = /(?:<(\/?)([a-zA-Z][a-zA-Z0-9\:-]*)(?:\s([^>]*?))?((?:\s*\/)?)>|(<\!\-\-)([\s\S]*?)(\-\->)|(<\!)([\s\S]*?)(>))/gm;
var CHAR_AT = 64;
var CHAR_DOT = 46;
var CHAR_HYPHEN = 45;
var CHAR_COLON = 58;
var CHAR_UNDERSCORE = 95;
var CHAR_EQUALS = 61;
var CHAR_DOUBLE_QUOTE = 34;
var CHAR_SINGLE_QUOTE = 39;
var CHAR_BACKSLASH = 92;
function isAttrKeyIdentifier(chr) {
  return chr >= 97 && chr <= 122 || // a-z
  chr >= 65 && chr <= 90 || // A-Z
  chr >= 48 && chr <= 57 || // 0-9
  chr === CHAR_AT || chr === CHAR_DOT || chr === CHAR_HYPHEN || chr === CHAR_COLON || chr === CHAR_UNDERSCORE;
}
function splitAttrs(str) {
  let obj = {};
  if (str) {
    let state = "none";
    let currentKey;
    let currentValue = "";
    let tokenStartIndex;
    for (let currentIndex = 0; currentIndex < str.length; currentIndex++) {
      const currentChar = str.charCodeAt(currentIndex);
      if (state === "none") {
        if (isAttrKeyIdentifier(currentChar)) {
          if (currentKey) {
            obj[currentKey] = currentValue;
            currentKey = void 0;
            currentValue = "";
          }
          tokenStartIndex = currentIndex;
          state = "key";
        } else if (currentChar === CHAR_EQUALS && currentKey) {
          state = "value";
        }
      } else if (state === "key") {
        if (!isAttrKeyIdentifier(currentChar)) {
          currentKey = str.substring(tokenStartIndex, currentIndex);
          if (currentChar === CHAR_EQUALS) {
            state = "value";
          } else {
            state = "none";
          }
        }
      } else if (currentChar === CHAR_DOUBLE_QUOTE || currentChar === CHAR_SINGLE_QUOTE) {
        const quote = currentChar === CHAR_DOUBLE_QUOTE ? '"' : "'";
        const valueStart = currentIndex + 1;
        let closeIndex = str.indexOf(quote, valueStart);
        while (closeIndex > 0 && str.charCodeAt(closeIndex - 1) === CHAR_BACKSLASH) {
          closeIndex = str.indexOf(quote, closeIndex + 1);
        }
        if (closeIndex === -1) {
          break;
        }
        currentValue = str.substring(valueStart, closeIndex);
        currentIndex = closeIndex;
        state = "none";
      }
    }
    if (state === "key" && tokenStartIndex != void 0 && tokenStartIndex < str.length) {
      currentKey = str.substring(tokenStartIndex, str.length);
    }
    if (currentKey) {
      obj[currentKey] = currentValue;
    }
  }
  return obj;
}
function parse(input) {
  let str = typeof input === "string" ? input : input.value;
  let doc, parent, token, text, i, bStart, bText, bEnd, tag;
  const tags = [];
  DOM_PARSER_RE.lastIndex = 0;
  parent = doc = {
    type: DOCUMENT_NODE,
    children: []
  };
  let lastIndex = 0;
  function commitTextNode() {
    text = str.substring(lastIndex, DOM_PARSER_RE.lastIndex - token[0].length);
    if (text) {
      parent.children.push({
        type: TEXT_NODE,
        value: text,
        parent
      });
    }
  }
  while (token = DOM_PARSER_RE.exec(str)) {
    bStart = token[5] || token[8];
    bText = token[6] || token[9];
    bEnd = token[7] || token[10];
    if (RAW_TAGS.has(parent.name) && token[2] !== parent.name) {
      i = DOM_PARSER_RE.lastIndex - token[0].length;
      if (parent.children.length > 0) {
        parent.children[0].value += token[0];
      }
      continue;
    } else if (bStart === "<!--") {
      i = DOM_PARSER_RE.lastIndex - token[0].length;
      if (RAW_TAGS.has(parent.name)) {
        continue;
      }
      tag = {
        type: COMMENT_NODE,
        value: bText,
        parent,
        loc: [
          {
            start: i,
            end: i + bStart.length
          },
          {
            start: DOM_PARSER_RE.lastIndex - bEnd.length,
            end: DOM_PARSER_RE.lastIndex
          }
        ]
      };
      tags.push(tag);
      tag.parent.children.push(tag);
    } else if (bStart === "<!") {
      i = DOM_PARSER_RE.lastIndex - token[0].length;
      tag = {
        type: DOCTYPE_NODE,
        value: bText,
        parent,
        loc: [
          {
            start: i,
            end: i + bStart.length
          },
          {
            start: DOM_PARSER_RE.lastIndex - bEnd.length,
            end: DOM_PARSER_RE.lastIndex
          }
        ]
      };
      tags.push(tag);
      tag.parent.children.push(tag);
    } else if (token[1] !== "/") {
      commitTextNode();
      if (RAW_TAGS.has(parent.name)) {
        lastIndex = DOM_PARSER_RE.lastIndex;
        commitTextNode();
        continue;
      } else {
        tag = {
          type: ELEMENT_NODE,
          name: token[2] + "",
          attributes: splitAttrs(token[3]),
          parent,
          children: [],
          loc: [
            {
              start: DOM_PARSER_RE.lastIndex - token[0].length,
              end: DOM_PARSER_RE.lastIndex
            }
          ]
        };
        tags.push(tag);
        tag.parent.children.push(tag);
        if (token[4] && token[4].indexOf("/") > -1 || VOID_TAGS.has(tag.name)) {
          tag.loc[1] = tag.loc[0];
          tag.isSelfClosingTag = true;
        } else {
          parent = tag;
        }
      }
    } else {
      commitTextNode();
      if (token[2] + "" === parent.name) {
        tag = parent;
        parent = tag.parent;
        tag.loc.push({
          start: DOM_PARSER_RE.lastIndex - token[0].length,
          end: DOM_PARSER_RE.lastIndex
        });
        text = str.substring(tag.loc[0].end, tag.loc[1].start);
        if (tag.children.length === 0) {
          tag.children.push({
            type: TEXT_NODE,
            value: text,
            parent
          });
        }
      } else if (token[2] + "" === tags[tags.length - 1].name && tags[tags.length - 1].isSelfClosingTag === true) {
        tag = tags[tags.length - 1];
        tag.loc.push({
          start: DOM_PARSER_RE.lastIndex - token[0].length,
          end: DOM_PARSER_RE.lastIndex
        });
      }
    }
    lastIndex = DOM_PARSER_RE.lastIndex;
  }
  text = str.slice(lastIndex);
  parent.children.push({
    type: TEXT_NODE,
    value: text,
    parent
  });
  return doc;
}
var Walker = class {
  constructor(callback) {
    this.callback = callback;
  }
  async visit(node, parent, index) {
    await this.callback(node, parent, index);
    if (Array.isArray(node.children)) {
      let promises = [];
      for (let i = 0; i < node.children.length; i++) {
        const child = node.children[i];
        promises.push(this.visit(child, node, i));
      }
      await Promise.all(promises);
    }
  }
};
var WalkerSync = class {
  constructor(callback) {
    this.callback = callback;
  }
  visit(node, parent, index) {
    this.callback(node, parent, index);
    if (Array.isArray(node.children)) {
      for (let i = 0; i < node.children.length; i++) {
        const child = node.children[i];
        this.visit(child, node, i);
      }
    }
  }
};
var HTMLString = Symbol("HTMLString");
var AttrString = Symbol("AttrString");
var RenderFn = Symbol("RenderFn");
function mark(str, tags = [HTMLString]) {
  const v = { value: str };
  for (const tag of tags) {
    Object.defineProperty(v, tag, {
      value: true,
      enumerable: false,
      writable: false
    });
  }
  return v;
}
function __unsafeHTML(str) {
  return mark(str);
}
function __unsafeRenderFn(node, fn) {
  Object.defineProperty(node, RenderFn, {
    value: fn,
    enumerable: false
  });
  return node;
}
var ESCAPE_CHARS = {
  "&": "&amp;",
  "<": "&lt;",
  ">": "&gt;"
};
function escapeHTML(str) {
  return str.replace(/[&<>]/g, (c) => ESCAPE_CHARS[c] || c);
}
function attrsToString(attributes) {
  let attrStr = "";
  for (const [key, value] of Object.entries(attributes)) {
    attrStr += ` ${key}="${value}"`;
  }
  return attrStr;
}
function attrs(attributes) {
  return mark(attrsToString(attributes), [HTMLString, AttrString]);
}
function html(tmpl, ...vals) {
  let buf = "";
  for (let i = 0; i < tmpl.length; i++) {
    buf += tmpl[i];
    const expr = vals[i];
    if (buf.endsWith("...") && expr && typeof expr === "object") {
      buf = buf.slice(0, -3).trimEnd();
      buf += attrsToString(expr);
    } else if (expr && expr[AttrString]) {
      buf = buf.trimEnd();
      buf += expr.value;
    } else if (expr && expr[HTMLString]) {
      buf += expr.value;
    } else if (typeof expr === "string") {
      buf += escapeHTML(expr);
    } else if (expr || expr === 0) {
      buf += String(expr);
    }
  }
  return mark(buf);
}
function walk(node, callback) {
  const walker = new Walker(callback);
  return walker.visit(node);
}
function walkSync(node, callback) {
  const walker = new WalkerSync(callback);
  return walker.visit(node);
}
function canSelfClose(node) {
  if (node.children.length === 0) {
    let n = node;
    while (n = n.parent) {
      if (n.name === "svg") return true;
    }
  }
  return false;
}
async function renderElement(node) {
  const { name, attributes = {} } = node;
  const children = await Promise.all(
    node.children.map((child) => render(child))
  ).then((res) => res.join(""));
  if (RenderFn in node) {
    const value = await node[RenderFn](attributes, mark(children));
    if (value && value[HTMLString]) return value.value;
    return escapeHTML(String(value));
  }
  if (name === Fragment) return children;
  const isSelfClosing = canSelfClose(node);
  if (isSelfClosing || VOID_TAGS.has(name)) {
    return `<${node.name}${attrsToString(attributes)}${isSelfClosing ? " /" : ""}>`;
  }
  return `<${node.name}${attrsToString(attributes)}>${children}</${node.name}>`;
}
function renderElementSync(node) {
  const { name, attributes = {} } = node;
  const children = node.children.map((child) => renderSync(child)).join("");
  if (RenderFn in node) {
    const value = node[RenderFn](attributes, mark(children));
    if (value && value[HTMLString]) return value.value;
    return escapeHTML(String(value));
  }
  if (name === Fragment) return children;
  const isSelfClosing = canSelfClose(node);
  if (isSelfClosing || VOID_TAGS.has(name)) {
    return `<${node.name}${attrsToString(attributes)}${isSelfClosing ? " /" : ""}>`;
  }
  return `<${node.name}${attrsToString(attributes)}>${children}</${node.name}>`;
}
function renderSync(node) {
  switch (node.type) {
    case DOCUMENT_NODE:
      return node.children.map((child) => renderSync(child)).join("");
    case ELEMENT_NODE:
      return renderElementSync(node);
    case TEXT_NODE:
      return `${node.value}`;
    case COMMENT_NODE:
      return `<!--${node.value}-->`;
    case DOCTYPE_NODE:
      return `<!${node.value}>`;
  }
}
async function render(node) {
  switch (node.type) {
    case DOCUMENT_NODE:
      return Promise.all(
        node.children.map((child) => render(child))
      ).then((res) => res.join(""));
    case ELEMENT_NODE:
      return renderElement(node);
    case TEXT_NODE:
      return `${node.value}`;
    case COMMENT_NODE:
      return `<!--${node.value}-->`;
    case DOCTYPE_NODE:
      return `<!${node.value}>`;
  }
}
function parseTransformArgs(markup, transformers) {
  if (!Array.isArray(transformers)) {
    throw new Error(
      `Invalid second argument for \`transform\`! Expected \`Transformer[]\` but got \`${typeof transformers}\``
    );
  }
  const doc = typeof markup === "string" ? parse(markup) : markup;
  return { doc };
}
async function transform(markup, transformers = []) {
  const { doc } = parseTransformArgs(markup, transformers);
  let newDoc = doc;
  for (const t of transformers) {
    newDoc = await t(newDoc);
  }
  return render(newDoc);
}
function transformSync(markup, transformers = []) {
  const { doc } = parseTransformArgs(markup, transformers);
  let newDoc = doc;
  for (const t of transformers) {
    newDoc = t(newDoc);
  }
  return renderSync(newDoc);
}
export {
  COMMENT_NODE,
  DOCTYPE_NODE,
  DOCUMENT_NODE,
  ELEMENT_NODE,
  Fragment,
  RenderFn,
  TEXT_NODE,
  __unsafeHTML,
  __unsafeRenderFn,
  attrs,
  h,
  html,
  parse,
  render,
  renderSync,
  transform,
  transformSync,
  walk,
  walkSync
};
