import type { SSRManifest } from '../app/types.js';
import type { AstroLogger } from './core.js';
/**
 * One identity-stable logger per manifest. Created on first access as a
 * console logger at `manifest.logLevel`.
 *
 * The instance is never replaced: `getResolvedLogger` swaps the
 * destination in place via `AstroLogger.setDestination`, so every holder
 * (state-captured logger, adapterLogger's retained options) writes to the
 * new destination immediately.
 */
export declare function getLogger(manifest: SSRManifest): AstroLogger;
/**
 * Composition-time injection (dev server logger, DevApp console logger, an
 * App facade constructed with a custom logger path, tests). Must be called
 * before the first `getLogger()` read to take effect deterministically;
 * replaces the stored instance either way.
 */
export declare function setLogger(manifest: SSRManifest, logger: AstroLogger): void;
/**
 * The manifest's logger with the user-configured destination (the
 * `manifest.logger` thunk) resolved and applied via `setDestination` on the
 * identity-stable logger. Memoized single-flight; awaited at request entry.
 *
 * Never rejects: a destination that fails to load is reported through the
 * unswapped (console) logger, which stays in place — a broken custom
 * destination cannot fail a request. Because the derivation always resolves,
 * the memo's delete-on-rejection retry path never triggers: the thunk runs
 * at most once per manifest.
 */
export declare function getResolvedLogger(manifest: SSRManifest): Promise<AstroLogger>;
