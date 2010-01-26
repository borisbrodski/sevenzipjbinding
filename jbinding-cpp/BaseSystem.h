/*
 * BaseSystem.h
 *
 *  Created on: Jan 16, 2010
 *      Author: boris
 */

#ifndef BASESYSTEM_H_
#define BASESYSTEM_H_

#ifdef COMPRESS_MT
#include "Windows/Synchronization.h"
#endif

#if defined(COMPRESS_MT) || defined(COMPRESS_BZIP2_MT) || defined(COMPRESS_MF_MT) || defined(BENCH_MT)
#ifndef MINGW
    #include <pthread.h>
#endif
#endif

typedef size_t ThreadId;

#ifdef COMPRESS_MT
    typedef NWindows::NSynchronization::CCriticalSection PlatformCriticalSection;
#endif


inline ThreadId PlatformGetCurrentThreadId() {
#if defined(COMPRESS_MT) || defined(COMPRESS_BZIP2_MT) || defined(COMPRESS_MF_MT) || defined(BENCH_MT)
    #ifdef MINGW
        return (ThreadId)GetCurrentThreadId();
    #else
        return (ThreadId)pthread_self();
    #endif
#else
    return 0;
#endif
}



#endif /* BASESYSTEM_H_ */
