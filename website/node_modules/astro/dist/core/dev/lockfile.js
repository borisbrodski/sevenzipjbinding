import { existsSync, readFileSync, unlinkSync, writeFileSync, mkdirSync } from "node:fs";
import { fileURLToPath } from "node:url";
import findProcess from "find-proc";
const GRACEFUL_SHUTDOWN_TIMEOUT = 5e3;
function getLockFileURL(root, command = "dev") {
  return new URL(`.astro/${command}.json`, root);
}
function getLogFileURL(root, command = "dev") {
  return new URL(`.astro/${command}.log`, root);
}
function isStringArray(value) {
  return Array.isArray(value) && value.every((item) => typeof item === "string");
}
function isResolvedServerUrls(value) {
  if (typeof value !== "object" || value === null) {
    return false;
  }
  const { local, network } = value;
  return isStringArray(local) && isStringArray(network);
}
function parseLockFile(content) {
  try {
    const data = JSON.parse(content);
    if (typeof data.pid !== "number" || typeof data.port !== "number" || typeof data.url !== "string" || typeof data.background !== "boolean" || typeof data.startedAt !== "string") {
      return null;
    }
    if (data.urls !== void 0 && !isResolvedServerUrls(data.urls)) {
      return null;
    }
    return data;
  } catch {
    return null;
  }
}
function serializeLockFile(data) {
  return JSON.stringify(data, null, "	");
}
function isProcessAlive(pid) {
  try {
    process.kill(pid, 0);
    return true;
  } catch {
    return false;
  }
}
const ASTRO_COMMAND_PATTERN = /(?:^|[\\/\s"'])(?:astro[\\/]bin[\\/]astro\.mjs|\.bin[\\/]astro(?:\.cmd)?)(?=$|[\s"'])/i;
function isAstroCommand(command) {
  return ASTRO_COMMAND_PATTERN.test(command);
}
async function isLockFileProcessAlive(data, find = findProcess) {
  if (data.pid === process.pid) {
    return false;
  }
  if (!isProcessAlive(data.pid)) {
    return false;
  }
  try {
    const processInfo = (await find("pid", data.pid, { logLevel: "error" })).find(
      ({ pid }) => pid === data.pid
    );
    return processInfo?.cmd === void 0 || isAstroCommand(processInfo.cmd);
  } catch {
    return true;
  }
}
function readLockFile(root, command = "dev") {
  const lockFileURL = getLockFileURL(root, command);
  try {
    const content = readFileSync(lockFileURL, "utf-8");
    return parseLockFile(content);
  } catch {
    return null;
  }
}
function writeLockFile(root, data, command = "dev") {
  const lockFileURL = getLockFileURL(root, command);
  const dirPath = fileURLToPath(new URL(".astro/", root));
  try {
    if (!existsSync(dirPath)) {
      mkdirSync(dirPath, { recursive: true });
    }
    writeFileSync(lockFileURL, serializeLockFile(data), "utf-8");
  } catch (err) {
    const message = err instanceof Error ? err.message : String(err);
    throw new Error(`Failed to write lock file: ${message}`);
  }
}
function removeLockFile(root, command = "dev") {
  const lockFileURL = getLockFileURL(root, command);
  try {
    unlinkSync(lockFileURL);
  } catch (err) {
    if (err?.code !== "ENOENT") {
      throw err;
    }
  }
}
function evaluateExistingServer(data, alive) {
  if (data === null) {
    return null;
  }
  return { data, stale: !alive };
}
async function killDevServer(root, data) {
  try {
    process.kill(data.pid, "SIGTERM");
  } catch {
  }
  const deadline = Date.now() + GRACEFUL_SHUTDOWN_TIMEOUT;
  while (Date.now() < deadline) {
    if (!isProcessAlive(data.pid)) break;
    await new Promise((r) => setTimeout(r, 100));
  }
  if (isProcessAlive(data.pid)) {
    try {
      process.kill(data.pid, "SIGKILL");
    } catch {
    }
  }
  removeLockFile(root);
}
async function checkExistingServer(root, command = "dev") {
  const data = readLockFile(root, command);
  const result = evaluateExistingServer(
    data,
    data !== null && await isLockFileProcessAlive(data)
  );
  if (result === null) {
    return null;
  }
  if (result.stale) {
    removeLockFile(root, command);
    return null;
  }
  return result.data;
}
export {
  GRACEFUL_SHUTDOWN_TIMEOUT,
  checkExistingServer,
  evaluateExistingServer,
  getLogFileURL,
  isAstroCommand,
  isLockFileProcessAlive,
  isProcessAlive,
  killDevServer,
  parseLockFile,
  readLockFile,
  removeLockFile,
  serializeLockFile,
  writeLockFile
};
