#!/bin/sh
ME=$(readlink -f $0)
SCRIPT_HOME=`echo $ME | sed 's|\(.*/\)\?[^/]*|\1|g'`

# Multi-platform set since 23.01-2.2: AllWindows + AllPlatforms (12 platforms incl. Linux
# glibc+musl+ARM and the universal Mac). AllLinux and AllMac are no longer produced:
# the universal 'Mac' build replaced AllMac, and AllPlatforms covers the AllLinux use case
# (runtime auto-detection picks arch + ARM level + glibc/musl).
$SCRIPT_HOME/build-multiplatform-release.sh --name AllWindows \
    sevenzipjbinding-*-Windows-*

$SCRIPT_HOME/build-multiplatform-release.sh \
    sevenzipjbinding-*-*

for file in sevenzipjbinding-*.zip; do cmake -DFILENAME=$file -DDESCRIPTION="Uploaded by build-all-multiplatform-releases.sh" -P $SCRIPT_HOME/upload-release.cmake; done
