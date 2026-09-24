#!/bin/bash

ME="$(readlink -f "$0")"
SCRIPT_DIR="$(dirname "$ME")"

#REPO=sonatype-nexus-snapshots
REPO=nexus-releases

echo -n "Enter version: "
read VERSION

# Artifact set since 26.03-2.5:
#  - AllLinux dropped (no such zip anymore; AllPlatforms covers the use case)
#  - macOS is now THREE artifacts: -all-mac (universal fat, both arches; the Mac entry inside
#    AllPlatforms), -mac-x86_64 (thin Intel) and -mac-arm64 (thin Apple Silicon). The old universal
#    -mac id was DROPPED (breaking): depend on -all-mac for "any Mac", or a thin id to save space.
#  - musl/Alpine platforms added
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllPlatforms.zip "$REPO"

$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllPlatforms.zip     "$REPO" -all-platforms
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllWindows.zip       "$REPO" -all-windows
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-i386.zip       "$REPO" -linux-i386
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-amd64.zip      "$REPO" -linux-amd64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-arm64.zip      "$REPO" -linux-arm64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-armv5.zip      "$REPO" -linux-armv5
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-armv6.zip      "$REPO" -linux-armv6
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-armv7.zip      "$REPO" -linux-armv7
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-amd64-musl.zip "$REPO" -linux-amd64-musl
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-arm64-musl.zip "$REPO" -linux-arm64-musl
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Linux-armv7-musl.zip "$REPO" -linux-armv7-musl
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-AllMac.zip           "$REPO" -all-mac
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Mac-x86_64.zip       "$REPO" -mac-x86_64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Mac-arm64.zip        "$REPO" -mac-arm64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-amd64.zip    "$REPO" -windows-amd64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-x86.zip      "$REPO" -windows-x86
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-arm64.zip    "$REPO" -windows-arm64

 


