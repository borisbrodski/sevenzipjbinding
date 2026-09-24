import type { ResolvedServerUrls } from 'vite';
import { type Flags } from '../flags.js';
interface DevOptions {
    flags: Flags;
}
/**
 * `yargs-parser` camel-cases `--ignore-lock` to `flags.ignoreLock`.
 */
export declare function isIgnoreLock(flags: Flags): boolean;
/**
 * Returns an error message when `--ignore-lock` is combined with an explicit `--background`,
 * or `null` otherwise. A background server started with `--ignore-lock` could never be found
 * by `astro dev stop`/`status`/`logs` because the flag skips the lock file entirely.
 * Background implied by AI agent detection never reaches this check: the CLI drops it
 * whenever `--ignore-lock` is set.
 */
export declare function getBackgroundIgnoreLockConflict(flags: Flags): string | null;
/**
 * Pick the URL to record in the lock file.
 *
 * Vite only reports a `local` URL for loopback hosts. With `--host <custom-address>` set to a
 * specific non-loopback address the URL lands in `network` instead and `local` is empty, so
 * prefer `local` but fall back to `network` rather than reading `undefined`.
 *
 * Returns `null` when the server exposed no usable URL, which leaves the (purely bookkeeping)
 * lock file unwritten instead of taking down an otherwise healthy dev server.
 */
export declare function resolveLockFileUrl(resolvedUrls: ResolvedServerUrls): string | null;
/**
 * `--force` (replace the existing server) and `--ignore-lock` (start alongside it,
 * untracked) express contradictory intent. Returns an error message if both are set,
 * or `null` otherwise.
 */
export declare function getForceIgnoreLockConflict(flags: Flags): string | null;
export declare function dev({ flags }: DevOptions): Promise<import("../../core/dev/dev.js").DevServer | undefined>;
export {};
