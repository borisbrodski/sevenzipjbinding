/*
 * JBindingTools.cpp
 *
 *  Created on: Jan 31, 2010
 *      Author: boris
 */

#include "SevenZipJBinding.h"

#include "JBindingTools.h"


JT_BEGIN_CLASS("net/sf/sevenzipjbinding", SevenZipException)
/*    */JT_CLASS_VIRTUAL_METHOD_OBJECT("Ljava/lang/Throwable;", initCause, JT_THROWABLE(cause,_))
/*    */JT_CLASS_CONSTRUCTOR(JT_STRING(message, JT_STRING(param2, _))) // TODO remove if not needed
/*    */JT_CLASS_CONSTRUCTOR(JT_STRING(message, _)) // TODO remove if not needed
/*    */JT_CLASS_CONSTRUCTOR(_) // TODO remove if not needed
/*    */JT_CLASS_FINAL_METHOD(Void, setCauseLastThrown, JT_THROWABLE(causeLastThrown,_))
/*    */JT_CLASS_FINAL_METHOD(Void, setCauseFirstPotentialThrown, JT_THROWABLE(causeFirstPotentialThrown,_))
/*    */JT_CLASS_FINAL_METHOD(Void, setCauseLastPotentialThrown, JT_THROWABLE(causeLastPotentialThrown,_))
JT_END_CLASS

JavaVM * JBindingSession::_vm = NULL;

void JBindingSession::handleThrownException(jthrowable exceptionLocalRef) {
    ThreadId threadId = PlatformGetCurrentThreadId();

    _threadContextMapCriticalSection.Enter();
    ThreadContext & threadContext = _threadContextMap[threadId];
    MY_ASSERT(!threadContext._javaNativeContext.size())

    // All active JNINativeCallContext objects should be notified
    ThreadContextMap::iterator threadContextIterator = _threadContextMap.begin();
    while (threadContextIterator != _threadContextMap.end()) {
        std::list<JNINativeCallContext *> & jniNativeCallContextList =
                threadContextIterator->second._javaNativeContext;
        if (jniNativeCallContextList.size()) {
            (*jniNativeCallContextList.begin())->exceptionThrownInOtherThread(threadContext._env,
                    exceptionLocalRef);
        }
    }
    _threadContextMapCriticalSection.Leave();
}

JNINativeCallContext::~JNINativeCallContext() {
    _jbindingSession.unregisterNativeContext(*this);

    //jthrowable firstThrownException = NULL;
    //jthrowable lastThrownException = NULL;
    //jthrowable firstThrownExceptionInOtherThread = NULL;
    //jthrowable lastThrownExceptionInOtherThread = NULL;

    if (_firstThrownException && !_lastThrownException && !_firstThrownExceptionInOtherThread && !_lastThrownExceptionInOtherThread ) {
        TRACE("Rethrowing exception " << _lastThrownException)
        _jniCallOriginalEnv->Throw(_firstThrownException);
    } else {
        if (_firstThrownException) {
            jthrowable sevenZipException;
            if (jni::SevenZipException::_isInstance(_jniCallOriginalEnv, _firstThrownException)) {
                // Last thrown exception is SevenZipException. Reuse it.
                sevenZipException = _firstThrownException;
            } else {
                // Create new SevenZipException to pass more than one caused by.
                sevenZipException = static_cast<jthrowable>(jni::SevenZipException::newInstance(_jniCallOriginalEnv));
                assertNoExceptionOnJniCallOriginalEnv();

                jni::SevenZipException::initCause(_jniCallOriginalEnv, sevenZipException, _firstThrownException);
                assertNoExceptionOnJniCallOriginalEnv();
            }

            if (_lastThrownException) {
                jni::SevenZipException::setCauseLastThrown(_jniCallOriginalEnv, sevenZipException, _lastThrownException);
                assertNoExceptionOnJniCallOriginalEnv();
            }
            if (_firstThrownExceptionInOtherThread) {
                jni::SevenZipException::setCauseFirstPotentialThrown(_jniCallOriginalEnv, sevenZipException, _firstThrownExceptionInOtherThread);
                assertNoExceptionOnJniCallOriginalEnv();
            }
            if (_lastThrownExceptionInOtherThread) {
                jni::SevenZipException::setCauseLastPotentialThrown(_jniCallOriginalEnv, sevenZipException, _lastThrownExceptionInOtherThread);
                assertNoExceptionOnJniCallOriginalEnv();
            }
            _jniCallOriginalEnv->Throw(sevenZipException);
        }
    }

    if (_firstThrownException) {
        //firstThrownException = _jniCallOriginalEnv->NewLocalRef(_firstThrownException);
        _jniCallOriginalEnv->DeleteGlobalRef(_firstThrownException);
    }
    if (_lastThrownException) {
        //lastThrownException = _jniCallOriginalEnv->NewLocalRef(_lastThrownException);
        _jniCallOriginalEnv->DeleteGlobalRef(_lastThrownException);
    }
    if (_firstThrownExceptionInOtherThread) {
        //firstThrownExceptionInOtherThread = _jniCallOriginalEnv->NewLocalRef(_firstThrownExceptionInOtherThread);
        _jniCallOriginalEnv->DeleteGlobalRef(_firstThrownExceptionInOtherThread);
    }
    if (_lastThrownExceptionInOtherThread) {
        //lastThrownExceptionInOtherThread = _jniCallOriginalEnv->NewLocalRef(_lastThrownExceptionInOtherThread);
        _jniCallOriginalEnv->DeleteGlobalRef(_lastThrownExceptionInOtherThread);
    }

    // Throw Exceptions
    // - throw SevenZipException directly
    // - throw all other exceptions wrapped in SevenZipException
    // - set lastThrownException

}

void JNINativeCallContext::throwException(char const * msg, ...) {
    // TODO
}

#ifdef USE_MY_ASSERTS
int JBindingSession::_attachedThreadCount = 0;
extern "C" JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_junit_tools_SevenZipDebug_nativeGetAttachedThreadCount(
                                                                                                                      JNIEnv * env,
                                                                                                                      jclass clazz) {

    return JBindingSession::_attachedThreadCount;
}
#endif
