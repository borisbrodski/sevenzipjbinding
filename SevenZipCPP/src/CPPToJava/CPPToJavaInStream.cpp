#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaInStream.h"


STDMETHODIMP CPPToJavaInStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition)
{
	jlongArray newPositionArray = _env->NewLongArray(1);
	FATALIF(newPositionArray == NULL, "Out of local resource of out of memory: newPositionArray == NULL");

	_env->ExceptionClear();
	jint result = _env->CallIntMethod(_javaImplementation, _seekMethodID, (jlong)offset, (jint)seekOrigin, newPositionArray);

	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
		_env->DeleteLocalRef(newPositionArray);
		return S_FALSE;
	}

	if (result)
	{
		_env->DeleteLocalRef(newPositionArray);
		return result;
	}
	
	jlong * newPositionArrayData = _env->GetLongArrayElements(newPositionArray, NULL);
	if (newPosition)
	{
		*newPosition = (UInt64)*newPositionArrayData;
	}
	
	_env->ReleaseLongArrayElements(newPositionArray, newPositionArrayData, JNI_ABORT);
	_env->DeleteLocalRef(newPositionArray);

	return result;
}


