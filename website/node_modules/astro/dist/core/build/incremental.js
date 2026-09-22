import fs from "node:fs";
const INCREMENTAL_CACHE_FILE = "incremental-build.json";
const INCREMENTAL_OUTPUT_DIR = "dist/";
const CACHE_VERSION = 1;
function getManifestFile(settings) {
  return new URL(INCREMENTAL_CACHE_FILE, settings.config.cacheDir);
}
function getCachedOutputFile(settings, outputFile) {
  return new URL(outputFile, new URL(INCREMENTAL_OUTPUT_DIR, settings.config.cacheDir));
}
function readManifest(settings, expectedConfigHash, expectedLockfileHash) {
  try {
    const raw = fs.readFileSync(getManifestFile(settings), "utf-8");
    const data = JSON.parse(raw);
    if (data.version !== CACHE_VERSION) {
      return null;
    }
    if (data.configHash !== expectedConfigHash) {
      return null;
    }
    if (data.lockfileHash !== expectedLockfileHash) {
      return null;
    }
    return data;
  } catch {
    return null;
  }
}
class IncrementalBuildCache {
  #previous;
  #next;
  #contentEntryHashes;
  #createdDirs = /* @__PURE__ */ new Set();
  constructor(configHash, lockfileHash, keyDigest, contentEntryHashes = /* @__PURE__ */ new Map(), previous = null) {
    this.#previous = previous;
    this.#contentEntryHashes = contentEntryHashes;
    this.#next = { version: CACHE_VERSION, configHash, lockfileHash, keyDigest, routes: {} };
  }
  /**
   * Load the cache from disk. When no valid manifest exists (missing, wrong
   * version, or a config or lockfile hash mismatch) the returned cache has no
   * previous build, so every path is rendered as a full build.
   *
   * `contentEntryHashes` is this build's map of content-entry render hashes,
   * used to detect when the content a path renders has changed.
   *
   * `force` ignores any existing manifest so every path is rebuilt, while still
   * recording a fresh cache for the next build.
   */
  static load(settings, configHash, lockfileHash, keyDigest, contentEntryHashes = /* @__PURE__ */ new Map(), force = false) {
    return new IncrementalBuildCache(
      configHash,
      lockfileHash,
      keyDigest,
      contentEntryHashes,
      force ? null : readManifest(settings, configHash, lockfileHash)
    );
  }
  /**
   * Determine if a path can be reused from the previous build. A path is
   * skippable when:
   * 1. It returned a cacheKey in this build.
   * 2. The previous cache has an entry for the route.
   * 3. The route's dependency hash matches the previous build (template code is identical).
   * 4. The previous cache has an entry for this exact path.
   * 5. The path's cacheKey matches the previous build (user data is identical).
   * 6. Every content entry the path rendered last build still has a matching
   *    render hash (imported components inside that content are unchanged).
   * 7. If the path renders a server island, the encryption key is unchanged, so
   *    the ciphertext baked into the restored HTML is still decryptable.
   */
  canSkip(routeComponent, pathname, dependencyHash, cacheKey, hasServerIsland = false) {
    const routeEntry = this.#previous?.routes[routeComponent];
    if (!routeEntry) return false;
    if (hasServerIsland && this.#previous?.keyDigest !== this.#next.keyDigest) return false;
    if (routeEntry.dependencyHash !== dependencyHash) return false;
    const pathEntry = routeEntry.paths[pathname];
    if (!pathEntry) return false;
    if (pathEntry.cacheKey !== cacheKey) return false;
    if (pathEntry.contentHashes) {
      for (const [entryPath, previousHash] of Object.entries(pathEntry.contentHashes)) {
        if (this.#contentEntryHashes.get(entryPath) !== previousHash) return false;
      }
    }
    return true;
  }
  /**
   * The content entries a path rendered in the previous build, so a skipped path
   * can carry its content-entry tracking forward without re-rendering.
   */
  previousContentEntryKeys(routeComponent, pathname) {
    const pathEntry = this.#previous?.routes[routeComponent]?.paths[pathname];
    return pathEntry?.contentHashes ? Object.keys(pathEntry.contentHashes) : void 0;
  }
  /**
   * The image transforms a path resolved in the previous build, so a skipped
   * path can replay them and carry them forward without re-rendering.
   */
  previousStaticImages(routeComponent, pathname) {
    return this.#previous?.routes[routeComponent]?.paths[pathname]?.staticImages;
  }
  /** The untransformed image references recorded by a path in the previous build. */
  previousReferencedImages(routeComponent, pathname) {
    return this.#previous?.routes[routeComponent]?.paths[pathname]?.referencedImages;
  }
  /**
   * The response headers a path collected in the previous build, so a skipped
   * path can replay them into a `staticHeaders` adapter's headers file.
   */
  previousHeaders(routeComponent, pathname) {
    return this.#previous?.routes[routeComponent]?.paths[pathname]?.headers;
  }
  /** Record a path in the next manifest so a later build can skip or prune it. */
  record(routeComponent, dependencyHash, pathname, cacheKey, outputFile, contentEntryKeys, staticImages, referencedImages, headers) {
    let routeEntry = this.#next.routes[routeComponent];
    if (!routeEntry) {
      routeEntry = { dependencyHash, paths: {} };
      this.#next.routes[routeComponent] = routeEntry;
    }
    routeEntry.dependencyHash = dependencyHash;
    const pathEntry = { cacheKey, outputFile };
    if (contentEntryKeys && contentEntryKeys.length > 0) {
      const contentHashes = {};
      for (const key of contentEntryKeys) {
        const hash = this.#contentEntryHashes.get(key);
        if (hash !== void 0) contentHashes[key] = hash;
      }
      if (Object.keys(contentHashes).length > 0) pathEntry.contentHashes = contentHashes;
    }
    if (staticImages && staticImages.length > 0) pathEntry.staticImages = staticImages;
    if (referencedImages && referencedImages.length > 0) {
      pathEntry.referencedImages = referencedImages;
    }
    if (headers && headers.length > 0) pathEntry.headers = headers;
    routeEntry.paths[pathname] = pathEntry;
  }
  /**
   * Cache copies recorded in the previous build that are no longer keyed in this
   * one, either because the path was removed from `getStaticPaths()` or dropped
   * its `cacheKey`. Their stored copies are stale and should be pruned. Paths
   * that are still keyed keep their copies, even when the `cacheKey` changed.
   */
  findOrphanedFiles() {
    if (!this.#previous) return [];
    const orphans = [];
    for (const [routeComponent, routeEntry] of Object.entries(this.#previous.routes)) {
      const nextRouteEntry = this.#next.routes[routeComponent];
      for (const [pathname, pathEntry] of Object.entries(routeEntry.paths)) {
        if (!nextRouteEntry?.paths[pathname]) {
          orphans.push(pathEntry.outputFile);
        }
      }
    }
    return orphans;
  }
  writeManifest(settings) {
    const manifestFile = getManifestFile(settings);
    fs.mkdirSync(new URL("./", manifestFile), { recursive: true });
    fs.writeFileSync(manifestFile, JSON.stringify(this.#next, null, "	"));
  }
  async restoreOutputFile(settings, outputFile, destination) {
    const cachedOutputFile = getCachedOutputFile(settings, outputFile);
    if (!fs.existsSync(cachedOutputFile)) return false;
    await this.#ensureDir(new URL("./", destination));
    await fs.promises.copyFile(cachedOutputFile, destination);
    return true;
  }
  async writeOutputFile(settings, outputFile, body) {
    const cachedOutputFile = getCachedOutputFile(settings, outputFile);
    await this.#ensureDir(new URL("./", cachedOutputFile));
    await fs.promises.writeFile(cachedOutputFile, body);
  }
  async deleteOutputFile(settings, outputFile) {
    await fs.promises.rm(getCachedOutputFile(settings, outputFile), { force: true });
  }
  async #ensureDir(dir) {
    const key = dir.href;
    if (this.#createdDirs.has(key)) return;
    await fs.promises.mkdir(dir, { recursive: true });
    this.#createdDirs.add(key);
  }
}
export {
  IncrementalBuildCache
};
