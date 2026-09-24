#!/bin/sh
ME=$(readlink -f $0)
SCRIPT_HOME=`echo $ME | sed 's|\(.*/\)\?[^/]*|\1|g'`

# Multi-platform set: AllWindows + AllPlatforms (13 platforms incl. Linux glibc+musl+ARM, the
# universal Mac, and Windows x86/x64/arm64). AllLinux and AllMac are no longer produced: the
# universal 'Mac' build replaced AllMac, and AllPlatforms covers the AllLinux use case (runtime
# auto-detection picks OS + arch + ARM level + glibc/musl). The globs below pick up new per-platform
# zips automatically, so Windows-arm64 folds into AllWindows and AllPlatforms with no change here.
$SCRIPT_HOME/build-multiplatform-release.sh --name AllWindows \
    sevenzipjbinding-*-Windows-*

# AllPlatforms: every per-platform zip, but for macOS use ONLY the universal AllMac (one fat dylib
# covers Intel + Apple Silicon). Exclude the thin per-arch Mac zips so they are not duplicated inside
# AllPlatforms. build-multiplatform-release.sh skips the AllWindows/AllPlatforms bundles internally.
$SCRIPT_HOME/build-multiplatform-release.sh \
    $(ls sevenzipjbinding-*-*.zip 2>/dev/null | grep -vE 'sevenzipjbinding-.*-Mac-(x86_64|arm64)\.zip$')

for file in sevenzipjbinding-*.zip; do cmake -DFILENAME=$file -DDESCRIPTION="Uploaded by build-all-multiplatform-releases.sh" -P $SCRIPT_HOME/upload-release.cmake; done
