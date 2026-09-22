export declare const ASTRO_INCREMENTAL_META_KEY = "astroIncremental";
export declare function createContentDataIncrementalMetadata(): {
    astroIncremental: {
        kind: string;
    };
};
export declare function isContentDataIncrementalModule(info: {
    meta?: Record<string, any>;
} | null | undefined): boolean;
