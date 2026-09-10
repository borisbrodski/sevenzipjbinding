#!/bin/bash
# Generate the 7-Zip-JBinding JavaDoc with the website theme, into website/public/javadoc/
# so Astro serves it at /javadoc/ (linked from the site header) and bundles it in dist/.
# Modern doclet (JDK 17) + our theme.css (--add-stylesheet) + a small theme-sync script (-top).
set -euo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"          # SevenZipJBinding/
SRC="$REPO/jbinding-java/src"
OUT="$HERE/../public/javadoc"
JD="${JAVADOC:-/usr/lib/jvm/java-17-openjdk-amd64/bin/javadoc}"
VERSION="$(sed -n 's/^SET(SEVENZIPJBINDING_VERSON \([^)]*\)).*/\1/p' "$REPO/CMakeLists.txt" | head -1)"
[ -n "$VERSION" ] || VERSION="23.01-2.2"

echo "JavaDoc $VERSION  ->  $OUT   (javadoc: $JD)"
rm -rf "$OUT"; mkdir -p "$OUT"

"$JD" -quiet -Xdoclint:none -notimestamp -public -author -version --allow-script-in-comments \
  -sourcepath "$SRC" -subpackages net.sf.sevenzipjbinding \
  -d "$OUT" \
  -windowtitle "7-Zip-JBinding $VERSION API" \
  -doctitle "7-Zip-JBinding <span style='background:linear-gradient(96deg,#0aa8dc,#1878e0 55%,#5848f0);-webkit-background-clip:text;background-clip:text;-webkit-text-fill-color:transparent'>API</span> &mdash; $VERSION" \
  -header "<a href='/' style='font-weight:700'>7-Zip-JBinding</a>" \
  -bottom "<span style='color:#7b8090'>7-Zip-JBinding $VERSION &middot; LGPL-2.1 &middot; <a href='/'>Home</a> &middot; <a href='/javadoc-history/'>Older versions</a> &middot; <a href='https://github.com/borisbrodski/sevenzipjbinding'>GitHub</a></span>" \
  --add-stylesheet "$HERE/theme.css" \
  -top "$(cat "$HERE/top.html")"

echo "Done. Files: $(find "$OUT" -type f | wc -l)"
