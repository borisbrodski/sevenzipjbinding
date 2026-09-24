import nodeFs from "node:fs";
import path from "node:path";
const KNOWN_LOCKFILES = [
  "pnpm-lock.yaml",
  "package-lock.json",
  "yarn.lock",
  "bun.lock",
  "bun.lockb"
];
class LockfileFinder {
  #fs;
  #lockfileNames;
  constructor(fs = nodeFs, lockfileNames = KNOWN_LOCKFILES) {
    this.#fs = fs;
    this.#lockfileNames = lockfileNames;
  }
  /**
   * Walk upward from `startDir` and return the absolute paths of every known
   * lockfile in the first directory that contains at least one. In a workspace
   * the lockfile lives above the individual project, so the walk continues until
   * a directory yields a match. Returns an empty array if the filesystem root is
   * reached without finding one.
   */
  find(startDir) {
    let dir = path.resolve(startDir);
    let previous = "";
    while (dir !== previous) {
      const found = [];
      for (const name of this.#lockfileNames) {
        const candidate = path.join(dir, name);
        if (this.#fs.existsSync(candidate)) {
          found.push(candidate);
        }
      }
      if (found.length > 0) return found;
      previous = dir;
      dir = path.dirname(dir);
    }
    return [];
  }
}
export {
  KNOWN_LOCKFILES,
  LockfileFinder
};
