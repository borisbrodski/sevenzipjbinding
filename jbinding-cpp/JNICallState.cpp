#include "SevenZipJBinding.h"

#include "JNICallState.h"

static struct
{
    HRESULT errCode;
    const char * message;
} SevenZipErrorMessages [] =
{
        { S_OK, "OK" }, // ((HRESULT)0x00000000L)
        { S_FALSE, "FALSE" }, // ((HRESULT)0x00000001L)
        { E_NOTIMPL, "Not implemented" }, // ((HRESULT)0x80004001L)
        { E_NOINTERFACE, "No interface" }, // ((HRESULT)0x80004002L)
        { E_ABORT, "Abort" }, // ((HRESULT)0x80004004L)
        { E_FAIL, "Fail" }, // ((HRESULT)0x80004005L)
        { STG_E_INVALIDFUNCTION, "Invalid function" }, // ((HRESULT)0x80030001L)
        { E_OUTOFMEMORY, "Out of memory" }, // ((HRESULT)0x8007000EL)
        { E_INVALIDARG, "Invalid argument" }, // ((HRESULT)0x80070057L)
        { 0, NULL },
};

/*
 * Return error message from error code
 */
static const char * GetSevenZipErrorMessage(HRESULT hresult)
{
    for (int i = 0; SevenZipErrorMessages[i].message != NULL; i++)
    {
        if (SevenZipErrorMessages[i].errCode == hresult)
        {
            return SevenZipErrorMessages[i].message;
        }
    }
    return "Unknown error code";
}

JNIEnv * NativeMethodContext::BeginCPPToJava()
{
    TRACE_OBJECT_CALL("BeginCPPToJava")

    DWORD currentThreadId = GetCurrentThreadId();

    if (currentThreadId == _initThreadId)
    {
        return _initEnv;
    }

    if (_threadInfoMap.find(currentThreadId) == _threadInfoMap.end())
    {
        JNIEnv * env;
        TRACE2("JNIEnv* was requested from other thread. Current threadId=%lu, initThreadId=%lu", currentThreadId, _initThreadId)
        jint result = _vm->GetEnv((void**)&env, JNI_VERSION_1_6);
        if (result == JNI_OK) {
            TRACE("Current thread is already attached")
            return env;
        }
        TRACE("Attaching current thread to VM.")
        if ((result = _vm->AttachCurrentThread((void**)&env, NULL)) || env == NULL)
        {
            TRACE1("New thread couldn't be attached: %li", result)
            throw SevenZipException("Can't attach current thread (id: %i) to the VM", currentThreadId);
        }
        TRACE1("Thread attached. New env=0x%08X", (size_t)env);

        ThreadInfo * threadInfo = new ThreadInfo(env);
        _threadInfoMap[currentThreadId] = threadInfo;

        return env;
    } else {
        ThreadInfo * threadInfo = _threadInfoMap[currentThreadId];
        threadInfo->_callCounter++;
        TRACE1("Begin => deattaching counter: %i", threadInfo->_callCounter)

        return threadInfo->_env;
    }
}

void NativeMethodContext::EndCPPToJava()
{
    TRACE_OBJECT_CALL("EndCPPToJava")

    DWORD currentThreadId = GetCurrentThreadId();

    if (currentThreadId == _initThreadId)
    {
        return;
    }
#ifdef _DEBUG
    if (_threadInfoMap.find(currentThreadId) == _threadInfoMap.end())
    {
        TRACE1("EndCPPToJava(): unknown current thread (id: %i)", (size_t)currentThreadId)
        throw SevenZipException("EndCPPToJava(): unknown current thread (id: %i)", currentThreadId);
    }
#endif //_DEBUG

    ThreadInfo * threadInfo = _threadInfoMap[currentThreadId];
    if (--threadInfo->_callCounter <= 0)
    {
        TRACE("End => Deataching current thread from JavaVM")
        _vm->DetachCurrentThread();
        _threadInfoMap.erase(currentThreadId);
        delete threadInfo;
    }
    else
    {
        TRACE1("End => deattaching counter: %i", threadInfo->_callCounter)
    }
}

/**
 * Save last occurred exception '_env->ExceptionOccurred()'
 * in global variable. Next call to ThrowSevenZipException(...) will set
 * 'lastOccurredException' as cause.
 *
 * If _env->ExceptionOccurred() returns NULL,
 * last occurred exception will be set to NULL.
 */
void NativeMethodContext::SaveLastOccurredException(JNIEnv * env)
{
    TRACE_OBJECT_CALL("SaveLastOccurredException")

    if (_lastOccurredException)
    {
        env->DeleteGlobalRef(_lastOccurredException);
    }

    jthrowable lastOccurredException = env->ExceptionOccurred();
    if (lastOccurredException)
    {
        _lastOccurredException = (jthrowable)env->NewGlobalRef(lastOccurredException);
    }
    else
    {
        _lastOccurredException = NULL;
    }
}

