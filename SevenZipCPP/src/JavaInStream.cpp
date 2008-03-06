#include "StdAfx.h"

#include "jnitools.h"
#include "JavaInStream.h"


void JavaInStream::Init()
{
	char classname[1024];

	this->SeekMethodID = env->GetMethodID(javaImplementationClass, "seek", "(JI[J)I");
	
	FATALIF1(this->SeekMethodID == NULL, "'int seek(long, int, long[])' method in class %s not found!",
			GetJavaClassName(env, javaImplementationClass, classname, sizeof(classname)));
}

STDMETHODIMP JavaInStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition)
{
	jlongArray newPositionArray = env->NewLongArray(1);
	FATALIF(newPositionArray == NULL, "Out of local resource of out of memory: newPositionArray == NULL");

	env->ExceptionClear();
	jint result = env->CallIntMethod(javaImplementation, SeekMethodID, (jlong)offset, (jint)seekOrigin, newPositionArray);

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


