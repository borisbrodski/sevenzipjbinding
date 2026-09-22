import type { DataEntry, ImmutableDataStore } from './data-store.js';
/**
 * A read-only, async view over content collection data, used at runtime by
 * `getCollection()` and `getEntry()`. It lets the runtime read from different
 * data sources (an in-memory snapshot today, potentially a database or remote
 * source in the future) through one interface, without depending on Node.js
 * APIs.
 *
 * The query methods are async so a source can perform I/O when resolving data.
 * The default {@link InMemorySource} resolves synchronously.
 */
export interface DataStoreSource {
    hasCollection(collection: string): Promise<boolean> | boolean;
    get<T = DataEntry>(collection: string, key: string): Promise<T | undefined> | T | undefined;
    entries<T = DataEntry>(collection: string): Promise<Array<[id: string, T]>> | Array<[id: string, T]>;
    values<T = DataEntry>(collection: string): Promise<Array<T>> | Array<T>;
    keys(collection: string): Promise<Array<string>> | Array<string>;
    has(collection: string, key: string): Promise<boolean> | boolean;
    collections(): Promise<Map<string, Map<string, any>>> | Map<string, Map<string, any>>;
}
/**
 * A {@link DataStoreSource} backed by an in-memory {@link ImmutableDataStore}.
 * All queries resolve synchronously; the async signatures exist to satisfy the
 * {@link DataStoreSource} contract.
 */
export declare class InMemorySource implements DataStoreSource {
    #private;
    constructor(store: ImmutableDataStore);
    hasCollection(collection: string): boolean;
    get<T = DataEntry>(collection: string, key: string): T | undefined;
    entries<T = DataEntry>(collection: string): Array<[id: string, T]>;
    values<T = DataEntry>(collection: string): Array<T>;
    keys(collection: string): Array<string>;
    has(collection: string, key: string): boolean;
    collections(): Map<string, Map<string, any>>;
}
