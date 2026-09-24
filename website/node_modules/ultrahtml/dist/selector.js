// src/selector.ts
import { ELEMENT_NODE, TEXT_NODE, walkSync } from "./index.js";

// node_modules/.pnpm/parsel-js@1.1.2/node_modules/parsel-js/dist/parsel.min.js
var e = { attribute: /\[\s*(?:(?<namespace>\*|[-\w\P{ASCII}]*)\|)?(?<name>[-\w\P{ASCII}]+)\s*(?:(?<operator>\W?=)\s*(?<value>.+?)\s*(\s(?<caseSensitive>[iIsS]))?\s*)?\]/gu, id: /#(?<name>[-\w\P{ASCII}]+)/gu, class: /\.(?<name>[-\w\P{ASCII}]+)/gu, comma: /\s*,\s*/g, combinator: /\s*[\s>+~]\s*/g, "pseudo-element": /::(?<name>[-\w\P{ASCII}]+)(?:\((?<argument>¶*)\))?/gu, "pseudo-class": /:(?<name>[-\w\P{ASCII}]+)(?:\((?<argument>¶*)\))?/gu, universal: /(?:(?<namespace>\*|[-\w\P{ASCII}]*)\|)?\*/gu, type: /(?:(?<namespace>\*|[-\w\P{ASCII}]*)\|)?(?<name>[-\w\P{ASCII}]+)/gu };
var t = /* @__PURE__ */ new Set(["combinator", "comma"]);
var n = /* @__PURE__ */ new Set(["not", "is", "where", "has", "matches", "-moz-any", "-webkit-any", "nth-child", "nth-last-child"]);
var s = /(?<index>[\dn+-]+)\s+of\s+(?<subtree>.+)/;
var o = { "nth-child": s, "nth-last-child": s };
var r = (t2) => {
  switch (t2) {
    case "pseudo-element":
    case "pseudo-class":
      return new RegExp(e[t2].source.replace("(?<argument>\xB6*)", "(?<argument>.*)"), "gu");
    default:
      return e[t2];
  }
};
function c(e2, t2) {
  let n2 = 0, s2 = "";
  for (; t2 < e2.length; t2++) {
    const o2 = e2[t2];
    switch (o2) {
      case "(":
        ++n2;
        break;
      case ")":
        --n2;
    }
    if (s2 += o2, 0 === n2) return s2;
  }
  return s2;
}
function i(n2, s2 = e) {
  if (!n2) return [];
  const o2 = [n2];
  for (const [e2, t2] of Object.entries(s2)) for (let n3 = 0; n3 < o2.length; n3++) {
    const s3 = o2[n3];
    if ("string" != typeof s3) continue;
    t2.lastIndex = 0;
    const r3 = t2.exec(s3);
    if (!r3) continue;
    const c2 = r3.index - 1, i2 = [], a2 = r3[0], l2 = s3.slice(0, c2 + 1);
    l2 && i2.push(l2), i2.push({ ...r3.groups, type: e2, content: a2 });
    const u2 = s3.slice(c2 + a2.length + 1);
    u2 && i2.push(u2), o2.splice(n3, 1, ...i2);
  }
  let r2 = 0;
  for (const e2 of o2) switch (typeof e2) {
    case "string":
      throw new Error(`Unexpected sequence ${e2} found at index ${r2}`);
    case "object":
      r2 += e2.content.length, e2.pos = [r2 - e2.content.length, r2], t.has(e2.type) && (e2.content = e2.content.trim() || " ");
  }
  return o2;
}
var a = /(['"])([^\\\n]+?)\1/g;
var l = /\\./g;
function u(t2, n2 = e) {
  if ("" === (t2 = t2.trim())) return [];
  const s2 = [];
  t2 = (t2 = t2.replace(l, (e2, t3) => (s2.push({ value: e2, offset: t3 }), "\uE000".repeat(e2.length)))).replace(a, (e2, t3, n3, o3) => (s2.push({ value: e2, offset: o3 }), `${t3}${"\uE001".repeat(n3.length)}${t3}`));
  {
    let e2, n3 = 0;
    for (; (e2 = t2.indexOf("(", n3)) > -1; ) {
      const o3 = c(t2, e2);
      s2.push({ value: o3, offset: e2 }), t2 = `${t2.substring(0, e2)}(${"\xB6".repeat(o3.length - 2)})${t2.substring(e2 + o3.length)}`, n3 = e2 + o3.length;
    }
  }
  const o2 = i(t2, n2), u2 = /* @__PURE__ */ new Set();
  for (const e2 of s2.reverse()) for (const t3 of o2) {
    const { offset: n3, value: s3 } = e2;
    if (!(t3.pos[0] <= n3 && n3 + s3.length <= t3.pos[1])) continue;
    const { content: o3 } = t3, r2 = n3 - t3.pos[0];
    t3.content = o3.slice(0, r2) + s3 + o3.slice(r2 + s3.length), t3.content !== o3 && u2.add(t3);
  }
  for (const e2 of u2) {
    const t3 = r(e2.type);
    if (!t3) throw new Error(`Unknown token type: ${e2.type}`);
    t3.lastIndex = 0;
    const n3 = t3.exec(e2.content);
    if (!n3) throw new Error(`Unable to parse content for ${e2.type}: ${e2.content}`);
    Object.assign(e2, n3.groups);
  }
  return o2;
}
function f(e2, { list: t2 = true } = {}) {
  if (t2 && e2.find((e3) => "comma" === e3.type)) {
    const t3 = [], n2 = [];
    for (let s2 = 0; s2 < e2.length; s2++) if ("comma" === e2[s2].type) {
      if (0 === n2.length) throw new Error("Incorrect comma at " + s2);
      t3.push(f(n2, { list: false })), n2.length = 0;
    } else n2.push(e2[s2]);
    if (0 === n2.length) throw new Error("Trailing comma");
    return t3.push(f(n2, { list: false })), { type: "list", list: t3 };
  }
  for (let t3 = e2.length - 1; t3 >= 0; t3--) {
    let n2 = e2[t3];
    if ("combinator" === n2.type) {
      let s2 = e2.slice(0, t3), o2 = e2.slice(t3 + 1);
      return { type: "complex", combinator: n2.content, left: f(s2), right: f(o2) };
    }
  }
  switch (e2.length) {
    case 0:
      throw new Error("Could not build AST.");
    case 1:
      return e2[0];
    default:
      return { type: "compound", list: [...e2] };
  }
}
function* p(e2, t2) {
  switch (e2.type) {
    case "list":
      for (let t3 of e2.list) yield* p(t3, e2);
      break;
    case "complex":
      yield* p(e2.left, e2), yield* p(e2.right, e2);
      break;
    case "compound":
      yield* e2.list.map((t3) => [t3, e2]);
      break;
    default:
      yield [e2, t2];
  }
}
function m(e2, { recursive: t2 = true, list: s2 = true } = {}) {
  const r2 = u(e2);
  if (!r2) return;
  const c2 = f(r2, { list: s2 });
  if (!t2) return c2;
  for (const [e3] of p(c2)) {
    if ("pseudo-class" !== e3.type || !e3.argument) continue;
    if (!n.has(e3.name)) continue;
    let t3 = e3.argument;
    const s3 = o[e3.name];
    if (s3) {
      const n2 = s3.exec(t3);
      if (!n2) continue;
      Object.assign(e3, n2.groups), t3 = n2.groups.subtree;
    }
    t3 && Object.assign(e3, { subtree: m(t3, { recursive: true, list: true }) });
  }
  return c2;
}
function d(e2, t2) {
  return t2 = t2 || Math.max(...e2) + 1, e2[0] * (t2 << 1) + e2[1] * t2 + e2[2];
}
function w(e2) {
  let t2 = e2;
  if ("string" == typeof t2 && (t2 = m(t2, { recursive: true })), !t2) return [];
  if ("list" === t2.type && "list" in t2) {
    let e3 = 10;
    const n2 = t2.list.map((t3) => {
      const n3 = w(t3);
      return e3 = Math.max(e3, ...w(t3)), n3;
    }), s3 = n2.map((t3) => d(t3, e3));
    return n2[s3.indexOf(Math.max(...s3))];
  }
  const s2 = [0, 0, 0];
  for (const [e3] of p(t2)) switch (e3.type) {
    case "id":
      s2[0]++;
      break;
    case "class":
    case "attribute":
      s2[1]++;
      break;
    case "pseudo-element":
    case "type":
      s2[2]++;
      break;
    case "pseudo-class":
      if ("where" === e3.name) break;
      if (!n.has(e3.name) || !e3.subtree) {
        s2[1]++;
        break;
      }
      w(e3.subtree).forEach((e4, t3) => s2[t3] += e4), "nth-child" !== e3.name && "nth-last-child" !== e3.name || s2[1]++;
  }
  return s2;
}

// src/selector.ts
function specificity(selector) {
  return d(w(selector), 10);
}
function matches(node, selector) {
  const match = selectorToMatch(selector);
  return match(node, node.parent, nthChildIndex(node, node.parent));
}
function querySelector(node, selector) {
  const match = selectorToMatch(selector);
  try {
    return select(
      node,
      (n2, parent, index) => {
        let m2 = match(n2, parent, index);
        if (!m2) return false;
        return m2;
      },
      { single: true }
    )[0];
  } catch (e2) {
    if (e2 instanceof Error) {
      throw e2;
    }
    return e2;
  }
}
function querySelectorAll(node, selector) {
  const match = selectorToMatch(selector);
  return select(node, (n2, parent, index) => {
    let m2 = match(n2, parent, index);
    if (!m2) return false;
    return m2;
  });
}
var selector_default = querySelectorAll;
function select(node, match, opts = { single: false }) {
  let nodes = [];
  walkSync(node, (n2, parent, index) => {
    if (n2 && n2.type !== ELEMENT_NODE) return;
    if (match(n2, parent, index)) {
      if (opts.single) throw n2;
      nodes.push(n2);
    }
  });
  return nodes;
}
var getAttributeMatch = (selector) => {
  const { operator = "=" } = selector;
  switch (operator) {
    case "=":
      return (a2, b) => a2 === b;
    case "~=":
      return (a2, b) => a2.split(/\s+/g).includes(b);
    case "|=":
      return (a2, b) => a2.startsWith(b + "-");
    case "*=":
      return (a2, b) => a2.indexOf(b) > -1;
    case "$=":
      return (a2, b) => a2.endsWith(b);
    case "^=":
      return (a2, b) => a2.startsWith(b);
  }
  return (a2, b) => false;
};
var nthChildIndex = (node, parent) => parent?.children.filter((n2) => n2.type === ELEMENT_NODE).findIndex((n2) => n2 === node);
var nthChild = (formula) => {
  let [_, A = "1", B = "0"] = /^\s*(?:(-?(?:\d+)?)n)?\s*\+?\s*(\d+)?\s*$/gm.exec(formula) ?? [];
  if (A.length === 0) A = "1";
  const a2 = Number.parseInt(A === "-" ? "-1" : A);
  const b = Number.parseInt(B);
  return (n2) => a2 * n2 + b;
};
var lastChild = (node, parent) => parent?.children.filter((n2) => n2.type === ELEMENT_NODE).pop() === node;
var firstChild = (node, parent) => parent?.children.filter((n2) => n2.type === ELEMENT_NODE).shift() === node;
var onlyChild = (node, parent) => parent?.children.filter((n2) => n2.type === ELEMENT_NODE).length === 1;
var createMatch = (selector) => {
  switch (selector.type) {
    case "type":
      return (node) => {
        if (selector.content === "*") return true;
        return node.name === selector.name;
      };
    case "class":
      return (node) => node.attributes?.class?.split(/\s+/g).includes(selector.name);
    case "id":
      return (node) => node.attributes?.id === selector.name;
    case "pseudo-class": {
      switch (selector.name) {
        case "global":
          return (...args) => selectorToMatch(m(selector.argument))(...args);
        case "not":
          return (...args) => !createMatch(selector.subtree)(...args);
        case "is":
          return (...args) => selectorToMatch(selector.subtree)(...args);
        case "where":
          return (...args) => selectorToMatch(selector.subtree)(...args);
        case "root":
          return (node, parent) => node.type === ELEMENT_NODE && node.name === "html";
        case "empty":
          return (node) => node.type === ELEMENT_NODE && (node.children.length === 0 || node.children.every(
            (n2) => n2.type === TEXT_NODE && n2.value.trim() === ""
          ));
        case "first-child":
          return (node, parent) => firstChild(node, parent);
        case "last-child":
          return (node, parent) => lastChild(node, parent);
        case "only-child":
          return (node, parent) => onlyChild(node, parent);
        case "nth-child":
          return (node, parent) => {
            const target = nthChildIndex(node, parent) + 1;
            if (Number.isNaN(Number(selector.argument))) {
              switch (selector.argument) {
                case "odd":
                  return Math.abs(target % 2) == 1;
                case "even":
                  return target % 2 === 0;
                default: {
                  if (!selector.argument) {
                    throw new Error(`Unsupported empty nth-child selector!`);
                  }
                  const nth = nthChild(selector.argument);
                  const elements = parent?.children.filter(
                    (n2) => n2.type === ELEMENT_NODE
                  );
                  const childIndex = nthChildIndex(node, parent) + 1;
                  for (let i2 = 0; i2 < elements.length; i2++) {
                    let n2 = nth(i2);
                    if (n2 > elements.length) return false;
                    if (n2 === childIndex) return true;
                  }
                  return false;
                }
              }
            }
            return target === Number(selector.argument);
          };
        default:
          throw new Error(`Unhandled pseudo-class: ${selector.name}!`);
      }
    }
    case "attribute":
      return (node) => {
        let { caseSensitive, name, value } = selector;
        if (!node.attributes) return false;
        const attrs = Object.entries(node.attributes);
        for (let [attr, attrVal] of attrs) {
          if (caseSensitive === "i") {
            value = name.toLowerCase();
            attrVal = attr.toLowerCase();
          }
          if (attr !== name) continue;
          if (!value) return true;
          if ((value[0] === '"' || value[0] === "'") && value[0] === value[value.length - 1]) {
            value = JSON.parse(value);
          }
          if (value) {
            return getAttributeMatch(selector)(attrVal, value);
          }
        }
        return false;
      };
    case "universal":
      return (_) => {
        return true;
      };
    default: {
      throw new Error(`Unhandled selector: ${selector.type}`);
    }
  }
};
var selectorToMatch = (sel) => {
  let selector = typeof sel === "string" ? m(sel) : sel;
  switch (selector?.type) {
    case "list": {
      const matchers = selector.list.map((s2) => createMatch(s2));
      return (node, parent, index) => {
        for (const match of matchers) {
          if (match(node, parent)) return true;
        }
        return false;
      };
    }
    case "compound": {
      const matchers = selector.list.map((s2) => createMatch(s2));
      return (node, parent, index) => {
        for (const match of matchers) {
          if (!match(node, parent)) return false;
        }
        return true;
      };
    }
    case "complex": {
      const { left, right, combinator } = selector;
      const matchLeft = selectorToMatch(left);
      const matchRight = selectorToMatch(right);
      let leftMatches = /* @__PURE__ */ new WeakSet();
      return (node, parent, i2 = 0) => {
        if (matchLeft(node)) {
          leftMatches.add(node);
        } else if (parent && leftMatches.has(parent) && combinator === " ") {
          leftMatches.add(node);
        }
        if (!matchRight(node)) return false;
        switch (combinator) {
          case " ":
          // fall-through
          case ">":
            return parent ? leftMatches.has(parent) : false;
          case "~": {
            if (!parent) return false;
            for (let sibling of parent.children.slice(0, i2)) {
              if (leftMatches.has(sibling)) return true;
            }
            return false;
          }
          case "+": {
            if (!parent) return false;
            let prevSiblings = parent.children.slice(0, i2).filter((el) => el.type === ELEMENT_NODE);
            if (prevSiblings.length === 0) return false;
            const prev = prevSiblings[prevSiblings.length - 1];
            if (!prev) return false;
            if (leftMatches.has(prev)) return true;
          }
          default:
            return false;
        }
      };
    }
    default:
      return createMatch(selector);
  }
};
export {
  selector_default as default,
  matches,
  querySelector,
  querySelectorAll,
  specificity
};
