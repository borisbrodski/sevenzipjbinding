# 7-Zip-JBinding LLM Wiki

## Overview

This is a persistent, LLM-readable knowledge base for the 7-Zip-JBinding project. Built following Andrej Karpathy's LLM Wiki pattern.

**Project:** 7-Zip-JBinding (Java JNI bindings for 7-Zip/p7zip)
**Version:** 16.02-2.01
**Author:** Boris Brodski
**License:** GNU LGPL 2.1+

## Wiki Structure

```
llm-wiki/
├── index.md              # This file - Wiki catalog
├── log.md                # Action log (append-only)
├── SCHEMA.md             # Wiki schema and conventions
├── entities/             # Core entity pages
│   ├── 7-zip-jbinding-overview.md
│   ├── jni-interface-architecture.md
│   ├── java-api-interfaces.md
│   ├── build-system.md
│   ├── test-framework.md
│   └── known-issues-todos.md
├── comparisons/          # Comparative analysis
│   └── archive-format-support.md
└── raw/                  # Raw source analysis (if needed)
    └── sources/
```

## Entity Pages

| Page | Description | Tags |
|------|-------------|------|
| [[7-zip-jbinding-overview]] | Project architecture, components, key files | architecture, jni, java-api |
| [[jni-interface-architecture]] | JNI bridge layer design, callback system | jni, architecture, design-pattern |
| [[java-api-interfaces]] | Java API interfaces, archive formats | java-api, components |
| [[build-system]] | CMake build configuration, packaging | build-system, components |
| [[test-framework]] | JUnit test suite structure, coverage | quality, testing |
| [[known-issues-todos]] | Bugs, TODOs, code quality issues | quality, bugs, maintenance |

## Comparison Pages

| Page | Description | Tags |
|------|-------------|------|
| [[archive-format-support]] | 7-Zip C++ vs 7-Zip-JBinding format coverage gap | components, architecture, comparison |

## Quick Links

### Core Concepts
- [[7-zip-jbinding-overview#initialization-flow]] - Native library initialization
- [[jni-interface-architecture#exception-handling]] - Error handling patterns
- [[java-api-interfaces#archive-format-support]] - Supported archive formats

### Development
- [[build-system#testing]] - Running tests
- [[known-issues-todos]] - Known bugs and TODOs
- [[build-system#platform-detection]] - Platform support
- [[archive-format-support]] - Missing format implementations

### Testing
- [[test-framework#test-categories]] - Test suite organization
- [[test-framework#coverage]] - Format and operation coverage

## Tag Index

### Components
- [[java-api-interfaces]]
- [[jni-interface-architecture]]
- [[build-system]]
- [[archive-format-support]]

### Architecture
- [[7-zip-jbinding-overview]]
- [[jni-interface-architecture]]
- [[archive-format-support]]

### Quality
- [[known-issues-todos]]
- [[test-framework]]

### Platforms
- [[build-system#platform-detection]]

### Comparison
- [[archive-format-support]]

## Operations

This wiki follows Karpathy's 3 operations:

1. **Ingest** - Analyze source files, create summary pages
2. **Query** - Use LLM to retrieve information from pages
3. **Lint** - Check for contradictions, orphans, stale content

See [[SCHEMA.md]] for detailed conventions.

## Last Updated

2026-04-22 - Initial wiki creation with 7 core entity pages
2026-04-22 - Added archive-format-support.md comparison page (23 missing formats identified)
2026-04-22 - XZ format extraction support verified and documented
2026-04-22 - XZ documentation updates across all documentation files
