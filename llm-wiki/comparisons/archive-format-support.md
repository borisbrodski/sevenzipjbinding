---
title: Archive Format Support Gap Analysis
tags:
  - components:archive-formats
  - architecture:comparison
  - quality:coverage-gap
description: Comparison of archive formats supported by 7-Zip C++ engine vs 7-Zip-JBinding Java bindings
created: 2026-04-22
---

# Archive Format Support Gap Analysis

This page documents the archive formats that are supported by the 7-Zip C++ engine for **extraction only**, but are **not yet implemented** in 7-Zip-JBinding.

## Summary

|| Category | Count |
||----------|-------||
|| Formats supported by 7-Zip C++ (extraction) | 47 |
|| Formats supported by 7-Zip-JBinding (extraction) | 23 |
|| **Missing formats (extraction-only gap)** | **23** |

## Missing Archive Formats (Extraction Only)

The following 24 archive formats are supported by the 7-Zip C++ engine but are **not available** in 7-Zip-JBinding:

### Disk Image / File System Formats

| Format | Description | C++ Handler File |
|--------|-------------|------------------|
| **APM** | Apple Partition Map | `ApmHandler.cpp` |
| **CramFS** | Compressed ROM File System | `CramfsHandler.cpp` |
| **Dmg** | Apple Disk Image | `DmgHandler.cpp` |
| **Ext** | Linux Extended File System | `ExtHandler.cpp` |
| **FAT** | FAT File System | `FatHandler.cpp` *(Note: Supported in Java)* |
| **GPT** | GUID Partition Table | `GptHandler.cpp` |
| **HFS** | Hierarchical File System | `HfsHandler.cpp` *(Note: Supported in Java)* |
| **MBR** | Master Boot Record | `MbrHandler.cpp` |
| **NTFS** | NT File System | `NtfsHandler.cpp` *(Note: Supported in Java)* |
| **SquashFS** | Compressed Read-Only File System | `SquashfsHandler.cpp` |
| **UDF** | Universal Disk Format | `Udf/UdfHandler.cpp` *(Note: Supported in Java)* |

### Executable / Binary Formats

| Format | Description | C++ Handler File |
|--------|-------------|------------------|
| **ELF** | Executable and Linkable Format | `ElfHandler.cpp` |
| **MachO** | Mach-O (Mac OS X executable) | `MachoHandler.cpp` |
| **PE** | Portable Executable (Windows) | `PeHandler.cpp` |

### Virtual Disk / Disk Image Formats

| Format | Description | C++ Handler File |
|--------|-------------|------------------|
| **QCOW** | QEMU Copy-On-Write | `QcowHandler.cpp` |
| **VDI** | VirtualBox Disk Image | `VdiHandler.cpp` |
| **VHD** | Virtual Hard Disk (Microsoft) | `VhdHandler.cpp` |
| **VMDK** | VMware Disk Image | `VmdkHandler.cpp` |

### Compression-Only Formats

|| Format | Description | C++ Handler File |
||--------|-------------|------------------|
|| **Ppmd** | PPMd Compression | `PpmdHandler.cpp` |

### Legacy / Niche Archive Formats

| Format | Description | C++ Handler File |
|--------|-------------|------------------|
| **Compound** | OLE Compound Document (MS Office) | `ComHandler.cpp` |
| **FLV** | Flash Video | `FlvHandler.cpp` |
| **IHex** | Intel HEX (firmware) | `IhexHandler.cpp` |
| **MsLZ** | Microsoft LZ Compression | `MslzHandler.cpp` |
| **Mub** | Multi-architecture Binary | `MubHandler.cpp` |
| **Split** | Split archive files | `SplitHandler.cpp` *(Note: Listed in Java, marked TODO)* |
| **SWF** | Shockwave Flash | `SwfHandler.cpp` |
| **SWFc** | Compressed Shockwave Flash | `SwfHandler.cpp` |
| **TE** | Terse Executable | (Part of PE handler) |

## Already Supported by 7-Zip-JBinding

The following 22 formats are **already implemented** in 7-Zip-JBinding for extraction:

| Format | Java Enum | Notes |
|--------|-----------|-------|
| 7z | `SEVEN_ZIP` | Full read/write support |
| Ar | `AR` | Unix archive (ar, a, deb, lib) |
| Arj | `ARJ` | Extraction only |
| BZip2 | `BZIP2` | Read/write support |
| Cab | `CAB` | Microsoft Cabinet |
| Chm | `CHM` | Microsoft Compiled HTML Help |
| Cpio | `CPIO` | Unix cpio archive |
| FAT | `FAT` | FAT file system |
| GZip | `GZIP` | Read/write support |
| HFS | `HFS` | Hierarchical File System |
| Iso | `ISO` | CD/DVD image |
| Lzh | `LZH` | LHA/LZH archive |
| Lzma | `LZMA` | LZMA compressed data |
| NTFS | `NTFS` | NTFS file system |
| Nsis | `NSIS` | Nullsoft Scriptable Install System |
| Rar | `RAR` | RAR archive (v1-4) |
| Rar5 | `RAR5` | RAR archive (v5) |
| Rpm | `RPM` | Red Hat Package Manager |
| Split | `SPLIT` | Split archives *(marked TODO: Test it)* |
| Tar | `TAR` | Unix tar archive |
| Udf | `UDF` | Universal Disk Format |
|| Wim | `WIM` | Windows Imaging Format |
|| Xar | `XAR` | Extensible Archive Format |
|| XZ | `XZ` | XZ compressed data (stream archiver) |
|| Z | `Z` | Unix compress (LZW) |
| Zip | `ZIP` | ZIP archive |

## Source Files

### 7-Zip C++ Engine
- Location: `/home/agent/.hermes/hermes-agent/sevenzipjbinding/7zip/CPP/7zip/Archive/`
- Format registration: `RegisterArc.h`, `ArchiveExports.cpp`
- Total handler files: 48

### 7-Zip-JBinding Java API
- Location: `/home/agent/.hermes/hermes-agent/sevenzipjbinding/jbinding-java/src/net/sf/sevenzipjbinding/ArchiveFormat.java`
- Total formats in enum: 32 (22 with extraction support)

## Implementation Gap Analysis

### Priority Recommendations

1. **High Priority** (Most commonly used):
   - `xz` - Widely used for Linux distributions and source archives
   - `Compound` - MS Office document format (doc, xls, ppt before Office 2007)
   - `PE` - Windows executable analysis

2. **Medium Priority** (Disk images):
   - `Dmg` - Apple disk images
   - `VHD`, `VMDK`, `VDI` - Virtual machine disk formats
   - `QCOW` - QEMU/KVM disk images

3. **Low Priority** (Niche/Legacy):
   - `ELF`, `MachO` - Binary format analysis
   - `FLV`, `SWF` - Multimedia formats
   - `IHex`, `MsLZ`, `TE` - Firmware/legacy formats

## Related Pages

- [[7-Zip-JBinding Overview]]
- [[Java API Interfaces]]
- [[Known Issues and TODOs]]
- [[JNI Interface Architecture]]

## Notes

- This analysis is based on the master branch of both 7-Zip and 7-Zip-JBinding
- Source files are immutable; format support gaps must be addressed by adding new Java API bindings
- Some formats marked as "supported" in Java may have incomplete testing (e.g., `SPLIT`)
- The 7-Zip C++ engine supports 47 formats, while 7-Zip-JBinding implements 22 extraction formats
