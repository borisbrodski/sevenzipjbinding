import type { AstroMarkdownOptions, MarkdownHeading, MdxRendererOptions, MdxRenderResult } from '@astrojs/internal-helpers/markdown';
import { type HastPluginDefinition, type MdastPluginDefinition } from 'satteri';
import type { SatteriResolvedOptions } from '../processor.js';
export type { MdastPluginDefinition, HastPluginDefinition, MarkdownHeading };
export type { AstroMetadata } from './hast-astro-metadata.js';
declare module 'hast' {
    interface ElementData {
        lang?: string | null;
    }
}
export declare function createSatteriMdxProcessor(shared: AstroMarkdownOptions, mdx: MdxRendererOptions, satteriOptions: SatteriResolvedOptions): {
    process(content: string, filePath: string, frontmatter: Record<string, any>): Promise<MdxRenderResult>;
};
