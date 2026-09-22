import { promises as fs } from "node:fs";
import * as devalue from "devalue";
import xxhash, {} from "xxhash-wasm";
import { emptyDir } from "../core/fs/index.js";
import { DATA_STORE_MANIFEST_FILE } from "./consts.js";
function sortCollections(collections) {
  return new Map(
    [...collections.entries()].sort(([a], [b]) => a.localeCompare(b)).map(([key, collection]) => [
      key,
      new Map([...collection.entries()].sort(([a], [b]) => a.localeCompare(b)))
    ])
  );
}
function serializeDataStore(collections) {
  return devalue.stringify(sortCollections(collections));
}
const ENCODER = new TextEncoder();
function getChunkEnd(str, startIndex, maxBytes) {
  let index = startIndex;
  let bytes = 0;
  while (index < str.length) {
    const codePoint = str.codePointAt(index);
    const charLength = codePoint > 65535 ? 2 : 1;
    const charBytes = ENCODER.encode(str.slice(index, index + charLength)).length;
    if (bytes + charBytes > maxBytes && index > startIndex) {
      break;
    }
    index += charLength;
    bytes += charBytes;
    if (bytes >= maxBytes) {
      break;
    }
  }
  return { endIndex: index, bytes };
}
function chunkString(str, maxBytes) {
  const chunks = [];
  let startIndex = 0;
  while (startIndex < str.length) {
    const { endIndex } = getChunkEnd(str, startIndex, maxBytes);
    chunks.push(str.slice(startIndex, endIndex));
    startIndex = endIndex;
  }
  return chunks;
}
async function writeFileAtomic(file, data) {
  const tempFile = file instanceof URL ? new URL(`${file.href}.tmp`) : `${file}.tmp`;
  const oldData = await fs.readFile(file, "utf-8").catch(() => "");
  if (oldData === data) {
    return false;
  }
  await fs.writeFile(tempFile, data);
  await fs.rename(tempFile, file);
  return true;
}
class FileWriter {
  #file;
  constructor(file) {
    this.#file = file;
  }
  get target() {
    return this.#file;
  }
  async write(collections) {
    return await writeFileAtomic(this.#file, serializeDataStore(collections));
  }
}
class ChunkedWriter {
  #dir;
  #manifestFile;
  #chunkSize;
  #hasher;
  #writtenFiles = /* @__PURE__ */ new Set();
  constructor(dir, chunkSize) {
    this.#dir = dir;
    this.#manifestFile = new URL(`./${DATA_STORE_MANIFEST_FILE}`, dir);
    this.#chunkSize = chunkSize;
  }
  get target() {
    return this.#manifestFile;
  }
  async write(collections) {
    if (!this.#hasher) {
      this.#hasher = await xxhash();
    }
    this.#writtenFiles = /* @__PURE__ */ new Set();
    const manifest = {};
    for (const [collectionName, entries] of sortCollections(collections)) {
      manifest[collectionName] = await this.#writeCollection(entries);
    }
    const didWrite = await writeFileAtomic(this.#manifestFile, JSON.stringify(manifest));
    this.#writtenFiles.add(DATA_STORE_MANIFEST_FILE);
    emptyDir(this.#dir, this.#writtenFiles);
    return didWrite;
  }
  async #writeCollection(entries) {
    const parts = [];
    let chunk = "";
    let chunkBytes = 0;
    const writeChunk = async () => {
      const fileName = `${this.#hasher.h64ToString(chunk)}.txt`;
      await writeFileAtomic(new URL(`./${fileName}`, this.#dir), chunk);
      parts.push(fileName);
      this.#writtenFiles.add(fileName);
      chunk = "";
      chunkBytes = 0;
    };
    for (const [id, entry] of entries) {
      const serialized = `${devalue.stringify([id, entry])}
`;
      let startIndex = 0;
      while (startIndex < serialized.length) {
        const { endIndex, bytes } = getChunkEnd(
          serialized,
          startIndex,
          this.#chunkSize - chunkBytes
        );
        chunk += serialized.slice(startIndex, endIndex);
        chunkBytes += bytes;
        startIndex = endIndex;
        if (chunkBytes >= this.#chunkSize) {
          await writeChunk();
        }
      }
    }
    if (chunk) {
      await writeChunk();
    }
    return parts;
  }
}
export {
  ChunkedWriter,
  FileWriter,
  chunkString,
  serializeDataStore,
  writeFileAtomic
};
