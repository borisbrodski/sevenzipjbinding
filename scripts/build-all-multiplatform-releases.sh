#!/bin/sh
ME=$(readlink -f $0)
SCRIPT_HOME=`echo $ME | sed 's|\(.*/\)\?[^/]*|\1|g'`

# Multi-platform set: AllWindows + AllPlatforms. AllPlatforms bundles every per-platform native -
# Linux glibc (i386/amd64/ARMv5/6/7/ARM64) + Linux musl (amd64/ARMv7/ARM64) + macOS (the universal
# AllMac fat dylib) + Windows (x86/x64/arm64) + FreeBSD-amd64 - and runtime auto-detection picks
# OS + arch + ARM level + glibc/musl. The thin per-arch Mac zips (Mac-x86_64/Mac-arm64) are shipped
# standalone but EXCLUDED from AllPlatforms (AllMac already covers both). AllLinux is not produced
# (AllPlatforms covers it). The globs pick up new per-platform zips automatically.
$SCRIPT_HOME/build-multiplatform-release.sh --name AllWindows \
    sevenzipjbinding-*-Windows-*

# AllPlatforms: every per-platform zip, but for macOS use ONLY the universal AllMac (one fat dylib
# covers Intel + Apple Silicon). Exclude the thin per-arch Mac zips so they are not duplicated inside
# AllPlatforms. build-multiplatform-release.sh skips the AllWindows/AllPlatforms bundles internally.
$SCRIPT_HOME/build-multiplatform-release.sh \
    $(ls sevenzipjbinding-*-*.zip 2>/dev/null | grep -vE 'sevenzipjbinding-.*-Mac-(x86_64|arm64)\.zip$')

for file in sevenzipjbinding-*.zip; do cmake -DFILENAME=$file -DDESCRIPTION="Uploaded by build-all-multiplatform-releases.sh" -P $SCRIPT_HOME/upload-release.cmake; done
