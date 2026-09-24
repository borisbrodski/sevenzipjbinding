import { manifest } from "virtual:astro:manifest";
import { createBuildEnvironment } from "../core/build/environment.js";
import { BuildApp } from "../core/build/app.js";
import { setEnvironment } from "../core/environment/index.js";
const buildEnv = createBuildEnvironment();
setEnvironment(manifest, buildEnv.env);
const app = new BuildApp(manifest, buildEnv);
export {
  app,
  manifest
};
