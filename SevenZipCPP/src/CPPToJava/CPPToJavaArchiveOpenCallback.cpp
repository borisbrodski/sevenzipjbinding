#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaArchiveOpenCallback.h"


STDMETHODIMP CPPToJavaArchiveOpenCallback::SetCompleted(const UInt64 *files, const UInt64 *bytes)
{
    TRACE_OBJECT_CALL("SetCompleted")
    
    JNIEnv * env = BeginCPPToJavaCall();

    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;
    
    if (files)
    {
        filesLongObject = LongToObject(env, *files);
    }
    
    if (bytes)
    {
        bytesLongObject = LongToObject(env, *bytes);
    }
    
	env->ExceptionClear();
	env->CallVoidMethod(_javaImplementation, _setCompletedMethodID, filesLongObject, bytesLongObject);
	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}
	
    EndCPPToJavaCall();
	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenCallback::SetTotal(const UInt64 *files, const UInt64 *bytes)
{
    TRACE_OBJECT_CALL("SetTotal")
    
    JNIEnv * env = BeginCPPToJavaCall();

    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;
    
    if (files)
    {
        filesLongObject = LongToObject(env, *files);
    }
    
    if (bytes)
    {
        bytesLongObject = LongToObject(env, *bytes);
    }

	env->ExceptionClear();
	env->CallVoidMethod(_javaImplementation, _setTotalMethodID, filesLongObject, bytesLongObject);
	if (env->ExceptionCheck())
	{
	    SaveLastOccurredException(env);
	    
	    EndCPPToJavaCall();
		return S_FALSE;
	}
	
    EndCPPToJavaCall();
	return S_OK;
}

