/**
 * Build-time snippet extraction — the successor of doc/build_html.cmake.
 *
 * Snippets are REAL, TESTED programs living in
 *   test/JavaTests/src/net/sf/sevenzipjbinding/junit/snippets/<Name>.java
 * between `/* BEGIN_SNIPPET(Name) *​/` … `/* END_SNIPPET *​/` markers, with
 * formatting markers (`/*f*​/x/* *​/`, trailing `//`) that keep the source
 * pretty in the IDE. Expected outputs (verified by the paired <Name>Test)
 * live in doc/web.components/output/<Name>.html.
 *
 * Everything here runs at BUILD time (Astro frontmatter) — the site fails to
 * build if a referenced snippet disappears, keeping code on the page honest.
 */
import { readFileSync, existsSync, readdirSync } from 'node:fs';
import { resolve, relative } from 'node:path';
import { codeToHtml } from 'shiki';

const REPO_ROOT = resolve(import.meta.dirname, '../../..');
const JAVADOC_DIR = resolve(REPO_ROOT, 'website/public/javadoc');
const SNIPPET_DIR = resolve(REPO_ROOT, 'test/JavaTests/src/net/sf/sevenzipjbinding/junit/snippets');
const OUTPUT_DIR = resolve(REPO_ROOT, 'doc/web.components/output');

