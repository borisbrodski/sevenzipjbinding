#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaInStream.h"


STDMETHODIMP CPPToJavaInStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition)
{
    TRACE_OBJECT_CALL("Seek");

	TRACE2("SEEK(offset=%i, origin=%i)", (int)offset, (int)seekOrigin);

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

	jlongArray newPositionArray = env->NewLongArray(1);
	FATALIF(newPositionArray == NULL, "Out of local resource of out of memory: newPositionArray == NULL");

	TRACE("1")
	jniInstance.IsExceptionOccurs();
	TRACE("1.5")
	jniInstance.PrepareCall();
	TRACE("2")
	jint result = env->CallIntMethod(_javaImplementation, _seekMethodID, (jlong)offset, (jint)seekOrigin, newPositionArray);

	TRACE("3")
	if (jniInstance.IsExceptionOccurs())
	{
		env->DeleteLocalRef(newPositionArray);
		return S_FALSE;
	}

	TRACE("2")
	if (result)
	{
		env->DeleteLocalRef(newPositionArray);
		return result;
	}

	jlong * newPositionArrayData = env->GetLongArrayElements(newPositionArray, NULL);
	if (newPosition)
	{
		*newPosition = (UInt64)*newPositionArrayData;
	}
	TRACE1("SEEK: New Pos: %i", (int)(UInt64)*newPositionArrayData)

	env->ReleaseLongArrayElements(newPositionArray, newPositionArrayData, JNI_ABORT);
	env->DeleteLocalRef(newPositionArray);

	return result;
}


