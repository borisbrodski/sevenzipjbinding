const SYSTEM_METRICS = {
  "Times New Roman": {
    ascent: 1825,
    descent: -443,
    lineGap: 87,
    unitsPerEm: 2048,
    xWidthAvg: 832
  },
  "Times New Roman Bold": {
    ascent: 1825,
    descent: -443,
    lineGap: 87,
    unitsPerEm: 2048,
    xWidthAvg: 886
  },
  // Times New Roman Italic almost has the same properties as Times New Roman, we don't include it
  Arial: {
    ascent: 1854,
    descent: -434,
    lineGap: 67,
    unitsPerEm: 2048,
    xWidthAvg: 913
  },
  "Arial Bold": {
    ascent: 1854,
    descent: -434,
    lineGap: 67,
    unitsPerEm: 2048,
    xWidthAvg: 983
  },
  // Arial Italic has the same properties as Arial, we don't include it
  "Courier New": {
    ascent: 1705,
    descent: -615,
    lineGap: 0,
    unitsPerEm: 2048,
    xWidthAvg: 1229
  },
  // Courier New Bold has the same properties as Courier New, we don't include it
  // Courier New Italic has the same properties as Courier New, we don't include it
  BlinkMacSystemFont: {
    ascent: 1980,
    descent: -432,
    lineGap: 0,
    unitsPerEm: 2048,
    xWidthAvg: 853
  },
  "Segoe UI": {
    ascent: 2210,
    descent: -514,
    lineGap: 0,
    unitsPerEm: 2048,
    xWidthAvg: 908
  },
  Roboto: {
    ascent: 1900,
    descent: -500,
    lineGap: 0,
    unitsPerEm: 2048,
    xWidthAvg: 911
  },
  "Helvetica Neue": {
    ascent: 952,
    descent: -213,
    lineGap: 28,
    unitsPerEm: 1e3,
    xWidthAvg: 450
  }
};
const DEFAULT_FALLBACKS = {
  serif: {
    normal: ["Times New Roman"],
    bold: ["Times New Roman Bold"]
  },
  "sans-serif": {
    normal: ["Arial"],
    bold: ["Arial Bold"]
  },
  monospace: { normal: ["Courier New"] },
  "system-ui": {
    normal: ["BlinkMacSystemFont", "Segoe UI", "Roboto", "Helvetica Neue", "Arial"],
    bold: ["BlinkMacSystemFont", "Segoe UI", "Roboto", "Helvetica Neue", "Arial Bold"]
  },
  "ui-serif": {
    normal: ["Times New Roman"],
    bold: ["Times New Roman Bold"]
  },
  "ui-sans-serif": {
    normal: ["Arial"],
    bold: ["Arial Bold"]
  },
  "ui-monospace": { normal: ["Courier New"] }
};
class RealSystemFallbacksProvider {
  getLocalFonts(fallback, variant) {
    const entry = DEFAULT_FALLBACKS[fallback];
    if (!entry) {
      return null;
    }
    return entry[variant] ?? entry.normal ?? null;
  }
  getMetricsForLocalFont(family) {
    return SYSTEM_METRICS[family];
  }
}
export {
  RealSystemFallbacksProvider
};
