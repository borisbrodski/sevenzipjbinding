import { getAmbientManifest } from "../manifest/ambient.js";
import { getRenderOptions } from "../app/render-options.js";
import { handleRequest } from "../routing/handler.js";
import { FetchState } from "./fetch-state.js";
class DefaultFetchHandler {
  #manifest;
  /**
   * `BaseApp` passes itself so states resolve that app's manifest ahead of
   * the ambient one; generated builds construct the handler with no
   * arguments and use the ambient manifest.
   */
  constructor(app) {
    this.#manifest = app?.manifest;
  }
  fetch = (request) => {
    const options = getRenderOptions(request);
    const manifest = this.#manifest ?? getAmbientManifest();
    return handleRequest(new FetchState(manifest, request, options));
  };
}
export {
  DefaultFetchHandler
};
