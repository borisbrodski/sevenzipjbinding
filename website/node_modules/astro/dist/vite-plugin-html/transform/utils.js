const NEEDS_ESCAPE_RE = /[`\\]|\$\{/g;
function needsEscape(value) {
  NEEDS_ESCAPE_RE.lastIndex = 0;
  return typeof value === "string" && NEEDS_ESCAPE_RE.test(value);
}
function escapeTemplateLiteralCharacters(value) {
  NEEDS_ESCAPE_RE.lastIndex = 0;
  let char;
  let startIndex = 0;
  let segment = "";
  let text = "";
  while ([char] = NEEDS_ESCAPE_RE.exec(value) ?? []) {
    if (!char) {
      text += value.slice(startIndex);
      break;
    }
    const endIndex = NEEDS_ESCAPE_RE.lastIndex - char.length;
    const prefix = segment === "\\" ? "" : "\\";
    segment = prefix + char;
    text += value.slice(startIndex, endIndex) + segment;
    startIndex = NEEDS_ESCAPE_RE.lastIndex;
  }
  return text;
}
export {
  escapeTemplateLiteralCharacters,
  needsEscape
};
