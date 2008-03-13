#include "StdAfx.h"

#include "jnitools.h"
#include "JavaProgressImpl.h"


void JavaProgressImpl::Init()
{
	char classname[1024];
	
	this->SetCompletedMethodID = env->GetMethodID(javaImplementationClass, "setCompleted", "(J)V");
	FATALIF1(this->SetCompletedMethodID == NULL, "'void setCompleted(long)' method in class %s not found!",
			GetJavaClassName(env, javaImplementationClass, classname, sizeof(classname)));

	this->SetTotalMethodID = env->GetMethodID(javaImplementationClass, "setTotal", "(J)V");
	FATALIF1(this->SetTotalMethodID == NULL, "'void setTotal(long)' method in class %s not found!",
			GetJavaClassName(env, javaImplementationClass, classname, sizeof(classname)));
}

STDMETHODIMP JavaProgressImpl::SetCompleted(const UInt64 * completeValue)
{
	env->ExceptionClear();
	env->CallVoidMethod(this->javaImplementation, this->SetCompletedMethodID, (jlong)(*completeValue));
	if (env->ExceptionCheck())
	{
		return S_FALSE;
	}
	
	return S_OK;
}

STDMETHODIMP JavaProgressImpl::SetTotal(UINT64 total)
{
	env->ExceptionClear();
	env->CallVoidMethod(this->javaImplementation, this->SetTotalMethodID, (jlong)total);
	if (env->ExceptionCheck())
	{
		return S_FALSE;
	}
	
	return S_OK;
}

