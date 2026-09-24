// src/index.ts
import os from "os";

// src/lib/unix.ts
import { execSync } from "child_process";
function getProcessInfo(pid) {
  try {
    const output = execSync(`ps -p ${pid} -o pid=,ppid=,command=`, {
      encoding: "utf8",
      timeout: 5e3
      // 5 second timeout
    }).trim();
    if (!output) {
      return null;
    }
    const [pidStr, ppidStr, ...commandParts] = output.split(/\s+/);
    const parsedPid = pidStr ? parseInt(pidStr, 10) : NaN;
    const parsedPpid = ppidStr ? parseInt(ppidStr, 10) : NaN;
    if (isNaN(parsedPid) || isNaN(parsedPpid)) {
      return null;
    }
    return {
      pid: parsedPid,
      ppid: parsedPpid,
      command: commandParts.join(" ") || void 0
    };
  } catch (error) {
    if (error instanceof Error && error.message.includes("timeout")) {
      console.warn(`Process lookup timed out for PID ${pid}`);
    }
    return null;
  }
}
function getAncestryUnix(startPid) {
  const result = [];
  const visited = /* @__PURE__ */ new Set();
  let currentPid = startPid;
  let maxDepth = 1e3;
  while (currentPid && maxDepth > 0) {
    if (visited.has(currentPid)) {
      console.warn(`Detected cycle in process tree at PID ${currentPid}`);
      break;
    }
    visited.add(currentPid);
    const info = getProcessInfo(currentPid);
    if (!info || info.ppid === 0 || info.ppid === 1) break;
    result.push(info);
    currentPid = info.ppid;
    maxDepth--;
  }
  if (maxDepth === 0) {
    console.warn(`Reached maximum depth limit while traversing process tree from PID ${startPid}`);
  }
  return result;
}

// src/lib/windows.ts
import { execSync as execSync2 } from "child_process";
function getProcessInfo2(pid) {
  try {
    const output = execSync2(
      `wmic process where (ProcessId=${pid}) get ProcessId,ParentProcessId,CommandLine /format:csv`,
      {
        encoding: "utf8",
        timeout: 1e4
        // 10 second timeout (wmic can be slower)
      }
    );
    if (!output) {
      return null;
    }
    const lines = output.split("\n").filter((line) => line.trim() && !line.startsWith("Node"));
    const fields = lines.pop()?.split(",");
    if (!fields || fields.length < 4) return null;
    const [_node, commandLine, parentPid, thisPid] = fields;
    const parsedPid = thisPid ? parseInt(thisPid.trim(), 10) : NaN;
    const parsedPpid = parentPid ? parseInt(parentPid.trim(), 10) : NaN;
    if (isNaN(parsedPid) || isNaN(parsedPpid)) {
      return null;
    }
    return {
      pid: parsedPid,
      ppid: parsedPpid,
      command: commandLine?.trim() || void 0
    };
  } catch (error) {
    if (error instanceof Error && error.message.includes("timeout")) {
      console.warn(`Process lookup timed out for PID ${pid}`);
    }
    return null;
  }
}
function getAncestryWindows(startPid) {
  const result = [];
  const visited = /* @__PURE__ */ new Set();
  let currentPid = startPid;
  let maxDepth = 1e3;
  while (currentPid && maxDepth > 0) {
    if (visited.has(currentPid)) {
      console.warn(`Detected cycle in process tree at PID ${currentPid}`);
      break;
    }
    visited.add(currentPid);
    const info = getProcessInfo2(currentPid);
    if (!info || info.ppid === 0 || info.ppid === 4) break;
    result.push(info);
    currentPid = info.ppid;
    maxDepth--;
  }
  if (maxDepth === 0) {
    console.warn(`Reached maximum depth limit while traversing process tree from PID ${startPid}`);
  }
  return result;
}

// src/index.ts
function getProcessAncestry(pid = process.pid) {
  if (typeof pid !== "number" || !Number.isInteger(pid) || pid <= 0) {
    throw new Error("PID must be a positive integer");
  }
  if (os.platform() === "win32") {
    return getAncestryWindows(pid);
  } else {
    return getAncestryUnix(pid);
  }
}
export {
  getProcessAncestry
};
