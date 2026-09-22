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

// node_modules/.pnpm/stylis@4.3.4/node_modules/stylis/src/Enum.js
var COMMENT = "comm";
var RULESET = "rule";
var DECLARATION = "decl";
var IMPORT = "@import";
var KEYFRAMES = "@keyframes";
var LAYER = "@layer";

// node_modules/.pnpm/stylis@4.3.4/node_modules/stylis/src/Utility.js
var abs = Math.abs;
var from = String.fromCharCode;
function trim(value) {
  return value.trim();
}
function replace(value, pattern, replacement) {
  return value.replace(pattern, replacement);
}
function indexof(value, search, position2) {
  return value.indexOf(search, position2);
}
function charat(value, index) {
  return value.charCodeAt(index) | 0;
}
function substr(value, begin, end) {
  return value.slice(begin, end);
}
function strlen(value) {
  return value.length;
}
function sizeof(value) {
  return value.length;
}
function append(value, array) {
  return array.push(value), value;
}

// node_modules/.pnpm/stylis@4.3.4/node_modules/stylis/src/Tokenizer.js
var line = 1;
var column = 1;
var length = 0;
var position = 0;
var character = 0;
var characters = "";
function node(value, root, parent, type, props, children, length2, siblings) {
  return { value, root, parent, type, props, children, line, column, length: length2, return: "", siblings };
}
function char() {
  return character;
}
function prev() {
  character = position > 0 ? charat(characters, --position) : 0;
  if (column--, character === 10)
    column = 1, line--;
  return character;
}
function next() {
  character = position < length ? charat(characters, position++) : 0;
  if (column++, character === 10)
    column = 1, line++;
  return character;
}
function peek() {
  return charat(characters, position);
}
function caret() {
  return position;
}
function slice(begin, end) {
  return substr(characters, begin, end);
}
function token(type) {
  switch (type) {
    // \0 \t \n \r \s whitespace token
    case 0:
    case 9:
    case 10:
    case 13:
    case 32:
      return 5;
    // ! + , / > @ ~ isolate token
    case 33:
    case 43:
    case 44:
    case 47:
    case 62:
    case 64:
    case 126:
    // ; { } breakpoint token
    case 59:
    case 123:
    case 125:
      return 4;
    // : accompanied token
    case 58:
      return 3;
    // " ' ( [ opening delimit token
    case 34:
    case 39:
    case 40:
    case 91:
      return 2;
    // ) ] closing delimit token
    case 41:
    case 93:
      return 1;
  }
  return 0;
}
function alloc(value) {
  return line = column = 1, length = strlen(characters = value), position = 0, [];
}
function dealloc(value) {
  return characters = "", value;
}
function delimit(type) {
  return trim(slice(position - 1, delimiter(type === 91 ? type + 2 : type === 40 ? type + 1 : type)));
}
function whitespace(type) {
  while (character = peek())
    if (character < 33)
      next();
    else
      break;
  return token(type) > 2 || token(character) > 3 ? "" : " ";
}
function escaping(index, count) {
  while (--count && next())
    if (character < 48 || character > 102 || character > 57 && character < 65 || character > 70 && character < 97)
      break;
  return slice(index, caret() + (count < 6 && peek() == 32 && next() == 32));
}
function delimiter(type) {
  while (next())
    switch (character) {
      // ] ) " '
      case type:
        return position;
      // " '
      case 34:
      case 39:
        if (type !== 34 && type !== 39)
          delimiter(character);
        break;
      // (
      case 40:
        if (type === 41)
          delimiter(type);
        break;
      // \
      case 92:
        next();
        break;
    }
  return position;
}
function commenter(type, index) {
  while (next())
    if (type + character === 47 + 10)
      break;
    else if (type + character === 42 + 42 && peek() === 47)
      break;
  return "/*" + slice(index, position - 1) + "*" + from(type === 47 ? type : next());
}
function identifier(index) {
  while (!token(peek()))
    next();
  return slice(index, position);
}

