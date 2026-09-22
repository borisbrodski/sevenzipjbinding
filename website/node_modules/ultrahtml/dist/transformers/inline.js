// src/transformers/inline.ts
import { walkSync, ELEMENT_NODE, TEXT_NODE } from "../index.js";
import { querySelectorAll, specificity } from "../selector.js";

// node_modules/.pnpm/stylis@4.3.4/node_modules/stylis/src/Enum.js
var COMMENT = "comm";
var RULESET = "rule";
var DECLARATION = "decl";

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
  for (var i3 = 0, j = 0, k = 0; i3 < index; ++i3)
    for (var x = 0, y = substr(value, post + 1, post = abs(j = points[i3])), z = value; x < size; ++x)
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

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/ast/ast.js
var isParserError = (e5) => "object" == typeof e5 && null !== e5 && "errid" in e5;
var splitMediaQueryList = (e5) => {
  const t3 = [[]], r3 = [];
  for (const n2 of e5)
    if ("comma" === n2.type && 0 === r3.length) t3.push([]);
    else {
      switch (n2.type) {
        case "function":
        case "(":
          r3.push(")");
          break;
        case "[":
          r3.push("]");
          break;
        case "{":
          r3.push("}");
          break;
        case ")":
        case "]":
        case "}":
          r3.at(-1) === n2.type && r3.pop();
          break;
      }
      t3[t3.length - 1].push(n2);
    }
  return t3;
};
var readMediaQueryList = (e5) => {
  const t3 = splitMediaQueryList(e5);
  if (1 === t3.length && 0 === t3[0].length)
    return { type: "query-list", mediaQueries: [{ type: "query" }] };
  {
    const e6 = [];
    for (const r3 of t3) {
      const t4 = readMediaQuery(r3);
      isParserError(t4) ? e6.push({ type: "query", prefix: "not" }) : e6.push(t4);
    }
    return { type: "query-list", mediaQueries: e6 };
  }
};
var readMediaQuery = (e5) => {
  var t3, r3, n2;
  const i3 = e5.at(0);
  if (i3) {
    if ("(" === i3.type) {
      const r4 = readMediaCondition(e5, true);
      if (isParserError(r4)) {
        const { start: n3, end: a2 } = null !== (t3 = e5.at(1)) && void 0 !== t3 ? t3 : i3;
        return { errid: "EXPECT_FEATURE_OR_CONDITION", start: n3, end: a2, child: r4 };
      }
      return { type: "query", mediaCondition: r4 };
    }
    if ("ident" === i3.type) {
      let t4, a2;
      const { value: d, end: s2 } = i3;
      "only" !== d && "not" !== d || (t4 = d);
      const o3 = void 0 === t4 ? 0 : 1, l2 = e5.at(o3);
      if (!l2) return { errid: "EXPECT_LPAREN_OR_TYPE", start: s2, end: s2 };
      if ("ident" !== l2.type) {
        if ("not" === t4 && "(" === l2.type) {
          const t5 = readMediaCondition(e5.slice(o3), true);
          if (isParserError(t5)) {
            const { start: n3, end: i4 } = null !== (r3 = e5.at(o3 + 1)) && void 0 !== r3 ? r3 : l2;
            return { errid: "EXPECT_CONDITION", start: n3, end: i4, child: t5 };
          }
          return {
            type: "query",
            mediaCondition: { type: "condition", operator: "not", children: [t5] }
          };
        }
        {
          const { start: e6, end: t5 } = l2;
          return { errid: "EXPECT_TYPE", start: e6, end: t5 };
        }
      }
      {
        const { value: e6, start: r4, end: n3 } = l2;
        if ("all" === e6) a2 = void 0;
        else if ("print" === e6 || "screen" === e6) a2 = e6;
        else {
          if ("tty" !== e6 && "tv" !== e6 && "projection" !== e6 && "handheld" !== e6 && "braille" !== e6 && "embossed" !== e6 && "aural" !== e6 && "speech" !== e6)
            return { errid: "EXPECT_TYPE", start: r4, end: n3 };
          t4 = "not" === t4 ? void 0 : "not", a2 = void 0;
        }
      }
      if (o3 + 1 === e5.length) return { type: "query", prefix: t4, mediaType: a2 };
      {
        const r4 = e5[o3 + 1];
        if ("ident" === r4.type && "and" === r4.value) {
          const r5 = e5.at(-1), i4 = e5.at(o3 + 2);
          let d2, s3 = r5.end + 1;
          if ("ident" === (null == i4 ? void 0 : i4.type) && "not" === i4.value) {
            s3 += 1;
            const t5 = readMediaCondition(e5.slice(o3 + 3), false);
            d2 = isParserError(t5) ? t5 : { type: "condition", operator: "not", children: [t5] };
          } else d2 = readMediaCondition(e5.slice(o3 + 2), false);
          const { start: l3, end: u2 } = null !== (n2 = e5.at(o3 + 2)) && void 0 !== n2 ? n2 : { start: s3, end: s3 };
          return isParserError(d2) ? { errid: "EXPECT_CONDITION", start: l3, end: u2, child: d2 } : { type: "query", prefix: t4, mediaType: a2, mediaCondition: d2 };
        }
        return { errid: "EXPECT_AND", start: r4.start, end: r4.end };
      }
    }
    return { errid: "EXPECT_LPAREN_OR_TYPE_OR_MODIFIER", start: i3.start, end: i3.end };
  }
  return { errid: "EMPTY_QUERY", start: 0, end: 0 };
};
var readMediaCondition = (e5, t3, r3) => {
  const n2 = e5.at(0);
  if (n2) {
    if ("(" !== n2.type) return { errid: "EXPECT_LPAREN", start: n2.start, end: n2.end };
    let i3, a2 = e5.length - 1, d = 0, s2 = 0;
    for (const [t4, r4] of e5.entries())
      if ("(" === r4.type ? (s2 += 1, d = Math.max(d, s2)) : ")" === r4.type && (s2 -= 1), 0 === s2) {
        a2 = t4;
        break;
      }
    if (0 !== s2) return { errid: "MISMATCH_PARENS", start: n2.start, end: e5[e5.length - 1].end };
    const o3 = e5.slice(0, a2 + 1);
    if (i3 = 1 === d ? readMediaFeature(o3) : "ident" === o3[1].type && "not" === o3[1].value ? readMediaCondition(o3.slice(2, -1), true, "not") : readMediaCondition(o3.slice(1, -1), true), isParserError(i3))
      return {
        errid: "EXPECT_FEATURE_OR_CONDITION",
        start: n2.start,
        end: o3[o3.length - 1].end,
        child: i3
      };
    if (a2 === e5.length - 1) return { type: "condition", operator: r3, children: [i3] };
    {
      const n3 = e5[a2 + 1];
      if ("ident" !== n3.type || "and" !== n3.value && "or" !== n3.value)
        return { errid: "EXPECT_AND_OR_OR", start: n3.start, end: n3.end };
      if (void 0 !== r3 && r3 !== n3.value)
        return { errid: "MIX_AND_WITH_OR", start: n3.start, end: n3.end };
      if ("or" === n3.value && !t3) return { errid: "MIX_AND_WITH_OR", start: n3.start, end: n3.end };
      const d2 = readMediaCondition(e5.slice(a2 + 2), t3, n3.value);
      return isParserError(d2) ? d2 : { type: "condition", operator: n3.value, children: [i3, ...d2.children] };
    }
  }
  return { errid: "EMPTY_CONDITION", start: 0, end: 0 };
};
var readMediaFeature = (e5) => {
  const t3 = e5.at(0);
  if (t3) {
    if ("(" !== t3.type) return { errid: "EXPECT_LPAREN", start: t3.start, end: t3.end };
    const r3 = e5[e5.length - 1];
    if (")" !== r3.type) return { errid: "EXPECT_RPAREN", start: r3.end + 1, end: r3.end + 1 };
    const n2 = [e5[0]];
    for (let t4 = 1; t4 < e5.length; t4++) {
      if (t4 < e5.length - 2) {
        const r4 = e5[t4], i4 = e5[t4 + 1], a2 = e5[t4 + 2];
        if ("number" === r4.type && r4.value > 0 && "delim" === i4.type && 47 === i4.value && "number" === a2.type && a2.value > 0) {
          n2.push({
            type: "ratio",
            numerator: r4.value,
            denominator: a2.value,
            hasSpaceBefore: r4.hasSpaceBefore,
            hasSpaceAfter: a2.hasSpaceAfter,
            start: r4.start,
            end: a2.end
          }), t4 += 2;
          continue;
        }
      }
      n2.push(e5[t4]);
    }
    const i3 = n2[1];
    if ("ident" === i3.type && 3 === n2.length)
      return { type: "feature", context: "boolean", feature: i3.value };
    if (5 === n2.length && "ident" === n2[1].type && "colon" === n2[2].type) {
      const e6 = n2[3];
      if ("number" === e6.type || "dimension" === e6.type || "ratio" === e6.type || "ident" === e6.type) {
        let t4, r4 = n2[1].value;
        const i4 = r4.slice(0, 4);
        "min-" === i4 ? (t4 = "min", r4 = r4.slice(4)) : "max-" === i4 && (t4 = "max", r4 = r4.slice(4));
        const { hasSpaceBefore: a2, hasSpaceAfter: d, start: s2, end: o3, ...l2 } = e6;
        return { type: "feature", context: "value", prefix: t4, feature: r4, value: l2 };
      }
      return { errid: "EXPECT_VALUE", start: e6.start, end: e6.end };
    }
    if (n2.length >= 5) {
      const e6 = readRange(n2);
      if (isParserError(e6))
        return { errid: "EXPECT_RANGE", start: t3.start, end: n2[n2.length - 1].end, child: e6 };
      {
        const { feature: t4, ...r4 } = e6;
        return { type: "feature", context: "range", feature: t4, range: r4 };
      }
    }
    return { errid: "INVALID_FEATURE", start: t3.start, end: e5[e5.length - 1].end };
  }
  return { errid: "EMPTY_FEATURE", start: 0, end: 0 };
};
var readRange = (e5) => {
  var t3, r3, n2, i3, a2, d, s2, o3;
  if (e5.length < 5)
    return {
      errid: "INVALID_RANGE",
      start: null !== (r3 = null === (t3 = e5.at(0)) || void 0 === t3 ? void 0 : t3.start) && void 0 !== r3 ? r3 : 0,
      end: null !== (i3 = null === (n2 = e5.at(-1)) || void 0 === n2 ? void 0 : n2.end) && void 0 !== i3 ? i3 : 0
    };
  if ("(" !== e5[0].type) return { errid: "EXPECT_LPAREN", start: e5[0].start, end: e5[0].end };
  const l2 = e5[e5.length - 1];
  if (")" !== l2.type) return { errid: "EXPECT_RPAREN", start: l2.start, end: l2.end };
  const u2 = { feature: "" }, p = "number" === e5[1].type || "dimension" === e5[1].type || "ratio" === e5[1].type || "ident" === e5[1].type && "infinite" === e5[1].value;
  if ("delim" === e5[2].type) {
    if (60 === e5[2].value)
      "delim" !== e5[3].type || 61 !== e5[3].value || e5[3].hasSpaceBefore ? u2[p ? "leftOp" : "rightOp"] = "<" : u2[p ? "leftOp" : "rightOp"] = "<=";
    else if (62 === e5[2].value)
      "delim" !== e5[3].type || 61 !== e5[3].value || e5[3].hasSpaceBefore ? u2[p ? "leftOp" : "rightOp"] = ">" : u2[p ? "leftOp" : "rightOp"] = ">=";
    else {
      if (61 !== e5[2].value) return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
      u2[p ? "leftOp" : "rightOp"] = "=";
    }
    if (p) u2.leftToken = e5[1];
    else {
      if ("ident" !== e5[1].type) return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
      u2.feature = e5[1].value;
    }
    const t4 = 2 + (null !== (d = null === (a2 = u2[p ? "leftOp" : "rightOp"]) || void 0 === a2 ? void 0 : a2.length) && void 0 !== d ? d : 0), r4 = e5[t4];
    if (p) {
      if ("ident" !== r4.type) return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
      if (u2.feature = r4.value, e5.length >= 7) {
        const r5 = e5[t4 + 1], n4 = e5[t4 + 2];
        if ("delim" !== r5.type) return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
        {
          const i5 = r5.value;
          if (60 === i5)
            "delim" !== n4.type || 61 !== n4.value || n4.hasSpaceBefore ? u2.rightOp = "<" : u2.rightOp = "<=";
          else {
            if (62 !== i5) return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
            "delim" !== n4.type || 61 !== n4.value || n4.hasSpaceBefore ? u2.rightOp = ">" : u2.rightOp = ">=";
          }
          const a3 = t4 + 1 + (null !== (o3 = null === (s2 = u2.rightOp) || void 0 === s2 ? void 0 : s2.length) && void 0 !== o3 ? o3 : 0), d2 = e5.at(a3);
          if (a3 + 2 !== e5.length) return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
          u2.rightToken = d2;
        }
      } else if (t4 + 2 !== e5.length) return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
    } else u2.rightToken = r4;
    let n3;
    const { leftToken: i4, leftOp: f, feature: c, rightOp: y, rightToken: h } = u2;
    let E, v;
    if (void 0 !== i4) {
      if ("ident" === i4.type) {
        const { type: e6, value: t5 } = i4;
        "infinite" === t5 && (E = { type: e6, value: t5 });
      } else if ("number" === i4.type || "dimension" === i4.type || "ratio" === i4.type) {
        const { hasSpaceBefore: e6, hasSpaceAfter: t5, start: r5, end: n4, ...a3 } = i4;
        E = a3;
      }
    }
    if (void 0 !== h) {
      if ("ident" === h.type) {
        const { type: e6, value: t5 } = h;
        "infinite" === t5 && (v = { type: e6, value: t5 });
      } else if ("number" === h.type || "dimension" === h.type || "ratio" === h.type) {
        const { hasSpaceBefore: e6, hasSpaceAfter: t5, start: r5, end: n4, ...i5 } = h;
        v = i5;
      }
    }
    if (void 0 !== E && void 0 !== v)
      if ("<" !== f && "<=" !== f || "<" !== y && "<=" !== y) {
        if (">" !== f && ">=" !== f || ">" !== y && ">=" !== y)
          return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
        n3 = { leftToken: E, leftOp: f, feature: c, rightOp: y, rightToken: v };
      } else n3 = { leftToken: E, leftOp: f, feature: c, rightOp: y, rightToken: v };
    else
      (void 0 === E && void 0 === f && void 0 !== y && void 0 !== v || void 0 !== E && void 0 !== f && void 0 === y && void 0 === v) && (n3 = { leftToken: E, leftOp: f, feature: c, rightOp: y, rightToken: v });
    return null != n3 ? n3 : { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
  }
  return { errid: "INVALID_RANGE", start: e5[0].start, end: l2.end };
};

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/flatten/flatten.js
var flattenMediaQueryList = (e5) => ({
  type: "query-list",
  mediaQueries: e5.mediaQueries.map((e6) => flattenMediaQuery(e6))
});
var flattenMediaQuery = (e5) => e5.mediaCondition ? {
  type: "query",
  prefix: e5.prefix,
  mediaType: e5.mediaType,
  mediaCondition: flattenMediaCondition(e5.mediaCondition)
} : e5;
var flattenMediaCondition = (e5) => {
  const o3 = [];
  for (const t3 of e5.children)
    if ("condition" === t3.type) {
      const i3 = flattenMediaCondition(t3);
      void 0 === i3.operator && 1 === i3.children.length ? o3.push(i3.children[0]) : i3.operator !== e5.operator || "and" !== i3.operator && "or" !== i3.operator ? o3.push(i3) : o3.push(...i3.children);
    } else o3.push(t3);
  if (1 === o3.length) {
    const t3 = o3[0];
    if ("condition" === t3.type) {
      if (void 0 === e5.operator) return t3;
      if ("not" === e5.operator && "not" === t3.operator)
        return { type: "condition", children: t3.children };
    }
  }
  return { type: "condition", operator: e5.operator, children: o3 };
};

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/internals.js
var invertParserError = (e5) => {
  const t3 = [e5];
  for (let d = e5.child; void 0 !== d; d = d.child) t3.push(d);
  for (let e6 = t3.length - 2; e6 >= 0; e6--) t3[e6 + 1].child = t3.at(e6);
  return delete t3[0].child, t3.at(-1);
};
var deleteUndefinedValues = (e5) => {
  switch (e5.type) {
    case "query-list":
      for (const t3 of e5.mediaQueries) deleteUndefinedValues(t3);
      return e5;
    case "query":
      return void 0 === e5.prefix && delete e5.prefix, void 0 === e5.mediaType && delete e5.mediaType, void 0 === e5.mediaCondition ? delete e5.mediaCondition : deleteUndefinedValues(e5.mediaCondition), e5;
    case "condition":
      void 0 === e5.operator && delete e5.operator;
      for (const t3 of e5.children) deleteUndefinedValues(t3);
      return e5;
    case "feature":
      return "value" === e5.context ? (void 0 === e5.prefix && delete e5.prefix, deleteUndefinedValues(e5.value)) : "range" === e5.context && (void 0 === e5.range.leftOp && delete e5.range.leftOp, void 0 === e5.range.rightOp && delete e5.range.rightOp, void 0 === e5.range.leftToken ? delete e5.range.leftToken : deleteUndefinedValues(e5.range.leftToken), void 0 === e5.range.rightToken ? delete e5.range.rightToken : deleteUndefinedValues(e5.range.rightToken)), e5;
    default:
      return e5;
  }
};

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/lexer/codepoints.js
var e;
var readCodepoints = (s2) => {
  const t3 = (() => {
    let s3;
    return e ? s3 = e : (s3 = new TextEncoder(), e = s3), s3;
  })().encode(s2), r3 = [], a2 = t3.length;
  for (let e5 = 0; e5 < a2; e5 += 1) {
    const s3 = t3.at(e5);
    if (s3 < 128)
      switch (s3) {
        case 0:
          r3.push(65533);
          break;
        case 12:
          r3.push(10);
          break;
        case 13:
          r3.push(10), 10 === t3.at(e5 + 1) && (e5 += 1);
          break;
        default:
          r3.push(s3);
      }
    else
      s3 < 224 ? r3.push(s3 << 59 >>> 53 | t3[++e5] << 58 >>> 58) : s3 < 240 ? r3.push(s3 << 60 >>> 48 | t3[++e5] << 58 >>> 52 | t3[++e5] << 58 >>> 58) : r3.push(
        s3 << 61 >>> 43 | t3[++e5] << 58 >>> 46 | t3[++e5] << 58 >>> 52 | t3[++e5] << 58 >>> 58
      );
  }
  return r3;
};

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/lexer/process.js
var convertToParserTokens = (e5) => {
  const t3 = [];
  let r3 = false;
  for (const s2 of e5)
    switch (s2.type) {
      case "{":
        return { errid: "NO_LCURLY", start: s2.start, end: s2.end };
      case "semicolon":
        return { errid: "NO_SEMICOLON", start: s2.start, end: s2.end };
      case "whitespace":
        ;
        r3 = true, t3.length > 0 && (t3[t3.length - 1].hasSpaceAfter = true);
        break;
      case "EOF":
        break;
      default:
        t3.push({ ...s2, hasSpaceBefore: r3, hasSpaceAfter: false }), r3 = false;
    }
  return t3;
};

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/lexer/tokens.js
var e2 = 10;
var t = 32;
var n = 45;
var s = 48;
var u = 57;
var r = 65;
var o = 92;
var l = 97;
var i = 122;
var a = 128;
var codepointsToTokens = (f, c = 0) => {
  const p = [];
  for (; c < f.length; c += 1) {
    const d = f.at(c), h = c;
    if (47 === d && 42 === f.at(c + 1)) {
      c += 2;
      for (let e5 = f.at(c); void 0 !== e5; e5 = f.at(++c))
        if (42 === e5 && 47 === f.at(c + 1)) {
          c += 1;
          break;
        }
    } else if (9 === d || d === t || d === e2) {
      let n2 = f.at(++c);
      for (; 9 === n2 || n2 === t || n2 === e2; ) n2 = f.at(++c);
      c -= 1;
      const s2 = p.at(-1);
      "whitespace" === (null == s2 ? void 0 : s2.type) ? (p.pop(), p.push({ type: "whitespace", start: s2.start, end: c })) : p.push({ type: "whitespace", start: h, end: c });
    } else if (34 === d) {
      const e5 = consumeString(f, c);
      if (null === e5) return { errid: "INVALID_STRING", start: c, end: c };
      const [t3, n2] = e5;
      c = t3, p.push({ type: "string", value: n2, start: h, end: c });
    } else if (35 === d) {
      if (c + 1 < f.length) {
        const t3 = f.at(c + 1);
        if (95 === t3 || t3 >= r && t3 <= 90 || t3 >= l && t3 <= i || t3 >= a || t3 >= s && t3 <= u || t3 === o && c + 2 < f.length && f.at(c + 2) !== e2) {
          const e5 = wouldStartIdentifier(f, c + 1) ? "id" : "unrestricted", t4 = consumeIdentUnsafe(f, c + 1);
          if (null !== t4) {
            const [n2, s2] = t4;
            c = n2, p.push({ type: "hash", value: s2.toLowerCase(), flag: e5, start: h, end: c });
            continue;
          }
        }
      }
      p.push({ type: "delim", value: d, start: h, end: c });
    } else if (39 === d) {
      const e5 = consumeString(f, c);
      if (null === e5) return { errid: "INVALID_STRING", start: c, end: c };
      const [t3, n2] = e5;
      c = t3, p.push({ type: "string", value: n2, start: h, end: c });
    } else if (40 === d) p.push({ type: "(", start: h, end: c });
    else if (41 === d) p.push({ type: ")", start: h, end: c });
    else if (43 === d) {
      const e5 = consumeNumeric(f, c);
      if (null === e5) p.push({ type: "delim", value: d, start: h, end: c });
      else {
        const [t3, n2] = e5;
        c = t3, "dimension" === n2[0] ? p.push({
          type: "dimension",
          value: n2[1],
          unit: n2[2].toLowerCase(),
          flag: "number",
          start: h,
          end: c
        }) : "number" === n2[0] ? p.push({ type: n2[0], value: n2[1], flag: n2[2], start: h, end: c }) : p.push({ type: n2[0], value: n2[1], flag: "number", start: h, end: c });
      }
    } else if (44 === d) p.push({ type: "comma", start: h, end: c });
    else if (d === n) {
      const e5 = consumeNumeric(f, c);
      if (null !== e5) {
        const [t4, n2] = e5;
        c = t4, "dimension" === n2[0] ? p.push({
          type: "dimension",
          value: n2[1],
          unit: n2[2].toLowerCase(),
          flag: "number",
          start: h,
          end: c
        }) : "number" === n2[0] ? p.push({ type: n2[0], value: n2[1], flag: n2[2], start: h, end: c }) : p.push({ type: n2[0], value: n2[1], flag: "number", start: h, end: c });
        continue;
      }
      if (c + 2 < f.length) {
        const e6 = f.at(c + 1), t4 = f.at(c + 2);
        if (e6 === n && 62 === t4) {
          ;
          c += 2, p.push({ type: "CDC", start: h, end: c });
          continue;
        }
      }
      const t3 = consumeIdentLike(f, c);
      if (null !== t3) {
        const [e6, n2, s2] = t3;
        c = e6, p.push({ type: s2, value: n2, start: h, end: c });
        continue;
      }
      p.push({ type: "delim", value: d, start: h, end: c });
    } else if (46 === d) {
      const e5 = consumeNumeric(f, c);
      if (null !== e5) {
        const [t3, n2] = e5;
        c = t3, "dimension" === n2[0] ? p.push({
          type: "dimension",
          value: n2[1],
          unit: n2[2].toLowerCase(),
          flag: "number",
          start: h,
          end: c
        }) : "number" === n2[0] ? p.push({ type: n2[0], value: n2[1], flag: n2[2], start: h, end: c }) : p.push({ type: n2[0], value: n2[1], flag: "number", start: h, end: c });
        continue;
      }
      p.push({ type: "delim", value: d, start: h, end: c });
    } else if (58 === d) p.push({ type: "colon", start: h, end: c });
    else if (59 === d) p.push({ type: "semicolon", start: h, end: c });
    else if (60 === d) {
      if (c + 3 < f.length) {
        const e5 = f.at(c + 1), t3 = f.at(c + 2), s2 = f.at(c + 3);
        if (33 === e5 && t3 === n && s2 === n) {
          ;
          c += 3, p.push({ type: "CDO", start: h, end: c });
          continue;
        }
      }
      p.push({ type: "delim", value: d, start: h, end: c });
    } else if (64 === d) {
      const e5 = consumeIdent(f, c + 1);
      if (null !== e5) {
        const [t3, n2] = e5;
        c = t3, p.push({ type: "at-keyword", value: n2.toLowerCase(), start: h, end: c });
        continue;
      }
      p.push({ type: "delim", value: d, start: h, end: c });
    } else if (91 === d) p.push({ type: "[", start: h, end: c });
    else if (93 === d) p.push({ type: "]", start: h, end: c });
    else if (123 === d) p.push({ type: "{", start: h, end: c });
    else if (125 === d) p.push({ type: "}", start: h, end: c });
    else if (d >= s && d <= u) {
      const e5 = consumeNumeric(f, c), [t3, n2] = e5;
      c = t3, "dimension" === n2[0] ? p.push({
        type: "dimension",
        value: n2[1],
        unit: n2[2].toLowerCase(),
        flag: "number",
        start: h,
        end: c
      }) : "number" === n2[0] ? p.push({ type: n2[0], value: n2[1], flag: n2[2], start: h, end: c }) : p.push({ type: n2[0], value: n2[1], flag: "number", start: h, end: c });
    } else if (95 === d || d >= r && d <= 90 || d >= l && d <= i || d >= a || d === o) {
      const e5 = consumeIdentLike(f, c);
      if (null === e5) p.push({ type: "delim", value: d, start: h, end: c });
      else {
        const [t3, n2, s2] = e5;
        c = t3, p.push({ type: s2, value: n2, start: h, end: c });
      }
    } else p.push({ type: "delim", value: d, start: h, end: c });
  }
  return p.push({ type: "EOF", start: c, end: c }), p;
};
var consumeString = (t3, n2) => {
  if (t3.length <= n2 + 1) return null;
  const s2 = t3.at(n2), u2 = [];
  for (let r3 = n2 + 1; r3 < t3.length; r3 += 1) {
    const n3 = t3.at(r3);
    if (n3 === s2) return [r3, String.fromCodePoint(...u2)];
    if (n3 === o) {
      const e5 = consumeEscape(t3, r3);
      if (null === e5) return null;
      const [n4, s3] = e5;
      u2.push(s3), r3 = n4;
    } else {
      if (n3 === e2) return null;
      u2.push(n3);
    }
  }
  return null;
};
var wouldStartIdentifier = (t3, s2) => {
  const u2 = t3.at(s2);
  if (void 0 === u2) return false;
  if (u2 === n) {
    const u3 = t3.at(s2 + 1);
    if (void 0 === u3) return false;
    if (u3 === n || 95 === u3 || u3 >= r && u3 <= 90 || u3 >= l && u3 <= i || u3 >= a) return true;
    if (u3 === o) {
      if (t3.length <= s2 + 2) return false;
      return t3.at(s2 + 2) !== e2;
    }
    return false;
  }
  if (95 === u2 || u2 >= r && u2 <= 90 || u2 >= l && u2 <= i || u2 >= a) return true;
  if (u2 === o) {
    if (t3.length <= s2 + 1) return false;
    return t3.at(s2 + 1) !== e2;
  }
  return false;
};
var consumeEscape = (n2, i3) => {
  if (n2.length <= i3 + 1) return null;
  if (n2.at(i3) !== o) return null;
  const a2 = n2.at(i3 + 1);
  if (a2 === e2) return null;
  if (a2 >= s && a2 <= u || a2 >= r && a2 <= 70 || a2 >= l && a2 <= 102) {
    const o3 = [a2], f = Math.min(i3 + 7, n2.length);
    let c = i3 + 2;
    for (; c < f; c += 1) {
      const e5 = n2.at(c);
      if (!(e5 >= s && e5 <= u || e5 >= r && e5 <= 70 || e5 >= l && e5 <= 102)) break;
      o3.push(e5);
    }
    if (c < n2.length) {
      const s2 = n2.at(c);
      9 !== s2 && s2 !== t && s2 !== e2 || (c += 1);
    }
    return [c - 1, Number.parseInt(String.fromCodePoint(...o3), 16)];
  }
  return [i3 + 1, a2];
};
var consumeNumeric = (e5, t3) => {
  const n2 = consumeNumber(e5, t3);
  if (null === n2) return null;
  const [s2, u2, r3] = n2, o3 = consumeIdent(e5, s2 + 1);
  if (null !== o3) {
    const [e6, t4] = o3;
    return [e6, ["dimension", u2, t4]];
  }
  return s2 + 1 < e5.length && 37 === e5.at(s2 + 1) ? [s2 + 1, ["percentage", u2]] : [s2, ["number", u2, r3]];
};
var consumeNumber = (e5, t3) => {
  const r3 = e5.at(t3);
  if (void 0 === r3) return null;
  let o3 = "integer";
  const l2 = [];
  for (43 !== r3 && r3 !== n || (t3 += 1, r3 === n && l2.push(n)); t3 < e5.length; ) {
    const n2 = e5.at(t3);
    if (!(n2 >= s && n2 <= u)) break;
    l2.push(n2), t3 += 1;
  }
  if (t3 + 1 < e5.length) {
    const n2 = e5.at(t3), r4 = e5.at(t3 + 1);
    if (46 === n2 && r4 >= s && r4 <= u)
      for (l2.push(n2, r4), o3 = "number", t3 += 2; t3 < e5.length; ) {
        const n3 = e5.at(t3);
        if (!(n3 >= s && n3 <= u)) break;
        l2.push(n3), t3 += 1;
      }
  }
  if (t3 + 1 < e5.length) {
    const r4 = e5.at(t3), i4 = e5.at(t3 + 1), a3 = e5.at(t3 + 2);
    if (69 === r4 || 101 === r4) {
      let r5 = false;
      if (i4 >= s && i4 <= u ? (l2.push(69, i4), t3 += 2, r5 = true) : (i4 === n || 43 === i4) && void 0 !== a3 && a3 >= s && a3 <= u && (l2.push(69), i4 === n && l2.push(n), l2.push(a3), t3 += 3, r5 = true), r5)
        for (o3 = "number"; t3 < e5.length; ) {
          const n2 = e5.at(t3);
          if (!(n2 >= s && n2 <= u)) break;
          l2.push(n2), t3 += 1;
        }
    }
  }
  const i3 = String.fromCodePoint(...l2);
  let a2 = "number" === o3 ? Number.parseFloat(i3) : Number.parseInt(i3);
  return 0 === a2 && (a2 = 0), Number.isNaN(a2) ? null : [t3 - 1, a2, o3];
};
var consumeIdentUnsafe = (e5, t3) => {
  if (e5.length <= t3) return null;
  const o3 = [];
  for (let f = e5.at(t3); t3 < e5.length; f = e5.at(++t3)) {
    if (!(f === n || 95 === f || f >= r && f <= 90 || f >= l && f <= i || f >= a || f >= s && f <= u)) {
      {
        const n2 = consumeEscape(e5, t3);
        if (null !== n2) {
          const [e6, s2] = n2;
          o3.push(s2), t3 = e6;
          continue;
        }
      }
      break;
    }
    o3.push(f);
  }
  return 0 === t3 ? null : [t3 - 1, String.fromCodePoint(...o3)];
};
var consumeIdent = (e5, t3) => wouldStartIdentifier(e5, t3) ? consumeIdentUnsafe(e5, t3) : null;
var consumeUrl = (n2, s2) => {
  let u2 = n2.at(s2);
  for (; 9 === u2 || u2 === t || u2 === e2; ) u2 = n2.at(++s2);
  const r3 = [];
  let l2 = false;
  for (; s2 < n2.length; ) {
    if (41 === u2) return [s2, String.fromCodePoint(...r3)];
    if (34 === u2 || 39 === u2 || 40 === u2) return null;
    if (9 === u2 || u2 === t || u2 === e2) !l2 && r3.length > 0 && (l2 = true);
    else if (u2 === o) {
      const e5 = consumeEscape(n2, s2);
      if (null === e5 || l2) return null;
      const [t3, u3] = e5;
      r3.push(u3), s2 = t3;
    } else {
      if (l2) return null;
      r3.push(u2);
    }
    u2 = n2.at(++s2);
  }
  return null;
};
var consumeIdentLike = (n2, s2) => {
  const u2 = consumeIdent(n2, s2);
  if (null === u2) return null;
  const [r3, o3] = u2;
  if ("url" === o3.toLowerCase()) {
    if (n2.length > r3 + 1) {
      if (40 === n2.at(r3 + 1)) {
        for (let s3 = 2; r3 + s3 < n2.length; s3 += 1) {
          const u3 = n2.at(r3 + s3);
          if (34 === u3 || 39 === u3) return [r3 + 1, o3.toLowerCase(), "function"];
          if (9 !== u3 && u3 !== t && u3 !== e2) {
            const e5 = consumeUrl(n2, r3 + s3);
            if (null === e5) return null;
            const [t3, u4] = e5;
            return [t3, u4, "url"];
          }
        }
        return [r3 + 1, o3.toLowerCase(), "function"];
      }
    }
  } else if (n2.length > r3 + 1) {
    if (40 === n2.at(r3 + 1)) return [r3 + 1, o3.toLowerCase(), "function"];
  }
  return [r3, o3.toLowerCase(), "ident"];
};

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/lexer/lexer.js
var lexer = (m) => {
  const e5 = codepointsToTokens(readCodepoints(m));
  return isParserError(e5) ? e5 : convertToParserTokens(e5);
};

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/utils.js
var isParserError2 = (r3) => "object" == typeof r3 && null !== r3 && "errid" in r3;

// node_modules/.pnpm/media-query-parser@3.0.0-beta.1/node_modules/media-query-parser/dist/index.js
var parseMediaQueryList = (t3) => {
  const e5 = lexer(t3);
  return isParserError2(e5) ? invertParserError(e5) : deleteUndefinedValues(flattenMediaQueryList(readMediaQueryList(e5)));
};

// node_modules/.pnpm/media-query-fns@2.0.0/node_modules/media-query-fns/dist/helpers.js
var DISCRETE_FEATURES = {
  "any-hover": { none: 1, hover: 1 },
  "any-pointer": { none: 1, coarse: 1, fine: 1 },
  "color-gamut": { srgb: 1, p3: 1, rec2020: 1 },
  grid: { 0: 1, 1: 1 },
  hover: { none: 1, hover: 1 },
  "overflow-block": { none: 1, scroll: 1, paged: 1 },
  "overflow-inline": { none: 1, scroll: 1 },
  pointer: { none: 1, coarse: 1, fine: 1 },
  scan: { interlace: 1, progressive: 1 },
  update: { none: 1, slow: 1, fast: 1 },
  "display-mode": { fullscreen: 1, standalone: 1, "minimal-ui": 1, browser: 1 },
  "dynamic-range": { standard: 1, high: 1 },
  "environment-blending": { opaque: 1, additive: 1, subtractive: 1 },
  "forced-colors": { none: 1, active: 1 },
  "inverted-colors": { none: 1, inverted: 1 },
  "nav-controls": { none: 1, back: 1 },
  "prefers-color-scheme": { light: 1, dark: 1 },
  "prefers-contrast": { "no-preference": 1, less: 1, more: 1, custom: 1 },
  "prefers-reduced-data": { "no-preference": 1, reduce: 1 },
  "prefers-reduced-motion": { "no-preference": 1, reduce: 1 },
  "prefers-reduced-transparency": { "no-preference": 1, reduce: 1 },
  scripting: { none: 1, "initial-only": 1, enabled: 1 },
  "video-color-gamut": { srgb: 1, p3: 1, rec2020: 1 },
  "video-dynamic-range": { standard: 1, high: 1 }
};
var RANGE_NUMBER_FEATURES = {
  color: { feature: "color", type: "integer", bounds: [true, 0, 1 / 0, false] },
  "color-index": { feature: "color-index", type: "integer", bounds: [true, 0, 1 / 0, false] },
  monochrome: { feature: "monochrome", type: "integer", bounds: [true, 0, 1 / 0, false] },
  "device-height": { feature: "device-height", type: "length", bounds: [true, 0, 1 / 0, false] },
  "device-width": { feature: "device-width", type: "length", bounds: [true, 0, 1 / 0, false] },
  height: { feature: "height", type: "length", bounds: [true, 0, 1 / 0, false] },
  width: { feature: "width", type: "length", bounds: [true, 0, 1 / 0, false] },
  resolution: { feature: "resolution", type: "resolution", bounds: [true, 0, 1 / 0, false] },
  "horizontal-viewport-segments": {
    feature: "horizontal-viewport-segments",
    type: "integer",
    bounds: [true, 0, 1 / 0, false]
  },
  "vertical-viewport-segments": {
    feature: "vertical-viewport-segments",
    type: "integer",
    bounds: [true, 0, 1 / 0, false]
  }
};
var RANGE_RATIO_FEATURES = {
  "aspect-ratio": { feature: "aspect-ratio", type: "ratio", bounds: [false, [0, 1], [1 / 0, 1], false] },
  "device-aspect-ratio": {
    feature: "device-aspect-ratio",
    type: "ratio",
    bounds: [false, [0, 1], [1 / 0, 1], false]
  }
};
var permToConditionPairs = (e5) => Object.entries(e5).filter((e6) => void 0 !== e6[1]);
var e3 = new Set(Object.keys(DISCRETE_FEATURES));
var hasDiscreteKey = (t3) => e3.has(t3[0]);
var isDiscreteKey = (t3) => e3.has(t3);
var t2 = new Set(Object.keys(RANGE_RATIO_FEATURES));
var hasRangeRatioKey = (e5) => t2.has(e5[0]);
var isRangeRatioKey = (e5) => t2.has(e5);
var o2 = new Set(Object.keys(RANGE_NUMBER_FEATURES));
var hasRangeNumberKey = (e5) => o2.has(e5[0]);
var isRangeNumberKey = (e5) => o2.has(e5);
var hasRangeKey = (e5) => hasRangeNumberKey(e5) || hasRangeRatioKey(e5);
var isRangeKey = (e5) => isRangeNumberKey(e5) || isRangeRatioKey(e5);
var isFeatureKey = (e5) => isRangeNumberKey(e5) || isRangeRatioKey(e5) || isDiscreteKey(e5);
var attachPair = (e5, t3) => {
  e5[t3[0]] = t3[1];
};
var andRanges = (...e5) => e5.reduce(
  (e6, t3) => "{true}" === e6 ? t3 : "{true}" === t3 ? e6 : "{false}" === e6 || "{false}" === t3 ? "{false}" : ((e7, t4) => {
    const [o3, n2, r3, s2] = e7, a2 = "number" == typeof n2 ? n2 : n2[0] / n2[1], i3 = "number" == typeof r3 ? r3 : r3[0] / r3[1], [c, p, u2, d] = t4, l2 = "number" == typeof p ? p : p[0] / p[1], g = "number" == typeof u2 ? u2 : u2[0] / u2[1];
    let f = o3 !== c && !o3;
    a2 !== l2 && (f = a2 > l2);
    let h = s2 !== d && !s2;
    i3 !== g && (h = i3 < g);
    const y = f ? o3 : c, R = f ? n2 : p, b = h ? r3 : u2, m = h ? s2 : d;
    return R > b || R === b && (!y || !m) ? "{false}" : [y, R, b, m];
  })(e6, t3),
  "{true}"
);
var boundRange = (e5) => {
  if (hasRangeRatioKey(e5)) {
    const { bounds: t3 } = RANGE_RATIO_FEATURES[e5[0]], o3 = andRanges(e5[1], t3);
    if ("string" == typeof o3) return o3;
    if (o3[0] === t3[0] && o3[1][0] === t3[1][0] && o3[1][1] === t3[1][1] && o3[2][0] === t3[2][0] && o3[2][1] === t3[2][1] && o3[3] === t3[3])
      return "{true}";
    {
      const e6 = o3[1][0] / o3[1][1], t4 = o3[2][0] / o3[2][1];
      return e6 > t4 || e6 === t4 && (!o3[0] || !o3[3]) ? "{false}" : o3;
    }
  }
  {
    const { bounds: t3 } = RANGE_NUMBER_FEATURES[e5[0]], o3 = andRanges(e5[1], t3);
    return "string" == typeof o3 ? o3 : o3[0] === t3[0] && o3[1] === t3[1] && o3[2] === t3[2] && o3[3] === t3[3] ? "{true}" : o3[1] > o3[2] || o3[1] === o3[2] && (!o3[0] || !o3[3]) ? "{false}" : o3;
  }
};
var notRatioRange = (e5) => {
  if ("string" == typeof e5[1]) throw new Error("expected range");
  const { bounds: t3 } = RANGE_RATIO_FEATURES[e5[0]], [o3, n2, r3, s2] = e5[1], a2 = n2[0] / n2[1], i3 = r3[0] / r3[1], c = t3[1][0] / t3[1][1], p = t3[0], u2 = t3[2][0] / t3[2][1], d = t3[3], l2 = i3 > u2 || i3 === u2 && !(d && !s2);
  return a2 < c || a2 === c && !(p && !o3) ? l2 ? "{false}" : [[!s2, r3, t3[2], t3[3]]] : l2 ? [[t3[0], t3[1], n2, !o3]] : [
    [t3[0], t3[1], n2, !o3],
    [!s2, r3, t3[2], t3[3]]
  ];
};
function notNumberRange(e5) {
  if ("string" == typeof e5[1]) throw new Error("expected range");
  const { bounds: t3 } = RANGE_NUMBER_FEATURES[e5[0]], [o3, n2, r3, s2] = e5[1], a2 = t3[1], i3 = t3[0], c = t3[2], p = t3[3], u2 = r3 > c || r3 === c && !(p && !s2);
  return n2 < a2 || n2 === a2 && !(i3 && !o3) ? u2 ? "{false}" : [[!s2, r3, t3[2], t3[3]]] : u2 ? [[t3[0], t3[1], n2, !o3]] : [
    [t3[0], t3[1], n2, !o3],
    [!s2, r3, t3[2], t3[3]]
  ];
}

// node_modules/.pnpm/media-query-fns@2.0.0/node_modules/media-query-fns/dist/units.js
var i2 = {
  widthPx: 1920,
  heightPx: 1080,
  writingMode: "horizontal-tb",
  emPx: 16,
  lhPx: 16,
  exPx: 8,
  chPx: 8,
  capPx: 11,
  icPx: 16
};
var convertToUnit = (e5, t3) => {
  if ("number" === e5.type) return { type: "number", value: e5.value };
  if ("dimension" === e5.type) {
    let n2;
    switch (e5.unit) {
      case "s":
      case "ms":
        n2 = "time";
        break;
      case "hz":
      case "khz":
        n2 = "frequency";
        break;
      case "dpi":
      case "dpcm":
      case "dppx":
      case "x":
        n2 = "resolution";
        break;
      default:
        n2 = "length";
    }
    if ("px" === e5.unit) return { type: "dimension", subtype: "length", px: e5.value };
    if ("time" === n2)
      return {
        type: "dimension",
        subtype: "time",
        ms: "s" === e5.unit ? Math.round(1e3 * e5.value) : e5.value
      };
    if ("frequency" === n2)
      return {
        type: "dimension",
        subtype: "frequency",
        hz: "khz" === e5.unit ? Math.round(1e3 * e5.value) : e5.value
      };
    if ("resolution" === n2) {
      let t4 = e5.value;
      return "dpi" === e5.unit ? t4 = Number.parseFloat((0.0104166667 * e5.value).toFixed(3)) : "dpcm" === e5.unit && (t4 = Number.parseFloat((0.0264583333 * e5.value).toFixed(3))), { type: "dimension", subtype: "resolution", dppx: t4 };
    }
    if (e5.unit in t3) {
      const n3 = t3[e5.unit];
      return { type: "dimension", subtype: "length", px: Number.parseFloat((e5.value * n3).toFixed(3)) };
    }
    return { type: "ident", value: "{never}" };
  }
  return "ident" === e5.type ? "infinite" === e5.value ? { type: "infinite" } : { type: "ident", value: e5.value } : { type: "ratio", numerator: e5.numerator, denominator: e5.denominator };
};
var compileStaticUnitConversions = (e5) => {
  let t3 = {};
  "number" == typeof e5.emPx && (t3 = {
    exPx: Math.round(0.5 * e5.emPx),
    chPx: Math.round(0.5 * e5.emPx),
    capPx: Math.round(0.7 * e5.emPx),
    icPx: Math.round(e5.emPx)
  });
  const n2 = { ...i2, ...t3, ...e5 }, {
    widthPx: r3,
    heightPx: o3,
    writingMode: a2,
    emPx: u2,
    lhPx: p,
    exPx: l2,
    chPx: m,
    capPx: s2,
    icPx: f
  } = n2, v = r3 / 100, c = o3 / 100;
  return {
    em: u2,
    rem: u2,
    lh: p,
    rlh: p,
    ex: l2,
    ch: m,
    cap: s2,
    ic: f,
    vw: v,
    vh: c,
    vmin: Math.min(c, v),
    vmax: Math.max(c, v),
    vi: "horizontal-tb" === a2 ? v : c,
    vb: "horizontal-tb" === a2 ? c : v,
    cm: 37.79527559,
    mm: 0.03779527559,
    in: 96,
    q: 0.009448818898,
    pc: 16,
    pt: 16
  };
};
var r2 = { "<": ">", "<=": ">=", ">": "<", ">=": "<=" };
var simplifyMediaFeature = (n2, i3) => {
  if ("range" === n2.context) {
    if (isRangeKey(n2.feature)) {
      const { range: e5, feature: t3 } = n2;
      return void 0 !== e5.leftToken && void 0 !== e5.rightToken ? "<" === e5.leftOp || "<=" === e5.leftOp ? {
        type: "double",
        name: t3,
        minOp: e5.leftOp,
        min: convertToUnit(e5.leftToken, i3),
        maxOp: e5.rightOp,
        max: convertToUnit(e5.rightToken, i3)
      } : {
        type: "double",
        name: t3,
        minOp: ">" === e5.rightOp ? "<" : "<=",
        min: convertToUnit(e5.rightToken, i3),
        maxOp: e5.leftOp ? "<" : "<=",
        max: convertToUnit(e5.leftToken, i3)
      } : void 0 === e5.rightToken ? "=" === e5.leftOp ? { type: "equals", name: t3, value: convertToUnit(e5.leftToken, i3) } : { type: "single", name: t3, op: r2[e5.leftOp], value: convertToUnit(e5.leftToken, i3) } : "=" === e5.rightOp ? { type: "equals", name: t3, value: convertToUnit(e5.rightToken, i3) } : { type: "single", name: t3, op: e5.rightOp, value: convertToUnit(e5.rightToken, i3) };
    }
  } else if ("value" === n2.context) {
    if ("orientation" === n2.feature) {
      if (void 0 === n2.prefix && "ident" === n2.value.type) {
        if ("portrait" === n2.value.value)
          return {
            type: "single",
            name: "aspect-ratio",
            op: "<=",
            value: { type: "ratio", numerator: 1, denominator: 1 }
          };
        if ("landscape" === n2.value.value)
          return {
            type: "single",
            name: "aspect-ratio",
            op: ">=",
            value: { type: "ratio", numerator: 1, denominator: 1 }
          };
      }
    } else if (isFeatureKey(n2.feature)) {
      if (void 0 === n2.prefix)
        return { type: "equals", name: n2.feature, value: convertToUnit(n2.value, i3) };
      if (isRangeKey(n2.feature))
        return "min" === n2.prefix ? { type: "single", name: n2.feature, op: ">=", value: convertToUnit(n2.value, i3) } : { type: "single", name: n2.feature, op: "<=", value: convertToUnit(n2.value, i3) };
    }
  } else {
    if ("orientation" === n2.feature)
      return {
        type: "double",
        name: "aspect-ratio",
        min: { type: "ratio", numerator: 0, denominator: 1 },
        minOp: "<",
        maxOp: "<",
        max: { type: "ratio", numerator: Number.POSITIVE_INFINITY, denominator: 1 }
      };
    if (isFeatureKey(n2.feature)) return { type: "boolean", name: n2.feature };
  }
  return { type: "invalid", name: n2.feature };
};
var getRatio = (e5) => "number" === e5.type && e5.value > 0 ? [e5.value, 1] : "ratio" === e5.type ? [e5.numerator, e5.denominator] : null;
var getValue = (e5, t3) => {
  const i3 = RANGE_NUMBER_FEATURES[t3];
  if ("infinite" === e5.type) {
    if ("resolution" === t3) return Number.POSITIVE_INFINITY;
  } else if ("integer" === i3.type) {
    if ("number" === e5.type && Number.isInteger(e5.value)) return e5.value;
  } else if ("resolution" === i3.type) {
    if ("dimension" === e5.type && "resolution" === e5.subtype) return e5.dppx;
  } else if ("length" === i3.type) {
    if ("dimension" === e5.type && "length" === e5.subtype) return e5.px;
    if ("number" === e5.type && 0 === e5.value) return 0;
  }
  return null;
};

// node_modules/.pnpm/media-query-fns@2.0.0/node_modules/media-query-fns/dist/compile.js
var andPerms = (e5, r3) => {
  const t3 = [];
  for (const o3 of e5)
    for (const e6 of r3) {
      const r4 = mergePerms(o3, e6);
      Object.keys(r4).length > 0 && t3.push(r4);
    }
  return t3;
};
var mergePerms = (e5, r3) => {
  const a2 = {};
  for (const r4 of permToConditionPairs(e5)) void 0 !== r4[1] && attachPair(a2, r4);
  for (const e6 of permToConditionPairs(r3))
    if (e6[0] in a2) {
      if (void 0 !== a2[e6[0]]) {
        const r4 = a2;
        if ("media-type" === e6[0]) ;
        else if ("invalid-features" === e6[0]) r4[e6[0]].push(...e6[1]);
        else if ("{false}" === r4[e6[0]] || "{false}" === e6[1]) r4[e6[0]] = "{false}";
        else if ("{true}" === r4[e6[0]]) attachPair(r4, e6);
        else if ("{true}" === e6[1]) ;
        else {
          const r5 = a2;
          hasRangeNumberKey(e6) || hasRangeRatioKey(e6) ? attachPair(r5, [e6[0], andRanges(r5[e6[0]], e6[1])]) : "color-gamut" === e6[0] || "video-color-gamut" === e6[0] ? r5[e6[0]] = [
            r5[e6[0]][0] && e6[1][0],
            r5[e6[0]][1] && e6[1][1],
            r5[e6[0]][2] && e6[1][2],
            r5[e6[0]][3] && e6[1][3]
          ] : attachPair(r5, [e6[0], r5[e6[0]] === e6[1] ? r5[e6[0]] : "{false}"]);
        }
      }
    } else attachPair(a2, e6);
  return a2;
};
var notPerms = (e5) => e5.map((e6) => invertPerm(e6)).reduce((e6, r3) => andPerms(e6, r3));
var invertPerm = (e5) => {
  const r3 = permToConditionPairs(e5), o3 = [];
  for (const e6 of r3)
    if (void 0 !== e6[1]) {
      let r4, t3;
      if ("invalid-features" === e6[0]) return [{ [e6[0]]: e6[1] }];
      if ("media-type" === e6[0]) continue;
      if (r4 = e6, "{false}" === e6[1]) t3 = [[e6[0], "{true}"]];
      else if ("{true}" === e6[1]) t3 = [[e6[0], "{false}"]];
      else if (hasDiscreteKey(e6))
        if ("color-gamut" === e6[0]) {
          const r5 = e6[1];
          t3 = [["color-gamut", [!r5[0], !r5[1], !r5[2], !r5[3]]]];
        } else
          t3 = "grid" === e6[0] ? [["grid", 0 === e6[1] ? 1 : 0]] : Object.keys(DISCRETE_FEATURES[e6[0]]).filter((r5) => r5 !== e6[1]).map((r5) => [e6[0], r5]);
      else if (hasRangeRatioKey(e6)) {
        const r5 = notRatioRange(e6);
        t3 = ("{false}" === r5 ? ["{false}"] : r5).map((r6) => [e6[0], r6]);
      } else {
        const r5 = notNumberRange(e6);
        t3 = ("{false}" === r5 ? ["{false}"] : r5).map((r6) => [e6[0], r6]);
      }
      o3.push([r4, t3]);
    }
  const s2 = [];
  for (const [, e6] of o3) for (const r4 of e6) s2.push({ [r4[0]]: r4[1] });
  return s2;
};
var mediaFeatureToPerms = (e5, r3) => {
  const t3 = simplifyMediaFeature(e5, r3), o3 = [{ "invalid-features": [e5.feature] }];
  if ("invalid" === t3.type) return o3;
  if ("boolean" === t3.type)
    return "color-gamut" === t3.name ? [{ "color-gamut": [false, true, true, true] }] : "grid" === t3.name ? [{ grid: 1 }] : isDiscreteKey(t3.name) ? invertPerm({ [t3.name]: "none" }) : isRangeRatioKey(t3.name) ? [{ [t3.name]: [false, [0, 1], [Number.POSITIVE_INFINITY, 1], true] }] : [{ [t3.name]: [false, 0, Number.POSITIVE_INFINITY, true] }];
  if (isDiscreteKey(t3.name)) {
    if ("equals" === t3.type) {
      const e6 = t3.value;
      if ("grid" === t3.name) {
        if ("number" === e6.type && (0 === e6.value || 1 === e6.value)) return [{ grid: e6.value }];
      } else if ("ident" === e6.type && e6.value in DISCRETE_FEATURES[t3.name]) {
        if ("color-gamut" !== t3.name) return [{ [t3.name]: e6.value }];
        {
          const r4 = ["srgb", "p3", "rec2020"].indexOf(e6.value);
          if (-1 !== r4) return [{ "color-gamut": [false, r4 <= 0, r4 <= 1, r4 <= 2] }];
        }
      }
    }
    return o3;
  }
  if (isRangeRatioKey(t3.name)) {
    let e6 = null;
    if ("equals" === t3.type) {
      const r4 = getRatio(t3.value);
      null !== r4 && (e6 = [true, r4, r4, true]);
    } else if ("single" === t3.type) {
      const r4 = getRatio(t3.value);
      null !== r4 && (e6 = "<" === t3.op ? [true, [Number.NEGATIVE_INFINITY, 1], r4, false] : "<=" === t3.op ? [true, [Number.NEGATIVE_INFINITY, 1], r4, true] : ">" === t3.op ? [false, r4, [Number.POSITIVE_INFINITY, 1], true] : [true, r4, [Number.POSITIVE_INFINITY, 1], true]);
    } else if ("double" === t3.type) {
      const r4 = getRatio(t3.min), o4 = getRatio(t3.max);
      null !== r4 && null !== o4 && (e6 = ["<=" === t3.minOp, r4, o4, "<=" === t3.maxOp]);
    }
    return null === e6 ? o3 : [{ [t3.name]: boundRange([t3.name, e6]) }];
  }
  {
    let e6 = null;
    if ("equals" === t3.type) {
      const r4 = getValue(t3.value, t3.name);
      null !== r4 && (e6 = [true, r4, r4, true]);
    } else if ("single" === t3.type) {
      const r4 = getValue(t3.value, t3.name);
      null !== r4 && (e6 = "<" === t3.op ? [true, Number.NEGATIVE_INFINITY, r4, false] : "<=" === t3.op ? [true, Number.NEGATIVE_INFINITY, r4, true] : ">" === t3.op ? [false, r4, Number.POSITIVE_INFINITY, true] : [true, r4, Number.POSITIVE_INFINITY, true]);
    } else if ("double" === t3.type) {
      const r4 = getValue(t3.min, t3.name), o4 = getValue(t3.max, t3.name);
      null !== r4 && null !== o4 && (e6 = ["<=" === t3.minOp, r4, o4, "<=" === t3.maxOp]);
    }
    return null === e6 ? o3 : [{ [t3.name]: boundRange([t3.name, e6]) }];
  }
};
var mediaConditionToPerms = (e5, r3) => {
  const t3 = [];
  for (const o3 of e5.children)
    "context" in o3 ? t3.push(mediaFeatureToPerms(o3, r3)) : t3.push(mediaConditionToPerms(o3, r3));
  return "or" === e5.operator || void 0 === e5.operator ? t3.flat() : "and" === e5.operator ? t3.reduce((e6, r4) => andPerms(e6, r4)) : notPerms(t3[0]);
};
var simplifyPerms = (e5) => {
  const r3 = [], o3 = /* @__PURE__ */ new Set(), n2 = /* @__PURE__ */ new Set();
  for (const i3 of e5) {
    let e6 = false;
    if (Array.isArray(i3["invalid-features"]) && i3["invalid-features"].length > 0) {
      for (const e7 of i3["invalid-features"]) o3.add(e7);
      e6 = true;
    }
    const a2 = {};
    for (const r4 of permToConditionPairs(i3))
      if ("invalid-features" !== r4[0]) {
        if ("color-gamut" === r4[0]) {
          const e7 = r4[1].toString();
          "false,false,false,false" === e7 ? r4[1] = "{false}" : "true,true,true,true" === e7 && (r4[1] = "{true}");
        } else hasRangeKey(r4) && (r4[1] = boundRange(r4));
        "{false}" === r4[1] ? (n2.add(r4[0]), e6 = true) : "{true}" === r4[1] || "media-type" === r4[0] && "all" === r4[1] || attachPair(a2, r4);
      }
    e6 || r3.push(a2);
  }
  return { simplePerms: r3, invalidFeatures: [...o3].sort(), falseFeatures: [...n2].sort() };
};
var compileAST = (e5, r3 = {}) => {
  const t3 = compileStaticUnitConversions(r3), o3 = [];
  for (const r4 of e5.mediaQueries) {
    const e6 = [];
    "not" === r4.prefix ? ("print" === r4.mediaType ? e6.push({ "media-type": "not-print" }) : "screen" === r4.mediaType && e6.push({ "media-type": "not-screen" }), void 0 !== r4.mediaCondition && e6.push(...notPerms(mediaConditionToPerms(r4.mediaCondition, t3)))) : void 0 === r4.mediaCondition ? e6.push({ "media-type": r4.mediaType }) : e6.push(
      ...mediaConditionToPerms(r4.mediaCondition, t3).map((e7) => ({
        ...e7,
        "media-type": r4.mediaType
      }))
    ), o3.push(...e6);
  }
  return simplifyPerms(o3);
};
var compileQuery = (t3, o3 = {}) => {
  const n2 = parseMediaQueryList(t3);
  if (isParserError2(n2))
    throw new Error(`Error parsing media query list: ${n2.errid} at chars ${n2.start}:${n2.end}`);
  return compileAST(n2, o3);
};

// node_modules/.pnpm/media-query-fns@2.0.0/node_modules/media-query-fns/dist/matches.js
var DESKTOP_ENVIRONMENT = {
  mediaType: "screen",
  anyHover: "hover",
  anyPointer: "fine",
  colorGamut: "srgb-but-not-p3",
  grid: "bitmap",
  hover: "hover",
  overflowBlock: "scroll",
  overflowInline: "scroll",
  pointer: "fine",
  scan: "progressive",
  update: "fast",
  colorIndex: "none",
  colorBits: 8,
  monochromeBits: "not-monochrome",
  displayMode: "browser",
  dynamicRange: "not-hdr",
  environmentBlending: "opaque",
  forcedColors: "none",
  invertedColors: "none",
  navControls: "back",
  prefersColorScheme: "no-preference",
  prefersContrast: "no-preference",
  prefersReducedData: "no-preference",
  prefersReducedMotion: "no-preference",
  prefersReducedTransparency: "no-preference",
  scripting: "enabled",
  videoColorGamut: "srgb-but-not-p3",
  videoDynamicRange: "not-hdr",
  horizontalViewportSegments: 1,
  verticalViewportSegments: 1
};
var e4 = (e5) => new Error(`Invalid option: ${e5}`);
var validateEnv = (o3) => {
  if ("screen" !== o3.mediaType && "print" !== o3.mediaType && "not-screen-or-print" !== o3.mediaType)
    throw e4("mediaType");
  if ("none" !== o3.anyHover && "hover" !== o3.anyHover) throw e4("anyHover");
  if ("none" !== o3.anyPointer && "coarse" !== o3.anyPointer && "fine" !== o3.anyPointer)
    throw e4("anyPointer");
  if ("not-srgb" !== o3.colorGamut && "srgb-but-not-p3" !== o3.colorGamut && "p3-but-not-rec2020" !== o3.colorGamut && "rec2020" !== o3.colorGamut)
    throw e4("colorGamut");
  if ("bitmap" !== o3.grid && "grid" !== o3.grid) throw e4("grid");
  if ("none" !== o3.hover && "hover" !== o3.hover) throw e4("hover");
  if ("none" !== o3.overflowBlock && "scroll" !== o3.overflowBlock && "paged" !== o3.overflowBlock)
    throw e4("overflowBlock");
  if ("none" !== o3.overflowInline && "scroll" !== o3.overflowInline) throw e4("overflowInline");
  if ("none" !== o3.pointer && "coarse" !== o3.pointer && "fine" !== o3.pointer) throw e4("pointer");
  if ("interlace" !== o3.scan && "progressive" !== o3.scan) throw e4("scan");
  if ("none" !== o3.update && "slow" !== o3.update && "fast" !== o3.update) throw e4("update");
  if (!(Number.isInteger(o3.widthPx) && o3.widthPx >= 0)) throw e4("widthPx");
  if (!(Number.isInteger(o3.heightPx) && o3.heightPx >= 0)) throw e4("heightPx");
  if (!(Number.isInteger(o3.deviceWidthPx) && o3.deviceWidthPx >= 0)) throw e4("deviceWidthPx");
  if (!(Number.isInteger(o3.deviceHeightPx) && o3.deviceHeightPx >= 0)) throw e4("deviceHeightPx");
  if (!(Number.isInteger(o3.colorBits) && o3.colorBits >= 0)) throw e4("colorBits");
  if (o3.dppx <= 0) throw e4("dppx");
  if ("not-monochrome" !== o3.monochromeBits && !(Number.isInteger(o3.monochromeBits) && o3.monochromeBits >= 0))
    throw e4("monochromeBits");
  if ("none" !== o3.colorIndex && !(Number.isInteger(o3.colorIndex) && o3.colorIndex >= 0))
    throw e4("colorIndex");
};
var matches = (e5, o3) => {
  const r3 = { ...DESKTOP_ENVIRONMENT, ...o3 };
  validateEnv(r3);
  for (const o4 of e5.simplePerms) {
    let e6 = true;
    for (const i3 in o4) {
      const t3 = i3, n2 = o4;
      if ("media-type" === t3) {
        const o5 = n2[t3];
        if ("print" === o5) {
          if ("screen" === r3.mediaType || "not-screen-or-print" === r3.mediaType) {
            e6 = false;
            break;
          }
        } else if ("screen" === o5) {
          if ("print" === r3.mediaType || "not-screen-or-print" === r3.mediaType) {
            e6 = false;
            break;
          }
        } else if ("not-screen" === o5) {
          if ("screen" === r3.mediaType) {
            e6 = false;
            break;
          }
        } else if ("print" === r3.mediaType) {
          e6 = false;
          break;
        }
      } else if ("any-hover" === t3) {
        if (n2[t3] !== r3.anyHover) {
          e6 = false;
          break;
        }
      } else if ("hover" === t3) {
        if (n2[t3] !== r3.hover) {
          e6 = false;
          break;
        }
      } else if ("any-pointer" === t3) {
        if (n2[t3] !== r3.anyPointer) {
          e6 = false;
          break;
        }
      } else if ("pointer" === t3) {
        if (n2[t3] !== r3.pointer) {
          e6 = false;
          break;
        }
      } else if ("grid" === t3) {
        const o5 = n2[t3];
        if (0 === o5 && "grid" === r3.grid || 1 === o5 && "bitmap" === r3.grid) {
          e6 = false;
          break;
        }
      } else if ("color-gamut" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if ("not-srgb" === r3.colorGamut && !o5 || "srgb-but-not-p3" === r3.colorGamut && !i4 || "p3-but-not-rec2020" === r3.colorGamut && !s2 || "rec2020" === r3.colorGamut && !c) {
          e6 = false;
          break;
        }
      } else if ("video-color-gamut" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if ("not-srgb" === r3.videoColorGamut && !o5 || "srgb-but-not-p3" === r3.videoColorGamut && !i4 || "p3-but-not-rec2020" === r3.videoColorGamut && !s2 || "rec2020" === r3.videoColorGamut && !c) {
          e6 = false;
          break;
        }
      } else if ("overflow-block" === t3) {
        if (n2[t3] !== r3.overflowBlock) {
          e6 = false;
          break;
        }
      } else if ("overflow-inline" === t3) {
        if (n2[t3] !== r3.overflowInline) {
          e6 = false;
          break;
        }
      } else if ("scan" === t3) {
        if (n2[t3] !== r3.scan) {
          e6 = false;
          break;
        }
      } else if ("update" === t3) {
        if (n2[t3] !== r3.update) {
          e6 = false;
          break;
        }
      } else if ("scripting" === t3) {
        if (n2[t3] !== r3.scripting) {
          e6 = false;
          break;
        }
      } else if ("display-mode" === t3) {
        if (n2[t3] !== r3.displayMode) {
          e6 = false;
          break;
        }
      } else if ("environment-blending" === t3) {
        if (n2[t3] !== r3.environmentBlending) {
          e6 = false;
          break;
        }
      } else if ("forced-colors" === t3) {
        if (n2[t3] !== r3.forcedColors) {
          e6 = false;
          break;
        }
      } else if ("inverted-colors" === t3) {
        if (n2[t3] !== r3.invertedColors) {
          e6 = false;
          break;
        }
      } else if ("nav-controls" === t3) {
        if (n2[t3] !== r3.navControls) {
          e6 = false;
          break;
        }
      } else if ("prefers-color-scheme" === t3) {
        if (n2[t3] !== r3.prefersColorScheme) {
          e6 = false;
          break;
        }
      } else if ("prefers-contrast" === t3) {
        if (n2[t3] !== r3.prefersContrast) {
          e6 = false;
          break;
        }
      } else if ("prefers-reduced-data" === t3) {
        if (n2[t3] !== r3.prefersReducedData) {
          e6 = false;
          break;
        }
      } else if ("prefers-reduced-motion" === t3) {
        if (n2[t3] !== r3.prefersReducedMotion) {
          e6 = false;
          break;
        }
      } else if ("prefers-reduced-transparency" === t3) {
        if (n2[t3] !== r3.prefersReducedTransparency) {
          e6 = false;
          break;
        }
      } else if ("dynamic-range" === t3) {
        if ("high" === n2[t3] && "not-hdr" === r3.dynamicRange) {
          e6 = false;
          break;
        }
      } else if ("video-dynamic-range" === t3) {
        if ("high" === n2[t3] && "not-hdr" === r3.videoDynamicRange) {
          e6 = false;
          break;
        }
      } else if ("vertical-viewport-segments" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.verticalViewportSegments < i4 || r3.verticalViewportSegments > s2 || i4 === r3.verticalViewportSegments && !o5 || s2 === r3.verticalViewportSegments && !c) {
          e6 = false;
          break;
        }
      } else if ("horizontal-viewport-segments" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.horizontalViewportSegments < i4 || r3.horizontalViewportSegments > s2 || i4 === r3.horizontalViewportSegments && !o5 || s2 === r3.horizontalViewportSegments && !c) {
          e6 = false;
          break;
        }
      } else if ("width" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.widthPx < i4 || r3.widthPx > s2 || i4 === r3.widthPx && !o5 || s2 === r3.widthPx && !c) {
          e6 = false;
          break;
        }
      } else if ("device-width" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.deviceWidthPx < i4 || r3.deviceWidthPx > s2 || i4 === r3.deviceWidthPx && !o5 || s2 === r3.deviceWidthPx && !c) {
          e6 = false;
          break;
        }
      } else if ("height" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.heightPx < i4 || r3.heightPx > s2 || i4 === r3.heightPx && !o5 || s2 === r3.heightPx && !c) {
          e6 = false;
          break;
        }
      } else if ("device-height" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.deviceHeightPx < i4 || r3.deviceHeightPx > s2 || i4 === r3.deviceHeightPx && !o5 || s2 === r3.deviceHeightPx && !c) {
          e6 = false;
          break;
        }
      } else if ("color" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.colorBits < i4 || r3.colorBits > s2 || i4 === r3.colorBits && !o5 || s2 === r3.colorBits && !c) {
          e6 = false;
          break;
        }
      } else if ("monochrome" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if ("not-monochrome" === r3.monochromeBits)
          (i4 > 0 || 0 === i4 && !o5 || 0 === s2 && !c) && (e6 = false);
        else if (r3.monochromeBits < i4 || r3.monochromeBits > s2 || i4 === r3.monochromeBits && !o5 || s2 === r3.monochromeBits && !c) {
          e6 = false;
          break;
        }
      } else if ("resolution" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if (r3.dppx < i4 || r3.dppx > s2 || i4 === r3.dppx && !o5 || s2 === r3.dppx && !c) {
          e6 = false;
          break;
        }
      } else if ("color-index" === t3) {
        const [o5, i4, s2, c] = n2[t3];
        if ("none" === r3.colorIndex) {
          if (i4 > 0 || 0 === i4 && !o5 || 0 === s2 && !c) {
            e6 = false;
            break;
          }
        } else if (r3.colorIndex < i4 || r3.colorIndex > s2 || i4 === r3.colorIndex && !o5 || s2 === r3.colorIndex && !c) {
          e6 = false;
          break;
        }
      } else if ("aspect-ratio" === t3) {
        const [o5, i4, s2, c] = n2[t3], a2 = i4[0] / i4[1], l2 = s2[0] / s2[1], f = r3.widthPx / r3.heightPx;
        if (f < a2 || f > l2 || a2 === f && !o5 || l2 === f && !c) {
          e6 = false;
          break;
        }
      } else {
        const [o5, i4, s2, c] = n2[t3], a2 = i4[0] / i4[1], l2 = s2[0] / s2[1], f = r3.deviceWidthPx / r3.deviceHeightPx;
        if (f < a2 || f > l2 || a2 === f && !o5 || l2 === f && !c) {
          e6 = false;
          break;
        }
      }
    }
    if (e6) return true;
  }
  return false;
};

