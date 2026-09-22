function getTimeStat(timeStart, timeEnd) {
  const buildTime = timeEnd - timeStart;
  if (buildTime < 1e3) {
    return `${Math.round(buildTime)}ms`;
  } else if (buildTime < 6e4) {
    return `${(buildTime / 1e3).toFixed(2)}s`;
  }
  const mins = Math.floor(buildTime / 6e4);
  const secs = Math.round(buildTime % 6e4 / 1e3);
  return `${mins}m ${secs}s`;
}
function shouldAppendForwardSlash(trailingSlash, buildFormat) {
  switch (trailingSlash) {
    case "always":
      return true;
    case "never":
      return false;
    case "ignore": {
      switch (buildFormat) {
        case "directory":
          return true;
        case "preserve":
        case "file":
          return false;
      }
    }
  }
}
const UNSAFE_CHUNK_CHAR_RE = /[^\w.\-/]/g;
function cleanChunkName(name) {
  return encodeName(name.replace(UNSAFE_CHUNK_CHAR_RE, "_"));
}
function encodeName(name) {
  for (let i = 0; i < name.length; i++) {
    if (name[i] === "%") {
      const third = name.codePointAt(i + 2) | 32;
      if (name[i + 1] !== "2" || third !== 102) {
        return `${name.replace(/%/g, "_percent_")}`;
      }
    }
  }
  return name;
}
function viteBuildReturnToRolldownOutputs(viteBuildReturn) {
  const result = [];
  if (Array.isArray(viteBuildReturn)) {
    result.push(...viteBuildReturn);
  } else if ("output" in viteBuildReturn) {
    result.push(viteBuildReturn);
  }
  return result;
}
export {
  cleanChunkName,
  getTimeStat,
  shouldAppendForwardSlash,
  viteBuildReturnToRolldownOutputs
};
