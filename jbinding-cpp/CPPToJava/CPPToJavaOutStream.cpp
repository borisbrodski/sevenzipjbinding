#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaOutStream.h"


STDMETHODIMP CPPToJavaOutStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition) {
    TRACE_OBJECT_CALL("Seek");

	TRACE("SEEK(offset=" << offset << ", origin=" << seekOrigin << ")");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    if (newPosition) {
    	*newPosition = 0;
    }

    jniInstance.PrepareCall();
	jlong returnedNewPosition = env->CallIntMethod(_javaImplementation, _seekMethodID, (jlong)offset, (jint)seekOrigin);

	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	if (newPosition) {
		*newPosition = (UInt64)returnedNewPosition;
	}

	TRACE("SEEK: New Pos: " << (UInt64)returnedNewPosition)

	return S_OK;
}

STDMETHODIMP CPPToJavaOutStream::SetSize(Int64 newSize) {
    TRACE_OBJECT_CALL("SetSize");

	TRACE("SetSize(size=" << newSize << ')');

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    jniInstance.PrepareCall();
	env->CallVoidMethod(_javaImplementation, _setSizeMethodID, (jlong)newSize);

	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	return S_OK;
}

