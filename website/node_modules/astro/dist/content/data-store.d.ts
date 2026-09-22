import type { MarkdownHeading } from '@astrojs/internal-helpers/markdown';
export interface RenderedContent {
    /** Rendered HTML string. If present then `render(entry)` will return a component that renders this HTML. */
    html: string;
    metadata?: {
        /** Any images that are present in this entry. Relative to the {@link DataEntry} filePath. */
        imagePaths?: Array<string>;
        /** Any headings that are present in this file. */
        headings?: MarkdownHeading[];
        /** Raw frontmatter, parsed from the file. This may include data from remark plugins. */
        frontmatter?: Record<string, any>;
        /** Any other metadata that is present in this file. */
        [key: string]: unknown;
    };
}
export interface DataEntry<TData extends Record<string, unknown> = Record<string, unknown>> {
    /** The ID of the entry. Unique per collection. */
    id: string;
    /** The parsed entry data */
    data: TData;
    /** The file path of the content, if applicable. Relative to the site root. */
    filePath?: string;
    /** The raw body of the content, if applicable. */
    body?: string;
    /** An optional content digest, to check if the content has changed. */
    digest?: number | string;
    /** The rendered content of the entry, if applicable. */
    rendered?: RenderedContent;
    /**
     * If an entry is a deferred, its rendering phase is delegated to a virtual module during the runtime phase when calling `renderEntry`.
     */
    deferredRender?: boolean;
    assetImports?: Array<string>;
    /**
     * Locations of image fields within `data`, recorded when the entry is stored.
     * Each path is the sequence of keys from `data` to a field that holds an image
     * src string. At read time these fields are resolved to `ImageMetadata` without
     * traversing or cloning the rest of `data`, so sibling values that devalue can
     * serialize but `structuredClone` cannot (e.g. class instances) are left
     * untouched.
     */
    imageImports?: (string | number)[][];
}
/**
 * Rebuilds a collection from ordered parts containing newline-delimited
 * `devalue.stringify([id, entry])` records.
 */
export declare class ChunkedCollectionParser {
    #private;
    add(part: string): void;
    finish(): Map<string, any>;
}
/**
 * A read-only data store for content collections. This is used to retrieve data from the content layer at runtime.
 * To add or modify data, use {@link MutableDataStore} instead.
 */
export declare class ImmutableDataStore {
    protected _collections: Map<string, Map<string, any>>;
    constructor();
    get<T = DataEntry>(collectionName: string, key: string): T | undefined;
    entries<T = DataEntry>(collectionName: string): Array<[id: string, T]>;
    values<T = DataEntry>(collectionName: string): Array<T>;
    keys(collectionName: string): Array<string>;
    has(collectionName: string, key: string): boolean;
    hasCollection(collectionName: string): boolean;
    collections(): Map<string, Map<string, any>>;
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
    static manifestToMap(manifest: Record<string, Array<string | {
        default: string;
    }>>): Map<string, Map<string, any>>;
    /**
     * Attempts to load a DataStore from the virtual module.
     * This only works in Vite.
     */
    static fromModule(): Promise<ImmutableDataStore>;
    static fromMap(data: Map<string, Map<string, any>>): Promise<ImmutableDataStore>;
}
