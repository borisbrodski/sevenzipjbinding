#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaSequentialOutStream.h"

STDMETHODIMP CPPToJavaSequentialOutStream::Write(const void *data, UInt32 size, UInt32 *processedSize)
{
    TRACE_OBJECT_CALL("Write");
    
    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();
    
	jbyteArray dataArray = env->NewByteArray(size);
	env->SetByteArrayRegion(dataArray, 0, (jsize)size, (const jbyte*)data);
	
	
	// public int write(byte[] data);
	jniInstance.PrepareCall();
	jint result = env->CallIntMethod(_javaImplementation, _writeMethodID, dataArray);
	if (jniInstance.IsExceptionOccurs())
	{
		return S_FALSE;
	}
	*processedSize = (UInt32)result;
	
	return S_OK;
}

