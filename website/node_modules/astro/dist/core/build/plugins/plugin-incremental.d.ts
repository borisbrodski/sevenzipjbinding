import type { Plugin as VitePlugin } from 'vite';
import type { BuildInternals } from '../internal.js';
/**
 * Captures a dependency hash for each prerendered page route during the build.
 *
 * The base hash is derived during the prerender build from the sorted set of all
 * transitive module dependencies of the page, so any change to the page's
 * template, layouts, components, or utilities produces a different hash. During
 * the client build, the hash of each `client:only` component's and hoisted
 * `<script>`'s own module graph is folded in, since those never enter the
 * prerender graph. Content entries are hashed separately per entry, since their
 * render modules sit behind `content-data`-pruned bridges.
 */
export declare function pluginIncremental(internals: BuildInternals, root: URL): VitePlugin;
