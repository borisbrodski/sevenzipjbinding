import * as devalue from "devalue";
import { InMemorySource } from "./data-store-source.js";
class ChunkedCollectionParser {
  #entries = /* @__PURE__ */ new Map();
  #remainder = "";
  add(part) {
    const content = this.#remainder + part;
    const records = content.split("\n");
    this.#remainder = records.pop();
    for (const record of records) {
      const parsed = devalue.parse(record);
      if (!Array.isArray(parsed) || parsed.length !== 2 || typeof parsed[0] !== "string") {
        throw new Error("Invalid chunked data store entry");
      }
      this.#entries.set(parsed[0], parsed[1]);
    }
  }
  finish() {
    if (this.#remainder) {
      throw new Error("Invalid chunked data store entry");
    }
    return this.#entries;
  }
}
class ImmutableDataStore {
  _collections = /* @__PURE__ */ new Map();
  constructor() {
    this._collections = /* @__PURE__ */ new Map();
  }
  get(collectionName, key) {
    return this._collections.get(collectionName)?.get(String(key));
  }
  entries(collectionName) {
    const collection = this._collections.get(collectionName) ?? /* @__PURE__ */ new Map();
    return [...collection.entries()];
  }
  values(collectionName) {
    const collection = this._collections.get(collectionName) ?? /* @__PURE__ */ new Map();
    return [...collection.values()];
  }
  keys(collectionName) {
    const collection = this._collections.get(collectionName) ?? /* @__PURE__ */ new Map();
    return [...collection.keys()];
  }
  has(collectionName, key) {
    const collection = this._collections.get(collectionName);
    if (collection) {
      return collection.has(String(key));
    }
    return false;
  }
  hasCollection(collectionName) {
    return this._collections.has(collectionName);
  }
  collections() {
    return this._collections;
  }
  /**
   * Rebuilds a collections map from a chunked-store manifest whose part file
   * names have already been swapped for their contents.
   *
   * Each collection maps to a list of parts. A part is either a raw string
   * (when the store is loaded from disk) or an ESM namespace from a virtual
   * chunk import (`{ default: string }`, when emitted at runtime). Each part
   * contains independently serialized entry records. This is the inverse of
   * {@link import('./data-store-writer.js').ChunkedWriter} and stays free of
   * Node built-ins so it can run at runtime.
   */
  static manifestToMap(manifest) {
    const collections = /* @__PURE__ */ new Map();
    for (const [collectionName, parts] of Object.entries(manifest)) {
      const parser = new ChunkedCollectionParser();
      for (const part of parts) {
        parser.add(typeof part === "string" ? part : part.default);
      }
      collections.set(collectionName, parser.finish());
    }
    return collections;
  }
  /**
   * Attempts to load a DataStore from the virtual module.
   * This only works in Vite.
   */
  static async fromModule() {
    try {
      const data = await import("astro:data-layer-content");
      if (data.default instanceof Map) {
        return ImmutableDataStore.fromMap(data.default);
      }
      if (Array.isArray(data.default)) {
        const map2 = devalue.unflatten(data.default);
        return ImmutableDataStore.fromMap(map2);
      }
      const map = ImmutableDataStore.manifestToMap(data.default);
      return ImmutableDataStore.fromMap(map);
    } catch {
    }
    return new ImmutableDataStore();
  }
  static async fromMap(data) {
    const store = new ImmutableDataStore();
    store._collections = data;
    return store;
  }
}
function dataStoreSingleton() {
  let instance = void 0;
  return {
    get: async () => {
      if (!instance) {
        instance = ImmutableDataStore.fromModule().then((store) => new InMemorySource(store));
      }
      return instance;
    },
    // Note: currently unused, but kept for API stability.
    set: (store) => {
      instance = new InMemorySource(store);
    }
  };
}
const globalDataStore = dataStoreSingleton();
export {
  ChunkedCollectionParser,
  ImmutableDataStore,
  globalDataStore
};