// node_modules/.pnpm/stylis@4.3.4/node_modules/stylis/src/Parser.js
function compile(value) {
  return dealloc(parse("", null, null, null, [""], value = alloc(value), 0, [0], value));
}
function parse(value, root, parent, rule, rules, rulesets, pseudo, points, declarations) {
  var index = 0;
  var offset = 0;
  var length2 = pseudo;
  var atrule = 0;
  var property = 0;
  var previous = 0;
  var variable = 1;
  var scanning = 1;
  var ampersand = 1;
  var character2 = 0;
  var type = "";
  var props = rules;
  var children = rulesets;
  var reference = rule;
  var characters2 = type;
  while (scanning)
    switch (previous = character2, character2 = next()) {
      // (
      case 40:
        if (previous != 108 && charat(characters2, length2 - 1) == 58) {
          if (indexof(characters2 += replace(delimit(character2), "&", "&\f"), "&\f", abs(index ? points[index - 1] : 0)) != -1)
            ampersand = -1;
          break;
        }
      // " ' [
      case 34:
      case 39:
      case 91:
        characters2 += delimit(character2);
        break;
      // \t \n \r \s
      case 9:
      case 10:
      case 13:
      case 32:
        characters2 += whitespace(previous);
        break;
      // \
      case 92:
        characters2 += escaping(caret() - 1, 7);
        continue;
      // /
      case 47:
        switch (peek()) {
          case 42:
          case 47:
            append(comment(commenter(next(), caret()), root, parent, declarations), declarations);
            if ((token(previous || 1) == 5 || token(peek() || 1) == 5) && strlen(characters2) && substr(characters2, -1, void 0) !== " ") characters2 += " ";
            break;
          default:
            characters2 += "/";
        }
        break;
      // {
      case 123 * variable:
        points[index++] = strlen(characters2) * ampersand;
      // } ; \0
      case 125 * variable:
      case 59:
      case 0:
        switch (character2) {
          // \0 }
          case 0:
          case 125:
            scanning = 0;
          // ;
          case 59 + offset:
            if (ampersand == -1) characters2 = replace(characters2, /\f/g, "");
            if (property > 0 && (strlen(characters2) - length2 || variable === 0 && previous === 47))
              append(property > 32 ? declaration(characters2 + ";", rule, parent, length2 - 1, declarations) : declaration(replace(characters2, " ", "") + ";", rule, parent, length2 - 2, declarations), declarations);
            break;
          // @ ;
          case 59:
            characters2 += ";";
          // { rule/at-rule
          default:
            append(reference = ruleset(characters2, root, parent, index, offset, rules, points, type, props = [], children = [], length2, rulesets), rulesets);
            if (character2 === 123)
              if (offset === 0)
                parse(characters2, root, reference, reference, props, rulesets, length2, points, children);
              else
                switch (atrule === 99 && charat(characters2, 3) === 110 ? 100 : atrule) {
                  // d l m s
                  case 100:
                  case 108:
                  case 109:
                  case 115:
                    parse(value, reference, reference, rule && append(ruleset(value, reference, reference, 0, 0, rules, points, type, rules, props = [], length2, children), children), rules, children, length2, points, rule ? props : children);
                    break;
                  default:
                    parse(characters2, reference, reference, reference, [""], children, 0, points, children);
                }
        }
        index = offset = property = 0, variable = ampersand = 1, type = characters2 = "", length2 = pseudo;
        break;
      // :
      case 58:
        length2 = 1 + strlen(characters2), property = previous;
      default:
        if (variable < 1) {
          if (character2 == 123)
            --variable;
          else if (character2 == 125 && variable++ == 0 && prev() == 125)
            continue;
        }
        switch (characters2 += from(character2), character2 * variable) {
          // &
          case 38:
            ampersand = offset > 0 ? 1 : (characters2 += "\f", -1);
            break;
          // ,
          case 44:
            points[index++] = (strlen(characters2) - 1) * ampersand, ampersand = 1;
            break;
          // @
          case 64:
            if (peek() === 45)
              characters2 += delimit(next());
            atrule = peek(), offset = length2 = strlen(type = characters2 += identifier(caret())), character2++;
            break;
          // -
          case 45:
            if (previous === 45 && strlen(characters2) == 2)
              variable = 0;
        }
    }
  return rulesets;
}
function ruleset(value, root, parent, index, offset, rules, points, type, props, children, length2, siblings) {
  var post = offset - 1;
  var rule = offset === 0 ? rules : [""];
  var size = sizeof(rule);
  for (var i2 = 0, j = 0, k = 0; i2 < index; ++i2)
    for (var x = 0, y = substr(value, post + 1, post = abs(j = points[i2])), z = value; x < size; ++x)
      if (z = trim(j > 0 ? rule[x] + " " + y : replace(y, /&\f/g, rule[x])))
        props[k++] = z;
  return node(value, root, parent, offset === 0 ? RULESET : type, props, children, length2, siblings);
}
function comment(value, root, parent, siblings) {
  return node(value, root, parent, COMMENT, from(char()), substr(value, 2, -2), 0, siblings);
}
function declaration(value, root, parent, length2, siblings) {
  return node(value, root, parent, DECLARATION, substr(value, 0, length2), substr(value, length2 + 1, -1), length2, siblings);
}

// node_modules/.pnpm/stylis@4.3.4/node_modules/stylis/src/Serializer.js
function serialize(children, callback) {
  var output = "";
  for (var i2 = 0; i2 < children.length; i2++)
    output += callback(children[i2], i2, children, callback) || "";
  return output;
}
function stringify(element, index, children, callback) {
  switch (element.type) {
    case LAYER:
      if (element.children.length) break;
    case IMPORT:
    case DECLARATION:
      return element.return = element.return || element.value;
    case COMMENT:
      return "";
    case KEYFRAMES:
      return element.return = element.value + "{" + serialize(element.children, callback) + "}";
    case RULESET:
      if (!strlen(element.value = element.props.join(","))) return "";
  }
  return strlen(children = serialize(element.children, callback)) ? element.return = element.value + "{" + children + "}" : "";
}

