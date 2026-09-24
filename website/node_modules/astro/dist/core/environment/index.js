import { productionEnvironment } from "./production.js";
const environments = /* @__PURE__ */ new WeakMap();
function setEnvironment(manifest, env) {
  environments.set(manifest, env);
}
function getEnvironment(manifest) {
  return environments.get(manifest) ?? productionEnvironment;
}
export {
  getEnvironment,
  setEnvironment
};
