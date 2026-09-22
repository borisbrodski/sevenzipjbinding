/** Subset of `node:fs` the hasher depends on, so tests can pass a fake. */
export interface LockfileHasherFs {
    readFileSync(path: string): Buffer;
}
export declare class LockfileHasher {
    #private;
    constructor(fs?: LockfileHasherFs);
    /**
     * Produce a sha256 over the raw bytes of the given lockfiles. Files are sorted
     * by name so discovery order does not affect the result, and each name is mixed
     * in so identical content under different package managers still yields distinct
     * hashes. Returns an empty string for an empty list, letting callers treat
     * "no lockfile" as "no signal" rather than a hash collision.
     */
    hash(lockfilePaths: string[]): Promise<string>;
}
