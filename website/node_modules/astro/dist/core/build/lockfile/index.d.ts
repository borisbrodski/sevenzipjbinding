import nodeFs from 'node:fs';
export { KNOWN_LOCKFILES, LockfileFinder, type LockfileFinderFs } from './finder.js';
export { LockfileHasher, type LockfileHasherFs } from './hasher.js';
/**
 * Find the nearest lockfiles at or above `startDir` and hash them into a single
 * digest used to globally invalidate the incremental build cache when
 * dependencies change. Returns an empty string when no lockfile is found.
 */
export declare function computeLockfileHash(startDir: string, fs?: typeof nodeFs): Promise<string>;
