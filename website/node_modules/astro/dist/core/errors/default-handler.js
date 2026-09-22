import { removeTrailingForwardSlash } from "@astrojs/internal-helpers/path";
import { getEnvironment } from "../environment/index.js";
import { FetchState } from "../fetch/fetch-state.js";
import { prepareResponse } from "../app/prepare-response.js";
import { attachCookiesToResponse } from "../cookies/index.js";
import { getCookiesFromResponse } from "../cookies/response.js";
import { handleMiddleware } from "../middleware/astro-middleware.js";
import { handlePages } from "../pages/handler.js";
import { matchRoute } from "../routing/match.js";
import { getRouteTable } from "../routing/route-table.js";
import { provideSession } from "../session/provider.js";
import { validateHost } from "../app/validate-headers.js";
import { getErrorRoutePath } from "../../i18n/error-routes.js";
import { getOutputFilename } from "../output-filename.js";
import { rewroteToEmptyErrorResponse } from "./handler.js";
async function renderDefaultError(manifest, request, {
  status,
  response: originalResponse,
  skipMiddleware = false,
  error,
  pathname,
  ...resolvedRenderOptions
}) {
  const resolvedPathname = pathname ?? new FetchState(manifest, request).pathname;
  const routeTable = getRouteTable(manifest);
  const errorRoutePath = getErrorRoutePath(
    resolvedPathname,
    status,
    routeTable.routes,
    manifest.i18n?.locales,
    manifest.trailingSlash === "always"
  );
  const errorRouteData = matchRoute(errorRoutePath, routeTable);
  const url = new URL(request.url);
  if (errorRouteData) {
    if (errorRouteData.prerender) {
      const allowedDomains = manifest.allowedDomains;
      const validatedHost = validateHost(url.host, url.protocol.replace(":", ""), allowedDomains);
      const safeOrigin = validatedHost ? url.origin : `${url.protocol}//localhost`;
      const statusURL = new URL(
        `${removeTrailingForwardSlash(manifest.base)}${getOutputFilename(
          manifest.buildFormat,
          errorRouteData.route,
          errorRouteData
        )}`,
        safeOrigin
      );
      if (statusURL.toString() !== request.url && resolvedRenderOptions.prerenderedErrorPageFetch) {
        try {
          const response2 = await resolvedRenderOptions.prerenderedErrorPageFetch(
            statusURL.toString()
          );
          const override = { status, removeContentEncodingHeaders: true };
          const newResponse = mergeResponses(response2, originalResponse, override);
          prepareResponse(newResponse, resolvedRenderOptions);
          return newResponse;
        } catch {
          const response2 = mergeResponses(new Response(null, { status }), originalResponse);
          prepareResponse(response2, resolvedRenderOptions);
          return response2;
        }
      }
    }
    const mod = await getEnvironment(manifest).getComponentByRoute(manifest, errorRouteData);
    const errorState = new FetchState(manifest, request);
    errorState.skipMiddleware = skipMiddleware;
    errorState.clientAddress = resolvedRenderOptions.clientAddress;
    errorState.routeData = errorRouteData;
    errorState.pathname = resolvedPathname;
    errorState.status = status;
    errorState.componentInstance = mod;
    errorState.locals = resolvedRenderOptions.locals ?? {};
    errorState.initialProps = { error };
    try {
      await provideSession(errorState);
      const response2 = await handleMiddleware(errorState, handlePages);
      if (rewroteToEmptyErrorResponse(skipMiddleware, errorRouteData, errorState.routeData, response2)) {
        return renderDefaultError(manifest, request, {
          ...resolvedRenderOptions,
          status,
          error,
          response: originalResponse,
          skipMiddleware: true,
          pathname: resolvedPathname
        });
      }
      const newResponse = mergeResponses(response2, originalResponse);
      prepareResponse(newResponse, resolvedRenderOptions);
      return newResponse;
    } catch {
      if (skipMiddleware === false) {
        return renderDefaultError(manifest, request, {
          ...resolvedRenderOptions,
          status,
          error,
          response: originalResponse,
          skipMiddleware: true,
          pathname: resolvedPathname
        });
      }
    } finally {
      await errorState.finalizeAll();
    }
  }
  const response = mergeResponses(new Response(null, { status }), originalResponse);
  prepareResponse(response, resolvedRenderOptions);
  return response;
}
function mergeResponses(newResponse, originalResponse, override) {
  let newResponseHeaders = newResponse.headers;
  if (override?.removeContentEncodingHeaders) {
    newResponseHeaders = new Headers(newResponseHeaders);
    newResponseHeaders.delete("Content-Encoding");
    newResponseHeaders.delete("Content-Length");
  }
  if (!originalResponse) {
    if (override !== void 0) {
      return new Response(newResponse.body, {
        status: override.status,
        statusText: newResponse.statusText,
        headers: newResponseHeaders
      });
    }
    return newResponse;
  }
  const status = override?.status ? override.status : originalResponse.status === 200 ? newResponse.status : originalResponse.status;
  try {
    originalResponse.headers.delete("Content-type");
    originalResponse.headers.delete("Content-Length");
    originalResponse.headers.delete("Transfer-Encoding");
  } catch {
  }
  const newHeaders = new Headers();
  const seen = /* @__PURE__ */ new Set();
  for (const [name, value] of originalResponse.headers) {
    newHeaders.append(name, value);
    seen.add(name.toLowerCase());
  }
  for (const [name, value] of newResponseHeaders) {
    const lower = name.toLowerCase();
    if (!seen.has(lower) || lower === "set-cookie") {
      newHeaders.append(name, value);
    }
  }
  const mergedResponse = new Response(newResponse.body, {
    status,
    statusText: status === 200 ? newResponse.statusText : originalResponse.statusText,
    // If you're looking at here for possible bugs, it means that it's not a bug.
    // With the middleware, users can meddle with headers, and we should pass to the 404/500.
    // If users see something weird, it's because they are setting some headers they should not.
    //
    // Although, we don't want it to replace the content-type, because the error page must return `text/html`
    headers: newHeaders
  });
  const originalCookies = getCookiesFromResponse(originalResponse);
  const newCookies = getCookiesFromResponse(newResponse);
  if (originalCookies) {
    if (newCookies) {
      originalCookies.merge(newCookies);
    }
    attachCookiesToResponse(mergedResponse, originalCookies);
  } else if (newCookies) {
    attachCookiesToResponse(mergedResponse, newCookies);
  }
  return mergedResponse;
}
export {
  renderDefaultError
};
