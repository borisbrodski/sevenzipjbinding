#include "StdAfx.h"

#include "jnitools.h"
#include "JavaSequentialOutStreamImpl.h"

void JavaSequentialOutStreamImpl::Init()
{
	char classname[1024];
	
	// public int write(byte[] data);
	this->WriteMethodID = env->GetMethodID(javaImplementationClass, "write", "([B)I");
	FATALIF1(this->WriteMethodID == NULL, "'int write(byte[] data)' method in class %s not found!",
			GetJavaClassName(env, javaImplementationClass, classname, sizeof(classname)));
}

STDMETHODIMP JavaSequentialOutStreamImpl::Write(const void *data, UInt32 size, UInt32 *processedSize)
{
	jbyteArray dataArray = env->NewByteArray(size);
	env->SetByteArrayRegion(dataArray, 0, (jsize)size, (const jbyte*)data);
	
	// public int write(byte[] data);
	env->ExceptionClear();
	jint result = env->CallIntMethod(this->javaImplementation, this->WriteMethodID, dataArray);
	if (env->ExceptionCheck()) {
		return S_FALSE;
	}
	*processedSize = (UInt32)result;
	return S_OK;
}

