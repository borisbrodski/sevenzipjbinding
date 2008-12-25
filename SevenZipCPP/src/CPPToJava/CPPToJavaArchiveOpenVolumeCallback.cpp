#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaInStream.h"

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetProperty(PROPID propID, PROPVARIANT *value)
{
    TRACE_OBJECT_CALL("GetProperty")
    
    JNIEnv * env = BeginCPPToJavaCall();
    
	env->ExceptionClear();
	jobject result = env->CallObjectMethod(_javaImplementation, _getPropertyMethodID, (jint)propID);
	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}
	
	// TODO Convert object to Variant
	
    EndCPPToJavaCall();
	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetStream(const wchar_t *name, IInStream **inStream)
{
    TRACE_OBJECT_CALL("GetStream")
    
    JNIEnv * env = BeginCPPToJavaCall();
    jstring nameString = env->NewString((jchar *)name, (jsize)wcslen(name));
    
	env->ExceptionClear();
	jobject inStreamImpl = env->CallObjectMethod(_javaImplementation, _getStreamMethodID, nameString);
	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}

	if (inStream)
	{
	    CMyComPtr<IInStream> inStreamComPtr = new CPPToJavaInStream(_vm, env, inStreamImpl);
	    *inStream = inStreamComPtr.Detach();
	}
	
    EndCPPToJavaCall();
	return S_OK;
}

