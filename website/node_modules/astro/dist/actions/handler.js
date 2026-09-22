import {
  createCrossOriginForbiddenResponse,
  isForbiddenCrossOriginRequest
} from "../core/app/origin-check.js";
import { markFeatureUsed, FetchFeatures } from "../core/fetch/features.js";
import { getActionContext, serializeActionResult } from "./runtime/server.js";
function handleAction(apiContext, state) {
  markFeatureUsed(state.manifest, FetchFeatures.actions);
  if (apiContext.isPrerendered) {
    return void 0;
  }
  const { action, setActionResult } = getActionContext(apiContext);
  if (!action) {
    return void 0;
  }
  if (state.manifest.checkOrigin && isForbiddenCrossOriginRequest(apiContext.request, apiContext.url, apiContext.isPrerendered)) {
    return Promise.resolve(createCrossOriginForbiddenResponse(apiContext.request));
  }
  return executeAction(action, setActionResult);
}
async function executeAction(action, setActionResult) {
  const actionResult = await action.handler();
  const serialized = serializeActionResult(actionResult);
  if (action.calledFrom === "rpc") {
    if (serialized.type === "empty") {
      return new Response(null, {
        status: serialized.status
      });
    }
    return new Response(serialized.body, {
      status: serialized.status,
      headers: {
        "Content-Type": serialized.contentType
      }
    });
  }
  setActionResult(action.name, serialized);
  return void 0;
}
export {
  handleAction
};
