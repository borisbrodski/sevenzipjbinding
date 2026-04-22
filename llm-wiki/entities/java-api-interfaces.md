---
title: Java API Interfaces
created: 2026-04-22
updated: 2026-04-22
type: entity
tags: [java-api, components, design-pattern]
sources: [jbinding-java/src/net/sf/sevenzipjbinding/ArchiveFormat.java, jbinding-java/src/net/sf/sevenzipjbinding/IInArchive.java, jbinding-java/src/net/sf/sevenzipjbinding/IOutArchive.java]
---

# Java API Interfaces

## Overview

7-Zip-JBinding exposes 7-Zip functionality through a set of Java interfaces that follow the original 7-Zip C++ architecture. The API is divided into three categories:

1. **Core interfaces** - Archive operations (open, extract, create)
2. **Stream interfaces** - Data I/O abstraction
3. **Callback interfaces** - Progress, password, and event notifications

## Archive Format Support

`ArchiveFormat.java` (422 lines) defines all supported formats:

| Format | Extraction | Compression | Enum Value |
|--------|------------|-------------|------------|
| 7z | ✓ | ✓ | `SEVEN_ZIP` |
| ZIP | ✓ | ✓ | `ZIP` |
| TAR | ✓ | ✓ | `TAR` |
| GZip | ✓ | ✓ | `GZIP` |
| BZip2 | ✓ | ✓ | `BZIP2` |
| RAR | ✓ | ✗ | `RAR` |
| RAR5 | ✓ | ✗ | `RAR5` |
| ARJ | ✓ | ✗ | `ARJ` |
| CAB | ✓ | ✗ | `CAB` |
| CHM | ✓ | ✗ | `CHM` |
| CPIO | ✓ | ✗ | `CPIO` |
| ISO | ✓ | ✗ | `ISO` |
| LZH | ✓ | ✗ | `LZH` |
| LZMA | ✓ | ✗ | `LZMA` |
| NSIS | ✓ | ✗ | `NSIS` |
| RPM | ✓ | ✗ | `RPM` |
| UDF | ✓ | ✗ | `UDF` |
| WIM | ✓ | ✗ | `WIM` |
| XAR | ✓ | ✗ | `XAR` |
| Z | ✓ | ✗ | `Z` |
| FAT | ✓ | ✗ | `FAT` |
| NTFS | ✓ | ✗ | `NTFS` |

**Key Methods:**
- `isOutArchiveSupported()` - Returns `true` if compression is supported
- `getOutArchiveImplementation()` - Returns the implementation class
- `supportMultipleFiles()` - Returns `true` if format supports multi-file archives

## Core Interfaces

### IInArchive (Extraction)

Main interface for extracting from archives. Key methods:

```java
int getNumberOfItems();                          // Get item count
PropInfo getProperty(int index, PropID propID);  // Get item properties
void extract(int[] indices, boolean testMode, IArchiveExtractCallback callback);
```

**Properties (PropID):**
- `PATH` - File path within archive
- `SIZE` - Uncompressed size
- `PACKEDSIZE` - Compressed size
- `CTIME` / `MTIME` / `ATIME` - Timestamps
- `ISDIR` - Is directory
- `ENCODEDMETHOD` - Name encoding method
- `COMMENT` - Archive comment

### IOutArchive (Creation)

Interface for creating archives. Format-specific implementations:

- `IOutCreateArchive7z` - 7z format with encryption
- `IOutCreateArchiveZip` - ZIP format
- `IOutCreateArchiveTar` - TAR format
- `IOutCreateArchiveGZip` - GZip format
- `IOutCreateArchiveBZip2` - BZip2 format

**Configuration methods:**
- `setLevel(int)` - Compression level (0-9)
- `setEncryptHeaders(boolean)` - Encrypt archive headers (7z only)
- `setMultithreading(boolean)` - Enable multi-threading

### IOutArchiveBase

Base interface for all output archives with common functionality:
- Update operations
- Property setting
- Callback registration

## Stream Interfaces

### Input Streams

| Interface | Description | Use Case |
|-----------|-------------|----------|
| `ISequentialInStream` | Sequential read-only | Simple extraction |
| `IInStream` | Seekable input | Random access, volumes |
| `ISeekableStream` | Extended seekable | Advanced scenarios |

### Output Streams

| Interface | Description | Use Case |
|-----------|-------------|----------|
| `ISequentialOutStream` | Sequential write-only | Simple creation |
| `IOutStream` | Seekable output | Random access writes |

**Implementation note:** The library provides built-in implementations:
- `ByteArrayStream` - In-memory byte array stream
- `RandomAccessFileInStream` / `OutStream` - File-based streams
- `SequentialInStreamImpl` - Wrapper for Java InputStream

## Callback Interfaces

### IArchiveOpenCallback

Called during archive opening:

```java
void setTotal(long files, long bytes);    // Total to process
void setCompleted(long files, long bytes); // Progress update
```

### IArchiveExtractCallback

Called during extraction:

```java
void setTotal(long total);                           // Total items
void setCompleted(long completeValue);               // Progress
void getStream(int index, ISequentialOutStream[] outStream);
void prepareOperation(ExtractAskMode askMode);
void setOperationResult(ExtractOperationResult result);
```

**ExtractAskMode values:**
- `EXTRACT` - Extract file
- `TEST` - Test without extracting
- `SKIP` - Skip file

**ExtractOperationResult values:**
- `OK`, `DATA_ERROR`, `CRC_ERROR`, `UNSUPPORTED`, `HEADERS_ERROR`, etc.

### ICryptoGetTextPassword

Password callback for encrypted archives:

```java
String cryptoGetTextPassword();  // Return password
```

### IProgress

General progress callback:

```java
void setTotal(long total);
void setCompleted(long completeValue);
```

## Implementation Pattern

```java
// Open archive
SevenZip.initSevenZipFromPlatformJAR();
IInArchive archive = SevenZip.openInArchive(ArchiveFormat.ZIP, inStream);

// Extract with callback
IArchiveExtractCallback callback = new IArchiveExtractCallback() {
    public void getStream(int index, ISequentialOutStream[] outStream) {
        // Return output stream for this file
    }
    // ... other methods
};

archive.extract(null, false, callback);
archive.close();
```

## Simple API

For common operations, use the simplified API in `simple/` package:

- `SimpleInArchive` - Simplified extraction
- `SimpleOutArchive` - Simplified creation

Reduces boilerplate for typical use cases.

## Cross-References

- [[7-zip-jbinding-overview]] - Project overview
- [[jni-interface-architecture]] - JNI layer details
- [[error-handling]] - Exception handling
- [[known-issues-todos]] - Known issues
