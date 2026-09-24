import * as path from "node:path";
import { fileURLToPath } from "node:url";
const exportConstPartialTrueRe = /export\s+const\s+partial\s*=\s*true/;
const leadingComponentRe = /^\s*<\s*([A-Za-z][A-Za-z0-9]*)\b/;
function shouldAddCharset(content, filePath, srcDir) {
  if (!srcDir) return false;
  const srcDirPath = fileURLToPath(srcDir).replace(/\\/g, "/");
  const pagesDir = path.posix.join(srcDirPath, "pages");
  const normalizedFilePath = filePath.replace(/\\/g, "/");
  if (!normalizedFilePath.startsWith(pagesDir)) return false;
  const segments = normalizedFilePath.slice(pagesDir.length).split("/");
  if (segments.some((part) => part.startsWith("_"))) return false;
  if (exportConstPartialTrueRe.test(content)) return false;
  const stripped = content.replace(/\{\/\*[\s\S]*?\*\/\}/g, "");
  for (const rawLine of stripped.split("\n")) {
    const line = rawLine.trim();
    if (!line) continue;
    if (line.startsWith("import ") || line.startsWith("export ")) continue;
    if (line.startsWith("//") || line.startsWith("/*")) continue;
    if (line.startsWith("{")) continue;
    const match = leadingComponentRe.exec(line);
    if (match) {
      const tag = match[1];
      if (tag[0] >= "A" && tag[0] <= "Z") return false;
    }
    break;
  }
  return true;
}
export {
  shouldAddCharset
};
