// Adapted from https://github.com/rolldown/rolldown/blob/41e26d6903d777bae3d37306a86da9fd1d80a1e3/packages/rolldown/src/webcontainer-fallback.cjs

const fs = require('node:fs');
const childProcess = require('node:child_process');

const pkg = JSON.parse(fs.readFileSync(require.resolve('@astrojs/compiler-binding/package.json'), 'utf-8'));
const version = pkg.version;

const baseDir = `/tmp/astro-compiler-${version}`;
const bindingEntry = `${baseDir}/node_modules/@astrojs/compiler-binding-wasm32-wasi/astro.wasi.cjs`;

if (!fs.existsSync(bindingEntry)) {
  const bindingPkg = `@astrojs/compiler-binding-wasm32-wasi@${version}`;
  fs.rmSync(baseDir, { recursive: true, force: true });
  fs.mkdirSync(baseDir, { recursive: true });
  console.log(`[astro] Downloading ${bindingPkg} on WebContainer...`);
  childProcess.execFileSync('pnpm', ['i', bindingPkg], {
    cwd: baseDir,
    stdio: 'inherit',
  });
}

module.exports = require(bindingEntry);
