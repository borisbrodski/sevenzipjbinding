#!/bin/bash
OPT=

read -p "Download it-test pack from sourceforge web space (y/n)? "
if [ "$REPLY" != "y" ] ; then
    OPT=-DSD=1
fi

#cmake $OPT "-DPACKAGES=Linux-i386;AllLinux;AllPlatforms" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake
#cmake $OPT "-DPACKAGES=Linux-amd64;AllLinux;AllPlatforms" "-DJAVA_PARAMS:STRING=-DTEST_PROFILE=STRESS" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake
cmake $OPT "-DPACKAGES=Linux-amd64;AllLinux;AllPlatforms" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake
