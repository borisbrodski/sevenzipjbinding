import { removeTrailingForwardSlash } from "@astrojs/internal-helpers/path";
import { shouldAppendForwardSlash } from "../core/build/util.js";
import { clientLocalsSymbol } from "../core/constants.js";
import { getEnvironment, setEnvironment } from "../core/environment/index.js";
import { createSafeError } from "../core/errors/index.js";
import { setLogger } from "../core/logger/manifest-logger.js";
import { createRequest } from "../core/request.js";
import { validateAndDecodePathname } from "../core/util/pathname.js";
import { SERIALIZED_MANIFEST_ID } from "../manifest/serialized.js";
import { recordServerError } from "../vite-plugin-astro-server/error.js";
import { runWithErrorHandling } from "../vite-plugin-astro-server/index.js";
import { handle500Response, writeSSRResult } from "../vite-plugin-astro-server/response.js";
const runnerManifests = /* @__PURE__ */ new WeakMap();
async function loadFetchHandler(app, loader) {
  try {
    const { manifest } = await loader.import(SERIALIZED_MANIFEST_ID);
    if (manifest && manifest !== runnerManifests.get(loader) && manifest !== app.manifest) {
      setEnvironment(manifest, getEnvironment(app.manifest));
      setLogger(manifest, app.logger);
      runnerManifests.set(loader, manifest);
    }
  } catch {
  }
  try {
    const mod = await loader.import("virtual:astro:fetchable");
    if (mod?.default && !mod.isDefaultFetchHandler) {
      app.setFetchHandler(mod.default);
    }
  } catch {
  }
}
async function handleDevRequest(app, deps, { incomingRequest, incomingResponse, isHttps, prerenderOnly }) {
  const { loader, settings, controller } = deps;
  const manifest = app.manifest;
  const protocol = isHttps ? "https" : "http";
  const host = incomingRequest.headers[":authority"] ?? incomingRequest.headers.host;
  const origin = `${protocol}://${host}`;
  const url = new URL(origin + incomingRequest.url);
  let pathname;
  if (manifest.trailingSlash === "never" && !incomingRequest.url) {
    pathname = "";
  } else {
    try {
      pathname = validateAndDecodePathname(url.pathname);
    } catch {
      pathname = decodeURI(url.pathname);
    }
  }
  url.pathname = removeTrailingForwardSlash(manifest.base) + url.pathname;
  if (url.pathname.endsWith("/") && !shouldAppendForwardSlash(manifest.trailingSlash, manifest.buildFormat)) {
    url.pathname = url.pathname.slice(0, -1);
  }
  await loadFetchHandler(app, loader);
  let handled = true;
  await runWithErrorHandling({
    controller,
    pathname,
    async run() {
      const matchedRoute = await app.devMatch(pathname, { prerenderOnly });
      if (!matchedRoute) {
        if (prerenderOnly) {
          handled = false;
          return;
        }
        throw new Error("No route matched, and default 404 route was not found.");
      }
      if (prerenderOnly && !matchedRoute.routeData.prerender) {
        handled = false;
        return;
      }
      let body = void 0;
      if (!(incomingRequest.method === "GET" || incomingRequest.method === "HEAD")) {
        let bytes = [];
        await new Promise((resolve, reject) => {
          incomingRequest.on("data", (part) => {
            bytes.push(part);
          });
          incomingRequest.on("end", resolve);
          incomingRequest.on("error", reject);
        });
        body = Buffer.concat(bytes);
      }
      const abortController = new AbortController();
      const socket = incomingRequest.socket;
      const onSocketClose = () => {
        if (!abortController.signal.aborted) {
          abortController.abort();
        }
      };
      if (socket.destroyed) {
        onSocketClose();
      } else {
        socket.on("close", onSocketClose);
      }
      try {
        const request = createRequest({
          url,
          headers: incomingRequest.headers,
          method: incomingRequest.method,
          body,
          logger: app.logger,
          isPrerendered: matchedRoute.routeData.prerender,
          routePattern: matchedRoute.routeData.component,
          init: { signal: abortController.signal }
        });
        const locals = Reflect.get(incomingRequest, clientLocalsSymbol);
        for (const [name, value] of Object.entries(settings.config.server.headers ?? {})) {
          if (value) incomingResponse.setHeader(name, value);
        }
        const clientAddress = incomingRequest.socket.remoteAddress;
        const response = await app.render(request, {
          locals,
          routeData: matchedRoute.routeData,
          clientAddress
        });
        await writeSSRResult(request, response, incomingResponse);
      } finally {
        socket.off("close", onSocketClose);
      }
    },
    onError(_err) {
      const error = createSafeError(_err);
      if (loader) {
        const { errorWithMetadata } = recordServerError(loader, manifest, app.logger, error);
        handle500Response(loader, incomingResponse, errorWithMetadata);
      }
      return error;
    }
  });
  return handled;
}
export {
  handleDevRequest
};
