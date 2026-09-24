import type { Plugin as VitePlugin } from 'vite';
import type { AstroSettings } from '../../types/astro.js';
import { type NormalizedLoggerConfig } from './utils.js';
export declare const VIRTUAL_LOGGER_ID = "virtual:astro:logger";
/** Resolves an entrypoint to a module id, or `null` if it cannot be resolved. */
type ResolveEntrypoint = (entrypoint: string) => Promise<string | null>;
interface EmittedDestination {
    /** The expression instantiating the destination, e.g. `_logger0({ level: 'info' })` */
    expression: string;
    /** The import statements `expression` depends on, in declaration order */
    imports: string[];
}
/**
 * Emits the source of a logger destination, so that the handler is part of the bundle
 * rather than imported at runtime from a path that no longer exists once deployed.
 *
 * This is the build-time counterpart of `createDestination()` in `./load.ts`: both walk
 * the same normalized config, but this one *generates code* that instantiates the
 * destination, while the Node one instantiates it directly through `import()`.
 */
export declare function emitDestination(config: NormalizedLoggerConfig, resolveEntrypoint: ResolveEntrypoint, 
/**
 * Import names must be unique across the whole virtual module, so nested
 * destinations continue numbering where their parent left off.
 */
nameOffset?: number): Promise<EmittedDestination>;
/**
 * Emits `virtual:astro:logger`: the configured destination as the default export
 * (`null` when the user configured none) plus the resolved log level.
 *
 * The module is always registered, even without a `logger` config, so that code
 * generated ahead of the user's config — `astro:content`, which is built from a
 * static template — can import it unconditionally. It deliberately imports
 * nothing but the user's destination entrypoint: unlike `virtual:astro:manifest`
 * it is a leaf, so reaching a logger through it cannot pull renderers and pages
 * into an importer's module graph.
 */
export declare function vitePluginLogger({ settings }: {
    settings: AstroSettings;
}): VitePlugin;
export {};
