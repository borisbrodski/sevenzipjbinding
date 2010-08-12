/*
 * BaseSystem.h
 *
 *  Created on: Jan 16, 2010
 *      Author: boris
 */

#ifndef BASESYSTEM_H_
#define BASESYSTEM_H_

#ifndef _7ZIP_ST
#include "Windows/Synchronization.h"
#endif

#ifndef  _7ZIP_ST
#ifndef MINGW
    #include <pthread.h>
#endif
#endif

typedef size_t ThreadId;

#ifndef _7ZIP_ST
    typedef NWindows::NSynchronization::CCriticalSection PlatformCriticalSection;
#endif


inline ThreadId PlatformGetCurrentThreadId() {
#ifndef _7ZIP_ST
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
