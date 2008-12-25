#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaInStream.h"


STDMETHODIMP CPPToJavaInStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition)
{
    TRACE_OBJECT_CALL("Seek")
    
    JNIEnv * env = BeginCPPToJavaCall();
    
	jlongArray newPositionArray = env->NewLongArray(1);
	FATALIF(newPositionArray == NULL, "Out of local resource of out of memory: newPositionArray == NULL");

	env->ExceptionClear();
	jint result = env->CallIntMethod(_javaImplementation, _seekMethodID, (jlong)offset, (jint)seekOrigin, newPositionArray);

	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
		env->DeleteLocalRef(newPositionArray);
		
		EndCPPToJavaCall();
		return S_FALSE;
	}

	if (result)
	{
		env->DeleteLocalRef(newPositionArray);
		
		EndCPPToJavaCall();
		return result;
	}
	
	jlong * newPositionArrayData = env->GetLongArrayElements(newPositionArray, NULL);
	if (newPosition)
	{
		*newPosition = (UInt64)*newPositionArrayData;
	}
	
	env->ReleaseLongArrayElements(newPositionArray, newPositionArrayData, JNI_ABORT);
	env->DeleteLocalRef(newPositionArray);

	EndCPPToJavaCall();
	return result;
}


