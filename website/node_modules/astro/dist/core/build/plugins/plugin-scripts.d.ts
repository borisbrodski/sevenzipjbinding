import type { BuildOptions, Plugin as VitePlugin, Rollup } from 'vite';
import type { BuildInternals } from '../internal.js';
type GetModuleInfo = (moduleId: string) => Rollup.ModuleInfo | null;
export type ScriptChunkInfo = Pick<Rollup.OutputChunk, 'code' | 'facadeModuleId' | 'fileName' | 'imports' | 'dynamicImports' | 'moduleIds'>;
export declare function chunkHasDynamicImports(output: Pick<ScriptChunkInfo, 'dynamicImports' | 'moduleIds'>, getModuleInfo: GetModuleInfo): boolean;
export declare function shouldInlineScriptChunk(output: ScriptChunkInfo, { discoveredScripts, importedIds, assetInlineLimit, getModuleInfo, }: {
    discoveredScripts: Set<string>;
    importedIds: Set<string>;
    assetInlineLimit: NonNullable<BuildOptions['assetsInlineLimit']>;
    getModuleInfo: GetModuleInfo;
}): boolean;
/**
 * Inline scripts from Astro files directly into the HTML.
 */
export declare function pluginScripts(internals: BuildInternals): VitePlugin;
export {};
