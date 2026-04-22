---
title: Test Framework
created: 2026-04-22
updated: 2026-04-22
type: entity
tags: [quality, testing, components]
sources: [test/JavaTests/src/net/sf/sevenzipjbinding/junit/AllTestSuite.java, CMakeLists.txt]
---

# Test Framework

## Overview

7-Zip-JBinding uses a comprehensive JUnit-based test suite to verify all functionality. The test suite is integrated with CMake's CTest framework and runs automatically during the build process.

**Test Count:** 7,510 individual tests
**Test Suites:** 18 suites
**Pass Rate:** 100% (verified)
**Execution Time:** ~9.5 minutes (569 seconds)

## Test Suite Structure

### Main Test Suite

**File:** `test/JavaTests/src/net/sf/sevenzipjbinding/junit/AllTestSuite.java` (533 lines)

The `AllTestSuite` class organizes all tests into logical groups using static arrays and a TreeMap for test discovery.

### Test Categories

| Category | Test Count | Description |
|----------|------------|-------------|
| Common tests | 8 | Core functionality, initialization, version |
| Init tests (Std) | 1 | Standard initialization verification |
| Init tests (Verify) | 1 | Initialization artifact verification |
| Tools tests | 32 | Utility classes, JNI tools, streams |
| Snippets tests | 17 | Code snippet examples, basic usage |
| Encoding tests | 1 | Unicode filename support |
| Bug report tests | 6 | Regression tests for reported bugs |
| Single file tests | 50 | Single-file extraction for all formats |
| Multiple files tests | 36 | Multi-file extraction for all formats |
| Bad archive tests | 1 | Garbage archive handling |
| Compression tests | 68 | Compression, update, level settings |
| Misc tests | 1 | Miscellaneous functionality |

## Test Categories in Detail

### 1. Common Tests

**Purpose:** Verify core library functionality.

**Tests:**
- `ArchiveFormatTest` - Archive format enum verification
- `CHeadCacheInStreamTest` - Header caching stream
- `DeclareThrowsSevenZipExceptionTest` - Exception declarations
- `ExceptionHandlingTest` - JNI exception handling
- `HeadCacheOnAutodetectionTest` - Performance optimization
- `JUnitInitializationTest` - JUnit setup verification
- `TestBaseTest` - Test base class functionality
- `VersionTest` - Version information

### 2. Initialization Tests

**Standard Initialization:**
- `StandardInitializationTest` - Verifies standard initialization flow

**Verification:**
- `InitializationDoesNotVerifyArtifactsTest` - Tests artifact verification behavior

**Note:** Tests run in two phases (init-std-1, init-std-2) to verify multi-stage initialization.

### 3. Tools Tests

**Purpose:** Test utility classes and JNI infrastructure.

**Tests:**
- `ByteArrayStreamTest` (9 variants) - In-memory stream with various buffer sizes
- `EnumTest` - Enum serialization
- `JBindingTest` - JNI binding infrastructure
- `JNIToolsTest` - JNI utility functions
- `ParamSpecTest` - Parameter specification
- `VolumedArchiveInStreamTest` (12 variants) - Multi-volume archive handling
- `TypeVariableResolverTest` - Type resolution

### 4. Snippets Tests

**Purpose:** Verify example code snippets work correctly.

**Tests:**
- `GetNumberOfItemInArchive` - Count archive items
- `ExtractItemsTest` - Extract files
- `FirstStepsSimpleSnippets` - Basic usage examples
- `ListItemsTest` - List archive contents
- `OpenMultipartArchive7zTest` - Multi-part 7z archives
- `OpenMultipartArchiveRarTest` - Multi-part RAR archives
- `PrintCountOfItemsTest` - Print item count
- `SevenZipJBindingInitCheckTest` - Initialization check
- `CompressMessageTest` - Compression with messages
- `CompressNonGeneric*Test` - Non-generic compression (7z, BZip2, GZip, Tar, Zip)
- `CompressGenericTest` - Generic compression
- `CompressWithErrorTest` - Error handling
- `CompressWithPasswordTest` - Password-protected archives
- `Update*Test` - Archive update operations (add, remove, alter)

### 5. Encoding Tests

**Purpose:** Verify Unicode and character encoding support.

**Tests:**
- `UnicodeFilenamesInArchive` - Unicode filename extraction

**Encoding variants tested:**
- UTF-8
- CP1252 (Windows Western)
- CP1251 (Windows Cyrillic)

### 6. Bug Report Tests

**Purpose:** Regression tests for specific bug reports.

**Tests:**
- `RarPasswordToLongCrash` - RAR password length crash
- `WrongCRCGetterInSimpleInterface` - CRC calculation error
- `Ticket18NullAsPassword` - Null password handling
- `SevenZipInTar` - 7z compression in TAR
- `CallMethodsOnClosedInStreamTest` - Closed stream access
- `CallMethodsOnClosedOutStreamTest` - Closed stream access
- `OpenMultipartCabWithNonVolumedCallbackTest` - CAB volume handling

### 7. Single File Extraction Tests

**Purpose:** Extract single files from all supported archive formats.

**Formats tested (50 tests):**
- ARJ, BZip2, CAB, CHM, CPIO, DEB, FAT, GZip, ISO, LZH, LZMA
- NSIS (solid, standard), NTFS
- RAR, RAR5 (with/without headers, passwords, volumes)
- RPM, 7z (with/without headers, passwords, volumes)
- TAR, UDF, VHD, WIM, XAR, Z, ZIP

