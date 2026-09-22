import * as eslexer from "es-module-lexer";
async function isActionsFilePresent(fs, srcDir) {
  await eslexer.init;
  const actionsFile = search(fs, srcDir);
  if (!actionsFile) return false;
  let contents;
  try {
    contents = fs.readFileSync(actionsFile.url, "utf-8");
  } catch {
    return false;
  }
  const [, exports] = eslexer.parse(contents, actionsFile.url.pathname);
  for (const exp of exports) {
    if (exp.n === "server") {
      return actionsFile.filename;
    }
  }
  return false;
}
function search(fs, srcDir) {
  const filenames = [
    "actions.mjs",
    "actions.js",
    "actions.mts",
    "actions.ts",
    "actions/index.mjs",
    "actions/index.js",
    "actions/index.mts",
    "actions/index.ts"
  ];
  for (const filename of filenames) {
    const url = new URL(filename, srcDir);
    if (fs.existsSync(url)) {
      return { filename, url };
    }
  }
  return void 0;
}
export {
  isActionsFilePresent
};