void NativeMethodContext::ThrowSevenZipExceptionWithMessage(char * message)
{
    TRACE_OBJECT_CALL("ThrowSevenZipExceptionWithMessage");

    if (_firstThrowenExceptionMessage == NULL)
    {
        TRACE1("SET: Setting new 'first throwen exception message' to '%s'", message);
        _firstThrowenExceptionMessage = strdup(message);
    }
    else
    {
        TRACE1("IGNORE: 'first throwen exception message' already set. New exception with message '%s' will be ignored", message);
    }
}

void NativeMethodContext::JNIThrowException(JNIEnv * env)
{
    TRACE_OBJECT_CALL("JNIThrowException");

    if (_lastOccurredException && _firstThrowenExceptionMessage == NULL)
    {
        TRACE("_lastOccurredException is not NULL, but _firstThrowenExceptionMessage is. Throwing last occured exception directly");
        env->Throw(_lastOccurredException);
        return;
    }

    if (!_firstThrowenExceptionMessage)
    {
        TRACE("_firstThrowenExceptionMessage is NULL. Nothing to throw.");
        return;
    }

    TRACE2("Throwing new exception with text '%s' and cause 0x%08X", _firstThrowenExceptionMessage, (size_t)_lastOccurredException);

    jclass exceptionClass = env->FindClass(SEVEN_ZIP_EXCEPTION);
    FATALIF(exceptionClass == NULL, "SevenZipException class '" SEVEN_ZIP_EXCEPTION "' can't be found");

    jstring messageString = env->NewStringUTF(_firstThrowenExceptionMessage);

    jmethodID constructorId = env->GetMethodID(exceptionClass, "<init>", "(" JAVA_STRING_T JAVA_THROWABLE_T ")V");
    FATALIF(constructorId == NULL, "Can't find " SEVEN_ZIP_EXCEPTION "(String, Throwable) constructor")

    jthrowable exception = (jthrowable)env->NewObject(exceptionClass, constructorId, messageString, _lastOccurredException);
    FATALIF(exception == NULL, SEVEN_ZIP_EXCEPTION " can't be created");

    env->ReleaseStringUTFChars(messageString, _firstThrowenExceptionMessage);
    env->Throw(exception);
}

/**
 * Throw SevenZipException with error message.
 */
void NativeMethodContext::ThrowSevenZipException(const char * fmt, ...)
{
    TRACE_OBJECT_CALL("ThrowSevenZipException(char *, ...)");

    va_list args;
    va_start(args, fmt);
    _VThrowSevenZipException(fmt, args);
    va_end(args);

}

void NativeMethodContext::_VThrowSevenZipException(const char * fmt, va_list args)
{
    TRACE_OBJECT_CALL("_VThrowSevenZipException(char *, va_list)");

    char buffer[64 * 1024];
    vsnprintf(buffer, sizeof(buffer), fmt, args);
    buffer[sizeof(buffer) - 1] = '\0';
    ThrowSevenZipExceptionWithMessage(buffer);
}

/**
 * Throw SevenZipException with error message.
 */
void NativeMethodContext::ThrowSevenZipException(HRESULT hresult, const char * fmt, ...)
{
    TRACE_OBJECT_CALL("ThrowSevenZipException(HRESULT, char *, ...)");

    va_list args;
    va_start(args, fmt);
    _VThrowSevenZipException(hresult, fmt, args);
    va_end(args);
}

void NativeMethodContext::_VThrowSevenZipException(HRESULT hresult, const char * fmt, va_list args)
{
    TRACE_OBJECT_CALL("_VThrowSevenZipException(HRESULT, char *, va_list)");

    char buffer[64 * 1024];

    snprintf(buffer, sizeof(buffer), "HRESULT: 0x%X (%s). ", (int)hresult, GetSevenZipErrorMessage(hresult));
    int beginIndex = strlen(buffer);
    vsnprintf(&buffer[beginIndex], sizeof(buffer) - beginIndex, fmt, args);
    buffer[sizeof(buffer) - 1] = '\0';

    ThrowSevenZipExceptionWithMessage(buffer);
}


void NativeMethodContext::ThrowSevenZipException(SevenZipException * sevenZipException)
{
    TRACE_OBJECT_CALL("ThrowSevenZipException(SevenZipException)");

    if (sevenZipException->GetHResult())
    {
        this->ThrowSevenZipException(sevenZipException->GetHResult(), "%s", sevenZipException->GetMessage());
    }
    else
    {
        this->ThrowSevenZipExceptionWithMessage(sevenZipException->GetMessage());
    }
}