**Variants:**
- Standard extraction
- Header password protection
- Full password protection
- Callback-based password
- Multi-volume archives

### 8. Multiple Files Extraction Tests

**Purpose:** Extract multiple files from archives.

**Formats tested (36 tests):**
- Same formats as single-file tests
- Focus on multi-file handling
- Volume support verification

### 9. Bad Archive Tests

**Purpose:** Verify graceful handling of corrupted archives.

**Tests:**
- `GarbageArchiveFileTest` - Extract from invalid/garbage data

### 10. Compression Tests

**Purpose:** Test archive creation and update functionality.

**Categories:**

**Exception handling:**
- `CompressExceptionGetItemInformationTest`
- `CompressExceptionGetConnectedArchiveTest`

**Standalone compression:**
- `StandaloneCompress*Test` - ZIP, BZip2, GZip, 7z, TAR

**Standalone update:**
- `StandaloneUpdateArchive*Test` - Add, remove, update content, update properties
- `StandaloneUpdateNonGeneric*Test` - Non-generic update (7z, TAR, Zip, BZip2, GZip)

**Feature tests:**
- `CompressFeatureSetLevel` - Compression level
- `CompressFeatureSetLevelRatioImpact` - Level vs ratio
- `CompressFeatureSetSolid` - Solid archive
- `CompressFeatureSetThreadCount` - Multi-threading

**Generic compression:**
- Single file: 7z, BZip2, GZip, TAR, ZIP
- Multiple files: 7z, TAR, ZIP

**Non-generic compression:**
- Single file: 7z, BZip2, GZip, TAR, ZIP

**Update operations:**
- Multiple files: 7z, TAR, ZIP (generic and non-generic)
- Single file: Generic and non-generic (7z, BZip2, GZip, TAR, ZIP)

**Miscellaneous:**
- `TraceCompressionTest` - Compression tracing

## CMake Integration

### Test Configuration

```cmake
SET(DART_TESTING_TIMEOUT "36000")  # 10 hours
INCLUDE(CTest)
```

### Test Execution

```bash
ctest  # Run all tests
```

**Timeout:** Up to 90 minutes on slow CPUs

### Test Runner

Tests are executed via `JUnitRunner.cmake`:

```cmake
java -Xmx512m \
  -cp "junit:hamcrest:tests.jar:sevenzipjbinding.jar:platform.jar" \
  -DSINGLEBUNDLE="<test-suite>" \
  -Djava.io.tmpdir=<tmpdir> \
  org.junit.runner.JUnitCore net.sf.sevenzipjbinding.junit.AllTestSuite
```

### Test Suites Defined in CMake

| CMake Test | Description |
|------------|-------------|
| JUnit-common | Common tests |
| JUnit-common-no-privileged-init | Without privileged initialization |
| JUnit-init-std-1/2 | Standard initialization phases |
| JUnit-init-verify-1/2 | Initialization verification |
| JUnit-tools | Tools tests |
| JUnit-snippets | Snippets tests |
| JUnit-snippets-no-privileged-init | Without privileged init |
| JUnit-encoding-utf-8/cp1252/cp1251 | Encoding tests |
| JUnit-bug-reports | Bug report tests |
| JUnit-single-file-extraction | Single file tests |
| JUnit-multiple-files-extraction | Multiple file tests |
| JUnit-compression | Compression tests |
| JUnit-badarchive | Bad archive tests |
| JUnit-misc | Misc tests |

## Test Infrastructure

### Dependencies

- **JUnit 4.11** - Testing framework
- **Hamcrest 1.3** - Matcher library

### Test Base Class

All tests extend a common base class that provides:
- Archive creation utilities
- Temporary file management
- Stream helpers
- Assertion helpers

### Configuration

Tests use system properties for configuration:

```java
-Dsevenzip.no_doprivileged_initialization=1  // Skip privileged init
-Dfile.encoding=UTF8                          // Character encoding
-Dskip-debug-mode-tests=true                  // Skip debug-only tests
-Dsevenziptest.standard_initialization_test_phase=1  // Init phase
```

## Test Data

Test archives are stored in the test resources directory and include:
- Sample files for each supported format
- Password-protected archives
- Multi-volume archives
- Corrupted/garbage archives
- Unicode filename archives

## Coverage

### Formats Covered

All 22+ archive formats are tested for extraction:
- 7z, ZIP, TAR, GZip, BZip2 (compression + extraction)
- RAR, RAR5, ARJ, CAB, CHM, CPIO, ISO, LZH, LZMA (extraction only)
- NSIS, RPM, UDF, WIM, XAR, Z, FAT, NTFS (extraction only)

### Operations Covered

- Archive opening (with/without password)
- File extraction (single/multiple)
- Archive creation (all supported formats)
- Archive updating (add, remove, modify)
- Progress callbacks
- Password callbacks
- Volume handling

## Known Test Issues

1. **OpenMultipartCabWithNonVolumedCallbackTest** - TODO: Extract to special group (line 350)
2. **Debug mode tests** - Skipped in release builds via `skip-debug-mode-tests` property

## Cross-References

- [[7-zip-jbinding-overview]] - Project overview
- [[build-system#testing]] - Build system testing
- [[known-issues-todos]] - Test-related TODOs
- [[java-api-interfaces]] - API being tested
