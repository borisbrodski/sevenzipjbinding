import { createRequire } from "node:module";
import { pathToFileURL } from "node:url";
async function getCloudflareCompatibilityDate(root) {
  const require2 = createRequire(root);
  const infoPath = require2.resolve("@astrojs/cloudflare/info");
  const infoUrl = pathToFileURL(infoPath).toString();
  const infoModule = await import(infoUrl);
  return infoModule.getLocalWorkerdCompatibilityDate().date;
}
export {
  getCloudflareCompatibilityDate
};
