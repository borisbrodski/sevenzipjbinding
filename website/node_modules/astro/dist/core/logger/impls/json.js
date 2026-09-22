import {
  AstroLogger,
  levels
} from "../core.js";
import { matchesLevel } from "../public.js";
const SGR_REGEX = new RegExp(`${String.fromCharCode(27)}\\[[0-9;]*m`, "g");
function jsonLoggerDestination(config = {}) {
  const { pretty = false, level = "info" } = config;
  return {
    write(event) {
      if (!matchesLevel(event.level, level)) {
        return;
      }
      const dest = levels[event.level] >= levels["error"] ? console.error : console.info;
      const message = event.message.replace(SGR_REGEX, "");
      const payload = pretty ? JSON.stringify({ message, label: event.label, level: event.level }, null, 2) : JSON.stringify({ message, label: event.label, level: event.level });
      dest(payload);
    }
  };
}
function createJsonLoggerFromFlags(config) {
  return new AstroLogger({
    destination: jsonLoggerDestination({ pretty: false }),
    level: config.logLevel ?? "info"
  });
}
export {
  SGR_REGEX,
  createJsonLoggerFromFlags,
  jsonLoggerDestination as default
};
