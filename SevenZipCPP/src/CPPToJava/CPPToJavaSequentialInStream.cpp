#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaSequentialInStream.h"


STDMETHODIMP CPPToJavaSequentialInStream::Read(void *data, UInt32 size, UInt32 *processedSize)
{
    TRACE_OBJECT_CALL("Read");
    
    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();
    
	jbyteArray byteArray = env->NewByteArray(size);
	FATALIF(byteArray == NULL, "Out of local resource of out of memory: byteArray == NULL") // TODO Change to EXCEPTION_IF()
	
	jintArray intArray = env->NewIntArray(1);
	FATALIF(intArray == NULL, "Out of local resource of out of memory: intArray == NULL");

	jniInstance.PrepareCall();
	jint result = env->CallIntMethod(_javaImplementation, _readMethodID, byteArray, intArray);

	if (jniInstance.IsExceptionOccurs())
	{
		env->DeleteLocalRef(byteArray);
		env->DeleteLocalRef(intArray);
		return S_FALSE;
	}
	
	if (result)
	{
		env->DeleteLocalRef(byteArray);
		env->DeleteLocalRef(intArray);
		
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

	return result;
}

