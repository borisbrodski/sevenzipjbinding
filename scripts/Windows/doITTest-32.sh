#!/bin/sh


# Run this from the Windows git bash


echo "Start ssh-agent and add key"
eval `ssh-agent -s`
ssh-add

rm -rf SevenZipJBinding.IT-Tests
mkdir SevenZipJBinding.IT-Tests
cmake "-DPACKAGES=Windows-x86;AllWindows;AllPlatforms" "-DJAVA=C:/Program Files (x86)/Java/jdk1.5.0_22/bin/java.exe" -DSCP="C:\Program Files (x86)\Git\bin\scp.exe" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake


