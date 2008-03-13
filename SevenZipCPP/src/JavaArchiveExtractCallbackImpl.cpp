#include "StdAfx.h"

#include "jnitools.h"
#include "JavaArchiveExtractCallbackImpl.h"
#include "JavaSequentialOutStreamImpl.h"

void JavaArchiveExtractCallbackImpl::Init()
{
	char classname[1024];

	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	this->GetStreamMethodID = env->GetMethodID(javaImplementationClass,
			"getStream",
			"(IL" EXTRACTASKMODE_CLASS ";)L" SEQUENTIALOUTSTREAM_CLASS ";");
	FATALIF1(this->GetStreamMethodID == NULL, "'SequentialOutStream getStream(int, ExtractAskMode)' method in class %s not found!",
			GetJavaClassName(env, javaImplementationClass, classname, sizeof(classname)));

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	this->PrepareOperationMethodID = env->GetMethodID(javaImplementationClass,
			"prepareOperation", "(L" EXTRACTASKMODE_CLASS ";)Z");
	FATALIF1(this->PrepareOperationMethodID == NULL, "'boolean prepareOperation(ExtractAskMode)' method in class %s not found!",
			GetJavaClassName(env, javaImplementationClass, classname, sizeof(classname)));

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	this->SetOperationResultMethodID = env->GetMethodID(
			javaImplementationClass, "setOperationResult",
			"(L" EXTRACTOPERATIONRESULT_CLASS ";)V");
	FATALIF1(this->SetOperationResultMethodID == NULL, "'void setOperationResult(ExtractOperationResult extractOperationResult)' method in class %s not found!",
			GetJavaClassName(env, javaImplementationClass, classname, sizeof(classname)));

	this->ExtractOperationResultClass
			= env->FindClass(EXTRACTOPERATIONRESULT_CLASS);
	FATALIF1(this->ExtractOperationResultClass == NULL, "Can't file java class '%s'", EXTRACTOPERATIONRESULT_CLASS);
	this->ExtractOperationResultClass
			= (jclass)env->NewGlobalRef(this->ExtractOperationResultClass);

	// public static ExtractOperationResult getOperationResult(int index)
	this->ExtractOperationResultGetOperationResultMethodID = 
			env->GetStaticMethodID(this->ExtractOperationResultClass, "getOperationResult", "(I)L" EXTRACTOPERATIONRESULT_CLASS ";");
	FATALIF1(this->ExtractOperationResultGetOperationResultMethodID == NULL, "'ExtractOperationResult getOperationResult(int index)' method in class %s not found!",
			GetJavaClassName(env, this->ExtractOperationResultClass, classname, sizeof(classname)));
	
	this->ExtractAskModeClass = env->FindClass(EXTRACTASKMODE_CLASS);
	FATALIF1(this->ExtractAskModeClass == NULL, "Can't file java class '%s'", EXTRACTASKMODE_CLASS);
	this->ExtractAskModeClass = (jclass)env->NewGlobalRef(this->ExtractAskModeClass);

	// public static ExtractAskMode getExtractAskModeByIndex(int index)
	this->ExtractAskModeGetExtractAskModeByIndexMethodID = 
			env->GetStaticMethodID(this->ExtractAskModeClass, "getExtractAskModeByIndex", "(I)L" EXTRACTASKMODE_CLASS ";");
	FATALIF1(this->SetOperationResultMethodID == NULL, "'public static ExtractAskMode getExtractAskModeByIndex(int index)' method in class %s not found!",
			GetJavaClassName(env, this->ExtractAskModeClass, classname, sizeof(classname)));
}

/*
 STDMETHODIMP JavaArchiveExtractCallbackImpl::CryptoGetTextPassword(BSTR *password)
 {
 printf("> Asked for password!\n");
 *password = L"test1";
 return S_OK;
 }
 */

STDMETHODIMP JavaArchiveExtractCallbackImpl::GetStream(UInt32 index, ISequentialOutStream **outStream,
		Int32 askExtractMode)
{
	jobject askExtractModeObject = env->CallStaticObjectMethod(this->ExtractAskModeClass, this->ExtractAskModeGetExtractAskModeByIndexMethodID,
			(jint)askExtractMode);
	
	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	env->ExceptionClear();
	jobject result = env->CallObjectMethod(this->javaImplementation, this->GetStreamMethodID, (jint)index, askExtractModeObject);
	if (env->ExceptionCheck())
	{
		return S_FALSE;
	}

	if (result == NULL)
	{
		*outStream = NULL;
		return S_OK;
	}
	
	CMyComPtr<ISequentialOutStream> outStreamComPtr = new JavaSequentialOutStreamImpl(env, result);
	*outStream = outStreamComPtr.Detach();
	return S_OK;
}

STDMETHODIMP JavaArchiveExtractCallbackImpl::PrepareOperation(Int32 askExtractMode)
{

	jobject askExtractModeObject = env->CallStaticObjectMethod(this->ExtractAskModeClass, this->ExtractAskModeGetExtractAskModeByIndexMethodID, (jint)askExtractMode);

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	env->ExceptionClear();
	jboolean result = env->CallBooleanMethod(this->javaImplementation, this->PrepareOperationMethodID, askExtractModeObject);

	if (env->ExceptionCheck() || !result)
	{
		return S_FALSE;
	}

	return S_OK;
}

STDMETHODIMP JavaArchiveExtractCallbackImpl::SetOperationResult(Int32 resultEOperationResult)
{
	jobject resultEOperationResultObject = env->CallStaticObjectMethod(this->ExtractOperationResultClass, this->ExtractOperationResultGetOperationResultMethodID, (jint)resultEOperationResult);

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	env->ExceptionClear();
	env->CallVoidMethod(this->javaImplementation, this->SetOperationResultMethodID, resultEOperationResultObject);

	if (env->ExceptionCheck())
	{
		return S_FALSE;
	}

	return S_OK;
}
