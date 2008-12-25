#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaProgress.h"


STDMETHODIMP CPPToJavaProgress::SetCompleted(const UInt64 * completeValue)
{
    TRACE_OBJECT_CALL("SetCompleted")
    
    JNIEnv * env = BeginCPPToJavaCall();

    env->ExceptionClear();
	env->CallVoidMethod(_javaImplementation, _setCompletedMethodID, (jlong)(*completeValue));
	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}
	
    EndCPPToJavaCall();
	return S_OK;
}

STDMETHODIMP CPPToJavaProgress::SetTotal(UINT64 total)
{
    TRACE_OBJECT_CALL("SetTotal")
    
    JNIEnv * env = BeginCPPToJavaCall();
    
	env->ExceptionClear();
	env->CallVoidMethod(_javaImplementation, _setTotalMethodID, (jlong)total);
	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}
	
    EndCPPToJavaCall();
	return S_OK;
}

