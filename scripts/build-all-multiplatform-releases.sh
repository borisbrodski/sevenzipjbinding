#!/bin/sh
ME=$(readlink -f $0)
SCRIPT_HOME=`echo $ME | sed 's|\(.*/\)\?[^/]*|\1|g'`

$SCRIPT_HOME/build-multiplatform-release.sh --name AllWindows \
    sevenzipjbinding-*-Windows-*

$SCRIPT_HOME/build-multiplatform-release.sh --name AllLinux \
    sevenzipjbinding-*-Linux-*

# Support only x64 Mac build from 7-Zip-JBinding 16.02-2.01
#$SCRIPT_HOME/build-multiplatform-release.sh --name AllMac \
#    sevenzipjbinding-*-Mac-* \

$SCRIPT_HOME/build-multiplatform-release.sh \
    sevenzipjbinding-*-*

for file in sevenzipjbinding-*.zip; do cmake -DFILENAME=$file -DDESCRIPTION="Uploaded by build-all-multiplatform-releases.sh" -P $SCRIPT_HOME/upload-release.cmake; done
