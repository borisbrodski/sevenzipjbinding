import { isGenericFontFamily, unifontFontFaceDataToProperties } from "../utils.js";
function deriveFallbackVariant(data) {
  const weight = data.weight;
  if (typeof weight === "number" && weight >= 700) {
    return "bold";
  }
  if (typeof weight === "string") {
    if (weight === "bold") return "bold";
    if (weight.includes(" ")) return "normal";
    const n = Number.parseInt(weight, 10);
    if (!Number.isNaN(n) && n >= 700) return "bold";
  }
  return "normal";
}
async function optimizeFallbacks({
  family,
  fallbacks: _fallbacks,
  collectedFonts,
  systemFallbacksProvider,
  fontMetricsResolver
}) {
  let fallbacks = [..._fallbacks];
  if (fallbacks.length === 0 || collectedFonts.length === 0) {
    return null;
  }
  const lastFallback = fallbacks[fallbacks.length - 1];
  if (!isGenericFontFamily(lastFallback)) {
    return null;
  }
  const collectedWithLocalFonts = collectedFonts.map((collected) => ({
    collected,
    localFonts: systemFallbacksProvider.getLocalFonts(lastFallback, deriveFallbackVariant(collected.data)) ?? []
  }));
  const uniqueLocalFonts = [];
  for (const { localFonts } of collectedWithLocalFonts) {
    for (const font of localFonts) {
      if (!uniqueLocalFonts.includes(font)) {
        uniqueLocalFonts.push(font);
      }
    }
  }
  if (uniqueLocalFonts.length === 0) {
    return null;
  }
  if (uniqueLocalFonts.includes(family.name)) {
    return null;
  }
  const nameForFont = (font) => (
    // We mustn't wrap in quote because that's handled by the CSS renderer
    `${family.uniqueName} fallback: ${font}`
  );
  fallbacks = [...uniqueLocalFonts.map(nameForFont), ...fallbacks];
  let css = "";
  for (const { collected, localFonts } of collectedWithLocalFonts) {
    const properties = unifontFontFaceDataToProperties(collected.data);
    const metrics = await fontMetricsResolver.getMetrics(family.name, collected);
    for (const font of localFonts) {
      css += fontMetricsResolver.generateFontFace({
        metrics,
        fallbackMetrics: systemFallbacksProvider.getMetricsForLocalFont(font),
        font,
        name: nameForFont(font),
        properties
      });
    }
  }
  return { css, fallbacks };
}
export {
  optimizeFallbacks
};
