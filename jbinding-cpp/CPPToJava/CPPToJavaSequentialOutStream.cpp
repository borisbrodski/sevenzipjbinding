#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaSequentialOutStream.h"

STDMETHODIMP CPPToJavaSequentialOutStream::Write(const void *data, UInt32 size, UInt32 *processedSize)
{
    TRACE_OBJECT_CALL("Write");

    if (size == 0)
    {
        *processedSize = 0;
        return S_OK;
    }

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

	jbyteArray dataArray = env->NewByteArray(size);
	env->SetByteArrayRegion(dataArray, 0, (jsize)size, (const jbyte*)data);


	// public int write(byte[] data);
	jniInstance.PrepareCall();
	jint result = env->CallIntMethod(_javaImplementation, _writeMethodID, dataArray);
	if (jniInstance.IsExceptionOccurs())
	{
	    jniInstance.GetEnv()->DeleteLocalRef(dataArray);
		return S_FALSE;
	}
    jniInstance.GetEnv()->DeleteLocalRef(dataArray);
	*processedSize = (UInt32)result;

    if (result <= 0)
    {
        jniInstance.ThrowSevenZipException("Implementation of 'int ISequentialOutStream.write(byte[])' "
                "should write at least one byte. Returned amount of written bytes: %i", result);
        return S_FALSE;
    }

	return S_OK;
}

