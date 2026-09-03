---
title: Build System
created: 2026-04-22
updated: 2026-04-22
type: entity
tags: [build-system, components, architecture]
sources: [CMakeLists.txt, cmake/FindJavaExtended.cmake]
---

# Build System

## Overview

7-Zip-JBinding uses CMake as its primary build system, supporting cross-platform compilation on Linux, Windows (MinGW), and macOS. The build produces Java JAR files containing both Java bytecode and native JNI libraries.

**Version:** 16.02-2.01

**Minimum CMake:** 2.8.11

## Build Configuration

### Platform Detection

```cmake
SET(PLATFORM ${JAVA_SYSTEM}-${JAVA_ARCH})  # e.g., "Linux-x86_64"
```

**Supported platforms:**
- Linux (x86, x86_64, aarch64)
- Windows (MinGW 32/64-bit)
- macOS (universal builds with `-arch` flags)

### Build Options

| Option | Default | Description |
|--------|---------|-------------|
| `CMAKE_BUILD_TYPE` | Release | Release or Debug |
| `STATIC_BUILD` | TRUE | Statically link runtime library |
| `MINGW32` | No | Use MinGW 32-bit toolchain |
| `MINGW64` | No | Use MinGW 64-bit toolchain |
| `RUNTIME_LIB` | - | Runtime library to package |
| `JAVA_TMP` | - | Temporary directory for Java |
| `JAVA_PARAMS` | - | Additional Java parameters |

### Platform-Specific Handling

**macOS:**
```cmake
IF("${CMAKE_SYSTEM_NAME}" STREQUAL "Darwin")
    SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -arch ${JAVA_ARCH}")
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -arch ${JAVA_ARCH}")
ENDIF()
```

**Windows:**
```cmake
IF(CMAKE_SYSTEM_NAME MATCHES "Windows.*")
    SET(WINDOWS Yes)
ENDIF()
IF(WINDOWS AND NOT MINGW)
    MESSAGE(FATAL_ERROR "On Windows only MinGW build is supported")
ENDIF()
```

## Build Artifacts

### Java Components

1. **Core JAR** (`sevenzipjbinding.jar`)
   - Location: `jbinding-java/sevenzipjbinding.jar`
   - Contains: Java API classes (`jbinding-java/src/`)
   - Compiled with: Java 1.5 (`-source 1.5 -target 1.5`)

2. **Platform JAR** (`sevenzipjbinding-<Platform>.jar`)
   - Location: `<Platform>/` directory
   - Contains: Native library + platform properties
   - Includes: `sevenzipjbinding-lib.properties` with SHA-1 hashes

### Native Library

- **Name:** `7z.dll` (Windows) or `lib7z.so` (Linux/macOS)
- **Location:** `jbinding-cpp/<platform>/`
- **Dependencies:** 7-Zip core (statically linked by default)

### Property Files

**`sevenzipjbinding-lib.properties`:**
```properties
build.ref=<random-12-char-id>
lib.1.name=7z.dll
lib.1.hash=<sha1-hash>
```

**`sevenzipjbinding-platforms.properties`:**
```properties
platform.1=Linux-x86_64
```

## Build Process

### Step 1: Java Compilation

```cmake
CREATE_COMPILE_JAVA_CUSTOM_COMMAND(
    core                    # Build name
    "${JAVA_SOURCE_DIR}"    # Source directory
    "${SEVENZIPJBINDING_MANIFEST}"  # Manifest
    "${SEVENZIPJBINDING_JAR}"       # Output JAR
)
```

**Compilation flags:**
- `-source 1.5 -target 1.5` - Java 1.5 compatibility
- `-encoding UTF-8` - UTF-8 source encoding
- Classpath includes: JUnit, Hamcrest (for tests)

### Step 2: C++ Compilation

```cmake
ADD_SUBDIRECTORY(jbinding-cpp)
```

The `jbinding-cpp/CMakeLists.txt` compiles:
1. 7-Zip core sources (`7zip/`, `p7zip/`)
2. JNI bridge layer (`jbinding-cpp/`)
3. Links into native library

### Step 3: Platform JAR Creation

```cmake
ADD_CUSTOM_COMMAND(
    OUTPUT ${SEVENZIPJBINDING_LIB_JAR}
    COMMAND ${CMAKE_COMMAND} -E copy <native-lib> ${PLATFORM}
    COMMAND ${JAVA_ARCHIVE} cfm <platform-jar> <manifest> -C ${PLATFORM} .
    COMMAND ${JAVA_ARCHIVE} uf <platform-jar> <platform-props>
)
```

### Step 4: Hash Calculation

```cmake
FILE(SHA1 "${native-lib}" HASH1)
FILE(WRITE <lib-properties> "build.ref=${BUILD_REF}\n\n")
FILE(APPEND <lib-properties> "lib.1.name=7z.dll\n")
FILE(APPEND <lib-properties> "lib.1.hash=${HASH1}\n")
```

**Purpose:** Integrity verification during initialization.

## Distribution Packaging

### CPack Configuration

```cmake
SET(CPACK_GENERATOR ZIP)
SET(CPACK_SOURCE_GENERATOR ZIP)
SET(CPACK_PACKAGE_FILE_NAME ${SEVENZIPJBINDING_FILENAME})
```

**Distribution includes:**
- Binary JARs (core + platform)
- Source code (Java + C++)
- Javadoc
- Website (HTML documentation)
- License files (AUTHORS, COPYING, README, etc.)

### Pre-package Steps

1. Generate Javadoc
2. Build HTML documentation
3. Create source archives (java-src.zip, cpp-src.zip)
4. Package javadoc.zip
5. Package website.zip

## Testing

### Test Configuration

```cmake
SET(DART_TESTING_TIMEOUT "36000")  # 10 hours
INCLUDE(CTest)
```

### Test Suite Structure

18 test suites defined in `CMakeLists.txt`:

| Test | Description |
|------|-------------|
| JUnit-common | Core functionality (268 tests) |
| JUnit-common-no-privileged-init | Without privileged initialization |
| JUnit-init-std-1/2 | Standard initialization phases |
| JUnit-init-verify-1/2 | Initialization verification |
| JUnit-tools | Tool tests (1590 tests) |
| JUnit-snippets | Code snippet tests (27 tests) |
| JUnit-encoding-utf-8/cp1252/cp1251 | Encoding tests |
| JUnit-bug-reports | Bug report regression (31 tests) |
| JUnit-single-file-extraction | Single file extraction (3056 tests) |
| JUnit-multiple-files-extraction | Multi-file extraction (1170 tests) |
| JUnit-compression | Compression tests (2312 tests) |
| JUnit-badarchive | Bad archive handling (50 tests) |
| JUnit-misc | Miscellaneous tests (3 tests) |

**Test execution:**
```bash
make           # Build
ctest          # Run tests (up to 90 minutes)
make package   # Create distribution
```

## Known Issues

1. **Policy CMP0026:** TODO comment about target location access (line 158)
2. **Version sync:** Version must be updated in both `CMakeLists.txt` and `SevenZip.java`
3. **Mac OS arch flags:** TODO to switch to `JAVA_SYSTEM` variable (line 53)

## Cross-References

- [[7-zip-jbinding-overview]] - Project overview
- [[jni-interface-architecture]] - JNI layer
- [[test-framework]] - Testing infrastructure
- [[known-issues-todos]] - Known issues
