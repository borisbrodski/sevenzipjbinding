#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaOutStream.h"


STDMETHODIMP CPPToJavaOutStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition)
{
    TRACE_OBJECT_CALL("Seek");

	TRACE2("SEEK(offset=%i, origin=%i)", (int)offset, (int)seekOrigin);

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

	TRACE1("SEEK: New Pos: %i", (int)(UInt64)returnedNewPosition)

	return S_OK;
}


