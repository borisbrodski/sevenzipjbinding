import {
  background as startBackgroundServer,
  devServerCommand,
  formatBackgroundOutput,
  formatServerRunningMessage as formatServerMessage
} from "../server.js";
function formatServerRunningMessage(data, { existing = false } = {}) {
  return formatServerMessage(data, devServerCommand, { existing });
}
async function background({
  flags,
  logger
}) {
  await startBackgroundServer({ flags, logger, config: devServerCommand });
}
export {
  background,
  formatBackgroundOutput,
  formatServerRunningMessage
};
