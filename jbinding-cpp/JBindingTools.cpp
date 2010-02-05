/*
 * JBindingTools.cpp
 *
 *  Created on: Jan 31, 2010
 *      Author: boris
 */

#include "SevenZipJBinding.h"

#include "JBindingTools.h"

JavaVM * JBindingSession::_vm = NULL;

void JBindingSession::exceptionThrown(JNIEnv * env, jthrowable throwable) {
    ThreadId threadId = PlatformGetCurrentThreadId();

    _threadContextMapCriticalSection.Enter();

    JNIEnv * _env;
    int _attachedThreadCount;
    std::list<JNINativeCallContext *> _javaNativeContext;


    ThreadContext & threadContext = _threadContextMap[threadId];

    if (/*threadContext.*/_javaNativeContext.size()) {
        // Exception can be delivered directly to java caller within current thread
        (*/*threadContext.*/_javaNativeContext.begin())->exceptionThrown(env, throwable);
    }

    _threadContextMapCriticalSection.Leave();
}
