import nodeFs from "node:fs";
import os from "node:os";
import { fileURLToPath } from "node:url";
import PLimit from "p-limit";
import PQueue from "p-queue";
import colors from "piccolore";
import {
  generateImagesForPath,
  getStaticImageList,
  prepareAssetsGenerationEnv,
  restoreReferencedImages,
  restoreStaticImages
} from "../../assets/build/generate.js";
import {
  appendForwardSlash,
  collapseDuplicateTrailingSlashes,
  hasFileExtension,
  joinPaths,
  removeLeadingForwardSlash,
  removeTrailingForwardSlash,
  trimSlashes
} from "../../core/path.js";
import { runHookBuildGenerated, toIntegrationResolvedRoute } from "../../integrations/hooks.js";
import { hashCryptoKey } from "../encryption.js";
import { AstroError, AstroErrorData } from "../errors/index.js";
import { getRedirectLocationOrThrow } from "../redirects/index.js";
import { createRequest } from "../request.js";
import { redirectTemplate } from "../routing/3xx.js";
import { routeIsRedirect } from "../routing/helpers.js";
import { getOutputFilename } from "../output-filename.js";
import { getOutFile, getOutFolder } from "./common.js";
import { createDefaultPrerenderer } from "./default-prerenderer.js";
import { IncrementalBuildCache } from "./incremental.js";
import { computeConfigHash } from "./config-hash/index.js";
import { computeLockfileHash } from "./lockfile/index.js";
import { hasPrerenderedPages } from "./internal.js";
import { getTimeStat, shouldAppendForwardSlash } from "./util.js";
async function generatePages(options, internals, prerenderOutputDir) {
  const generatePagesTimer = performance.now();
  const ssr = options.settings.buildOutput === "server";
  const logger = options.logger;
  const hasPagesToGenerate = hasPrerenderedPages(internals);
  if (ssr && !hasPagesToGenerate) {
    delete globalThis?.astroAsset?.addStaticImage;
  }
  if (!hasPagesToGenerate) {
    return;
  }
  let prerenderer;
  const settingsPrerenderer = options.settings.prerenderer;
  if (!settingsPrerenderer) {
    prerenderer = createDefaultPrerenderer({
      internals,
      options,
      prerenderOutputDir
    });
  } else if (typeof settingsPrerenderer === "function") {
    const defaultPrerenderer = createDefaultPrerenderer({
      internals,
      options,
      prerenderOutputDir
    });
    prerenderer = settingsPrerenderer(defaultPrerenderer);
  } else {
    prerenderer = settingsPrerenderer;
  }
  await prerenderer.setup?.();
  const verb = ssr ? "prerendering" : "generating";
  logger.info("SKIP_FORMAT", `
${colors.bgGreen(colors.black(` ${verb} static routes `))}`);
  const routeToHeaders = /* @__PURE__ */ new Map();
  let staticImageList = getStaticImageList();
  let cache = null;
  if (options.settings.config.experimental.incrementalBuild) {
    const [configHash, lockfileHash, keyDigest] = await Promise.all([
      computeConfigHash(options.settings.config),
      computeLockfileHash(fileURLToPath(options.settings.config.root)),
      options.key.then(hashCryptoKey)
    ]);
    cache = IncrementalBuildCache.load(
      options.settings,
      configHash,
      lockfileHash,
      keyDigest,
      internals.contentEntryRenderHashes ?? /* @__PURE__ */ new Map(),
      options.force
    );
  }
  try {
    const pathsWithRoutes = await prerenderer.getStaticPaths();
    const hasI18nDomains = ssr && options.settings.config.i18n?.domains && Object.keys(options.settings.config.i18n.domains).length > 0;
    const { config } = options.settings;
    const builtPaths = /* @__PURE__ */ new Map();
    const filteredPaths = [];
    const fallbackPaths = [];
    for (const pathWithRoute of pathsWithRoutes) {
      const { pathname, route } = pathWithRoute;
      if (hasI18nDomains && route.prerender) {
        throw new AstroError({
          ...AstroErrorData.NoPrerenderedRoutesWithDomains,
          message: AstroErrorData.NoPrerenderedRoutesWithDomains.message(route.component)
        });
      }
      const normalized = removeTrailingForwardSlash(pathname);
      if (!builtPaths.has(normalized)) {
        builtPaths.set(normalized, route);
      } else {
        const winningRoute = builtPaths.get(normalized);
        if (config.prerenderConflictBehavior === "error") {
          throw new AstroError({
            ...AstroErrorData.PrerenderRouteConflict,
            message: AstroErrorData.PrerenderRouteConflict.message(
              winningRoute.route,
              route.route,
              normalized
            ),
            hint: AstroErrorData.PrerenderRouteConflict.hint(winningRoute.route, route.route)
          });
        } else if (config.prerenderConflictBehavior === "warn") {
          const msg = AstroErrorData.PrerenderRouteConflict.message(
            winningRoute.route,
            route.route,
            normalized
          );
          logger.warn("build", msg);
        }
        continue;
      }
      const paths = route.type === "fallback" ? fallbackPaths : filteredPaths;
      paths.push(pathWithRoute);
    }
    const generationPhases = [filteredPaths, fallbackPaths];
    if (config.build.concurrency > 1) {
      const limit = PLimit(config.build.concurrency);
      const BATCH_SIZE = 1e5;
      for (const paths of generationPhases) {
        for (let i = 0; i < paths.length; i += BATCH_SIZE) {
          const promises = paths.slice(i, i + BATCH_SIZE).map(
            ({ pathname, route, cacheKey }) => limit(
              () => generatePathWithPrerenderer(
                prerenderer,
                pathname,
                route,
                options,
                internals,
                routeToHeaders,
                logger,
                cache,
                cacheKey
              )
            )
          );
          await Promise.all(promises);
        }
      }
    } else {
      for (const paths of generationPhases) {
        for (const { pathname, route, cacheKey } of paths) {
          await generatePathWithPrerenderer(
            prerenderer,
            pathname,
            route,
            options,
            internals,
            routeToHeaders,
            logger,
            cache,
            cacheKey
          );
        }
      }
    }
    for (const paths of generationPhases) {
      for (const { route: generatedRoute } of paths) {
        if (generatedRoute.distURL && generatedRoute.distURL.length > 0) {
          for (const pageData of Object.values(options.allPages)) {
            if (pageData.route.route === generatedRoute.route && pageData.route.component === generatedRoute.component) {
              pageData.route.distURL = generatedRoute.distURL;
              break;
            }
          }
        }
      }
    }
    if (cache) {
      const orphans = cache.findOrphanedFiles();
      let pruned = 0;
      for (const orphanFile of orphans) {
        try {
          await cache.deleteOutputFile(options.settings, orphanFile);
          pruned++;
        } catch (err) {
          logger.warn(
            "build",
            `Could not prune stale incremental cache file ${orphanFile}: ${err}`
          );
        }
      }
      if (pruned > 0) {
        logger.info("build", `Pruned ${pruned} stale file(s) from the incremental cache.`);
      }
      cache.writeManifest(options.settings);
    }
    staticImageList = getStaticImageList();
    if (prerenderer.collectStaticImages) {
      const adapterImages = await prerenderer.collectStaticImages();
      for (const [path, entry] of adapterImages) {
        const existing = staticImageList.get(path);
        if (existing) {
          for (const [hash, transform] of entry.transforms) {
            if (!existing.transforms.has(hash)) {
              existing.transforms.set(hash, transform);
            }
          }
        } else {
          staticImageList.set(path, entry);
        }
      }
    }
  } finally {
    await prerenderer.teardown?.();
  }
  logger.info(
    null,
    colors.green(`\u2713 Completed in ${getTimeStat(generatePagesTimer, performance.now())}.
`)
  );
  if (staticImageList.size) {
    logger.info("SKIP_FORMAT", `${colors.bgGreen(colors.black(` generating optimized images `))}`);
    const totalCount = Array.from(staticImageList.values()).map((x) => x.transforms.size).reduce((a, b) => a + b, 0);
    const cpuCount = os.availableParallelism();
    const assetsCreationPipeline = await prepareAssetsGenerationEnv(options, totalCount);
    const queue = new PQueue({ concurrency: Math.max(cpuCount, 1) });
    const errors = [];
    const assetsTimer = performance.now();
    for (const [originalPath, transforms] of staticImageList) {
      queue.add(() => generateImagesForPath(originalPath, transforms, assetsCreationPipeline)).catch((e) => {
        logger.warn("build", `Unable to generate optimized image for ${originalPath}: ${e}`);
        errors.push(new Error(`Error generating image for ${originalPath}: ${e}`, { cause: e }));
      });
    }
    await queue.onIdle();
    if (errors.length === 1) {
      throw errors[0];
    } else if (errors.length > 1) {
      throw new AggregateError(errors, `${errors.length} errors occurred during asset generation`);
    }
    const assetsTimeEnd = performance.now();
    logger.info(null, colors.green(`\u2713 Completed in ${getTimeStat(assetsTimer, assetsTimeEnd)}.
`));
    delete globalThis?.astroAsset?.addStaticImage;
  }
  await runHookBuildGenerated({
    settings: options.settings,
    logger,
    routeToHeaders
  });
}
const THRESHOLD_SLOW_RENDER_TIME_MS = 500;
function normalizePrerenderResult(result) {
  return result instanceof Response ? { response: result } : result;
}
async function renderPath({
  prerenderer,
  pathname,
  route,
  options,
  routeToHeaders = /* @__PURE__ */ new Map(),
  logger,
  collectMetadata
}) {
  const { config } = options.settings;
  if (route.type === "fallback" && route.pathname !== "/") {
    if (options.routesList.routes.some((routeData) => {
      if (routeData.pattern.test(pathname)) {
        if (routeData.params && routeData.params.length !== 0) {
          if (routeData.distURL && !routeData.distURL.find(
            (url2) => url2.href.replace(config.outDir.toString(), "").replace(/(?:\/index\.html|\.html)$/, "") === trimSlashes(pathname)
          )) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    })) {
      return null;
    }
  }
  const url = getUrlForPath(
    pathname,
    config.base,
    options.origin,
    config.build.format,
    config.trailingSlash,
    route.type,
    route.isIndex
  );
  const request = createRequest({
    url,
    headers: new Headers(),
    logger,
    isPrerendered: true,
    routePattern: route.component
  });
  let response;
  let metadata;
  try {
    const rendered = normalizePrerenderResult(
      await prerenderer.render(request, { routeData: route, collectMetadata })
    );
    response = rendered.response;
    metadata = rendered.metadata;
  } catch (err) {
    logger.error("build", `Caught error rendering ${pathname}: ${err}`);
    if (err && !AstroError.is(err) && !err.id && typeof err === "object") {
      err.id = route.component;
    }
    throw err;
  }
  let body;
  const responseHeaders = response.headers;
  if (response.status >= 300 && response.status < 400) {
    if (routeIsRedirect(route) && !config.build.redirects) {
      return null;
    }
    const locationSite = getRedirectLocationOrThrow(responseHeaders);
    const siteURL = config.site;
    const location = siteURL ? new URL(locationSite, siteURL) : locationSite;
    const fromPath = new URL(request.url).pathname;
    body = redirectTemplate({
      status: response.status,
      absoluteLocation: location,
      relativeLocation: locationSite,
      from: fromPath
    });
    if (config.compressHTML) {
      body = body.replaceAll("\n", "");
    }
    if (route.type !== "redirect") {
      route.redirect = location.toString();
    }
  } else {
    if (!response.body) {
      return null;
    }
    body = Buffer.from(await response.arrayBuffer());
  }
  const encodedPath = encodeURI(pathname);
  const outFolder = getOutFolder(options.settings, encodedPath, route);
  const outFile = getOutFile(config.build.format, outFolder, encodedPath, route);
  if (route.distURL) {
    route.distURL.push(outFile);
  } else {
    route.distURL = [outFile];
  }
  const integrationRoute = toIntegrationResolvedRoute(route, config.trailingSlash);
  if (options.settings.adapter?.adapterFeatures?.staticHeaders) {
    routeToHeaders.set(pathname, { headers: responseHeaders, route: integrationRoute });
  }
  if (checkPublicConflict(outFile, route, options.settings, logger)) return null;
  return { body, outFile, outFolder, metadata };
}
async function generatePathWithPrerenderer(prerenderer, pathname, route, options, internals, routeToHeaders, logger, cache, cacheKey) {
  const timeStart = performance.now();
  const { config } = options.settings;
  const filePath = getOutputFilename(config.build.format, pathname, route);
  const encodedPath = encodeURI(pathname);
  const outFolder = getOutFolder(options.settings, encodedPath, route);
  const outFile = getOutFile(config.build.format, outFolder, encodedPath, route);
  const relativeOutFile = outFile.href.slice(config.outDir.href.length);
  const dependencyHash = internals.pageDependencyHashes?.get(route.component) ?? "";
  const hasServerIsland = internals.serverIslandPageComponents?.has(route.component) ?? false;
  if (cacheKey !== void 0 && cache?.canSkip(route.component, pathname, dependencyHash, cacheKey, hasServerIsland)) {
    const existsInDist = nodeFs.existsSync(outFile);
    const restored = !existsInDist && await cache.restoreOutputFile(options.settings, relativeOutFile, outFile);
    if (existsInDist || restored) {
      const restoredImages = cache.previousStaticImages(route.component, pathname);
      if (restoredImages) restoreStaticImages(restoredImages);
      const restoredReferencedImages = cache.previousReferencedImages(route.component, pathname);
      if (restoredReferencedImages) restoreReferencedImages(restoredReferencedImages);
      const restoredHeaders = cache.previousHeaders(route.component, pathname);
      if (restoredHeaders && options.settings.adapter?.adapterFeatures?.staticHeaders) {
        routeToHeaders.set(pathname, {
          headers: new Headers(restoredHeaders),
          route: toIntegrationResolvedRoute(route, config.trailingSlash)
        });
      }
      cache.record(
        route.component,
        dependencyHash,
        pathname,
        cacheKey,
        relativeOutFile,
        cache.previousContentEntryKeys(route.component, pathname),
        restoredImages,
        restoredReferencedImages,
        restoredHeaders
      );
      if (route.type === "page") {
        addPageName(pathname, options);
      }
      if (route.distURL) {
        route.distURL.push(outFile);
      } else {
        route.distURL = [outFile];
      }
      logger.info(null, `  ${colors.green("\u251C\u2500")} ${colors.dim(filePath)}`, false);
      logger.info(
        "SKIP_FORMAT",
        restored ? ` ${colors.green("(restored)")}` : ` ${colors.green("(cached)")}`
      );
      return;
    }
  }
  logger.info(null, `  ${colors.blue("\u251C\u2500")} ${colors.dim(filePath)}`, false);
  if (route.type === "page") {
    addPageName(pathname, options);
  }
  const result = await renderPath({
    prerenderer,
    pathname,
    route,
    options,
    routeToHeaders,
    logger,
    collectMetadata: cache !== null
  });
  const contentEntryKeys = result?.metadata?.contentEntryKeys;
  const staticImages = result?.metadata?.staticImages;
  const referencedImages = result?.metadata?.referencedImages;
  const headers = cache ? [...routeToHeaders.get(pathname)?.headers ?? []] : void 0;
  if (!result) {
    logRenderTime(logger, timeStart, true);
    return;
  }
  await nodeFs.promises.mkdir(result.outFolder, { recursive: true });
  await nodeFs.promises.writeFile(result.outFile, result.body);
  if (cache && cacheKey !== void 0 && result.metadata !== void 0) {
    await cache.writeOutputFile(options.settings, relativeOutFile, result.body);
    cache.record(
      route.component,
      dependencyHash,
      pathname,
      cacheKey,
      relativeOutFile,
      contentEntryKeys,
      staticImages,
      referencedImages,
      headers
    );
  }
  logRenderTime(logger, timeStart, false);
}
function logRenderTime(logger, timeStart, notCreated) {
  const timeEnd = performance.now();
  const isSlow = timeEnd - timeStart > THRESHOLD_SLOW_RENDER_TIME_MS;
  const timeIncrease = (isSlow ? colors.red : colors.dim)(`(+${getTimeStat(timeStart, timeEnd)})`);
  const notCreatedMsg = notCreated ? colors.yellow("(file not created, response body was empty)") : "";
  logger.info("SKIP_FORMAT", ` ${timeIncrease} ${notCreatedMsg}`);
}
function addPageName(pathname, opts) {
  const trailingSlash = opts.settings.config.trailingSlash;
  const buildFormat = opts.settings.config.build.format;
  const pageName = shouldAppendForwardSlash(trailingSlash, buildFormat) ? pathname.replace(/\/?$/, "/").replace(/^\//, "") : pathname.replace(/^\//, "");
  opts.pageNames.push(pageName);
}
function getUrlForPath(pathname, base, origin, format, trailingSlash, routeType, isIndex) {
  let ending;
  const effectiveFormat = format === "preserve" ? isIndex ? "directory" : "file" : format;
  switch (effectiveFormat) {
    case "directory": {
      ending = trailingSlash === "never" ? "" : "/";
      break;
    }
    case "file":
    default: {
      ending = ".html";
      break;
    }
  }
  let buildPathname;
  if (pathname === "/" || pathname === "") {
    if (effectiveFormat === "file") {
      buildPathname = joinPaths(base, "index.html");
    } else {
      buildPathname = collapseDuplicateTrailingSlashes(base + ending, trailingSlash !== "never");
    }
  } else if (routeType === "endpoint") {
    const buildPathRelative = removeLeadingForwardSlash(pathname);
    let endpointPathname = joinPaths(base, buildPathRelative);
    if (trailingSlash === "always" && !hasFileExtension(pathname)) {
      endpointPathname = appendForwardSlash(endpointPathname);
    } else if (trailingSlash === "never") {
      endpointPathname = removeTrailingForwardSlash(endpointPathname);
    }
    buildPathname = endpointPathname;
  } else {
    const buildPathRelative = removeTrailingForwardSlash(removeLeadingForwardSlash(pathname)) + ending;
    buildPathname = joinPaths(base, buildPathRelative);
  }
  return new URL(buildPathname, origin);
}
function checkPublicConflict(outFile, route, settings, logger) {
  const outRoot = settings.buildOutput === "static" && !settings.adapter?.adapterFeatures?.preserveBuildClientDir ? settings.config.outDir : settings.config.build.client;
  const relativePath = outFile.href.slice(outRoot.href.length);
  const publicFileUrl = new URL(relativePath, settings.config.publicDir);
  if (nodeFs.existsSync(publicFileUrl)) {
    logger.warn(
      "build",
      `Skipping ${route.component} because a file with the same name exists in the public folder: ${relativePath}`
    );
    return true;
  }
  return false;
}
export {
  generatePages,
  renderPath
};
