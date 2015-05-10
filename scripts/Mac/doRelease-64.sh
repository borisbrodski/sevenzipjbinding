#!/bin/sh
rm -rf SevenZipJBinding.Release-64
mkdir SevenZipJBinding.Release-64
cd SevenZipJBinding.Release-64
cmake ../SevenZipJBinding  -DJAVA_JDK=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home
make
ctest -D Experimental
make package
cmake -P ../SevenZipJBinding/scripts/upload-release.cmake
cd ..

