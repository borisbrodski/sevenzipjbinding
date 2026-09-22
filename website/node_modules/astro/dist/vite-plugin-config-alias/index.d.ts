import { type Plugin as VitePlugin } from 'vite';
import type { AstroSettings } from '../types/astro.js';
/**
 * Fallback for tsconfig path aliases that Vite's `resolve.tsconfigPaths` does
 * not currently handle in Astro's pipeline.
 *
 * This plugin is intentionally limited to the syntax Astro already supported
 * before enabling `resolve.tsconfigPaths`:
 * - CSS: `@import "..."`
 * - CSS: `@import url("...")`
 * - CSS: quoted `url("...")` references
 * - Modules: JS, TS, and Astro import specifiers handled by `resolveId`
 *
 * Do not expand this fallback to new CSS at-rules or preprocessor syntax. It
 * does not support `@use`, `@forward`, `@reference`, `@config`, unquoted
 * `url(...)`, or every place a CSS tool might accept a file path. Those should
 * be handled by Vite's native resolver instead.
 *
 * @deprecated This fallback will be removed in a future Astro version once Vite
 * handles these remaining alias paths.
 */
export default function configAliasVitePlugin({ settings, }: {
    settings: AstroSettings;
}): VitePlugin[] | null;
