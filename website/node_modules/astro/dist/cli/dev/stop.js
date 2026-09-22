import {
  devServerCommand,
  formatStopOutput,
  stop as stopServer
} from "../server.js";
async function stop({
  flags,
  logger
}) {
  await stopServer({ flags, logger, config: devServerCommand });
}
export {
  formatStopOutput,
  stop
};
