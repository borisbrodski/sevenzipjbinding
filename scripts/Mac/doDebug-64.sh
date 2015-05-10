#!/bin/sh
rm -rf SevenZipJBinding.Debug-64
mkdir SevenZipJBinding.Debug-64
cd SevenZipJBinding.Debug-64
cmake ../SevenZipJBinding -DCMAKE_BUILD_TYPE=Debug -DJAVA_JDK=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home
make
ctest -D Experimental
