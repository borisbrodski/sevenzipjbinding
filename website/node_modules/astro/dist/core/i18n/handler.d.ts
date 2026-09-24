import { I18nRouter } from '../../i18n/router.js';
import type { SSRManifest } from '../app/types.js';
import type { FetchState } from '../fetch/fetch-state.js';
/**
 * The compiled i18n configuration for a manifest: the config values plus the
 * `I18nRouter` (with its inverted domain lookup table), compiled once and
 * reused across requests.
 */
export interface CompiledI18n {
    config: NonNullable<SSRManifest['i18n']>;
    base: SSRManifest['base'];
    trailingSlash: SSRManifest['trailingSlash'];
    format: SSRManifest['buildFormat'];
    router: I18nRouter;
}
/**
 * Pure compile from explicit values — used by `astro:i18n`'s manual-mode
 * middleware wrapper (`src/i18n/middleware.ts`), which receives the values as
 * arguments rather than reading a manifest.
 */
export declare function compileI18n(i18n: NonNullable<SSRManifest['i18n']>, base: SSRManifest['base'], trailingSlash: SSRManifest['trailingSlash'], format: SSRManifest['buildFormat']): CompiledI18n;
/**
 * The compiled i18n post-processor for a manifest, or `null` when i18n is
 * unset or the routing strategy is `manual`.
 */
export declare function getI18n(manifest: SSRManifest): CompiledI18n | null;
/**
 * Post-processes a rendered `Response` against the compiled i18n
 * configuration, as an explicit step in `handleRequest` after the middleware
 * chain returns. The manual-strategy public API (`astro:i18n.middleware(...)`)
 * wraps this in a middleware-shaped closure.
 */
export declare function finalizeI18n(compiled: CompiledI18n, state: FetchState, response: Response): Promise<Response>;
