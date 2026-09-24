import type { SerializedStaticImage } from '../../assets/types.js';
/**
 * The per-render store. One instance is created per collecting render and is
 * reachable only through the installed {@link RenderCollectorScope} while that
 * render's async execution is in scope. Fields are optional so record helpers
 * tolerate stores created by a different astro module instance (or version)
 * that only knows a subset of collectors.
 */
export interface RenderCollectors {
    /** Root-relative `filePath`s of the content entries rendered. */
    contentEntries?: Set<string>;
    /**
     * Every image transform resolved, dedup hits included; array push,
     * duplicates preserved.
     */
    staticImages?: SerializedStaticImage[];
    /** Absolute source paths of images referenced without a transform. */
    referencedImages?: Set<string>;
}
/**
 * Structurally satisfied by `AsyncLocalStorage<RenderCollectors>` — deliberate:
 * installing an ALS instance directly IS the only shipped implementation.
 */
export interface RenderCollectorScope {
    run<T>(store: RenderCollectors, fn: () => T): T;
    getStore(): RenderCollectors | undefined;
}
/**
 * Installs a render scope on the process-wide channel and returns the installed
 * scope. First-wins: when a scope is already installed (possibly by another
 * module instance), the existing scope is returned and the argument discarded,
 * so callers that both awaited an import converge on one scope.
 */
export declare function installRenderScope(scope: RenderCollectorScope): RenderCollectorScope;
/** The installed render scope, or `undefined` when none was installed. */
export declare function getInstalledRenderScope(): RenderCollectorScope | undefined;
/** Test-only: remove the installed scope so unit tests can reset the channel. */
export declare function uninstallRenderScope(): void;
/** The current render's collectors store, or `undefined` when not collecting. */
export declare function getRenderCollectors(): RenderCollectors | undefined;
