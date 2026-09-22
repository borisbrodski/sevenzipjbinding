import { removeQueryString } from "@astrojs/internal-helpers/path";
import { DEFAULT_OUTPUT_FORMAT } from "../consts.js";
const DATA_PREFIX = "data:";
function inferSourceFormat(src) {
  if (src.startsWith(DATA_PREFIX)) {
    const sepIndex = src.indexOf(";");
    const commaIndex = src.indexOf(",");
    const mimeEnd = sepIndex === -1 ? commaIndex : commaIndex === -1 ? sepIndex : Math.min(sepIndex, commaIndex);
    if (mimeEnd === -1) return void 0;
    const mime = src.slice(DATA_PREFIX.length, mimeEnd);
    if (mime === "image/svg+xml") return "svg";
    const sub = mime.split("/")[1];
    return sub || void 0;
  }
  try {
    const cleanSrc = removeQueryString(src).split("#")[0];
    const lastSlash = cleanSrc.lastIndexOf("/");
    const basename = lastSlash === -1 ? cleanSrc : cleanSrc.slice(lastSlash + 1);
    const lastDot = basename.lastIndexOf(".");
    if (lastDot === -1) return void 0;
    return basename.slice(lastDot + 1).toLowerCase();
  } catch {
    return void 0;
  }
}
function resolveDefaultOutputFormat(sourceFormat) {
  return sourceFormat === "svg" ? "svg" : DEFAULT_OUTPUT_FORMAT;
}
export {
  inferSourceFormat,
  resolveDefaultOutputFormat
};
