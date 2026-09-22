import type { Features, HastNode, HastPluginDefinition, HastPluginList, MdastPluginDefinition, MdastPluginList } from 'satteri';
import type { AstroMarkdownOptions, MarkdownHeading, MarkdownRenderer } from '@astrojs/internal-helpers/markdown';
type HighlightFn = (code: string, lang: string, meta?: string) => Promise<HastNode>;
declare module 'satteri' {
    interface DataMap {
        astro: SatteriAstroData;
    }
}
export interface SatteriAstroData {
    frontmatter: Record<string, any>;
    headings: MarkdownHeading[];
    localImagePaths: Set<string>;
    remoteImagePaths: Set<string>;
}
export declare function createCollectImagesPlugin(image?: AstroMarkdownOptions['image']): MdastPluginDefinition;
export declare function collectHastText(node: HastNode, frontmatter: Record<string, any> | undefined): string;
export declare function createHeadingIdsPlugin(): HastPluginDefinition;
/** Tags <img> with `__ASTRO_IMAGE_` for astro's vite-plugin-markdown to replace with the processed image's attributes. */
export declare function createImageMarkerPlugin(): HastPluginDefinition;
export declare function createHighlightPlugin(highlight: HighlightFn, excludeLangs: string[] | undefined): HastPluginDefinition;
export interface SatteriMarkdownProcessorOptions extends AstroMarkdownOptions {
    mdastPlugins?: MdastPluginList;
    hastPlugins?: HastPluginList;
    features?: Features;
}
/**
 * Build the highlighter for the Sätteri pipeline, or `undefined` when syntax
 * highlighting is disabled. Shiki and Prism both resolve to a `HighlightFn`
 * that turns a code block into a `<pre>` hast element. Returning real hast
 * (rather than a raw HTML string) keeps the `<pre>` addressable by the MDX
 * pipeline, so `components.pre` overrides still apply to highlighted blocks.
 */
export declare function createHighlightFn(syntaxHighlight: AstroMarkdownOptions['syntaxHighlight'], shikiConfig: AstroMarkdownOptions['shikiConfig'] | undefined): Promise<HighlightFn | undefined>;
export declare function createSatteriMarkdownProcessor(opts?: SatteriMarkdownProcessorOptions): Promise<MarkdownRenderer>;
export {};
