#!/bin/bash

ME="$(readlink -f "$0")"
SCRIPT_DIR="$(dirname "$ME")"

#REPO=sonatype-nexus-snapshots
REPO=nexus-releases

echo -n "Enter version: "
read VERSION

# Artifact set since 23.01-2.2:
#  - AllLinux dropped (no such zip anymore; AllPlatforms covers the use case)
#  - Mac is the universal (x86_64+arm64) build: canonical id -mac, PLUS a -mac-x86_64 compat
#    alias (same universal jar) so pre-23.01 POMs keep resolving
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
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Mac.zip              "$REPO" -mac
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Mac.zip              "$REPO" -mac-x86_64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-amd64.zip    "$REPO" -windows-amd64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-x86.zip      "$REPO" -windows-x86
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-$VERSION-Windows-arm64.zip    "$REPO" -windows-arm64

 


