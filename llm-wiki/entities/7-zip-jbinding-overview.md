---
title: 7-Zip-JBinding Overview
created: 2026-04-22
updated: 2026-04-22
type: entity
tags: [architecture, jni, java-api, 7zip-core]
sources: [README, CMakeLists.txt, jbinding-java/src/net/sf/sevenzipjbinding/SevenZip.java]
---

# 7-Zip-JBinding

## What It Is

7-Zip-JBinding is a free cross-platform Java JNI binding for the 7-Zip/p7zip compression library. It provides full Java API access to 7-Zip's compression and decompression capabilities.

**Version:** 16.02-2.01 (based on 7-Zip 16.02)

**License:** GNU LGPL 2.1+

**Author:** Boris Brodski

## Architecture Overview

The project consists of three main layers:

1. **Java API Layer** (`jbinding-java/`) - Pure Java interfaces and implementation classes
2. **JNI Bridge Layer** (`jbinding-cpp/`) - C++ code that bridges Java and C++
3. **7-Zip Core** (`7zip/` and `p7zip/`) - Original 7-Zip C++ source code (read-only)

## Key Components

### Java API (jbinding-java/src)

**Main Entry Point:**
- `SevenZip.java` - Main class for initialization, opening archives, and creating archives (1097 lines)

**Core Interfaces:**
- `IInArchive` - Interface for extracting from archives
- `IOutArchive` - Interface for creating archives
- `IArchiveOpenCallback` - Callback for archive open operations
- `IArchiveExtractCallback` - Callback for extraction operations
- `IOutCreateCallback` - Callback for creation operations
- `ISequentialInStream` / `ISequentialOutStream` - Stream interfaces
- `IInStream` / `IOutStream` - Seekable stream interfaces

**Archive Formats Supported:**
- 7z, ZIP, GZip, BZip2, TAR, ARJ, CAB, CHM, CPIO, CramFS, DEB, DMG, FAT, HFS, HFSX, ISO, LZH, LZMA, MBR, MSI, NSIS, NTFS, RAR, RAR5, RPM, SquashFS, UDF, VHD, WIM, XAR, XZ

**Sub-packages:**
- `impl/` - Internal implementation classes (15 files)
- `simple/` - Simplified API for common operations (3 files)
- `util/` - Utility classes (1 file)

### JNI Bridge (jbinding-cpp/)

**Structure:**
- `BaseSystem.h` - Base system definitions
- `JavaToCPP/` - Java-to-C++ direction (6 files)
- `CPPToJava/` - C++-to-Java direction (multiple callback wrappers)
- `Debug.cpp` - Debug utilities
- `JavaStaticInfo.cpp` - Static Java information

**Key Files:**
- `JavaToCPPSevenZip.cpp` - Main JNI entry points
- `JavaToCPPInArchiveImpl.cpp` - Archive opening JNI
- `JavaToCPPOutArchiveImpl.cpp` - Archive creation JNI
- `CPPToJavaArchiveExtractCallback.cpp` - Extraction callbacks
- `CPPToJavaOutStream.cpp` / `CPPToJavaInStream.cpp` - Stream wrappers

### Build System

**Main Build File:** `CMakeLists.txt` (433 lines)

**Build Process:**
1. Compiles 7-Zip C++ core (both 7zip/ and p7zip/)
2. Compiles JNI bridge layer
3. Packages into Java JAR files
4. Creates platform-specific distributions

## Initialization Flow

1. Load platform JAR (`sevenzipjbinding-<Platform>.jar`)
2. Read platform properties (`sevenzipjbinding-platforms.properties`)
3. Choose best matching platform
4. Extract native libraries to temp directory
5. Verify library integrity via SHA-1 hashes
6. Load native libraries via `System.load()`
7. Call native initialization function

See [[native-library-initialization]] for detailed flow.

## Test Suite

**Location:** `test/` directory

**Structure:**
- `test/JavaTests/` - JUnit test suite (282 Java files)
- `test/sevenzipjbinding-maven-test/` - Maven integration tests

**Test Categories:**
- Common tests (268 tests)
- Tools tests (1590 tests)
- Snippets tests (27 tests)
- Encoding tests (UTF-8, CP1252, CP1251)
- Bug report tests (31 tests)
- Single file extraction (3056 tests)
- Multiple file extraction (1170 tests)
- Compression tests (2312 tests)
- Bad archive tests (50 tests)

**Total:** 7,510 tests (100% pass rate verified)

See [[test-framework]] for details.

## Cross-References

- [[jni-interface-architecture]] - JNI layer details
- [[archive-format-support]] - Supported formats
- [[native-library-initialization]] - Initialization process
- [[test-framework]] - Testing infrastructure
- [[known-issues-todos]] - Known bugs and TODOs

## Related Documentation

- [[build-system]] - Build configuration
- [[memory-management]] - JNI memory handling
- [[error-handling]] - Exception handling patterns
