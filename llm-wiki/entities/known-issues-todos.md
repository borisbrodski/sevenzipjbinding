---
title: Known Issues & TODOs
created: 2026-04-22
updated: 2026-04-22
type: entity
tags: [quality, bugs, maintenance, code-review]
sources: [jbinding-java, jbinding-cpp]
---

# Known Issues & TODOs

This page tracks known bugs, incomplete features, and cleanup tasks in the 7-Zip-JBinding codebase. **Note:** Issues in 7zip/ and p7zip/ C++ source are excluded (read-only).

## Critical Issues

### 1. FATAL Macros Should Be Exceptions (C++)

**Location:** `jbinding-cpp/JavaStaticInfo.cpp:40`, `jbinding-cpp/Debug.h:72`

**Problem:** The code uses `FATAL()` macro for unrecoverable errors instead of throwing proper exceptions.

```cpp
// JavaStaticInfo.cpp:40
if (jni::OutOfMemoryError::_isInstance(env, exception)) {
    FATAL("Out of memory during method lookup: '%s', '%s'", _name, _signature); // TODO Change fatal => exception (+test)
}
```

**Impact:** Fatal errors terminate the program instead of allowing graceful error handling.

**Priority:** High

**Status:** TODO - Should convert to SevenZipException with proper test coverage.

---

### 2. Null Parameter Checks Missing (Java)

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/impl/InArchiveImpl.java:36`, `OutArchiveImpl.java:29`

**Problem:** No null checks before passing parameters to native code.

```java
// InArchiveImpl.java:36
//TODO null check all parameters: If null slips through into native code there will be no NPE :(
```

**Impact:** Null pointer exceptions in native code are harder to debug than Java NPEs.

**Priority:** High

**Status:** TODO - Add defensive null checks.

---

### 3. Unimplemented ByteArrayStream Methods

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/util/ByteArrayStream.java`

**Problem:** Several methods throw `IllegalStateException("Not implemented")`:

- Line 386: Method not implemented
- Line 397: Method not implemented
- Line 408: Method not implemented

**Impact:** These methods cannot be used reliably.

**Priority:** Medium

**Status:** TODO - Implement or remove methods.

---

### 4. Split Format Untested

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/ArchiveFormat.java:211`

```java
/**
 * Split format. TODO Test it
 */
SPLIT("Split", true),
```

**Impact:** Unknown reliability of Split archive format support.

**Priority:** Medium

**Status:** TODO - Add test coverage.

---

## Medium Priority Issues

### 5. Unused Code Cleanup (C++)

**Locations:**
- `jbinding-cpp/JBindingTools.h:56` - Unused `_objectList`
- `jbinding-cpp/JNISession.h:16` - File marked for removal
- `jbinding-cpp/Client7z.cpp:2` - File marked for removal

**Problem:** Dead code increases maintenance burden.

**Priority:** Medium

**Status:** TODO - Review and remove unused code.

---

### 6. File Organization Issues

**Locations:**
- `jbinding-cpp/SevenZipJBinding.h:134` - JavaStaticInfo.h include location
- `jbinding-cpp/JavaStaticInfo.h:44` - Include location issue

```cpp
// SevenZipJBinding.h:134
#include "JavaStaticInfo.h" // TODO Move from here
#include "JavaStatInfos/JavaStandardLibrary.h" // TODO Move from here
```

**Impact:** Poor code organization makes maintenance harder.

**Priority:** Low

**Status:** TODO - Refactor include structure.

---

### 7. Debug Code in Release

**Location:** `jbinding-cpp/CPPToJava/CPPToJavaArchiveUpdateCallback.cpp:262`

**Problem:** Debug code blocks (`#ifdef _DEBUG`) may remain in production builds.

**Priority:** Low

**Status:** Review needed.

---

## Documentation Issues

### 8. Unclear API Documentation

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/simple/ISimpleInArchiveItem.java:207-218`

```java
/**
 * TODO: the purpose of position isn't clear
 * @return TODO
 */
// Line 218: TODO: The meaning of the hostOS property is unknown
```

**Impact:** Users cannot understand API behavior.

**Priority:** Medium

**Status:** TODO - Research and document unclear APIs.

---

### 9. Missing Tests for Edge Cases

**Locations:**
- `jbinding-java/src/net/sf/sevenzipjbinding/ArchiveFormat.java:406` - Untested check
- `jbinding-java/src/net/sf/sevenzipjbinding/SevenZip.java:732` - Untested code path

```java
// ArchiveFormat.java:406
// TODO Test, that this check indeed isn't necessary
```

**Priority:** Medium

**Status:** TODO - Add test coverage.

---

## Code Quality Issues

### 10. Property Name Mapping Verification

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/PropID.java:231`

```java
// TODO Add test to ensure "kpidLink"(c++) == "LINK" (java)
```

**Impact:** Potential mismatch between C++ and Java property names.

**Priority:** Medium

**Status:** TODO - Add verification test.

---

### 11. Fuzzy Match Support Missing

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/SevenZip.java:883`

```java
// TODO allow fuzzy matches
```

**Impact:** Platform matching could be more flexible.

**Priority:** Low

**Status:** TODO - Implement fuzzy matching.

---

### 12. Resource Cleanup

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/util/ByteArrayStream.java:683`

```java
// TODO Set all references to null, prevent further calls to all methods
```

**Impact:** Potential memory leaks or use-after-close bugs.

**Priority:** Medium

**Status:** TODO - Implement proper cleanup.

---

## Build System Issues

### 13. CMake Policy CMP0026

**Location:** `CMakeLists.txt:158`

```cmake
# TODO: Not allowed due to policy CMP0026
GET_TARGET_PROPERTY(SEVENZIP_JBINDING_LIB 7-Zip-JBinding LOCATION)
```

**Impact:** Build system may need updates for newer CMake versions.

**Priority:** Low

**Status:** TODO - Review CMake policies.

---

### 14. Version Synchronization

**Locations:**
- `CMakeLists.txt:42`
- `jbinding-java/src/net/sf/sevenzipjbinding/SevenZip.java:195`

**Problem:** Version must be updated in two places simultaneously.

**Impact:** Risk of version mismatch.

**Priority:** Medium

**Status:** TODO - Centralize version definition.

---

## Legacy Code Markers

### 15. Deprecated Classes

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/impl/SequentialInStreamImpl.java:18`

```java
// TODO Remove in the next release
```

**Priority:** Low

**Status:** Pending release.

---

### 16. NCoderPropID Unused

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/NCoderPropID.java:6`

```java
// TODO Use it or remove it
```

**Priority:** Low

**Status:** Needs decision.

---

## Testing Gaps

### 17. Thread Safety Verification

**Location:** `jbinding-cpp/JBindingTools.cpp:119`

```cpp
|| _firstThrownExceptionInOtherThread || _lastThrownExceptionInOtherThread; // TODO Test it
```

**Impact:** Unknown thread safety of exception handling.

**Priority:** High

**Status:** TODO - Add thread safety tests.

---

### 18. Initialization Without Privileged Mode

**Location:** `jbinding-java/src/net/sf/sevenzipjbinding/SevenZip.java`

**Problem:** Tests exist for privileged initialization, but non-privileged mode needs more coverage.

**Priority:** Medium

**Status:** TODO - Expand test coverage.

---

## Summary by Priority

| Priority | Count |
|----------|-------|
| High | 3 |
| Medium | 10 |
| Low | 5 |

## Cross-References

- [[7-zip-jbinding-overview]] - Project overview
- [[jni-interface-architecture]] - JNI layer details
- [[build-system]] - Build configuration
- [[java-api-interfaces]] - Java API
