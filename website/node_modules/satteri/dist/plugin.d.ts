import type { MdastPluginInstance } from "./mdast/mdast-visitor.js";
import type { HastVisitorInstance } from "./hast/hast-visitor.js";
import type { Data, SourceFormat } from "./types.js";
/**
 * What a plugin factory is told about the document, before it is parsed.
 *
 * Return `null`, `undefined` or `false` from the factory to leave the plugin
 * out of the pipeline for this document.
 */
export interface PluginFactoryContext {
    /** The `fileURL` compile option, or `undefined` when none was given. */
    readonly fileURL: URL | undefined;
    /** Which kind of document is being compiled. */
    readonly sourceFormat: SourceFormat;
    /** The unparsed source, minus a leading BOM as the parser sees it. Intended
     *  for cheap checks, not for parsing Markdown. */
    readonly source: string;
    /** The document-level data bag, before any plugin has run. */
    readonly data: Data;
}
export type MdastPluginDefinition = MdastPluginInstance & {
    name: string;
};
export type HastPluginDefinition = HastVisitorInstance & {
    name: string;
};
type PluginEntry<D> = D | ((ctx: PluginFactoryContext) => PluginEntry<D>) | readonly PluginEntry<D>[] | null | undefined | false;
/** Entry accepted by `mdastPlugins`. */
export type MdastPluginEntry = PluginEntry<MdastPluginDefinition>;
/** Entry accepted by `hastPlugins`. */
export type HastPluginEntry = PluginEntry<HastPluginDefinition>;
/** Value accepted by the `mdastPlugins` option. */
export type MdastPluginList = readonly MdastPluginEntry[];
/** Value accepted by the `hastPlugins` option. */
export type HastPluginList = readonly HastPluginEntry[];
/** Older name for {@link MdastPluginEntry}. */
export type MdastPluginInput = MdastPluginEntry;
/** Older name for {@link HastPluginEntry}. */
export type HastPluginInput = HastPluginEntry;
/** The one place a plugin option becomes the definition array the pipeline
 *  runs. Factories resolve here and nowhere else, so each is called once per
 *  compile no matter how deeply it is nested. */
export declare function normalizePlugins<D>(entries: readonly PluginEntry<D>[], option: string, source: string, fileURL: URL | undefined, sourceFormat: SourceFormat, data: Data): D[];
export declare function defineMdastPlugin<P extends MdastPluginDefinition>(definition: P): P;
export declare function defineHastPlugin<P extends HastPluginDefinition>(definition: P): P;
export {};
