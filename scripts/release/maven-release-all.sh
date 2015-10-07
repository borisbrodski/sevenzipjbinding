#!/bin/bash

ME="$(readlink -f "$0")"
SCRIPT_DIR="$(dirname "$ME")"

PATH=~/Coding/JavaLibraries/apache-maven-3.2.5/bin:$PATH

#REPO=sonatype-nexus-snapshots
REPO=nexus-releases

$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-AllPlatforms.zip "$REPO"

$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-AllLinux.zip       "$REPO" -all-linux
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-AllMac.zip         "$REPO" -all-mac
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-AllPlatforms.zip   "$REPO" -all-platforms
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-AllWindows.zip     "$REPO" -all-windows
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-Linux-amd64.zip    "$REPO" -linux-amd64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-Linux-arm.zip      "$REPO" -linux-arm
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-Linux-i386.zip     "$REPO" -linux-i386
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-Mac-i386.zip       "$REPO" -mac-i386
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-Mac-x86_64.zip     "$REPO" -mac-x86_64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-Windows-amd64.zip  "$REPO" -windows-amd64
$SCRIPT_DIR/maven-release-jar.sh sevenzipjbinding-9.20-2.00beta-Windows-x86.zip    "$REPO" -windows-x86
