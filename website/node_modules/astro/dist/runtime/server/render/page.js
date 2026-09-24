import { isRoute404, isRoute500 } from "../../../core/routing/internal/route-errors.js";
import { isPropagatingHint } from "../../../core/head-propagation/resolver.js";
import { renderToAsyncIterable, renderToReadableStream, renderToString } from "./astro/render.js";
import { encoder } from "./common.js";
import { renderComponentToString } from "./component.js";
import { renderCspContent } from "./csp.js";
import { isDeno, isNode, isWorkerd } from "./util.js";
import { isAstroComponentFactory } from "./astro/factory.js";
async function renderPage(result, componentFactory, props, children, streaming, route) {
  if (!isAstroComponentFactory(componentFactory)) {
    const nonAstroMeta = result.componentMetadata.get(componentFactory.moduleId);
    result._metadata.headInTree = nonAstroMeta?.containsHead ?? false;
    result._metadata.routeHasPropagation = isPropagatingHint(nonAstroMeta?.propagation ?? "none");
    const pageProps = { ...props ?? {}, "server:root": true };
    const str = await renderComponentToString(
      result,
      componentFactory.name,
      componentFactory,
      pageProps,
      {},
      true,
      route
    );
    const bytes = encoder.encode(str);
    const headers2 = new Headers([
      ["Content-Type", "text/html"],
      ["Content-Length", bytes.byteLength.toString()]
    ]);
    if (result.shouldInjectCspMetaTags && (result.cspDestination === "header" || result.cspDestination === "adapter")) {
      headers2.set("content-security-policy", renderCspContent(result));
    }
    return new Response(bytes, {
      headers: headers2,
      status: result.response.status
    });
  }
  const pageMeta = result.componentMetadata.get(componentFactory.moduleId);
  result._metadata.headInTree = pageMeta?.containsHead ?? false;
  result._metadata.routeHasPropagation = isPropagatingHint(pageMeta?.propagation ?? "none");
  let body;
  if (streaming) {
    if (isNode && !isDeno) {
      const nodeBody = await renderToAsyncIterable(
        result,
        componentFactory,
        props,
        children,
        true,
        route
      );
      if (isWorkerd && !(nodeBody instanceof Response)) {
        body = ReadableStream.from(nodeBody);
      } else {
        body = nodeBody;
      }
    } else {
      body = await renderToReadableStream(result, componentFactory, props, children, true, route);
    }
  } else {
    body = await renderToString(result, componentFactory, props, children, true, route);
  }
  if (body instanceof Response) return body;
  const init = result.response;
  const headers = new Headers(init.headers);
  if (result.shouldInjectCspMetaTags && result.cspDestination === "header" || result.cspDestination === "adapter") {
    headers.set("content-security-policy", renderCspContent(result));
  }
  if (!streaming && typeof body === "string") {
    body = encoder.encode(body);
    headers.set("Content-Length", body.byteLength.toString());
  }
  let status = init.status;
  let statusText = init.statusText;
  if (route?.route && isRoute404(route.route)) {
    status = 404;
    if (statusText === "OK") {
      statusText = "Not Found";
    }
  } else if (route?.route && isRoute500(route.route)) {
    status = 500;
    if (statusText === "OK") {
      statusText = "Internal Server Error";
    }
  }
  if (status) {
    return new Response(body, { ...init, headers, status, statusText });
  } else {
    return new Response(body, { ...init, headers });
  }
}
export {
  renderPage
};
