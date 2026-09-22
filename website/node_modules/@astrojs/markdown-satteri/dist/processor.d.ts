import type { MarkdownProcessor } from '@astrojs/internal-helpers/markdown';
import type { Features, HastPluginEntry, HastPluginList, MdastPluginEntry, MdastPluginList } from 'satteri';
export interface SatteriFeatures extends Omit<Features, 'smartPunctuation'> {
    /**
     * Smart punctuation à la SmartyPants.
     *
     * Default: `true` in Astro.
     */
    smartPunctuation?: Features['smartPunctuation'];
}
export interface SatteriProcessorOptions {
    mdastPlugins?: MdastPluginList;
    hastPlugins?: HastPluginList;
    features?: SatteriFeatures;
}
/**
 * Resolved options on the processor returned by `satteri()`. Always populated
 * (the factory normalises absent inputs into defaults).
 */
export interface SatteriResolvedOptions {
    mdastPlugins: MdastPluginEntry[];
    hastPlugins: HastPluginEntry[];
    features: SatteriFeatures;
}
/**
 * Use the Sätteri Markdown processor for `markdown.processor`. Extend the pipeline
 * with mdast or hast plugins, or toggle Markdown features.
 *
 * ```js
 * import { satteri } from '@astrojs/markdown-satteri';
 *
 * export default defineConfig({
 *   markdown: {
 *     processor: satteri({ features: { directive: true } }),
 *   },
 * });
 * ```
 */
export declare function satteri(opts?: SatteriProcessorOptions): MarkdownProcessor<SatteriResolvedOptions>;
export declare function isSatteriProcessor(p: {
    name: string;
}): p is MarkdownProcessor<SatteriResolvedOptions>;
