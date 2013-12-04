#!/bin/bash
rm -rf SevenZipJBinding.Debug/
mkdir SevenZipJBinding.Debug/
cd SevenZipJBinding.Debug/
cmake ../SevenZipJBinding -DCMAKE_BUILD_TYPE=Debug -DJAVA_JDK=/usr/lib/jvm/java-5-sun
make
ctest -D Experimental
