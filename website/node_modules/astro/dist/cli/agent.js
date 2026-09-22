import { detectAgenticEnvironment } from "am-i-vibing";
function isRunByAgent() {
  try {
    return detectAgenticEnvironment().type === "agent";
  } catch {
    return false;
  }
}
export {
  isRunByAgent
};
