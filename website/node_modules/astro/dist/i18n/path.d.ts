import type { Locales } from '../types/public/config.js';
export declare function pathHasLocale(path: string, locales: Locales): boolean;
/**
 *
 * Given a locale, this function:
 * - replaces the `_` with a `-`;
 * - transforms all letters to be lowercase;
 */
export declare function normalizeTheLocale(locale: string): string;
/**
 *
 * Given a path or path segment, this function:
 * - removes the `.html` extension if it exists
 */
export declare function normalizeThePath(path: string): string;
