import { ELEMENT_NODE, parse, walkSync } from "ultrahtml";
import { escapeTemplateLiteralCharacters } from "./utils.js";
const SLOT_PREFIX = `___SLOTS___`;
function collectSlots(code) {
  const slots = [];
  walkSync(parse(code), (node) => {
    if (node.type !== ELEMENT_NODE || node.name !== "slot") return;
    if ("is:inline" in node.attributes) return;
    const [open, close] = node.loc;
    if (!close) return;
    const name = node.attributes.name ?? "default";
    const fallback = open.end < close.start ? code.slice(open.end, close.start) : code.slice(open.start, close.end);
    slots.push({
      start: open.start,
      end: close.end,
      value: `\${${SLOT_PREFIX}["${name}"] ?? \`${escapeTemplateLiteralCharacters(fallback).trim()}\`}`
    });
  });
  return slots;
}
export {
  SLOT_PREFIX,
  collectSlots
};
