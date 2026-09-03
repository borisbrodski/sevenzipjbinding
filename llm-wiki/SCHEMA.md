# Wiki Schema - 7-Zip-JBinding Codebase

## Domain
7-Zip-JBinding - Java JNI bindings for 7-Zip/p7zip compression library. Includes full 7-Zip C++ source code, Java API layer, and comprehensive test suite.

## Conventions
- File names: lowercase, hyphens, no spaces (e.g., `jni-interface.md`)
- Every wiki page starts with YAML frontmatter
- Use `[[wikilinks]]` to link between pages (minimum 2 outbound links per page)
- When updating a page, always bump the `updated` date
- Every new page must be added to `index.md` under the correct section
- Every action must be appended to `log.md`
- **Source files are immutable** - analysis goes in wiki pages only
- **7-Zip C++ source code** (in `7zip/` directory) is read-only - no issues tracked there

## Frontmatter
```yaml
---
title: Page Title
created: YYYY-MM-DD
updated: YYYY-MM-DD
type: entity | concept | comparison | query | summary | issue
tags: [from taxonomy below]
sources: [relative paths to source files]
---
```

## Tag Taxonomy
- **Components:** jni, java-api, native-lib, build-system, test-framework, 7zip-core
- **Architecture:** architecture, design-pattern, integration, protocol, memory-management
- **Quality:** performance, security, bug, todo, refactoring, test
- **7-Zip Integration:** compression, decompression, archive-format, codec
- **Meta:** comparison, timeline, decision, controversy

## Page Thresholds
- **Create a page** when a component/file has significant functionality (200+ lines) or appears in 2+ contexts
- **Add to existing page** when new info relates to already-covered topics
- **DON'T create a page** for trivial utilities or one-off helpers
- **Split a page** when it exceeds ~150 lines — break into sub-topics with cross-links

## Entity Pages
One page per notable module/component. Include:
- Overview / what it does
- Key functions and interfaces
- Relationships to other components (`[[wikilinks]]`)
- Source references

## Concept Pages
One page per architectural pattern or concept. Include:
- Definition / explanation
- Implementation details
- Related concepts (`[[wikilinks]]`)

## Issue Pages
For known bugs, TODOs, and suspicious code in the JBinding layer (NOT in 7-Zip core). Include:
- File and line numbers
- Description of the issue
- Impact/severity
- Suggested fix (if known)

## Update Policy
- New findings always add to existing pages rather than duplicating
- Contradictions noted with dates and context
- Issues tracked in `known-issues-todos.md` and linked from relevant pages
- 7-Zip C++ source code issues are documented but not treated as actionable TODOs