// src/transformers/inline.ts
function inline(opts) {
  const { useObjectSyntax = false } = opts ?? {};
  return (doc) => {
    const style = useObjectSyntax ? [":where([style]) {}"] : [];
    const actions = [];
    walkSync(doc, (node2, parent) => {
      if (node2.type === ELEMENT_NODE) {
        if (node2.name === "style") {
          style.push(
            node2.children.map((c) => c.type === TEXT_NODE ? c.value : "").join("")
          );
          actions.push(() => {
            parent.children = parent.children.filter((c) => c !== node2);
          });
        }
      }
    });
    for (const action of actions) {
      action();
    }
    const styles = style.join("\n");
    const css = compile(styles);
    const selectors = /* @__PURE__ */ new Map();
    function applyRule(rule) {
      if (rule.type === "rule") {
        const rules2 = Object.fromEntries(
          rule.children.map((child) => [
            child.props,
            child.children
          ])
        );
        for (const selector of rule.props) {
          const value = Object.assign(selectors.get(selector) ?? {}, rules2);
          selectors.set(selector, value);
        }
      } else if (rule.type === "@media" && opts?.env) {
        const env = getEnvironment(opts.env);
        const args = Array.isArray(rule.props) ? rule.props : [rule.props];
        const queries = args.map((arg) => compileQuery(arg));
        for (const query of queries) {
          if (matches(query, env)) {
            for (const child of rule.children) {
              applyRule(child);
            }
            return;
          }
        }
      }
    }
    for (const rule of css) {
      applyRule(rule);
    }
    const rules = /* @__PURE__ */ new Map();
    for (const [selector, styles2] of Array.from(selectors).sort(([a2], [b]) => {
      const $a = specificity(a2);
      const $b = specificity(b);
      if ($a > $b) return 1;
      if ($b > $a) return -1;
      return 0;
    })) {
      const nodes = querySelectorAll(doc, selector);
      for (const node2 of nodes) {
        const curr = rules.get(node2) ?? {};
        rules.set(node2, Object.assign(curr, styles2));
      }
    }
    for (const [node2, rule] of rules) {
      let style2 = node2.attributes.style ?? "";
      let styleObj = {};
      for (const decl of compile(style2)) {
        if (decl.type === "decl") {
          if (typeof decl.props === "string" && typeof decl.children === "string") {
            styleObj[decl.props] = decl.children;
          }
        }
      }
      styleObj = Object.assign({}, rule, styleObj);
      if (useObjectSyntax) {
        node2.attributes.style = styleObj;
      } else {
        node2.attributes.style = `${Object.entries(styleObj).map(([decl, value]) => `${decl}:${value.replace("!important", "")};`).join("")}`;
      }
    }
    return doc;
  };
}
function getEnvironment(baseEnv) {
  const {
    width,
    height,
    dppx = 1,
    widthPx = width,
    heightPx = height,
    deviceWidthPx = width * dppx,
    deviceHeightPx = height * dppx,
    ...env
  } = baseEnv;
  return {
    widthPx,
    heightPx,
    deviceWidthPx,
    deviceHeightPx,
    dppx,
    ...env
  };
}
export {
  inline as default
};
/*! Bundled license information:

media-query-parser/dist/index.js:
  (**! media-query-parser | Tom Golden <oss@tom.bio> (https://tom.bio) | @license MIT  *)
*/
