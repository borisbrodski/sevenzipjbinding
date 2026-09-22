/**
 * Lockfile names recognized across the package managers we support. A project
 * can commit more than one, so these carry no priority: every match in a
 * directory is returned.
 */
export declare const KNOWN_LOCKFILES: readonly ["pnpm-lock.yaml", "package-lock.json", "yarn.lock", "bun.lock", "bun.lockb"];
/** Subset of `node:fs` the finder depends on, so tests can pass a fake. */
export interface LockfileFinderFs {
    existsSync(path: string): boolean;
}
export declare class LockfileFinder {
    #private;
    constructor(fs?: LockfileFinderFs, lockfileNames?: readonly string[]);
    /**
     * Walk upward from `startDir` and return the absolute paths of every known
     * lockfile in the first directory that contains at least one. In a workspace
     * the lockfile lives above the individual project, so the walk continues until
     * a directory yields a match. Returns an empty array if the filesystem root is
     * reached without finding one.
     */
    find(startDir: string): string[];
}
