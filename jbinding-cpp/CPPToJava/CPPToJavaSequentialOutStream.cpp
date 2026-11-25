#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaSequentialOutStream.h"

STDMETHODIMP CPPToJavaSequentialOutStream::Write(const void *data, UInt32 size,
                                                 UInt32 *processedSize) noexcept {
    TRACE_OBJECT_CALL("Write");

    if (processedSize) {
        *processedSize = 0;
    }

    if (size == 0) {
        return S_OK;
    }

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jint result;
    if (_iReusableBufferOutStream) {
        if (_bufferLength < (jsize) size) {
            // Grow the buffer to the largest chunk seen; 7-Zip's chunks have a constant size per coder, so this
            // happens once or twice per stream.
            jbyteArray newBuffer = jniEnvInstance->NewByteArray((jsize) size);
            if (newBuffer == NULL) {
                jniEnvInstance.exceptionCheck(); // OutOfMemoryError
                return S_FALSE;
            }
            jbyteArray newGlobalBuffer = (jbyteArray) jniEnvInstance->NewGlobalRef(newBuffer);
            jniEnvInstance->DeleteLocalRef(newBuffer);
            if (newGlobalBuffer == NULL) {
                jniEnvInstance.exceptionCheck();
                return S_FALSE;
            }
            if (_buffer) {
                jniEnvInstance->DeleteGlobalRef(_buffer);
            }
            _buffer = newGlobalBuffer;
            _bufferLength = (jsize) size;
        }
        jniEnvInstance->SetByteArrayRegion(_buffer, 0, (jsize) size, (const jbyte*) data);

        // public int write(byte[] buffer, int offset, int length);
        result = _iReusableBufferOutStream->write(jniEnvInstance, _javaImplementation, _buffer, 0, (jint) size);
        if (jniEnvInstance.exceptionCheck()) {
            return S_FALSE;
        }
    } else {
        jbyteArray dataArray = jniEnvInstance->NewByteArray(size);
        if (dataArray == NULL) {
            jniEnvInstance.exceptionCheck(); // OutOfMemoryError
            return S_FALSE;
        }
        jniEnvInstance->SetByteArrayRegion(dataArray, 0, (jsize) size, (const jbyte*) data);

        // public int write(byte[] data);
        result = _iSequentialOutStream->write(jniEnvInstance, _javaImplementation, dataArray);
        jniEnvInstance->DeleteLocalRef(dataArray);
        if (jniEnvInstance.exceptionCheck()) {
            return S_FALSE;
        }
    }

    if (result <= 0) {
        jniEnvInstance.reportError("Implementation of 'int ISequentialOutStream.write(byte[])' "
            "should write at least one byte. Returned amount of written bytes: %i", result);
        return S_FALSE;
    }
    *processedSize = (UInt32) result;

    return S_OK;
}

