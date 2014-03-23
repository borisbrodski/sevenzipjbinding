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
/*    */JT_CLASS_CONSTRUCTOR(JT_STRING(message, _))
/*    */JT_CLASS_FINAL_METHOD(Void, setCauseLastThrown, JT_THROWABLE(causeLastThrown,_))
/*    */JT_CLASS_FINAL_METHOD(Void, setCauseFirstPotentialThrown, JT_THROWABLE(causeFirstPotentialThrown,_))
/*    */JT_CLASS_FINAL_METHOD(Void, setCauseLastPotentialThrown, JT_THROWABLE(causeLastPotentialThrown,_))
JT_END_CLASS

static const char * outOfMemory = "Out of memory";

static struct {
    HRESULT errCode;
    const char * message;
} SevenZipErrorMessages[] = { {S_OK, "OK"}, // ((HRESULT)0x00000000L)
                              {S_FALSE, "FALSE"}, // ((HRESULT)0x00000001L)
                              {E_NOTIMPL, "Not implemented"}, // ((HRESULT)0x80004001L)
                              {E_NOINTERFACE, "No interface"}, // ((HRESULT)0x80004002L)
                              {E_ABORT, "Abort"}, // ((HRESULT)0x80004004L)
                              {E_FAIL, "Fail"}, // ((HRESULT)0x80004005L)
                              {STG_E_INVALIDFUNCTION, "Invalid function"}, // ((HRESULT)0x80030001L)
                              {E_OUTOFMEMORY, "Out of memory"}, // ((HRESULT)0x8007000EL)
                              {E_INVALIDARG, "Invalid argument"}, // ((HRESULT)0x80070057L)
                              {0, NULL}, };

/*
 * Return error message from error code
 */
static const char * GetSevenZipErrorMessage(HRESULT hresult) {
    for (int i = 0; SevenZipErrorMessages[i].message != NULL; i++) {
        if (SevenZipErrorMessages[i].errCode == hresult) {
            return SevenZipErrorMessages[i].message;
        }
    }
    return "Unknown error code";
}

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
        threadContextIterator++;
    }
    _threadContextMapCriticalSection.Leave();
}

void JBindingSession::vReportError(const int hresult, const char * fmt, va_list args) {
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
            (*jniNativeCallContextList.begin())->vReportError(hresult, fmt, args);
        }
        threadContextIterator++;
    }
    _threadContextMapCriticalSection.Leave();
}

void JNINativeCallContext::vReportError(const int hresult, const char * fmt, va_list args) {
    if (_errorMessage) {
        TRACE("Ignoring error message: '" << fmt << "'. Keeping last error message: '" << _errorMessage);
        return;
    }

    char buffer[64 * 1024];
    if (hresult != NO_HRESULT) {
        snprintf(buffer, sizeof(buffer), "HRESULT: 0x%X (%s). ", (int) hresult,
                GetSevenZipErrorMessage(hresult));
        size_t beginIndex = strlen(buffer);
        vsnprintf(&buffer[beginIndex], sizeof(buffer) - beginIndex, fmt, args);
    } else {
        vsnprintf(buffer, sizeof(buffer), fmt, args);
    }
    buffer[sizeof(buffer) - 1] = '\0';
    int length = strlen(buffer);

    _errorMessage = (char *) malloc(length + 1);
    if (!_errorMessage) {
        _errorMessage = const_cast<char*> (outOfMemory);
    } else {
        memcpy(_errorMessage, buffer, length + 1);
    }
}

bool JNINativeCallContext::willExceptionBeThrown() {
    return _errorMessage || _firstThrownException || _lastThrownException
            || _firstThrownExceptionInOtherThread || _lastThrownExceptionInOtherThread; // TODO Test it
}

JNINativeCallContext::~JNINativeCallContext() {
    _jbindingSession.unregisterNativeContext(*this);

    //jthrowable firstThrownException = NULL;
    //jthrowable lastThrownException = NULL;
    //jthrowable firstThrownExceptionInOtherThread = NULL;
    //jthrowable lastThrownExceptionInOtherThread = NULL;
    if (!_errorMessage && _firstThrownException && !_lastThrownException
            && !_firstThrownExceptionInOtherThread && !_lastThrownExceptionInOtherThread) {
        TRACE("Rethrowing exception " << _lastThrownException)
        _jniCallOriginalEnv->Throw(_firstThrownException);
    } else {
        if (_errorMessage || _firstThrownException || _lastThrownException
                || _firstThrownExceptionInOtherThread || _lastThrownExceptionInOtherThread) {
            jthrowable sevenZipException;
            jstring message;
            if (_errorMessage) {
                message = _jniCallOriginalEnv->NewStringUTF(_errorMessage);
            } else {
                message = _jniCallOriginalEnv->NewStringUTF(
                        "One or multiple exceptions without specific error message were thrown. "
                            "See multiple 'caused by' exceptions for more information.");
            }
            sevenZipException = static_cast<jthrowable> (jni::SevenZipException::newInstance(
                    _jniCallOriginalEnv, message));
            assertNoExceptionOnJniCallOriginalEnv();

            if (_firstThrownException) {
                jni::SevenZipException::initCause(_jniCallOriginalEnv, sevenZipException,
                        _firstThrownException);
                assertNoExceptionOnJniCallOriginalEnv();
            }

            if (_lastThrownException) {
                jni::SevenZipException::setCauseLastThrown(_jniCallOriginalEnv, sevenZipException,
                        _lastThrownException);
                assertNoExceptionOnJniCallOriginalEnv();
            }
            if (_firstThrownExceptionInOtherThread) {
                jni::SevenZipException::setCauseFirstPotentialThrown(_jniCallOriginalEnv,
                        sevenZipException, _firstThrownExceptionInOtherThread);
                assertNoExceptionOnJniCallOriginalEnv();
            }
            if (_lastThrownExceptionInOtherThread) {
                jni::SevenZipException::setCauseLastPotentialThrown(_jniCallOriginalEnv,
                        sevenZipException, _lastThrownExceptionInOtherThread);
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

    if (_errorMessage && _errorMessage != outOfMemory) {
        free(_errorMessage);
    }
    // Throw Exceptions
    // - throw SevenZipException directly
    // - throw all other exceptions wrapped in SevenZipException
    // - set lastThrownException
}

#ifdef USE_MY_ASSERTS
int JBindingSession::_attachedThreadCount = 0;
PlatformCriticalSection JBindingSession::_attachedThreadCountCriticalSection;

extern "C" JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_junit_tools_SevenZipDebug_nativeGetAttachedThreadCount(
                                                                                                                      JNIEnv * env,
                                                                                                                      jclass clazz) {

    return JBindingSession::_attachedThreadCount;
}
#endif
