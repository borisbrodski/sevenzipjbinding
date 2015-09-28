#!/bin/sh

echo "Start ssh-agent and add key"
eval `ssh-agent -s`
ssh-add

rm -rf SevenZipJBinding.IT-Tests
mkdir SevenZipJBinding.IT-Tests
cmake "-DPACKAGES=Windows-x86;AllWindows;AllPlatforms" "-DJAVA=C:/Program Files (x86)/Java/jdk1.5.0_22/bin/java.exe" -DJAVA_PARAMS=-Xmx800M -DSCP="C:\Program Files (x86)\Git\bin\scp.exe" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake
rm -rf SevenZipJBinding.IT-Tests-i386
mv SevenZipJBinding.IT-Tests SevenZipJBinding.IT-Tests-i386

cmake "-DPACKAGES=Windows-amd64;AllWindows;AllPlatforms" "-DJAVA=C:/Program Files/Java/jdk1.5.0_20/bin/java.exe" -DJAVA_PARAMS=-Xmx800M -DSCP="C:\Program Files (x86)\Git\bin\scp.exe" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake
rm -rf SevenZipJBinding.IT-Tests-amd64
mv SevenZipJBinding.IT-Tests SevenZipJBinding.IT-Tests-amd64
