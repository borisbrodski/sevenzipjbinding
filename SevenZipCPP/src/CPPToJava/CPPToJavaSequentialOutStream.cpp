#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaSequentialOutStream.h"

STDMETHODIMP CPPToJavaSequentialOutStream::Write(const void *data, UInt32 size, UInt32 *processedSize)
{
    // JNI seems incapable to copy array region larger, that 8192 bytes
    // If someone finds out why, please let me know!
//	if (size > 8192) {
//	    size = 8192;
//	}
	
	jbyteArray dataArray = _env->NewByteArray(size);
	_env->SetByteArrayRegion(dataArray, 0, (jsize)size, (const jbyte*)data);
	
	
	// public int write(byte[] data);
	_env->ExceptionClear();
	jint result = _env->CallIntMethod(_javaImplementation, _writeMethodID, dataArray);
	if (_env->ExceptionCheck()) {
        SaveLastOccurredException(_env);
		return S_FALSE;
	}
	*processedSize = (UInt32)result;
	return S_OK;
}

