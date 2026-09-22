# CMake toolchain for building the Windows-on-Arm (aarch64) native library with llvm-mingw.
#
# llvm-mingw (https://github.com/mstorsjo/llvm-mingw) is a clang/LLD + mingw-w64 toolchain. It is the
# GCC-style (MinGW ABI/headers) way to target Windows arm64 - stock GNU gcc has no Windows-arm64
# backend. It runs natively on a Windows-arm64 host AND cross-compiles from Linux/macOS.
#
# Usage (native on a windows-11-arm runner, llvm-mingw bin/ on PATH):
#   cmake .. -DCMAKE_TOOLCHAIN_FILE=cmake/toolchain-windows-arm64-llvm-mingw.cmake \
#            -DJAVA_SYSTEM=Windows -DJAVA_ARCH=arm64 -DJAVA_JDK="$JAVA_HOME"
# Usage (cross from Linux): also pass -DLLVM_MINGW=/path/to/llvm-mingw and a Windows JDK's headers
#   via -DJAVA_JDK=<windows-jdk> (its include/win32/jni_md.h is required for a Windows JNI build).

set(CMAKE_SYSTEM_NAME Windows)
set(CMAKE_SYSTEM_PROCESSOR aarch64)

# MINGWARM64 selects the llvm-mingw / Windows-arm64 code paths in the project's CMake files.
set(MINGWARM64 Yes CACHE BOOL "Use llvm-mingw toolchain (Windows on Arm, aarch64)" FORCE)

set(_triple aarch64-w64-mingw32)

# Locate the toolchain: explicit -DLLVM_MINGW=..., $LLVM_MINGW, else assume it is on PATH.
if(NOT LLVM_MINGW AND DEFINED ENV{LLVM_MINGW})
    set(LLVM_MINGW $ENV{LLVM_MINGW})
endif()
if(LLVM_MINGW)
    set(_bin "${LLVM_MINGW}/bin/")
    set(CMAKE_FIND_ROOT_PATH "${LLVM_MINGW}/${_triple}")
else()
    set(_bin "")   # tools resolved from PATH
endif()

set(CMAKE_C_COMPILER   "${_bin}${_triple}-clang")
set(CMAKE_CXX_COMPILER "${_bin}${_triple}-clang++")
set(CMAKE_RC_COMPILER  "${_bin}${_triple}-windres")

# Find host programs (javac, java, jar) on the host; libraries/headers in the target sysroot.
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY)
