import { existsSync, promises as fs } from "node:fs";
import { fileURLToPath } from "node:url";
import * as devalue from "devalue";
import { forEach } from "neotraverse";
import { imageSrcToImportId } from "../assets/utils/resolveImports.js";
import { AstroError, AstroErrorData } from "../core/errors/index.js";
import { DATA_STORE_MANIFEST_FILE, IMAGE_IMPORT_PREFIX } from "./consts.js";
import {
  ChunkedWriter,
  FileWriter,
  serializeDataStore
} from "./data-store-writer.js";
import { ChunkedCollectionParser, ImmutableDataStore } from "./data-store.js";
import { contentModuleToId } from "./utils.js";
const SAVE_DEBOUNCE_MS = 500;
const MAX_DEPTH = 10;
class MutableDataStore extends ImmutableDataStore {
  #writer;
  #assetsFile;
  #modulesFile;
  #saveTimeout;
  #assetsSaveTimeout;
  #modulesSaveTimeout;
  #savePromise;
  #savePromiseResolve;
  #dirty = false;
  #assetsDirty = false;
  #modulesDirty = false;
  #assetImports = /* @__PURE__ */ new Set();
  #moduleImports = /* @__PURE__ */ new Map();
  #writeInProgress = false;
  #writeQueued = false;
  #fileWrittenListeners = /* @__PURE__ */ new Set();
  /**
   * Registers a listener called with the file path whenever this store writes a
   * file to disk (the data store itself, or the asset/module import files).
   * Writes that are skipped because the data on disk is already identical do
   * not notify. The dev server uses this to invalidate the content virtual
   * modules deterministically, instead of relying on the file watcher to
   * observe the write — on some platforms (notably Windows) the watcher can
   * miss the atomic rename that commits it.
   * Returns a function that removes the listener.
   */
  onFileWritten(listener) {
    this.#fileWrittenListeners.add(listener);
    return () => {
      this.#fileWrittenListeners.delete(listener);
    };
  }
  #notifyFileWritten(path) {
    if (this.#fileWrittenListeners.size === 0) {
      return;
    }
    const normalized = path instanceof URL ? fileURLToPath(path) : path.toString();
    for (const listener of this.#fileWrittenListeners) {
      listener(normalized);
    }
  }
  set(collectionName, key, value) {
    const collection = this._collections.get(collectionName) ?? /* @__PURE__ */ new Map();
    collection.set(String(key), value);
    this._collections.set(collectionName, collection);
    this.#saveToDiskDebounced();
  }
  delete(collectionName, key) {
    const collection = this._collections.get(collectionName);
    if (collection) {
      collection.delete(String(key));
      this.#saveToDiskDebounced();
      this.#writeAssetsImportsDebounced();
      this.#writeModulesImportsDebounced();
    }
  }
  clear(collectionName) {
    this._collections.delete(collectionName);
    this.#saveToDiskDebounced();
    this.#writeAssetsImportsDebounced();
    this.#writeModulesImportsDebounced();
  }
  clearAll() {
    this._collections.clear();
    this.#saveToDiskDebounced();
    this.#writeAssetsImportsDebounced();
    this.#writeModulesImportsDebounced();
  }
  addAssetImport(assetImport, filePath) {
    const id = imageSrcToImportId(assetImport, filePath);
    if (id) {
      this.#assetImports.add(id);
      this.#writeAssetsImportsDebounced();
    }
  }
  addAssetImports(assets, filePath) {
    assets.forEach((asset) => this.addAssetImport(asset, filePath));
  }
  addModuleImport(fileName) {
    const id = contentModuleToId(fileName);
    if (id) {
      this.#moduleImports.set(fileName, id);
      this.#writeModulesImportsDebounced();
    }
  }
  /**
   * Rebuilds #assetImports from the current entries in _collections.
   * This ensures stale import IDs are removed when entries are updated or deleted,
   * preventing unrecoverable ImageNotFound errors in astro dev after a content entry's
   * image path is temporarily set to an invalid value and then restored.
   */
  #rebuildAssetImports() {
    this.#assetImports.clear();
    for (const collection of this._collections.values()) {
      for (const entry of collection.values()) {
        const typedEntry = entry;
        if (typedEntry.assetImports?.length) {
          for (const assetImport of typedEntry.assetImports) {
            const id = imageSrcToImportId(assetImport, typedEntry.filePath);
            if (id) {
              this.#assetImports.add(id);
            }
          }
        }
      }
    }
  }
  /**
   * Rebuilds #moduleImports from the current entries in _collections.
   * This ensures stale module entries are removed when content files are
   * deleted or renamed, preventing Vite from attempting to resolve
   * non-existent files listed in content-modules.mjs.
   */
  #rebuildModuleImports() {
    this.#moduleImports.clear();
    for (const collection of this._collections.values()) {
      for (const entry of collection.values()) {
        const typedEntry = entry;
        if (typedEntry.deferredRender && typedEntry.filePath) {
          const id = contentModuleToId(typedEntry.filePath);
          if (id) {
            this.#moduleImports.set(typedEntry.filePath, id);
          }
        }
      }
    }
  }
  async writeAssetImports(filePath) {
    this.#assetsFile = filePath;
    this.#rebuildAssetImports();
    if (this.#assetImports.size === 0) {
      try {
        await this.#writeFileAtomic(filePath, "export default new Map();");
      } catch (err) {
        throw new AstroError(AstroErrorData.UnknownFilesystemError, { cause: err });
      }
    }
    if (!this.#assetsDirty && existsSync(filePath)) {
      return;
    }
    const imports = [];
    const exports = [];
    const sortedAssetImports = [...this.#assetImports].sort();
    sortedAssetImports.forEach((id, index) => {
      const symbol = `__ASTRO_IMAGE_IMPORT_${index}`;
      imports.push(`import ${symbol} from ${JSON.stringify(id)};`);
      exports.push(`[${JSON.stringify(id)}, ${symbol}]`);
    });
    const code = (
      /* js */
      `
${imports.join("\n")}
export default new Map([${exports.join(", ")}]);
		`
    );
    try {
      await this.#writeFileAtomic(filePath, code);
    } catch (err) {
      throw new AstroError(AstroErrorData.UnknownFilesystemError, { cause: err });
    }
    this.#assetsDirty = false;
  }
  async writeModuleImports(filePath) {
    this.#modulesFile = filePath;
    this.#rebuildModuleImports();
    if (this.#moduleImports.size === 0) {
      try {
        await this.#writeFileAtomic(filePath, "export default new Map();");
      } catch (err) {
        throw new AstroError(AstroErrorData.UnknownFilesystemError, { cause: err });
      }
      return;
    }
    if (!this.#modulesDirty && existsSync(filePath)) {
      return;
    }
    const lines = [];
    const sortedModuleImports = [...this.#moduleImports.entries()].sort(
      ([a], [b]) => a.localeCompare(b)
    );
    for (const [fileName, specifier] of sortedModuleImports) {
      lines.push(`[${JSON.stringify(fileName)}, () => import(${JSON.stringify(specifier)})]`);
    }
    const code = `
export default new Map([
${lines.join(",\n")}]);
		`;
    try {
      await this.#writeFileAtomic(filePath, code);
    } catch (err) {
      throw new AstroError(AstroErrorData.UnknownFilesystemError, { cause: err });
    }
    this.#modulesDirty = false;
  }
  #maybeResolveSavePromise() {
    if (!this.#saveTimeout && !this.#assetsSaveTimeout && !this.#modulesSaveTimeout && !this.#writeQueued && !this.#writeInProgress && this.#savePromiseResolve) {
      this.#savePromiseResolve();
      this.#savePromiseResolve = void 0;
      this.#savePromise = void 0;
    }
  }
  #writeAssetsImportsDebounced() {
    this.#assetsDirty = true;
    if (this.#assetsFile) {
      if (this.#assetsSaveTimeout) {
        clearTimeout(this.#assetsSaveTimeout);
      }
      if (!this.#savePromise) {
        this.#savePromise = new Promise((resolve) => {
          this.#savePromiseResolve = resolve;
        });
      }
      this.#assetsSaveTimeout = setTimeout(async () => {
        this.#assetsSaveTimeout = void 0;
        await this.writeAssetImports(this.#assetsFile);
        this.#maybeResolveSavePromise();
      }, SAVE_DEBOUNCE_MS);
    }
  }
  #writeModulesImportsDebounced() {
    this.#modulesDirty = true;
    if (this.#modulesFile) {
      if (this.#modulesSaveTimeout) {
        clearTimeout(this.#modulesSaveTimeout);
      }
      if (!this.#savePromise) {
        this.#savePromise = new Promise((resolve) => {
          this.#savePromiseResolve = resolve;
        });
      }
      this.#modulesSaveTimeout = setTimeout(async () => {
        this.#modulesSaveTimeout = void 0;
        await this.writeModuleImports(this.#modulesFile);
        this.#maybeResolveSavePromise();
      }, SAVE_DEBOUNCE_MS);
    }
  }
  // Skips the debounce and writes to disk immediately
  async #saveToDiskNow() {
    if (this.#saveTimeout) {
      clearTimeout(this.#saveTimeout);
    }
    this.#saveTimeout = void 0;
    if (this.#writer) {
      await this.writeToDisk();
    }
    this.#maybeResolveSavePromise();
  }
  #saveToDiskDebounced() {
    this.#dirty = true;
    if (this.#saveTimeout) {
      clearTimeout(this.#saveTimeout);
    }
    if (!this.#savePromise) {
      this.#savePromise = new Promise((resolve) => {
        this.#savePromiseResolve = resolve;
      });
    }
    this.#saveTimeout = setTimeout(async () => {
      this.#saveTimeout = void 0;
      if (this.#writer) {
        await this.writeToDisk();
      }
      this.#maybeResolveSavePromise();
    }, SAVE_DEBOUNCE_MS);
  }
  #writing = /* @__PURE__ */ new Set();
  #pending = /* @__PURE__ */ new Set();
  async #writeFileAtomic(filePath, data, depth = 0) {
    if (depth > MAX_DEPTH) {
      return;
    }
    const fileKey = filePath.toString();
    if (this.#writing.has(fileKey)) {
      this.#pending.add(fileKey);
      return;
    }
    this.#writing.add(fileKey);
    const tempFile = filePath instanceof URL ? new URL(`${filePath.href}.tmp`) : `${filePath}.tmp`;
    try {
      const oldData = await fs.readFile(filePath, "utf-8").catch(() => "");
      if (oldData === data) {
        return;
      }
      await fs.writeFile(tempFile, data);
      await fs.rename(tempFile, filePath);
      this.#notifyFileWritten(filePath);
    } finally {
      this.#writing.delete(fileKey);
      if (this.#pending.has(fileKey)) {
        this.#pending.delete(fileKey);
        await this.#writeFileAtomic(filePath, data, depth + 1);
      }
    }
  }
  scopedStore(collectionName) {
    return {
      get: (key) => this.get(collectionName, key),
      entries: () => this.entries(collectionName),
      values: () => this.values(collectionName),
      keys: () => this.keys(collectionName),
      set: ({
        id: key,
        data,
        body,
        filePath,
        deferredRender,
        digest,
        rendered,
        assetImports,
        imageImports: incomingImageImports
      }) => {
        if (!key) {
          throw new Error(`ID must be a non-empty string`);
        }
        const id = String(key);
        if (digest) {
          const existing = this.get(collectionName, id);
          if (existing && existing.digest === digest) {
            return false;
          }
        }
        const foundAssets = new Set(assetImports);
        const imageImports = [];
        const seenImageImportPaths = /* @__PURE__ */ new Set();
        const recordImageImport = (imagePath) => {
          const pathKey = JSON.stringify(imagePath);
          if (seenImageImportPaths.has(pathKey)) {
            return;
          }
          seenImageImportPaths.add(pathKey);
          imageImports.push(imagePath);
        };
        for (const existingImagePath of incomingImageImports ?? []) {
          recordImageImport([...existingImagePath]);
        }
        forEach(data, function(ctx, val) {
          if (typeof val === "string" && val.startsWith(IMAGE_IMPORT_PREFIX)) {
            const src = val.replace(IMAGE_IMPORT_PREFIX, "");
            foundAssets.add(src);
            recordImageImport(ctx.path.map((segment) => segment));
            ctx.update(src);
          }
        });
        const entry = {
          id,
          data
        };
        if (body) {
          entry.body = body;
        }
        if (filePath) {
          if (filePath.startsWith("/")) {
            throw new Error(`File path must be relative to the site root. Got: ${filePath}`);
          }
          entry.filePath = filePath;
        }
        if (foundAssets.size) {
          entry.assetImports = Array.from(foundAssets);
          this.addAssetImports(entry.assetImports, filePath);
        }
        if (imageImports.length) {
          entry.imageImports = imageImports;
        }
        if (digest) {
          entry.digest = digest;
        }
        if (rendered) {
          entry.rendered = rendered;
        }
        if (deferredRender) {
          entry.deferredRender = deferredRender;
          if (filePath) {
            this.addModuleImport(filePath);
          }
        }
        this.set(collectionName, id, entry);
        return true;
      },
      delete: (key) => this.delete(collectionName, key),
      clear: () => this.clear(collectionName),
      has: (key) => this.has(collectionName, key),
      addAssetImport: (assetImport, fileName) => this.addAssetImport(assetImport, fileName),
      addAssetImports: (assets, fileName) => this.addAssetImports(assets, fileName),
      addModuleImport: (fileName) => this.addModuleImport(fileName)
    };
  }
  /**
   * Returns a MetaStore for a given collection, or if no collection is provided, the default meta collection.
   */
  metaStore(collectionName = ":meta") {
    const collectionKey = `meta:${collectionName}`;
    return {
      get: (key) => this.get(collectionKey, key),
      set: (key, data) => this.set(collectionKey, key, data),
      delete: (key) => this.delete(collectionKey, key),
      has: (key) => this.has(collectionKey, key)
    };
  }
  /**
   * Returns a promise that resolves when all pending saves are complete.
   * This includes any in-progress debounced saves for the data store, asset imports, and module imports.
   */
  async waitUntilSaveComplete() {
    if (!this.#savePromise) {
      return Promise.resolve();
    }
    await this.#saveToDiskNow();
    return this.#savePromise;
  }
  toString() {
    return serializeDataStore(this._collections);
  }
  async writeToDisk() {
    if (!this.#dirty) {
      return;
    }
    if (!this.#writer) {
      throw new AstroError(AstroErrorData.UnknownFilesystemError);
    }
    if (this.#writeInProgress) {
      this.#writeQueued = true;
      return;
    }
    try {
      this.#dirty = false;
      this.#writeInProgress = true;
      const didWrite = await this.#writer.write(this._collections);
      if (didWrite) {
        this.#notifyFileWritten(this.#writer.target);
      }
    } catch (err) {
      throw new AstroError(AstroErrorData.UnknownFilesystemError, { cause: err });
    } finally {
      this.#writeInProgress = false;
      if (this.#writeQueued) {
        this.#writeQueued = false;
        await this.writeToDisk();
      }
    }
  }
  /**
   * Attempts to load a MutableDataStore from the virtual module.
   * This only works in Vite.
   */
  static async fromModule() {
    try {
      const data = await import("astro:data-layer-content");
      const map = devalue.unflatten(data.default);
      return MutableDataStore.fromMap(map);
    } catch {
    }
    return new MutableDataStore();
  }
  static async fromMap(data) {
    const store = new MutableDataStore();
    store._collections = data;
    return store;
  }
  static async fromString(data) {
    const map = devalue.parse(data);
    return MutableDataStore.fromMap(map);
  }
  static async fromFile(filePath) {
    try {
      if (existsSync(filePath)) {
        const data = await fs.readFile(filePath, "utf-8");
        const store2 = await MutableDataStore.fromString(data);
        store2.#writer = new FileWriter(filePath);
        return store2;
      } else {
        await fs.mkdir(new URL("./", filePath), { recursive: true });
      }
    } catch {
    }
    const store = new MutableDataStore();
    store.#writer = new FileWriter(filePath);
    return store;
  }
  /**
   * Loads a MutableDataStore from a chunked store directory, reading the manifest
   * and its referenced parts.
   * If the directory has no manifest yet (fresh build) it starts empty. If the
   * manifest exists but can't be read (corrupt cache), it warns and starts
   * empty so loaders rebuild it, rather than failing the sync.
   */
  static async fromDir(dirPath, chunkSize, logger) {
    const manifestFile = new URL(`./${DATA_STORE_MANIFEST_FILE}`, dirPath);
    if (existsSync(manifestFile)) {
      try {
        const manifestData = await fs.readFile(manifestFile, "utf-8");
        const manifest = JSON.parse(manifestData);
        const collections = /* @__PURE__ */ new Map();
        for (const collectionName in manifest) {
          const parser = new ChunkedCollectionParser();
          for (const fileName of manifest[collectionName]) {
            parser.add(await fs.readFile(new URL(`./${fileName}`, dirPath), "utf-8"));
          }
          collections.set(collectionName, parser.finish());
        }
        const store2 = await MutableDataStore.fromMap(collections);
        store2.#writer = new ChunkedWriter(dirPath, chunkSize);
        return store2;
      } catch (err) {
        logger.warn(
          "content",
          `Could not read the chunked data store at ${fileURLToPath(dirPath)}, rebuilding from scratch. ${err instanceof Error ? err.message : err}`
        );
      }
    }
    await fs.mkdir(dirPath, { recursive: true });
    const store = new MutableDataStore();
    store.#writer = new ChunkedWriter(dirPath, chunkSize);
    return store;
  }
}
export {
  MutableDataStore
};
