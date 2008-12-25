#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaSequentialInStream.h"


STDMETHODIMP CPPToJavaSequentialInStream::Read(void *data, UInt32 size, UInt32 *processedSize)
{
    TRACE_OBJECT_CALL("Read")
    
    JNIEnv * env = BeginCPPToJavaCall();
    
	jbyteArray byteArray = env->NewByteArray(size);
	FATALIF(byteArray == NULL, "Out of local resource of out of memory: byteArray == NULL")
	
	jintArray intArray = env->NewIntArray(1);
	FATALIF(intArray == NULL, "Out of local resource of out of memory: intArray == NULL");

	env->ExceptionClear();
	jint result = env->CallIntMethod(_javaImplementation, _readMethodID, byteArray, intArray);
		
	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
		env->DeleteLocalRef(byteArray);
		env->DeleteLocalRef(intArray);
		
		EndCPPToJavaCall();
		return S_FALSE;
	}
	
	if (result)
	{
		env->DeleteLocalRef(byteArray);
		env->DeleteLocalRef(intArray);
		
		EndCPPToJavaCall();
		return result;
	}
	
	jint * read = env->GetIntArrayElements(intArray, NULL);
	if (processedSize)
	{
		*processedSize = (UInt32)*read;
	}
	
	jbyte * buffer = env->GetByteArrayElements(byteArray, NULL);
	memcpy(data, buffer, size);
	env->ReleaseByteArrayElements(byteArray, buffer, JNI_ABORT);
	
	env->DeleteLocalRef(byteArray);
	env->DeleteLocalRef(intArray);
	env->ReleaseIntArrayElements(intArray, read, JNI_ABORT);

	EndCPPToJavaCall();
	return result;
}