// node_modules/.pnpm/stylis@4.3.4/node_modules/stylis/src/Middleware.js
function middleware(collection) {
  var length2 = sizeof(collection);
  return function(element, index, children, callback) {
    var output = "";
    for (var i2 = 0; i2 < length2; i2++)
      output += collection[i2](element, index, children, callback) || "";
    return output;
  };
}

// src/transformers/scope.ts
import { ELEMENT_NODE, TEXT_NODE, render, walkSync } from "../index.js";
import { matches } from "../selector.js";
function scope(opts = {}) {
  return async (doc) => {
    const hash = opts.hash ?? shorthash(await render(doc));
    const actions = [];
    let hasStyle = false;
    const selectors = /* @__PURE__ */ new Set();
    const nodes = /* @__PURE__ */ new Set();
    walkSync(doc, (node2) => {
      if (node2.type === ELEMENT_NODE && node2.name === "style") {
        if (!opts.attribute || hasAttribute(node2, opts.attribute)) {
          hasStyle = true;
          if (opts.attribute) {
            delete node2.attributes[opts.attribute];
          }
          for (const selector of getSelectors(node2.children[0].value)) {
            selectors.add(selector);
          }
        }
      }
      if (node2.type === ELEMENT_NODE) {
        nodes.add(node2);
      }
    });
    if (hasStyle) {
      walkSync(doc, (node2) => {
        if (node2.type === ELEMENT_NODE) {
          actions.push(() => scopeElement(node2, hash, selectors));
          if (node2.name === "style") {
            actions.push(() => {
              node2.children = node2.children.map((c2) => {
                if (c2.type !== TEXT_NODE) return c2;
                c2.value = scopeCSS(c2.value, hash);
                if (c2.value === "") {
                  node2.parent.children = node2.parent.children.filter(
                    (s2) => s2 !== node2
                  );
                }
                return c2;
              });
            });
          }
        }
      });
    }
    for (const action of actions) {
      action();
    }
    return doc;
  };
}
var NEVER_SCOPED = /* @__PURE__ */ new Set([
  "base",
  "font",
  "frame",
  "frameset",
  "head",
  "link",
  "meta",
  "noframes",
  "noscript",
  "script",
  "style",
  "title"
]);
function hasAttribute(node2, name) {
  if (name in node2.attributes) {
    return node2.attributes[name] !== "false";
  }
  return false;
}
function scopeElement(node2, hash, selectors) {
  const { name } = node2;
  if (!name) return;
  if (name.length < 1) return;
  if (NEVER_SCOPED.has(name)) return;
  if (node2.attributes["data-scope"]) return;
  for (const selector of selectors) {
    if (matches(node2, selector)) {
      node2.attributes["data-scope"] = hash;
      return;
    }
  }
}
function scopeSelector(selector, hash) {
  const ast = m(selector);
  const scope2 = (node2) => {
    switch (node2.type) {
      case "pseudo-class": {
        if (node2.name === "root") return node2.content;
        if (node2.name === "global") return node2.argument;
        return `${node2.content}:where([data-scope="${hash}"])`;
      }
      case "compound":
        return `${selector}:where([data-scope="${hash}"])`;
      case "complex": {
        const { left, right, combinator } = node2;
        return `${scope2(left)}${combinator}${scope2(right)}`;
      }
      case "list":
        return node2.list.map((s2) => scope2(s2)).join(" ");
      default:
        return `${node2.content}:where([data-scope="${hash}"])`;
    }
  };
  return scope2(ast);
}
function scopeCSS(css, hash) {
  return serialize(
    compile(css),
    middleware([
      (element) => {
        if (element.type === "rule") {
          if (Array.isArray(element.props)) {
            element.props = element.props.map(
              (prop) => scopeSelector(prop, hash)
            );
          } else {
            element.props = scopeSelector(element.props, hash);
          }
        }
      },
      stringify
    ])
  );
}
function getSelectors(css) {
  const selectors = /* @__PURE__ */ new Set();
  serialize(
    compile(css),
    middleware([
      (element) => {
        if (element.type === "rule") {
          if (Array.isArray(element.props)) {
            for (const p2 of element.props) {
              selectors.add(p2);
            }
          } else {
            selectors.add(element.props);
          }
        }
      }
    ])
  );
  return Array.from(selectors);
}
var dictionary = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY";
var binary = dictionary.length;
function bitwise(str) {
  let hash = 0;
  if (str.length === 0) return hash;
  for (let i2 = 0; i2 < str.length; i2++) {
    const ch = str.charCodeAt(i2);
    hash = (hash << 5) - hash + ch;
    hash = hash & hash;
  }
  return hash;
}
function shorthash(text) {
  let num;
  let result = "";
  let integer = bitwise(text);
  const sign = integer < 0 ? "Z" : "";
  integer = Math.abs(integer);
  while (integer >= binary) {
    num = integer % binary;
    integer = Math.floor(integer / binary);
    result = dictionary[num] + result;
  }
  if (integer > 0) {
    result = dictionary[integer] + result;
  }
  return sign + result;
}
export {
  scope as default
};
/**
 * shorthash - https://github.com/bibig/node-shorthash
 *
 * @license
 *
 * (The MIT License)
 *
 * Copyright (c) 2013 Bibig <bibig@me.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
