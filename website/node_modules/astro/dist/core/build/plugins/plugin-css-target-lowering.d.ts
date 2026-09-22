import type { Plugin as VitePlugin } from 'vite';
/**
 * Vite plugin that applies CSS target lowering without minification.
 *
 * Vite's `finalizeCss` only applies CSS target lowering (via lightningcss) when
 * `config.build.cssMinify` is truthy, because target lowering and minification
 * are coupled in `minifyCSS`. When users set `minify: false`, Astro forces
 * `cssMinify: false`, which disables target lowering for all CSS.
 *
 * This plugin fills that gap: when `cssMinify` is falsy and a CSS target is
 * configured, it runs lightningcss with `minify: false` on all CSS assets in
 * `generateBundle`, applying target lowering without minification.
 */
export declare function pluginCssTargetLowering(): VitePlugin;
