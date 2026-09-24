import { cssFitValues } from "../internal.js";
const POSITION_KEYWORDS = ["top", "bottom", "left", "right", "center"];
function getPositionEntries() {
  const entries = [];
  for (const kw of POSITION_KEYWORDS) {
    entries.push([kw, kw]);
  }
  for (const a of POSITION_KEYWORDS) {
    for (const b of POSITION_KEYWORDS) {
      if (a === b) continue;
      const cssValue = `${a} ${b}`;
      const dataAttr = `${a}-${b}`;
      entries.push([dataAttr, cssValue]);
    }
  }
  return entries;
}
function generateImageStylesCSS(defaultObjectFit, defaultObjectPosition) {
  const fitStyles = cssFitValues.map(
    (fit) => `
[data-astro-image-fit="${fit}"] {
  object-fit: ${fit};
}`
  ).join("\n");
  const defaultFitStyle = defaultObjectFit && cssFitValues.includes(defaultObjectFit) ? `
:where([data-astro-image]:not([data-astro-image-fit])) {
  object-fit: ${defaultObjectFit};
}` : "";
  const positionEntries = getPositionEntries();
  const positionStyles = positionEntries.map(
    ([dataAttr, cssValue]) => `
[data-astro-image-pos="${dataAttr}"] {
  object-position: ${cssValue};
}`
  ).join("\n");
  const defaultPositionStyle = defaultObjectPosition ? `
:where([data-astro-image]:not([data-astro-image-pos])) {
  object-position: ${defaultObjectPosition};
}` : "";
  const rules = `
:where([data-astro-image]) {
  height: auto;
}
:where([data-astro-image="full-width"]) {
  width: 100%;
}
:where([data-astro-image="constrained"]) {
  max-width: 100%;
}
${fitStyles}
${defaultFitStyle}
${positionStyles}
${defaultPositionStyle}
`.trim();
  return `@layer astro.images {
${rules}
}`;
}
export {
  generateImageStylesCSS
};
