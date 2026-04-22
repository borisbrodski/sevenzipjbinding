---
title: JNI Interface Architecture
created: 2026-04-22
updated: 2026-04-22
type: entity
tags: [jni, architecture, design-pattern, integration]
sources: [jbinding-cpp/SevenZipJBinding.h, jbinding-cpp/BaseSystem.h, jbinding-cpp/JavaStaticInfo.cpp]
---

# JNI Interface Architecture

## Overview

The JNI bridge layer connects Java API calls to 7-Zip C++ implementation. It implements a bidirectional callback system where:
- **Java → C++**: Java calls trigger 7-Zip operations (open, extract, create)
- **C++ → Java**: 7-Zip callbacks invoke Java implementations (progress, password, I/O streams)

## Directory Structure

```
jbinding-cpp/
├── BaseSystem.h              # Platform abstraction (threads, sync)
├── SevenZipJBinding.h        # Main JNI header with macros
├── JavaStaticInfo.cpp        # JNI method ID caching
├── JavaToCPP/                # Java-initiated operations
│   ├── JavaToCPPSevenZip.cpp
│   ├── JavaToCPPInArchiveImpl.cpp
│   └── JavaToCPPOutArchiveImpl.cpp
├── CPPToJava/                # C++-initiated callbacks
│   ├── CPPToJavaArchiveExtractCallback.*
│   ├── CPPToJavaInStream.*
│   ├── CPPToJavaOutStream.*
│   └── ... (15+ callback wrappers)
└── Debug.cpp                 # Debug/trace utilities
```

## Key Components

### 1. Method ID Caching (`JavaStaticInfo.cpp`)

Uses lazy initialization with double-checked locking to cache JNI method IDs:

```cpp
void JMethod::initMethodIDIfNecessary(JNIEnv *env, jclass jclazz) {
    if (isInitialized) return;
    
    _initCriticalSection.Enter();
    if (isInitialized) {
        _initCriticalSection.Leave();
        return;
    }
    
    initMethodID(env, jclazz);  // Get method ID via JNI
    isInitialized = true;
    _initCriticalSection.Leave();
}
```

**Pattern:** Thread-safe lazy initialization with critical section protection.

### 2. Exception Handling (`SevenZipJBinding.h`)

Defines macros for consistent exception handling:

```cpp
#define TRY try {
#define CATCH_SEVEN_ZIP_EXCEPTION(ctx, retval) \
    } catch(SevenZipException & e) { \
        TRACE("Exception catched: " << &e); \
        (ctx).ThrowSevenZipException(&e); \
    return retval; }

#define FATAL fatal  // Fatal errors (should become exceptions per TODO)
```

**Flow:**
1. Wrap C++ code in `TRY/CATCH_SEVEN_ZIP_EXCEPTION`
2. Catch `SevenZipException` and convert to Java exception
3. `FATAL` for unrecoverable errors (TODO: convert to exceptions)

### 3. Platform Abstraction (`BaseSystem.h`)

Cross-platform thread and sync primitives:

```cpp
#ifndef _7ZIP_ST
#include "Windows/Synchronization.h"  // Windows CRITICAL_SECTION
#include <pthread.h>                   // POSIX pthread
#endif

typedef size_t ThreadId;

inline ThreadId PlatformGetCurrentThreadId() {
#ifndef _7ZIP_ST
    #ifdef MINGW
        return (ThreadId)GetCurrentThreadId();
    #else
        return (ThreadId)pthread_self();
    #endif
#else
    return 0;  // Single-threaded build
#endif
}
```

**Supports:** Windows (MSVC, MinGW), Linux, macOS, single-threaded builds.

### 4. Callback Interfaces

**Java → C++ direction:**
- `ISequentialInStream` → `ISequentialInStream` C++ wrapper
- `ISequentialOutStream` → `ISequentialOutStream` C++ wrapper
- `IInStream` → `IInStream` C++ wrapper (seekable)
- `IArchiveOpenCallback` → Archive open callbacks
- `IArchiveExtractCallback` → Extraction callbacks
- `IOutCreateCallback` → Creation callbacks

**C++ → Java direction:**
- 7-Zip calls back into Java via cached method IDs
- Progress updates, password requests, volume loading

## JNI Entry Points

All JNI functions follow the pattern:

```cpp
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_ClassName_MethodName
  (JNIEnv *env, jobject obj, /* args */) {
    TRY
        // Extract Java object state
        // Call 7-Zip C++ API
        // Handle results
    CATCH_SEVEN_ZIP_EXCEPTION(context, )
}
```

## Memory Management

- **StackAllocatedObject**: COM-style reference counting (`AddRef`/`Release`)
- **7-Zip objects**: Managed by 7-Zip core, wrapped by JNI layer
- **Java objects**: Referenced via JNI global references to prevent GC

See [[memory-management]] for detailed analysis.

## Known Issues

1. **FATAL macros** - Should be converted to proper exceptions (see TODO in `JavaStaticInfo.cpp:40`)
2. **Method ID caching** - No invalidation on class reload (edge case)
3. **Thread safety** - Depends on `_7ZIP_ST` build flag

See [[known-issues-todos]] for full list.

## Cross-References

- [[7-zip-jbinding-overview]] - Project overview
- [[java-api-interfaces]] - Java layer details
- [[error-handling]] - Exception patterns
- [[memory-management]] - Memory handling
