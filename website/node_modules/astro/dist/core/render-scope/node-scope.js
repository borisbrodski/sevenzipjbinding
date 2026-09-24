import { AsyncLocalStorage } from "node:async_hooks";
import { installRenderScope } from "./scope.js";
function ensureAsyncRenderScope() {
  return installRenderScope(new AsyncLocalStorage());
}
export {
  ensureAsyncRenderScope
};
