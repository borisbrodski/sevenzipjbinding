rmdir /S /Q SevenZipJBinding.IT-Tests
mkdir SevenZipJBinding.IT-Tests
cmake "-DPACKAGES=Windows-amd64;AllWindows;AllPlatforms" -DSCP="C:\Program Files (x86)\Git\bin\scp.exe" -P SevenZipJBinding/scripts/integration-test/doITTests.cmake


