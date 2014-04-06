#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaSequentialOutStream.h"

STDMETHODIMP CPPToJavaSequentialOutStream::Write(const void *data, UInt32 size,
                                                 UInt32 *processedSize) {
    TRACE_OBJECT_CALL("Write");

    if (processedSize) {
        *processedSize = 0;
    }

    if (size == 0) {
        return S_OK;
    }

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jbyteArray dataArray = jniEnvInstance->NewByteArray(size);
    jniEnvInstance->SetByteArrayRegion(dataArray, 0, (jsize) size, (const jbyte*) data);

    // public int write(byte[] data);
    jint result = _iSequentialOutStream->write(jniEnvInstance, _javaImplementation, dataArray);
    if (jniEnvInstance.exceptionCheck()) {
        jniEnvInstance->DeleteLocalRef(dataArray);
        return S_FALSE;
    }
    jniEnvInstance->DeleteLocalRef(dataArray);
    *processedSize = (UInt32) result;

    if (result <= 0) {
        jniEnvInstance.reportError("Implementation of 'int ISequentialOutStream.write(byte[])' "
            "should write at least one byte. Returned amount of written bytes: %i", result);
        return S_FALSE;
    }

    return S_OK;
}

