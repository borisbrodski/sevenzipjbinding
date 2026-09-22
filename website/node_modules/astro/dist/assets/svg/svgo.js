import { optimize } from "svgo";
function svgoOptimizer(config) {
  return {
    name: "svgo",
    optimize: (contents, path) => optimize(contents, { ...config, path }).data
  };
}
export {
  svgoOptimizer
};
