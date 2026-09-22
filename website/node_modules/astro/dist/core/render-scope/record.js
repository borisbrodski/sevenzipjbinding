import { getInstalledRenderScope } from "./scope.js";
function recordContentEntryRender(filePath) {
  if (!filePath) return;
  getInstalledRenderScope()?.getStore()?.contentEntries?.add(filePath);
}
function recordStaticImage(image) {
  getInstalledRenderScope()?.getStore()?.staticImages?.push(image);
}
function recordReferencedImage(fsPath) {
  getInstalledRenderScope()?.getStore()?.referencedImages?.add(fsPath);
}
export {
  recordContentEntryRender,
  recordReferencedImage,
  recordStaticImage
};
