#include <jni.h>
#include "jnitools.h"

#include "JavaSequentialInStream.h"


void JavaSequentialInStream::Init(JNIEnv * env, jobject sequentialInStream)
{
	char classname[1024];
	
	this->env = env;
	this->SequentialInStream = sequentialInStream;
	
	jclass clazz = env->GetObjectClass(sequentialInStream);
	this->ReadMethodID = env->GetMethodID(clazz, "read", "([B[I)I");
	
	FATALIF1(this->ReadMethodID == NULL, "'int read(byte [], int [])' in class %s method not found!",
			GetJavaClassName(env, clazz, classname, sizeof(classname)));

}

STDMETHODIMP JavaSequentialInStream::Read(void *data, UInt32 size, UInt32 *processedSize)
{
	jbyteArray byteArray = env->NewByteArray(size);
	FATALIF(byteArray == NULL, "Out of local resource of out of memory: byteArray == NULL")
	
	jintArray intArray = env->NewIntArray(1);
	FATALIF(intArray == NULL, "Out of local resource of out of memory: intArray == NULL");
	
	jint result = env->CallIntMethod(SequentialInStream, ReadMethodID, byteArray, intArray);
		
	if (result)
	{
		env->DeleteLocalRef(byteArray);
		env->DeleteLocalRef(intArray);
		return result;
	}
	
	jint * read = env->GetIntArrayElements(intArray, NULL);
	*processedSize = (UInt32)*read;
	env->ReleaseIntArrayElements(intArray, read, JNI_ABORT);
	
	if (*processedSize == 0)
	{
		env->DeleteLocalRef(byteArray);
		env->DeleteLocalRef(intArray);
		return 0;
	}
	
	jbyte * buffer = env->GetByteArrayElements(byteArray, NULL);
	memcpy(data, buffer, size);
	env->ReleaseByteArrayElements(byteArray, buffer, JNI_ABORT);
	
	env->DeleteLocalRef(byteArray);
	env->DeleteLocalRef(intArray);

	return result;
}