/** Strip the marker syntax exactly like build_html.cmake did. */
function cleanLine(line: string): string {
  return line
    .replace(/\/\/$/, '') //                  trailing continuation marker
    .replace(/[ \t]*<br>$/, '') //            explicit break marker
    .replace(/\/\*f\*\/([^/]+)\/\*([^*]*)\*\//g, '$1$2') // /*f*/x/* */ -> x
    .replace(/\/\*sf\*\/([^/]+)\/\*([^*]*)\*\//g, '$1$2')
    .replace(/\/\*s\*\/([^/]+)\/\*([^*]*)\*\//g, '$1$2')
    .replace(/\/\*(  +)\*\//g, '$1'); //      /*   */ keeps alignment spaces
}

export function loadSnippet(className: string): string {
  const file = resolve(SNIPPET_DIR, `${className}.java`);
  const src = readFileSync(file, 'utf8'); // throws -> build fails if missing
  const begin = src.indexOf(`/* BEGIN_SNIPPET(`);
  const beginEol = src.indexOf('\n', begin);
  const end = src.indexOf('/* END_SNIPPET */');
  if (begin < 0 || end < 0) throw new Error(`No snippet markers in ${file}`);
  const body = src.slice(beginEol + 1, end);
  const lines = body.split('\n').map(cleanLine);
  // trim leading/trailing blank lines
  while (lines.length && lines[0]!.trim() === '') lines.shift();
  while (lines.length && lines[lines.length - 1]!.trim() === '') lines.pop();
  return lines.join('\n');
}

/** Load an expected-output fragment; they are plain text with light HTML escapes. */
export function loadOutput(name: string): string | null {
  const file = resolve(OUTPUT_DIR, `${name}.html`);
  if (!existsSync(file)) return null;
  return readFileSync(file, 'utf8')
    .replace(/<br\s*\/?>/gi, '')
    .replace(/<[^>]+>/g, '')
    .replace(/&#60;/g, '<')
    .replace(/&#62;/g, '>')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&nbsp;/g, ' ')
    .replace(/&amp;/g, '&')
    .trimEnd();
}

/**
 * Map simple class/interface name -> JavaDoc URL, built from the generated docs in
 * website/public/javadoc/. Used to auto-link every API type in a snippet (like the old site did).
 * Empty if JavaDoc hasn't been generated yet (build-javadoc.sh) — links are then simply skipped.
 */
function buildJavadocMap(): Map<string, string> {
  const map = new Map<string, string>();
  const pkgRoot = resolve(JAVADOC_DIR, 'net/sf/sevenzipjbinding');
  if (!existsSync(pkgRoot)) return map;
  const skip = /^(package-|class-use|.*-summary|.*-tree|doc-files)/;
  const walk = (dir: string) => {
    for (const e of readdirSync(dir, { withFileTypes: true })) {
      if (e.isDirectory()) { if (e.name !== 'class-use') walk(resolve(dir, e.name)); continue; }
      if (!e.name.endsWith('.html') || skip.test(e.name)) continue;
      const simple = e.name.slice(0, -5);
      if (!/^[A-Z][A-Za-z0-9]*$/.test(simple)) continue;
      const url = '/javadoc/' + relative(JAVADOC_DIR, resolve(dir, e.name)).replace(/\\/g, '/');
      if (!map.has(simple)) map.set(simple, url); // first wins on the rare name clash
    }
  };
  walk(pkgRoot);
  return map;
}
const JAVADOC_MAP = buildJavadocMap();

/**
 * Highlight Java with Shiki (dual light/dark theme) and auto-link API types to JavaDoc.
 * Linking is precise: only a Shiki token whose ENTIRE text is a known class name is wrapped,
 * so we never touch substrings, keywords or method names.
 */
export async function highlightJava(code: string): Promise<string> {
  const html = await codeToHtml(code, {
    lang: 'java',
    themes: { light: 'github-light', dark: 'one-dark-pro' },
  });
  if (JAVADOC_MAP.size === 0) return html;
  // Link documented API types wherever they appear as a WHOLE WORD inside a token's text.
  // We only touch text between tags (`>…<`), never tag attributes; whole-word (\b) + longest-first
  // avoids substrings (e.g. IInArchive inside IInArchiveImpl) and never matches keywords/methods.
  const names = [...JAVADOC_MAP.keys()].sort((a, b) => b.length - a.length);
  const wordRe = new RegExp('\\b(' + names.join('|') + ')\\b', 'g');
  return html.replace(/>([^<]+)</g, (_full, text: string) => {
    return '>' + text.replace(wordRe, (m, name: string) => {
      const url = JAVADOC_MAP.get(name);
      return url ? `<a class="jd-link" href="${url}">${name}</a>` : m;
    }) + '<';
  });
}

export interface SnippetDef {
  id: string;          // anchor id
  title: string;
  className: string;   // Java class in the snippets package
  output?: string;     // output fragment name (doc/web.components/output/<name>.html)
  desc: string;
}

export interface SnippetGroup {
  group: string;
  items: SnippetDef[];
}

/** The catalogue rendered on /snippets — grouped for the outline. */
export const CATALOGUE: SnippetGroup[] = [
  {
    group: 'Getting started',
    items: [
      { id: 'init-check', title: 'Verify native library initialization', className: 'SevenZipJBindingInitCheck', output: 'SevenZipJBindingInitCheck', desc: 'Initializes 7-Zip-JBinding explicitly and prints the native library version — a minimal smoke test of your setup.' },
      { id: 'print-count', title: 'Print the number of archive items', className: 'PrintCountOfItems', output: 'PrintCountOfItems', desc: 'The smallest useful program: open an archive (format auto-detected) and query one property.' },
    ],
  },
  {
    group: 'Extraction',
    items: [
      { id: 'list-simple', title: 'List items (simple interface)', className: 'ListItemsSimple', output: 'ListItemsOutput', desc: 'Iterate all items with the beginner-friendly simple interface: sizes, compressed sizes and paths.' },
      { id: 'list-standard', title: 'List items (standard interface)', className: 'ListItemsStandard', output: 'ListItemsOutput', desc: 'The same listing through the full-power standard interface with typed property access.' },
      { id: 'extract-simple', title: 'Extract items (simple interface)', className: 'ExtractItemsSimple', output: 'ExtractItems', desc: 'Extract every item and consume the data through a callback — no temp files involved.' },
      { id: 'extract-standard', title: 'Extract items (standard interface)', className: 'ExtractItemsStandard', output: 'ExtractItems', desc: 'Sequential extraction with the standard interface for maximum control and speed.' },
      { id: 'extract-standard-cb', title: 'Extract items (standard interface, callback)', className: 'ExtractItemsStandardCallback', output: 'ExtractItems', desc: 'The standard extraction driven entirely by your IArchiveExtractCallback — full control over data sinks and progress.' },
      { id: 'multipart-7z', title: 'Open multi-part 7z archives (7z.001, …)', className: 'OpenMultipartArchive7z', output: 'OpenMultipart7zArchiveOutput', desc: 'Combine volume files transparently with IArchiveOpenVolumeCallback.' },
      { id: 'multipart-rar', title: 'Open multi-part RAR archives (.part1.rar, …)', className: 'OpenMultipartArchiveRar', output: 'OpenMultipartRarArchiveOutput', desc: 'RAR volumes use their own naming scheme; this callback resolves them.' },
    ],
  },
  {
    group: 'Compression',
    items: [
      { id: 'compress-structure', title: 'The archive structure used in these examples', className: 'CompressArchiveStructure', desc: 'A small in-memory file tree shared by all compression snippets below.' },
      { id: 'compress-generic', title: 'Create archives (generic API, all formats)', className: 'CompressGeneric', output: 'CompressGenericZip', desc: 'One code path that can write 7z, Zip, Tar, GZip, BZip2 — the format is a runtime parameter.' },
      { id: 'compress-7z', title: 'Create a 7z archive (format-specific API)', className: 'CompressNonGeneric7z', output: 'CompressNonGeneric7z', desc: 'The 7z-specific interface exposes format features like solid blocks and header encryption.' },
      { id: 'compress-zip', title: 'Create a Zip archive', className: 'CompressNonGenericZip', output: 'CompressNonGenericZip', desc: 'Zip-specific archive creation with per-item attributes.' },
      { id: 'compress-tar', title: 'Create a Tar archive', className: 'CompressNonGenericTar', output: 'CompressNonGenericTar', desc: 'Tar with POSIX attributes — user, group and permissions.' },
      { id: 'compress-gzip', title: 'Create a GZip archive (single stream)', className: 'CompressNonGenericGZip', output: 'CompressNonGenericGZip', desc: 'Stream formats compress exactly one item.' },
      { id: 'compress-bzip2', title: 'Create a BZip2 archive (single stream)', className: 'CompressNonGenericBZip2', output: 'CompressNonGenericBZip2', desc: 'Same single-stream model as GZip, different codec.' },
      { id: 'compress-xz', title: 'Create an XZ archive (single stream) — new in 23.01', className: 'CompressNonGenericXz', output: 'CompressNonGenericXz', desc: 'XZ compression support is new in 23.01-2.2.' },
      { id: 'compress-password', title: 'Create a password-protected archive', className: 'CompressWithPassword', output: 'CompressWithPassword', desc: 'Encrypt a 7z archive with a password (7z also encrypts the archive headers).' },
      { id: 'compress-message', title: 'Compress a String to a byte array', className: 'CompressMessage', output: 'CompressMessage', desc: 'Fully in-memory round trip: no files touched at all.' },
      { id: 'compress-error', title: 'Handle errors during compression', className: 'CompressWithError', output: 'CompressWithErrorErr', desc: 'What proper SevenZipException handling looks like when a callback throws.' },
    ],
  },
  {
    group: 'Updating archives',
    items: [
      { id: 'update-add-remove', title: 'Add and remove items in an existing archive', className: 'UpdateAddRemoveItems', output: 'UpdateAddRemoveItems', desc: 'Modify an archive without repacking untouched items.' },
      { id: 'update-alter', title: 'Alter existing items (content & properties)', className: 'UpdateAlterItems', output: 'UpdateAlterItems', desc: 'Replace an item’s data and change its metadata in place.' },
    ],
  },
];
