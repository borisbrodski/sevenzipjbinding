#!/usr/bin/env bash
#
# update-vendor-7zip.sh — drop a new pristine 7-Zip release onto the upstream-7zip vendor branch.
#
# The upstream-7zip branch holds ONLY the pristine 7-Zip source under 7zip/. This script replaces
# that source with a newer release so the change can be merged into master (a 3-way merge re-applies
# our local edits automatically — see README.md and the vault page
# "20-Development/7-Zip Source Modifications.md").
#
# Usage:
#   ./update-vendor-7zip.sh <7zXXXX-src.7z | extracted-source-dir>
#   ./update-vendor-7zip.sh --prune <src>     # additionally strip non-code files (see PRUNE_GLOBS)
#
# Line endings: nothing is converted here — .gitattributes (* text=auto eol=lf) makes `git add`
# normalize the CRLF Windows source to LF exactly as master stores it. Keep .gitattributes present.
#
set -euo pipefail

PRUNE=0
if [ "${1:-}" = "--prune" ]; then PRUNE=1; shift; fi
SRC="${1:-}"

die() { echo "error: $*" >&2; exit 1; }
[ -n "$SRC" ] || die "usage: $0 [--prune] <7zXXXX-src.7z | source-dir>"

# Must be run from the repo root of the upstream-7zip branch.
[ -f .gitattributes ] || die "run from the repo root (no .gitattributes here)"
branch=$(git rev-parse --abbrev-ref HEAD)
[ "$branch" = "upstream-7zip" ] || die "expected branch 'upstream-7zip', on '$branch'"

# Resolve the source into a directory that contains Asm/ C/ CPP/ (DOC/ optional).
TMP=""
cleanup() { [ -n "$TMP" ] && rm -rf "$TMP"; }
trap cleanup EXIT

if [ -f "$SRC" ]; then
  command -v 7z >/dev/null 2>&1 || die "need the '7z' tool to extract $SRC"
  TMP=$(mktemp -d)
  echo ">> extracting $SRC ..."
  7z x -y -o"$TMP" "$SRC" >/dev/null
  SRCDIR="$TMP"
elif [ -d "$SRC" ]; then
  SRCDIR="$SRC"
else
  die "source not found: $SRC"
fi

# Some archives wrap everything in a single top-level dir; descend into it if so.
if [ ! -d "$SRCDIR/CPP" ] && [ "$(find "$SRCDIR" -maxdepth 1 -mindepth 1 -type d | wc -l)" = "1" ]; then
  SRCDIR=$(find "$SRCDIR" -maxdepth 1 -mindepth 1 -type d)
fi
[ -d "$SRCDIR/CPP" ] && [ -d "$SRCDIR/C" ] || die "source dir has no C/ and CPP/ (got: $SRCDIR)"

echo ">> replacing 7zip/ with the new pristine source ..."
rm -rf 7zip
mkdir -p 7zip
for d in Asm C CPP DOC; do
  [ -d "$SRCDIR/$d" ] && cp -a "$SRCDIR/$d" 7zip/
done

if [ "$PRUNE" = "1" ]; then
  echo ">> --prune: removing non-code files ..."
  # Non-code: docs, Windows VS project files, makefiles, resources, installers. Keep .c/.h/.cpp/.asm/.S.
  PRUNE_GLOBS=(
    '7zip/DOC'
    -o -name '*.dsp' -o -name '*.dsw' -o -name '*.vcproj' -o -name '*.sln'
    -o -name '*.mak' -o -name 'makefile' -o -name 'makefile.*'
    -o -name '*.rc' -o -name '*.ico' -o -name '*.bmp' -o -name '*.manifest' -o -name '*.def'
    -o -name '*.bat' -o -name '*.cmd' -o -name '*.htm' -o -name '*.html'
  )
  rm -rf 7zip/DOC
  find 7zip \( "${PRUNE_GLOBS[@]:2}" \) -type f -print -delete
fi

echo
echo ">> done. Next:"
echo "     git add -A && git commit -m 'Original 7-zip <VER> (<YYYY-MM-DD>)'"
echo "     git checkout master && git merge upstream-7zip"
echo "     # resolve real conflicts, then re-check '7-Zip Source Modifications.md'"
