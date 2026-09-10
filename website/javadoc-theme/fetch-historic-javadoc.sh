#!/bin/bash
# Download previous 7-Zip-JBinding releases from SourceForge and extract each one's bundled
# javadoc into website/public/javadoc-history/<version>/ for the historic-JavaDoc viewer.
# JavaDoc lives inside every release zip as javadoc.zip; we grab the smallest per-platform zip
# per version. Idempotent: skips a version whose history dir already exists.
set -u
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEST="$HERE/../public/javadoc-history"
BASE="https://sourceforge.net/projects/sevenzipjbind/files/7-Zip-JBinding"
mkdir -p "$DEST"

# version : smallest zip known to carry javadoc.zip (per SF listings)
ENTRIES=(
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
  out="$DEST/$ver"
  if [ -d "$out" ]; then echo "= $ver (already present, skip)"; continue; fi
  tmp="$(mktemp -d)"
  echo "+ $ver  <-  $file"
  if ! curl -sfL --max-time 300 -o "$tmp/rel.zip" "$BASE/$ver/$file/download"; then
    echo "    download failed, skipping"; rm -rf "$tmp"; continue
  fi
  ( cd "$tmp" && unzip -q rel.zip ) || { echo "    unzip failed"; rm -rf "$tmp"; continue; }
  jd="$(find "$tmp" -iname 'javadoc.zip' | head -1)"
  if [ -z "$jd" ]; then echo "    no javadoc.zip inside, skipping"; rm -rf "$tmp"; continue; fi
  mkdir -p "$out"
  ( cd "$out" && unzip -q "$jd" )
  # sanity: must have an index.html
  if [ ! -f "$out/index.html" ] && [ -z "$(find "$out" -name index.html | head -1)" ]; then
    echo "    no index.html, dropping"; rm -rf "$out"
  else
    echo "    -> $out ($(find "$out" -type f | wc -l) files)"
  fi
  rm -rf "$tmp"
done

echo "== historic javadoc versions =="
ls -1 "$DEST" 2>/dev/null
