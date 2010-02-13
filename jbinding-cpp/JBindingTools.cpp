/*
 * JBindingTools.cpp
 *
 *  Created on: Jan 31, 2010
 *      Author: boris
 */

#include "SevenZipJBinding.h"

#include "JBindingTools.h"

JavaVM * JBindingSession::_vm = NULL;

void JBindingSession::handleThrownException(jthrowable exceptionLocalRef) {
    ThreadId threadId = PlatformGetCurrentThreadId();

    _threadContextMapCriticalSection.Enter();
    ThreadContext & threadContext = _threadContextMap[threadId];
    MY_ASSERT(!threadContext._javaNativeContext.size())

    // All active JNINativeCallContext objects should be notified
    ThreadContextMap::iterator threadContextIterator = _threadContextMap.begin();
    while (threadContextIterator != _threadContextMap.end()) {
        std::list<JNINativeCallContext *> & jniNativeCallContextList = threadContextIterator->second._javaNativeContext;
        if (jniNativeCallContextList.size()) {
            (*jniNativeCallContextList.begin())->exceptionThrownInOtherThread(threadContext._env, exceptionLocalRef);
        }
    }
    _threadContextMapCriticalSection.Leave();
}


#ifdef USE_MY_ASSERTS
int JBindingSession::_attachedThreadCount = 0;
extern "C" JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_junit_tools_SevenZipDebug_nativeGetAttachedThreadCount(JNIEnv * env, jclass clazz)
{
    return JBindingSession::_attachedThreadCount;
}
#endif
