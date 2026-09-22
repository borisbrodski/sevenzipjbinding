import {
  devServerCommand,
  formatStatusOutput,
  status as serverStatus
} from "../server.js";
async function status({
  flags,
  logger
}) {
  await serverStatus({ flags, logger, config: devServerCommand });
}
export {
  formatStatusOutput,
  status
};
