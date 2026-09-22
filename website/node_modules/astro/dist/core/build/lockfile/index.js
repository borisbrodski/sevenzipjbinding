import nodeFs from "node:fs";
import { LockfileFinder } from "./finder.js";
import { LockfileHasher } from "./hasher.js";
import { KNOWN_LOCKFILES, LockfileFinder as LockfileFinder2 } from "./finder.js";
import { LockfileHasher as LockfileHasher2 } from "./hasher.js";
function computeLockfileHash(startDir, fs = nodeFs) {
  const finder = new LockfileFinder(fs);
  const hasher = new LockfileHasher(fs);
  return hasher.hash(finder.find(startDir));
}
export {
  KNOWN_LOCKFILES,
  LockfileFinder2 as LockfileFinder,
  LockfileHasher2 as LockfileHasher,
  computeLockfileHash
};
