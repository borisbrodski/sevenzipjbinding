#ifndef JNICALLSTATE_H_
#define JNICALLSTATE_H_

#include <map>
#include "JNITools.h"
#include "SevenZipException.h"
#include "SevenZipJBinding.h"

#ifdef COMPRESS_MT
#include "Windows/Synchronization.h"
#endif

using namespace std;

class NativeMethodContext;
class JNIInstance;

class ThreadInfo
{
    friend class NativeMethodContext;
private:
    int _callCounter;
    JNIEnv * _env;

    ThreadInfo(JNIEnv * env)
    {
        _callCounter = 1;
        _env = env;
    }
};
#if defined(COMPRESS_MT) || defined(COMPRESS_BZIP2_MT) || defined(COMPRESS_MF_MT) || defined(BENCH_MT)
#include <pthread.h>
inline DWORD GetCurrentThreadId() {
	return pthread_self();
}
#else
inline DWORD GetCurrentThreadId() {
	return 0;
}
#endif

class NativeMethodContext : public StackAllocatedObject
{
friend class JNIInstance;
private:
    JavaVM * _vm;
    DWORD _initThreadId;
    JNIEnv * _initEnv;
    map<DWORD, ThreadInfo *> _threadInfoMap;
    jthrowable _lastOccurredException;
    char * _firstThrowenExceptionMessage;
#ifdef COMPRESS_MT
    NWindows::NSynchronization::CCriticalSection _criticalSection;
#endif
public:
    NativeMethodContext(JNIEnv * initEnv)
	{
        // No SevenZipException can be thrown here

        TRACE_OBJECT_CREATION("NativeMethodContext");

        _lastOccurredException = NULL;
        _firstThrowenExceptionMessage = NULL;
	    _initEnv = initEnv;
	    _initThreadId = GetCurrentThreadId();
	    TRACE2("_initThreadId = %lu, this=0x%08X", (unsigned long int)_initThreadId, (size_t)this)
	    _vm = NULL;
	    if (initEnv->GetJavaVM(&_vm))
	    {
	        fatal("Can't get JavaVM from env");
	    }
	}
    ~NativeMethodContext()
    {
        JNIThrowException(_initEnv);

        if (_lastOccurredException)
        {
            _initEnv->DeleteGlobalRef(_lastOccurredException);
        }

        if (_firstThrowenExceptionMessage)
        {
            free(_firstThrowenExceptionMessage);
        }

        TRACE_OBJECT_CALL("~NativeMethodContext");
        TRACE_CLASS_CHECK_UNKNOWN_IMPL_DESTRUCTION("NativeMethodContext");
    }

	JNIEnv * BeginCPPToJava();

	void EndCPPToJava();

    void ThrowSevenZipException(HRESULT hresult, const char * fmt, ...);
    void ThrowSevenZipException(const char * fmt, ...);
    void _VThrowSevenZipException(HRESULT hresult, const char * fmt, va_list args);
    void _VThrowSevenZipException(const char * fmt, va_list args);
    void ThrowSevenZipException(SevenZipException * exception);

private:
    void JNIThrowException(JNIEnv * env);
    void ThrowSevenZipExceptionWithMessage(char * message);
    void SaveLastOccurredException(JNIEnv * env);
};

class JNIInstance : public Object
{
private:
    JNIEnv * _env;
    CMyComPtr<NativeMethodContext> _nativeMethodContext;

public:
    JNIInstance(NativeMethodContext * nativeMethodContext)
    {
        TRACE_OBJECT_CREATION("JNIInstance");

        _nativeMethodContext = nativeMethodContext;
        _env = _nativeMethodContext->BeginCPPToJava(); // TODO rename method to something like "BeginJNISession"
    }
    ~JNIInstance()
    {
        TRACE_OBJECT_CALL("~JNIInstance");
        _nativeMethodContext->EndCPPToJava(); // TODO rename method to something like EndJNISession
    }

    JNIEnv * GetEnv()
    {
        TRACE_OBJECT_CALL("GetEnv");
        return _env;
    }

    void PrepareCall()
    {
        TRACE_OBJECT_CALL("PrepareCall");
        _env->ExceptionClear();
    }

    /**
     * Check, if an exception after a jni call occurs.
     * In this case the exception will be saved to use it as a "cause".
     *
     * Return: 0 - no exception occurs, else otherwise
     */
    int IsExceptionOccurs()
    {
        TRACE_OBJECT_CALL("CheckException");
        if (_env->ExceptionCheck())
        {
            _nativeMethodContext->SaveLastOccurredException(_env);
            return 1;
        }

        return 0;
    }
    void ThrowSevenZipException(HRESULT hresult, const char * fmt, ...)
    {
        va_list args;
        va_start(args, fmt);
        _nativeMethodContext->_VThrowSevenZipException(hresult, fmt, args);
        va_end(args);
    }
    void ThrowSevenZipException(const char * fmt, ...)
    {
        va_list args;
        va_start(args, fmt);
        _nativeMethodContext->_VThrowSevenZipException(fmt, args);
        va_end(args);
    }
    void ThrowSevenZipException(SevenZipException * exception)
    {
        _nativeMethodContext->ThrowSevenZipException(exception);
    }
};

#endif /*JNICALLSTATE_H_*/
