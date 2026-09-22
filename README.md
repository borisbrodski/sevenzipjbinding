<div align="center">

# 7‑Zip‑JBinding

**The real 7‑Zip engine, bound to Java.**
Extract and create 20+ archive formats with native performance — one small library, every platform, no external tools.

[![Maven Central](https://img.shields.io/maven-central/v/net.sf.sevenzipjbinding/sevenzipjbinding?label=Maven%20Central)](https://central.sonatype.com/artifact/net.sf.sevenzipjbinding/sevenzipjbinding)
[![License: LGPL v2.1](https://img.shields.io/badge/License-LGPL%20v2.1-blue.svg)](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](#requirements)
[![7-Zip engine](https://img.shields.io/badge/7--Zip%20engine-23.01-1f8ceb.svg)](https://www.7-zip.org/)
[![macOS build](https://github.com/borisbrodski/sevenzipjbinding/actions/workflows/macos.yml/badge.svg)](https://github.com/borisbrodski/sevenzipjbinding/actions/workflows/macos.yml)

[**Website**](https://sevenzipjbind.sourceforge.net/) ·
[**Quick start**](#quick-start) ·
[**Snippets**](https://sevenzipjbind.sourceforge.net/snippets/) ·
[**JavaDoc**](https://sevenzipjbind.sourceforge.net/javadoc/) ·
[**Download**](https://sourceforge.net/projects/sevenzipjbind/files/7-Zip-JBinding/) ·
[**Issues**](https://github.com/borisbrodski/sevenzipjbinding/issues) ·
[**Sponsor** ❤](https://github.com/sponsors/borisbrodski)

</div>

---

7‑Zip‑JBinding is a free, cross‑platform Java binding for the [7‑Zip](https://www.7-zip.org/) /
[p7zip](https://p7zip.sourceforge.net/) archive engine. The unmodified 7‑Zip C/C++ engine runs
**in‑process** through JNI — no child processes, no external `7z` binary — and all I/O is
**callback‑driven**, so you can read and write archives entirely in memory, stream from any source,
and abort at any time.

## ✨ Highlights

- **20+ formats to extract, 6 to create/update** — see the [formats table](#-supported-formats).
- **Native performance, in‑process** — the genuine 7‑Zip 23.01 engine via JNI.
- **Callback‑driven, streaming I/O** — fully in‑memory round‑trips, partial extraction from huge
  archives, extract‑while‑downloading a remote archive, and abortable operations. No temp files
  unless you want them.
- **One jar, every platform** — Windows (x86, x64 **and** ARM64), Linux (glibc **and** musl/Alpine,
  x86 **and** ARM v5–v8), macOS (Intel + Apple Silicon). The `-all-platforms` jar **auto‑detects** the
  OS, architecture, ARM level, and glibc‑vs‑musl at runtime.
- **Two APIs** — a beginner‑friendly *simple* interface and a full‑power *standard* interface.
- **Encryption** — open and create password‑protected archives, including 7z encrypted headers.
- **Battle‑tested** — maintained since 2007, exercised by ~8800 JUnit tests on every supported
  platform (real VMs and real ARM hardware).

## 📦 Supported formats

| Format | Extract | Create / Update |
|---|:---:|:---:|
| **7z** | ✅ | ✅ |
| **ZIP** | ✅ | ✅ |
| **TAR** | ✅ | ✅ |
| **GZip** | ✅ | ✅ |
| **BZip2** | ✅ | ✅ |
| **XZ** | ✅ | ✅ |
| **RAR / RAR5** | ✅ | — |
| ARJ · CAB · CHM · CPIO · Ar/A/Deb/Lib · FAT · HFS · ISO · LZH · LZMA · NSIS · NTFS · RPM · SPLIT · UDF · WIM · XAR · Z | ✅ | — |

Plus archive‑format **auto‑detection**, **password‑protected** archives, and **multi‑volume (split)** archives.

## 🖥 Supported platforms

| OS | Architectures |
|---|---|
| **Windows** | x86 · x64 · ARM64 |
| **Linux** (glibc) | i386 · amd64 · ARMv5 · ARMv6 · ARMv7 · ARM64 |
| **Linux** (musl / Alpine) | amd64 · ARMv7 · ARM64 |
| **macOS** | universal — Intel (x86_64) + Apple Silicon (arm64) |

The right native library is selected automatically at runtime.

## 🚀 Install

Current version: **`23.01-2.3`** (7‑Zip engine 23.01).

**Maven**
```xml
<dependency>
    <groupId>net.sf.sevenzipjbinding</groupId>
    <artifactId>sevenzipjbinding</artifactId>
    <version>23.01-2.3</version>
</dependency>
<!-- native libraries for all platforms (auto-detected at runtime) -->
<dependency>
    <groupId>net.sf.sevenzipjbinding</groupId>
    <artifactId>sevenzipjbinding-all-platforms</artifactId>
    <version>23.01-2.3</version>
</dependency>
```

**Gradle**
```groovy
implementation 'net.sf.sevenzipjbinding:sevenzipjbinding:23.01-2.3'
implementation 'net.sf.sevenzipjbinding:sevenzipjbinding-all-platforms:23.01-2.3'
```

> 💡 `-all-platforms` bundles every native (largest, works everywhere). To keep your artifact small,
> depend on a single‑platform variant instead, e.g. `sevenzipjbinding-linux-amd64`,
> `sevenzipjbinding-windows-amd64`, `sevenzipjbinding-mac`, … (see all on
> [Maven Central](https://central.sonatype.com/namespace/net.sf.sevenzipjbinding)).

**Without a build tool** — download the ZIPs (jars + native libs) from
[SourceForge](https://sourceforge.net/projects/sevenzipjbind/files/7-Zip-JBinding/) or the
[GitHub release](https://github.com/borisbrodski/sevenzipjbinding/releases/latest) and put
`sevenzipjbinding.jar` + `sevenzipjbinding-<Platform>.jar` on your classpath.

## ⚡ Quick start

List the contents of an archive (format auto‑detected):

```java
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.simple.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import java.io.RandomAccessFile;

try (RandomAccessFile file = new RandomAccessFile("archive.7z", "r");
     IInArchive archive = SevenZip.openInArchive(null, // null → auto-detect the format
             new RandomAccessFileInStream(file))) {

    ISimpleInArchive simple = archive.getSimpleInterface();
    for (ISimpleInArchiveItem item : simple.getArchiveItems()) {
        System.out.printf("%12s  %s%n", item.getSize(), item.getPath());
    }
}
```

Full, **tested** examples for extraction, compression, updating, passwords, multi‑volume archives,
and in‑memory round‑trips are on the site:
👉 **https://sevenzipjbind.sourceforge.net/snippets/**

## 📚 Documentation

- **Website & guide** — https://sevenzipjbind.sourceforge.net/
- **Code snippets** (searchable, verified) — https://sevenzipjbind.sourceforge.net/snippets/
- **JavaDoc** (current) — https://sevenzipjbind.sourceforge.net/javadoc/
- **JavaDoc** (older releases) — https://sevenzipjbind.sourceforge.net/javadoc-history/

## ✅ Requirements

- **Java 8 or newer** at runtime.
- No external tools — the native 7‑Zip engine is bundled in the platform jar.

## 🔒 Known issue / security

**CVE‑2024‑11477** (7‑Zip Zstandard decompression, severity HIGH, CVSS 7.8): the bundled 7‑Zip engine
(23.01) predates the upstream fix, released in 7‑Zip 24.07, so processing **untrusted Zstandard‑
compressed** input may be affected. **Mitigation:** do not open/extract untrusted Zstandard data. This
will be resolved by a future engine upgrade — tracking:
[#72](https://github.com/borisbrodski/sevenzipjbinding/issues/72).

## 🔧 Build from source

You only need this to build the native library yourself; most users should just use the artifacts above.

```bash
# Linux — requires CMake (>= 2.6), a C/C++ toolchain and JDK 8+
cmake .            # or: cmake . -DJAVA_JDK=<path-to-jdk>
make
ctest              # run the tests
make package       # build the distribution ZIP
```

Windows builds use MinGW (recommended) or Cygwin; the full cross‑platform release matrix
(Linux glibc/musl, ARM, Windows, macOS) is produced with the helper scripts under
[`scripts/`](scripts/) (DockCross / Alpine / QEMU). See the scripts for details.

## 🤝 Contributing

Bug reports and pull requests are welcome on
[GitHub](https://github.com/borisbrodski/sevenzipjbinding/issues). When reporting an extraction/
compression bug, please include the archive format, platform, and a minimal reproducer if you can.

## 📄 License

7‑Zip‑JBinding, together with the bundled 7‑Zip/p7zip binaries, is licensed under the
**GNU LGPL‑2.1**. The 7‑Zip RAR decompression code additionally carries the **unRAR license
restriction** (it may not be used to develop a program that recreates the RAR compression algorithm).
See [`COPYING`](COPYING), [`LGPL`](LGPL) and [`License.txt`](License.txt).

## 🙏 Acknowledgements

Built on [7‑Zip](https://www.7-zip.org/) by Igor Pavlov and [p7zip](https://p7zip.sourceforge.net/).
Maintained by **Boris Brodski** since 2007.

7‑Zip‑JBinding is kept alive by everyone who reports problems, suggests improvements, contributes
code, or supports the project financially. A few in particular:

- **Daniel Wilhelm, Appwork GmbH & JDownloader** — sponsoring the RAR5 releases
- **Reinhard Pointner and the FileBot community** — testing and valuable donations
- **Theo Linder** — kind donation
- **[seven332](https://github.com/seven332/sevenzipjbinding/)** — Android integration and JNI fixes
- **AI coding assistants** — Claude (Anthropic), Qwen (on Hermes), and Grok Bot — for help
  modernizing the project for the 23.01‑2.2 release: the engine upgrade, the Java 8 port, the
  cross‑platform build & test farm, and the website and docs

The full credits are in [`THANKS`](THANKS), also shown on the
[website](https://sevenzipjbind.sourceforge.net/acknowledgements/).

If this library helps you, consider [sponsoring the project](https://github.com/sponsors/borisbrodski). ❤
</content>
