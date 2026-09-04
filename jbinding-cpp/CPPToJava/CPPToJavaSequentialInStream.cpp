#include "SevenZipJBinding.h"

#include <cstring>

#include "JBindingTools.h"
#include "CPPToJavaSequentialInStream.h"


STDMETHODIMP CPPToJavaSequentialInStream::Read(void *data, UInt32 size, UInt32 *processedSize) noexcept
{
    TRACE_OBJECT_CALL("Read");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (processedSize) {
    	*processedSize = 0;
    }

	jbyteArray byteArray = jniEnvInstance->NewByteArray(size);

	if (!byteArray) {
	    jniEnvInstance.reportError("Out of local resources or out of memory");
	}

	jint wasRead = _iSequentialInStream->read(jniEnvInstance, _javaImplementation, byteArray);
	if (jniEnvInstance.exceptionCheck())
	{
	    jniEnvInstance->DeleteLocalRef(byteArray);
		return S_FALSE;
	}

	if (processedSize)
	{
		*processedSize = (UInt32)wasRead;
	}

	jbyte * buffer = jniEnvInstance->GetByteArrayElements(byteArray, NULL);
	memcpy(data, buffer, wasRead);
	jniEnvInstance->ReleaseByteArrayElements(byteArray, buffer, JNI_ABORT);

	jniEnvInstance->DeleteLocalRef(byteArray);

	return S_OK;
}

