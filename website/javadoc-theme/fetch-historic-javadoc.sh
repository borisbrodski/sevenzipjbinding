#!/bin/bash
# Historic JavaDoc for the website viewer (/javadoc-history/): the original javadoc.zip bundled in
# every release zip, one directory per version.
#
# The canonical copy lives OUTSIDE the git repo, in $JAVADOC_HISTORY_ARCHIVE (default:
# <repo>/../Javadoc-History/<version>/). This script
#   1. fills missing versions of the archive from the SourceForge release zips (ENTRIES below),
#   2. mirrors the archive into website/public/javadoc-history/ (generated, not tracked), which
#      Astro serves.
#
# Add a new release (before or after it is uploaded anywhere) from a local release zip:
#   fetch-historic-javadoc.sh --add 26.03-2.3 Release-Artifacts/sevenzipjbinding-26.03-2.3-Linux-i386.zip
# and list the version in ORDER in src/pages/javadoc-history.astro.
set -u
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
ARCHIVE="${JAVADOC_HISTORY_ARCHIVE:-$(cd "$REPO/.." && pwd)/Javadoc-History}"
DEST="$HERE/../public/javadoc-history"
BASE="https://sourceforge.net/projects/sevenzipjbind/files/7-Zip-JBinding"
mkdir -p "$ARCHIVE" "$DEST"

# Extract the javadoc.zip inside release zip $2 into $ARCHIVE/$1. Returns non-zero on failure.
extract_release_javadoc() {
  local ver="$1" zip="$2" out="$ARCHIVE/$1" tmp jd
  tmp="$(mktemp -d)"
  ( cd "$tmp" && unzip -q "$zip" ) || { echo "    unzip failed"; rm -rf "$tmp"; return 1; }
  jd="$(find "$tmp" -iname 'javadoc.zip' | head -1)"
  if [ -z "$jd" ]; then echo "    no javadoc.zip inside"; rm -rf "$tmp"; return 1; fi
  rm -rf "$out"; mkdir -p "$out"
  ( cd "$out" && unzip -q "$jd" )
  rm -rf "$tmp"
  if [ -z "$(find "$out" -name index.html | head -1)" ]; then
    echo "    no index.html, dropping"; rm -rf "$out"; return 1
  fi
  echo "    -> $out ($(find "$out" -type f | wc -l) files)"
}

if [ "${1:-}" = "--add" ]; then
  [ $# -eq 3 ] || { echo "usage: $0 --add <version> <release-zip>" >&2; exit 2; }
  echo "+ $2  <-  $3"
  extract_release_javadoc "$2" "$(readlink -f "$3")" || exit 1
fi

# version : smallest zip known to carry javadoc.zip (per SF listings)
ENTRIES=(
  "23.01-2.2:sevenzipjbinding-23.01-2.2-Linux-i386.zip"
  "16.02-2.01:sevenzipjbinding-16.02-2.01-Linux-i386.zip"
  "9.20-2.00beta:sevenzipjbinding-9.20-2.00beta-Linux-i386.zip"
  "4.65-1.06rc-extr-only:sevenzipjbinding-4.65-1.06-rc-extr-only-Linux-i386.zip"
  "4.65-1.05rc-extr-only:sevenzipjbinding-4.65-1.05-rc-extr-only-Linux-i386.zip"
  "4.65-1.04rc-extr-only:sevenzipjbinding-4.65-1.04-rc-extr-only-Linux-i386.zip"
  "4.65-1.03rc-extr-only:sevenzipjbinding-4.65-1.03rc-extr-only-Linux-i386.zip"
  "4.65-1.02rc-extr-only:sevenzipjbinding-4.65-1.02rc-extr-only-Linux-i686.zip"
  "4.65-1.01rc-extr-only:sevenzipjbinding-4.65-1.01rc-extr-only-Linux-i686.zip"
  "4.65-1.0rc-extr-only:sevenzipjbinding-4.65-1.0rc-extr-only-Linux-i686.zip"
)

for e in "${ENTRIES[@]}"; do
  ver="${e%%:*}"; file="${e#*:}"
  if [ -d "$ARCHIVE/$ver" ]; then echo "= $ver (in archive)"; continue; fi
  tmp="$(mktemp -d)"
  echo "+ $ver  <-  SourceForge $file"
  if curl -sfL --max-time 300 -o "$tmp/rel.zip" "$BASE/$ver/$file/download"; then
    extract_release_javadoc "$ver" "$tmp/rel.zip"
  else
    echo "    download failed, skipping"
  fi
  rm -rf "$tmp"
done

# Mirror the archive into the site (exactly: versions removed from the archive disappear here too).
rm -rf "$DEST"; mkdir -p "$DEST"
cp -a "$ARCHIVE/." "$DEST/"

echo "== historic javadoc versions (archive: $ARCHIVE) =="
ls -1 "$DEST" 2>/dev/null
