import type { BuildInternals } from './internal.js';
import type { PageBuildData, StylesheetAsset } from './types.js';
/**
 * From its route and component, get the page data from the build internals.
 * @param internals Build Internals with all the pages
 * @param route The route of the page, used to identify the page
 * @param component The component of the page, used to identify the page
 */
export declare function getPageData(internals: BuildInternals, route: string, component: string): PageBuildData | undefined;
interface OrderInfo {
    depth: number;
    order: number;
}
/**
 * Sort a page's CSS by depth. A higher depth means that the CSS comes from shared subcomponents.
 * A lower depth means it comes directly from the top-level page.
 * Can be used to sort stylesheets so that shared rules come first
 * and page-specific rules come after.
 */
export declare function cssOrder(a: OrderInfo, b: OrderInfo): 1 | -1;
/**
 * Merges inline CSS into as few stylesheets as possible,
 * preserving ordering when there are non-inlined in between.
 *
 * CSS chunks containing `@import` are never merged with other chunks.
 * Per CSS spec, `@import` rules must appear at the top of a stylesheet
 * or browsers silently ignore them. Keeping these chunks as separate
 * `<style>` tags ensures the `@import` stays at the top of its own stylesheet.
 */
export declare function mergeInlineCss(acc: Array<StylesheetAsset>, current: StylesheetAsset): Array<StylesheetAsset>;
export {};
