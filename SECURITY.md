# Security policy

7-Zip-JBinding runs the native 7-Zip engine inside your JVM. Every archive you open is parsed by that
native code, so a vulnerability in the bundled 7-Zip version is a vulnerability of the application
that uses the library. Use only a supported release.

## Supported versions

| Release                  | Bundled engine            | Status |
|--------------------------|---------------------------|--------|
| `26.06-2.x` (current)    | 7-Zip 26.06 (2026-11-06)  | **Supported.** Contains every 7-Zip security fix up to and including 26.06. |
| `23.01-2.x`              | 7-Zip 23.01 (2023-06-20)  | **Not supported, do not use.** Affected by the 7-Zip vulnerabilities fixed between 24.07 and 26.06: CVE-2024-11477 (Zstandard), CVE-2024-11612, CVE-2025-53816 (RAR), CVE-2025-53817 (Compound), CVE-2026-14266 (XZ, heap overflow), CVE-2026-48092/48095/48101/48102/48103/48104/48111/48112 (SquashFS, NTFS, UEFI, UDF, WIM, Ar handlers) and the unnamed fixes of 26.03 to 26.06. |
| `16.02-2.01` and older   | 7-Zip / p7zip 16.02 (2016) | **Not supported, do not use.** Affected by everything above and by the 7-Zip vulnerabilities fixed between 16.03 and 23.01, among them CVE-2017-17969 (Zip Shrink decoder), CVE-2018-5996 and CVE-2018-10115 (RAR decoder), CVE-2016-9296 and CVE-2022-47069 (7z headers, p7zip), CVE-2023-31102 (7z decoder) and CVE-2023-40481 (SquashFS). |

The Windows-only issues of the 7-Zip graphical tools (installer DLL preloading, Mark-of-the-Web handling
such as CVE-2025-0411 and CVE-2026-58052, symbolic link extraction CVE-2025-11001, CVE-2025-11002,
CVE-2025-55188) do not apply: 7-Zip-JBinding neither extracts to the file system by itself nor ships
the 7-Zip executables.

Sources: the [7-Zip history](https://www.7-zip.org/history.txt), the [NVD](https://nvd.nist.gov/) and
issue [#97](https://github.com/borisbrodski/sevenzipjbinding/issues/97). The Ghidra project published
[GHSA-4pqp-hrg7-62xp](https://github.com/NationalSecurityAgency/ghidra/security/advisories/GHSA-4pqp-hrg7-62xp)
for shipping 16.02-2.01.

## Upgrading

The Java API of `26.06-2.x` is source and binary compatible with `16.02-2.01` and `23.01-2.x`; in most
projects the upgrade is a version change in the build file. See the
[README](README.md) for the Maven coordinates and the
[release notes](ReleaseNotes.txt) for behaviour changes.

## Reporting a vulnerability

Please report vulnerabilities privately, not in a public issue:

- through GitHub's private vulnerability reporting on this repository
  (**Security** → **Report a vulnerability**), or
- by e-mail to the maintainer (address in the commit log and in `THANKS`).

You will get an answer within a few days. Fixes are published as a new release on Maven Central,
GitHub and SourceForge together with a GitHub security advisory. Vulnerabilities in the 7-Zip
engine itself are best reported to [7-Zip](https://www.7-zip.org/) as well; 7-Zip-JBinding picks up
the fixed engine with its next release.
