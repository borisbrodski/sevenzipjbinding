import { type PathLike } from 'node:fs';
/** A chunked store manifest: each collection maps to its content-addressed part files. */
export type DataStoreManifest = Record<string, string[]>;
/**
 * Persists the content collection data produced by the content layer. This is
 * the write side that saves the data; {@link import('./data-store-source.js').DataStoreSource}
 * is the read side that loads it back. Implementations run in Node.js
 * (build/dev) and are never imported at runtime.
 */
export interface DataStoreWriter {
    /**
     * Serialize and persist the given collections.
     * Resolves to `true` if the data on disk changed, or `false` if the write
     * was skipped because the persisted data was already identical.
     */
    write(collections: Map<string, Map<string, any>>): Promise<boolean>;
    /**
     * The file whose write commits a store update: the store file itself, or
     * the manifest for chunked stores.
     */
    readonly target: PathLike;
}
/**
 * Serialize collections to a deterministic devalue string.
 */
export declare function serializeDataStore(collections: Map<string, Map<string, any>>): string;
/**
 * Split a string into parts each at most `maxBytes` UTF-8 bytes, never splitting
 * a Unicode code point across parts.
 *
 * The store is serialized with devalue and written to disk as UTF-8. Splitting
 * on UTF-16 code-unit boundaries (e.g. `String.prototype.slice`) can cut a
 * surrogate pair in half; encoding a lone surrogate to UTF-8 substitutes U+FFFD,
 * so concatenating the re-read parts would corrupt any astral-plane character
 * (emoji, some CJK, etc.). `getChunkEnd()` advances by Unicode code point, so
 * `str.slice` is only ever called on code-point boundaries and the parts always
 * rejoin to the exact original string.
 */
export declare function chunkString(str: string, maxBytes: number): string[];
/**
 * Atomically write `data` to `file`.
 *
 * The data is written to a temporary file and then renamed into place to avoid
 * partial reads. If the file already contains identical data, the write is
 * skipped. Callers are responsible for serializing concurrent writes to the
 * same file.
 *
 * Returns `true` if the file was written, or `false` if the write was skipped.
 */
export declare function writeFileAtomic(file: PathLike, data: string): Promise<boolean>;
/**
 * A {@link DataStoreWriter} that serializes the whole store to a single file.
 */
export declare class FileWriter implements DataStoreWriter {
    #private;
    constructor(file: PathLike);
    get target(): PathLike;
    write(collections: Map<string, Map<string, any>>): Promise<boolean>;
}
/**
 * A {@link DataStoreWriter} that splits the store across many content-addressed
 * files inside a directory, described by a manifest.
 *
 * Entries are serialized independently into parts no larger than a fixed byte
 * size, so writing a collection does not require a collection-wide serialized
 * string. Each part file is named by the xxhash of its contents, so unchanged
 * parts keep the same name across builds and their writes are skipped, and two
 * identical parts are naturally deduplicated. The manifest is written last as
 * the commit point, and stale files are pruned afterwards. This is the inverse
 * of {@link import('./data-store.js').ImmutableDataStore.manifestToMap}.
 */
export declare class ChunkedWriter implements DataStoreWriter {
    #private;
    constructor(dir: URL, chunkSize: number);
    get target(): PathLike;
    write(collections: Map<string, Map<string, any>>): Promise<boolean>;
}
