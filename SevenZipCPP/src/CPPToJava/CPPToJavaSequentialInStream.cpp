#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaSequentialInStream.h"


STDMETHODIMP CPPToJavaSequentialInStream::Read(void *data, UInt32 size, UInt32 *processedSize)
{
	jbyteArray byteArray = _env->NewByteArray(size);
	FATALIF(byteArray == NULL, "Out of local resource of out of memory: byteArray == NULL")
	
	jintArray intArray = _env->NewIntArray(1);
	FATALIF(intArray == NULL, "Out of local resource of out of memory: intArray == NULL");

	_env->ExceptionClear();
	jint result = _env->CallIntMethod(_javaImplementation, _readMethodID, byteArray, intArray);
		
	if (_env->ExceptionCheck())
	{
		_env->DeleteLocalRef(byteArray);
		_env->DeleteLocalRef(intArray);
		return S_FALSE;
	}
	
	if (result)
	{
		_env->DeleteLocalRef(byteArray);
		_env->DeleteLocalRef(intArray);
		return result;
	}
	
	jint * read = _env->GetIntArrayElements(intArray, NULL);
	if (processedSize)
	{
		*processedSize = (UInt32)*read;
	}
	
	jbyte * buffer = _env->GetByteArrayElements(byteArray, NULL);
	memcpy(data, buffer, size);
	_env->ReleaseByteArrayElements(byteArray, buffer, JNI_ABORT);
	
	_env->DeleteLocalRef(byteArray);
	_env->DeleteLocalRef(intArray);
	_env->ReleaseIntArrayElements(intArray, read, JNI_ABORT);

//	printf("Success read %i\n", *processedSize);
//	fflush(stdout);
//	
//	for (int i = 0; i < *processedSize; i++)
//	{
//		if (i%32==31)
//		{
//			printf("\n");
//		}
//		printf("%02X ", 0xFF & ((char*)data)[i]);
//	}
//	printf("\n");
//	fflush(stdout);
	
	return result;
}

