#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaInStream.h"


STDMETHODIMP CPPToJavaInStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition)
{
    TRACE_OBJECT_CALL("Seek");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

	jlongArray newPositionArray = env->NewLongArray(1);
	FATALIF(newPositionArray == NULL, "Out of local resource of out of memory: newPositionArray == NULL");

	jniInstance.PrepareCall();
	jint result = env->CallIntMethod(_javaImplementation, _seekMethodID, (jlong)offset, (jint)seekOrigin, newPositionArray);

	if (jniInstance.IsExceptionOccurs())
	{
		env->DeleteLocalRef(newPositionArray);
		return S_FALSE;
	}

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

	env->ReleaseLongArrayElements(newPositionArray, newPositionArrayData, JNI_ABORT);
	env->DeleteLocalRef(newPositionArray);

	return result;
}


