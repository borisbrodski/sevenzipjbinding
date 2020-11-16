#!/bin/bash

ME="$(readlink -f "$0")"
SCRIPT_DIR="$(dirname "$ME")"

#REPO=sonatype-nexus-snapshots
REPO=nexus-releases

echo -n "Enter version: "
read VERSION

$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllPlatforms.zip "$REPO"

$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllLinux.zip       "$REPO" -all-linux
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllPlatforms.zip   "$REPO" -all-platforms
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllWindows.zip     "$REPO" -all-windows
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-i386.zip     "$REPO" -linux-i386
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-amd64.zip    "$REPO" -linux-amd64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-arm64.zip    "$REPO" -linux-arm64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-armv5.zip    "$REPO" -linux-armv5
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-armv6.zip    "$REPO" -linux-armv6
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-armv71.zip   "$REPO" -linux-armv71
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Mac-x86_64.zip     "$REPO" -mac-x86_64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-amd64.zip  "$REPO" -windows-amd64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-x86.zip    "$REPO" -windows-x86

 


