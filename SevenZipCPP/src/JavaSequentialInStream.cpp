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
	jintArray intArray = env->NewIntArray(1);
	jint result = env->CallIntMethod(SequentialInStream, ReadMethodID, byteArray, intArray);
	
	jint * read = env->GetIntArrayElements(intArray, NULL);
	*processedSize = (UInt32)*read;
	env->ReleaseIntArrayElements(intArray, read, 0);
	
	jbyte * buffer = env->GetByteArrayElements(byteArray, NULL);
	memcpy(data, buffer, size);
	env->ReleaseByteArrayElements(byteArray, buffer, 0);
	
	return result;
}

