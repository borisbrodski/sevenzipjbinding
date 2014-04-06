#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaInStream.h"

STDMETHODIMP CPPToJavaInStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition) {
    TRACE_OBJECT_CALL("Seek");

    TRACE("SEEK(offset=" << offset << ", origin=" << seekOrigin << ")");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (newPosition) {
        *newPosition = 0;
    }

    jlong returnedNewPosition = _iSeekableStream->seek(jniEnvInstance, _javaImplementation,
            (jlong) offset, (jint) seekOrigin);

    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    if (newPosition) {
        *newPosition = (UInt64) returnedNewPosition;
    }

    TRACE("SEEK: New Pos: " << (UInt64)returnedNewPosition)

    return S_OK;
}

