#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaOutStream.h"


STDMETHODIMP CPPToJavaOutStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition) noexcept {
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

STDMETHODIMP CPPToJavaOutStream::SetSize(UInt64 newSize) noexcept {
    TRACE_OBJECT_CALL("SetSize");

	TRACE("SetSize(size=" << newSize << ')');

    JNIEnvInstance jniEnvInstance(_jbindingSession);

	_iOutStream->setSize(jniEnvInstance, _javaImplementation, (jlong)newSize);

	return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaOutStream::Write(const void *data, UInt32 size, UInt32 *processedSize) noexcept {
    TRACE("WRITE(size=" << size << ")")
    HRESULT result = _sequentialOutStream.Write(data, size, processedSize);
#ifdef TRACE_ON
    if (processedSize) {
        TRACE("WRITE: size=" << size << ", was written:" << *processedSize << ", result:" << result);
    }
#endif
    return result;
}

