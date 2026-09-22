import { renderDefaultError } from "./default-handler.js";
async function renderBuildError(manifest, request, options) {
  if (options.status === 500) {
    if (options.response) {
      return options.response;
    }
    throw options.error;
  }
  return renderDefaultError(manifest, request, {
    ...options,
    prerenderedErrorPageFetch: void 0
  });
}
export {
  renderBuildError
};
