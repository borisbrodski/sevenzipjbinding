import { devServerCommand, logs as serverLogs } from "../server.js";
async function logs({
  flags,
  logger
}) {
  await serverLogs({ flags, logger, config: devServerCommand });
}
export {
  logs
};
