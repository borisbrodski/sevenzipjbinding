import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";
import { generateContentHash } from "../../core/encryption.js";
import { prependForwardSlash, slash } from "../../core/path.js";
import { imageMetadata } from "./metadata.js";
import { hashTransform, propsToFilename } from "./hash.js";
const svgContentCache = /* @__PURE__ */ new WeakMap();
const keyRegistry = /* @__PURE__ */ new Map();
function keyFor(hash) {
  let key = keyRegistry.get(hash);
  if (!key) {
    key = { hash };
    keyRegistry.set(hash, key);
  }
  return key;
}
async function handleSvgDeduplication(fileData, filename, fileEmitter) {
  const contentHash = await generateContentHash(fileData.buffer);
  const key = keyFor(contentHash);
  const existing = svgContentCache.get(key);
  if (existing) {
    const handle = fileEmitter({
      name: existing.filename,
      source: fileData,
      type: "asset"
    });
    return handle;
  } else {
    const handle = fileEmitter({
      name: filename,
      source: fileData,
      type: "asset"
    });
    svgContentCache.set(key, { handle, filename });
    return handle;
  }
}
const TRANSIENT_ERROR_CODES = /* @__PURE__ */ new Set(["EMFILE", "ENFILE", "EAGAIN", "EBUSY"]);
const MAX_CONCURRENT_READS = 200;
let activeReads = 0;
const readQueue = [];
async function readFileWithRetry(url, maxRetries = 5) {
  if (activeReads >= MAX_CONCURRENT_READS) {
    await new Promise((resolve) => readQueue.push(resolve));
  }
  activeReads++;
  try {
    for (let attempt = 0; ; attempt++) {
      try {
        return await fs.readFile(url);
      } catch (err) {
        const code = err instanceof Error && "code" in err ? err.code : void 0;
        if (code && TRANSIENT_ERROR_CODES.has(code) && attempt < maxRetries) {
          await new Promise((resolve) => setTimeout(resolve, 50 * 2 ** attempt));
          continue;
        }
        throw err;
      }
    }
  } finally {
    activeReads--;
    if (readQueue.length > 0) {
      readQueue.shift()();
    }
  }
}
async function emitImageMetadata(id, fileEmitter) {
  if (!id) {
    return void 0;
  }
  const url = pathToFileURL(id);
  let fileData;
  try {
    fileData = await readFileWithRetry(url);
  } catch (err) {
    if (err instanceof Error && "code" in err && err.code === "ENOENT") {
      return void 0;
    }
    throw err;
  }
  const fileMetadata = await imageMetadata(fileData, id);
  if (path.extname(id).toLowerCase() === ".apng") {
    fileMetadata.format = "apng";
  }
  const emittedImage = {
    src: "",
    ...fileMetadata
  };
  Object.defineProperty(emittedImage, "fsPath", {
    enumerable: false,
    writable: false,
    value: fileURLToNormalizedPath(url)
  });
  let isBuild = typeof fileEmitter === "function";
  if (isBuild) {
    const pathname = decodeURI(url.pathname);
    const filename = path.basename(pathname, path.extname(pathname) + `.${fileMetadata.format}`);
    try {
      let handle;
      if (fileMetadata.format === "svg") {
        handle = await handleSvgDeduplication(fileData, filename, fileEmitter);
      } else {
        handle = fileEmitter({
          name: filename,
          source: fileData,
          type: "asset"
        });
      }
      emittedImage.src = `__ASTRO_ASSET_IMAGE__${handle}__`;
    } catch {
      isBuild = false;
    }
  }
  if (!isBuild) {
    url.searchParams.append("origWidth", fileMetadata.width.toString());
    url.searchParams.append("origHeight", fileMetadata.height.toString());
    url.searchParams.append("origFormat", fileMetadata.format);
    emittedImage.src = `/@fs` + prependForwardSlash(fileURLToNormalizedPath(url));
  }
  return emittedImage;
}
function fileURLToNormalizedPath(filePath) {
  return slash(fileURLToPath(filePath) + filePath.search).replace(/\\/g, "/");
}
export {
  emitImageMetadata,
  hashTransform,
  propsToFilename
};
