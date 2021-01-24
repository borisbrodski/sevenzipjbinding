/*
 * JBindingTools.cpp
 *
 *  Created on: Jan 31, 2010
 *      Author: boris
 */

#include "SevenZipJBinding.h"

#include "JBindingTools.h"

#ifdef __ANDROID_API__
#include "JavaStatInfos/JavaPackageSevenZip.h"
#endif

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

#ifdef __ANDROID_API__
jmethodID JBindingSession::_classLoaderID;
std::map<const char*, jobject> JBindingSession::_classLoaderObjects;

static jobject addClassLoaderObject(JNIEnv* env, const char* name) {
    jclass clazz = env->FindClass(name);
    jclass objectClass = env->GetObjectClass(clazz);
    jmethodID classLoaderID = env->GetMethodID(objectClass, "getClassLoader", "()Ljava/lang/ClassLoader;");
    env->DeleteLocalRef(objectClass);
    jobject classLoaderObject = env->CallObjectMethod(clazz, classLoaderID);
    env->DeleteLocalRef(clazz);
    jobject classObject = env->NewGlobalRef(classLoaderObject);
    env->DeleteLocalRef(classLoaderObject);
    return classObject;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *pjvm, void *reserved) {
    JNIEnv* env = nullptr;
    if (pjvm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
        jclass classLoaderClass = env->FindClass("java/lang/ClassLoader");
        JBindingSession::_classLoaderID = env->GetMethodID(classLoaderClass, "findClass", "(Ljava/lang/String;)Ljava/lang/Class;");
        env->DeleteLocalRef(classLoaderClass);

        JBindingSession::_classLoaderObjects.insert(std::make_pair(PROPERTYINFO_CLASS, addClassLoaderObject(env, PROPERTYINFO_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(PROPID_CLASS, addClassLoaderObject(env, PROPID_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEQUENTIALOUTSTREAM_CLASS, addClassLoaderObject(env, SEQUENTIALOUTSTREAM_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEQUENTIALINSTREAM_CLASS, addClassLoaderObject(env, SEQUENTIALINSTREAM_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(INSTREAM_CLASS, addClassLoaderObject(env, INSTREAM_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(CRYPTOGETTEXTPASSWORD_CLASS, addClassLoaderObject(env, CRYPTOGETTEXTPASSWORD_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(ARCHIVEOPENVOLUMECALLBACK_CLASS, addClassLoaderObject(env, ARCHIVEOPENVOLUMECALLBACK_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(EXTRACTASKMODE_CLASS, addClassLoaderObject(env, EXTRACTASKMODE_CLASS)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(EXTRACTOPERATIONRESULT_CLASS, addClassLoaderObject(env, EXTRACTOPERATIONRESULT_CLASS)));

//        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_PROP_ID, addClassLoaderObject(env, JAVA_PROP_ID)));
//        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_EXTRACT_ASK_MODE, addClassLoaderObject(env, JAVA_EXTRACT_ASK_MODE)));
//        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_EXTRACT_OPERATION_RESULT, addClassLoaderObject(env, JAVA_EXTRACT_OPERATION_RESULT)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_IIN_ARCHIVE, addClassLoaderObject(env, JAVA_IIN_ARCHIVE)));
//        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_IIN_STREAM, addClassLoaderObject(env, JAVA_IIN_STREAM)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_ISEQUENTIAL_IN_STREAM, addClassLoaderObject(env, JAVA_ISEQUENTIAL_IN_STREAM)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_ISEQUENTIAL_OUT_STREAM, addClassLoaderObject(env, JAVA_ISEQUENTIAL_OUT_STREAM)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_IOUT_ARCHIVE, addClassLoaderObject(env, JAVA_IOUT_ARCHIVE)));
//        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_IOUT_ITEM_CALLBACK_BASE, addClassLoaderObject(env, JAVA_IOUT_ITEM_CALLBACK_BASE)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_ARCHIVE_FORMAT, addClassLoaderObject(env, JAVA_ARCHIVE_FORMAT)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_OUT_ITEM_FACTORY, addClassLoaderObject(env, JAVA_OUT_ITEM_FACTORY)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_OUT_ITEM, addClassLoaderObject(env, JAVA_OUT_ITEM)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_IOUT_ITEM_BASE, addClassLoaderObject(env, JAVA_IOUT_ITEM_BASE)));

        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE"/IArchiveOpenCallback", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE"/IArchiveOpenCallback")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE"/IProgress", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE"/IProgress")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE"/IArchiveExtractCallback", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE"/IArchiveExtractCallback")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE"/IOutStream", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE"/IOutStream")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE"/ISeekableStream", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE"/ISeekableStream")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE"/IOutCreateCallback", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE"/IOutCreateCallback")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE_IMPL"/InArchiveImpl", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE_IMPL"/InArchiveImpl")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(SEVEN_ZIP_PACKAGE_IMPL"/OutArchiveImpl", addClassLoaderObject(env, SEVEN_ZIP_PACKAGE_IMPL"/OutArchiveImpl")));

        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_OBJECT, addClassLoaderObject(env, JAVA_OBJECT)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_BYTE, addClassLoaderObject(env, JAVA_BYTE)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_CHARACTER, addClassLoaderObject(env, JAVA_CHARACTER)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_SHORT, addClassLoaderObject(env, JAVA_SHORT)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_NUMBER, addClassLoaderObject(env, JAVA_NUMBER)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_INTEGER, addClassLoaderObject(env, JAVA_INTEGER)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_LONG, addClassLoaderObject(env, JAVA_LONG)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_FLOAT, addClassLoaderObject(env, JAVA_FLOAT)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_DOUBLE, addClassLoaderObject(env, JAVA_DOUBLE)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_BOOLEAN, addClassLoaderObject(env, JAVA_BOOLEAN)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_STRING, addClassLoaderObject(env, JAVA_STRING)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_THROWABLE, addClassLoaderObject(env, JAVA_THROWABLE)));
        JBindingSession::_classLoaderObjects.insert(std::make_pair(JAVA_DATE, addClassLoaderObject(env, JAVA_DATE)));

#ifdef NATIVE_JUNIT_TEST_SUPPORT
        JBindingSession::_classLoaderObjects.insert(std::make_pair("net/sf/sevenzipjbinding/junit/jbindingtools/Callback1", addClassLoaderObject(env, "net/sf/sevenzipjbinding/junit/jbindingtools/Callback1")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair("net/sf/sevenzipjbinding/junit/jbindingtools/JBindingTest", addClassLoaderObject(env, "net/sf/sevenzipjbinding/junit/jbindingtools/JBindingTest")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair("net/sf/sevenzipjbinding/junit/jbindingtools/ExceptionHandlingTest", addClassLoaderObject(env, "net/sf/sevenzipjbinding/junit/jbindingtools/ExceptionHandlingTest")));

        JBindingSession::_classLoaderObjects.insert(std::make_pair("net/sf/sevenzipjbinding/junit/jnitools/JTestAbstractClass", addClassLoaderObject(env, "net/sf/sevenzipjbinding/junit/jnitools/JTestAbstractClass")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair("net/sf/sevenzipjbinding/junit/jnitools/JTestFinalClass", addClassLoaderObject(env, "net/sf/sevenzipjbinding/junit/jnitools/JTestFinalClass")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair("net/sf/sevenzipjbinding/junit/jnitools/Interface1", addClassLoaderObject(env, "net/sf/sevenzipjbinding/junit/jnitools/Interface1")));
        JBindingSession::_classLoaderObjects.insert(std::make_pair("net/sf/sevenzipjbinding/junit/jnitools/ParamSpecTest", addClassLoaderObject(env, "net/sf/sevenzipjbinding/junit/jnitools/ParamSpecTest")));
#endif
    }
    return JNI_VERSION_1_6;
}

jclass findClass(JNIEnv* env, const char* name) {
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
    }
    jstring string = env->NewStringUTF(name);
    jclass clazz = static_cast<jclass>(env->CallObjectMethod(JBindingSession::_classLoaderObjects.at(name), JBindingSession::_classLoaderID, string));
    env->DeleteLocalRef(string);
    return clazz;
}
#endif

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
#ifdef __ANDROID_API__
            _jniCallOriginalEnv->DeleteLocalRef(message);
#endif
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
