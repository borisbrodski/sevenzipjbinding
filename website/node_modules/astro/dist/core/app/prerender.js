import { collectPrerenderMetadata } from "../render-scope/collect.js";
const NULL_BODY_STATUSES = [101, 204, 205, 304];
async function renderForPrerender(app, request, options) {
  const routeData = options?.routeData;
  if (!options?.collectMetadata) {
    return { response: await app.render(request, { routeData }) };
  }
  const { value: response, metadata } = await collectPrerenderMetadata(async () => {
    const rendered = await app.render(request, { routeData });
    const bytes = rendered.body === null ? null : await rendered.arrayBuffer();
    const nullBody = bytes === null || NULL_BODY_STATUSES.includes(rendered.status);
    return new Response(nullBody ? null : bytes, {
      status: rendered.status,
      statusText: rendered.statusText,
      headers: rendered.headers
    });
  }, app.logger);
  return { response, metadata };
}
export {
  renderForPrerender
};
