#!/bin/bash
ssh-add
OPT=

read -p "Download it-test pack from sourceforge web space (y/n)? "
if [ "$REPLY" != "y" ] ; then
    OPT="-DSD=1"
fi

# Test 32 bit
cmake $OPT "-DPACKAGES=Mac-i386;AllMac;AllPlatforms" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake


# Test 64 bit
OPT_JAVA="-DJAVA=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home/bin/java"
cmake $OPT $OPT_JAVA "-DPACKAGES=Mac-x86_64;AllMac;AllPlatforms" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake
