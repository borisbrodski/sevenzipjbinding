import type { Defaults, FontType } from './types.js';
export declare const DEFAULTS: Defaults;
export declare const VIRTUAL_MODULE_ID = "virtual:astro:assets/fonts/internal";
export declare const RESOLVED_VIRTUAL_MODULE_ID: string;
export declare const RUNTIME_VIRTUAL_MODULE_ID = "virtual:astro:assets/fonts/runtime";
export declare const RESOLVED_RUNTIME_VIRTUAL_MODULE_ID: string;
export declare const RUNTIME_FONT_FILE_URL_RESOLVER_VIRTUAL_MODULE_ID = "virtual:astro:assets/fonts/runtime/font-file-url-resolver";
export declare const RESOLVED_RUNTIME_FONT_FILE_URL_RESOLVER_VIRTUAL_MODULE_ID: string;
export declare const ASSETS_DIR = "fonts";
export declare const CACHE_DIR = "./fonts/";
export declare const FONT_TYPES: readonly ["woff2", "woff", "otf", "ttf", "eot"];
export declare const FONT_FORMATS: Array<{
    type: FontType;
    format: string;
}>;
export declare const GENERIC_FALLBACK_NAMES: readonly ["serif", "sans-serif", "monospace", "cursive", "fantasy", "system-ui", "ui-serif", "ui-sans-serif", "ui-monospace", "ui-rounded", "emoji", "math", "fangsong"];
export declare const FONTS_TYPES_FILE = "fonts.d.ts";
/**
 * Variable name used in the font-file-url-resolver virtual module to hold
 * the ephemeral font HTTP server address. The incremental build plugin
 * strips the variable declaration (which contains an OS-assigned port that
 * changes every build) from the module source before hashing so that the
 * dependency hash is deterministic across builds.
 */
export declare const FONTS_SERVER_ADDRESS_PLACEHOLDER = "__ASTRO_FONTS_SERVER_ADDRESS__";
