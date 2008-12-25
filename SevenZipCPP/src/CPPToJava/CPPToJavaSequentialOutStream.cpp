#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaSequentialOutStream.h"

STDMETHODIMP CPPToJavaSequentialOutStream::Write(const void *data, UInt32 size, UInt32 *processedSize)
{
    TRACE_OBJECT_CALL("Write")
    
    JNIEnv * env = BeginCPPToJavaCall();
    
	jbyteArray dataArray = env->NewByteArray(size);
	env->SetByteArrayRegion(dataArray, 0, (jsize)size, (const jbyte*)data);
	
	
	// public int write(byte[] data);
	env->ExceptionClear();
	jint result = env->CallIntMethod(_javaImplementation, _writeMethodID, dataArray);
	if (env->ExceptionCheck()) {
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}
	*processedSize = (UInt32)result;
	
    EndCPPToJavaCall();
	return S_OK;
}

