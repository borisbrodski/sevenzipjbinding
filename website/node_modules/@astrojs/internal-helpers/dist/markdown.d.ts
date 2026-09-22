import type * as hast from 'hast';
import type * as mdast from 'mdast';
import type { Options as SmartypantsOptions } from 'retext-smartypants';
import type { BuiltinTheme, HighlighterCoreOptions, LanguageRegistration, ShikiTransformer, ThemeRegistration, ThemeRegistrationRaw } from 'shiki';
import type { Plugin } from 'unified';
import type { RemotePattern } from './remote.js';
export type SyntaxHighlightConfigType = 'shiki' | 'prism';
export interface SyntaxHighlightConfig {
    type: SyntaxHighlightConfigType;
    excludeLangs?: string[];
}
type ThemePresets = BuiltinTheme | 'css-variables';
export interface ShikiConfig {
    langs?: LanguageRegistration[];
    theme?: ThemePresets | ThemeRegistration | ThemeRegistrationRaw;
    themes?: Record<string, ThemePresets | ThemeRegistration | ThemeRegistrationRaw>;
    langAlias?: HighlighterCoreOptions['langAlias'];
    defaultColor?: 'light' | 'dark' | string | false;
    wrap?: boolean | null;
    transformers?: ShikiTransformer[];
}
export type Smartypants = SmartypantsOptions;
export type RemarkPlugin<PluginParameters extends any[] = any[]> = Plugin<PluginParameters, mdast.Root>;
export type RemarkPlugins = (string | [string, any] | RemarkPlugin | [RemarkPlugin, any])[];
export type RehypePlugin<PluginParameters extends any[] = any[]> = Plugin<PluginParameters, hast.Root>;
export type RehypePlugins = (string | [string, any] | RehypePlugin | [RehypePlugin, any])[];
export type RemarkRehype = Record<string, unknown>;
export type { PluggableList } from 'unified';
export interface MarkdownHeading {
    depth: number;
    slug: string;
    text: string;
}
/**
 * Shared markdown configuration that lives on `config.markdown.*`. Passed into
 * `MarkdownProcessor.createRenderer` so each processor can honour cross-cutting
 * options (syntax highlighting, images, …) regardless of which processor is selected.
 */
export interface AstroMarkdownOptions {
    syntaxHighlight?: SyntaxHighlightConfig | SyntaxHighlightConfigType | false;
    shikiConfig?: ShikiConfig;
    /** @deprecated Configure via the processor instead, e.g. `unified({ gfm: false })` or `satteri({ features: { gfm: false } })`. */
    gfm?: boolean;
    /** @deprecated Configure via the processor instead, e.g. `unified({ smartypants: false })` or `satteri({ features: { smartPunctuation: false } })`. */
    smartypants?: boolean | SmartypantsOptions;
    /** @deprecated Use `markdown.processor: unified({ remarkPlugins })` instead. */
    remarkPlugins?: RemarkPlugins;
    /** @deprecated Use `markdown.processor: unified({ rehypePlugins })` instead. */
    rehypePlugins?: RehypePlugins;
    /** @deprecated Use `markdown.processor: unified({ remarkRehype })` instead. */
    remarkRehype?: RemarkRehype;
    image?: {
        domains?: string[];
        remotePatterns?: RemotePattern[];
    };
}
/** Runtime renderer for `.md` files. Returned by `MarkdownProcessor.createRenderer`. */
export interface MarkdownRenderer {
    render: (content: string, opts?: MarkdownRenderOptions) => Promise<MarkdownRenderResult>;
}
export interface MarkdownRenderOptions {
    fileURL?: URL;
    frontmatter?: Record<string, any>;
}
export interface MarkdownRenderResult {
    code: string;
    metadata: {
        headings: MarkdownHeading[];
        localImagePaths: string[];
        remoteImagePaths: string[];
        frontmatter: Record<string, any>;
    };
}
/**
 * The processor placed on `config.markdown.processor`. Factory functions like
 * `unified()` / `satteri()` return one of these; third-party processors can
 * implement the interface to plug in their own markdown rendering pipeline.
 *
 * `TOptions` is the processor-specific options bag (e.g. `UnifiedProcessorOptions`).
 * Integrations extend the pipeline by mutating `processor.options.*` directly.
 */
export interface MarkdownProcessor<TOptions extends object = object> {
    /** Identifier for this processor, e.g. `'unified'`. Surfaced in errors and warnings. */
    readonly name: string;
    /** Processor-specific options. Always present; pass `{}` for processors that take no options. */
    options: TOptions;
    /** Create the runtime renderer for `.md` files. */
    createRenderer(shared: AstroMarkdownOptions): Promise<MarkdownRenderer>;
    /**
     * Create the runtime renderer for `.mdx` files. Optional — when absent, `@astrojs/mdx`
     * throws, since the processor cannot render `.mdx`. The built-in `unified` / `satteri`
     * processors implement this; third-party processors should too to enable MDX support.
     */
    createMdxRenderer?(shared: AstroMarkdownOptions, mdx: MdxRendererOptions): Promise<MdxRenderer>;
}
/** Cross-cutting MDX options passed to `createMdxRenderer` regardless of processor. */
export interface MdxRendererOptions {
    optimize: boolean | {
        ignoreElementNames?: string[];
    };
    /**
     * Astro's `srcDir`. Pipelines use it to default layout-less MDX pages to UTF-8.
     * Optional because `@astrojs/mdx` 7.0.x calls `createMdxRenderer` without it, so a
     * pipeline must still render when it is absent.
     */
    srcDir?: URL;
    /** Whether Vite has sourcemaps enabled; a pipeline may emit a source map when true. */
    sourcemap?: boolean;
}
/** Runtime renderer for `.mdx` files returned by `createMdxRenderer`. */
export interface MdxRenderer {
    process(content: string, filePath: string, frontmatter: Record<string, any>): Promise<MdxRenderResult>;
}
export interface MdxRenderResult {
    code: string;
    /** Source map. Stringified to satisfy Vite's `SourceMapInput`. */
    map?: string | null;
    astroMetadata: AstroMetadata;
}
export interface AstroComponentMetadata {
    exportName: string;
    localName: string;
    specifier: string;
    resolvedPath: string;
}
export interface AstroMetadata {
    hydratedComponents: AstroComponentMetadata[];
    clientOnlyComponents: AstroComponentMetadata[];
    serverComponents: AstroComponentMetadata[];
    scripts: unknown[];
    propagation: string;
    containsHead: boolean;
    pageOptions: object;
}
export declare const defaultExcludeLanguages: string[];
export declare const syntaxHighlightDefaults: Required<SyntaxHighlightConfig>;
/** Default values for the markdown config, regardless of processor. */
export declare const markdownConfigDefaults: Required<Omit<AstroMarkdownOptions, 'image'>>;
