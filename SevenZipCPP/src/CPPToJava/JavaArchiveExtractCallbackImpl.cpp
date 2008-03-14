#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaArchiveExtractCallback.h"
#include "CPPToJavaSequentialOutStream.h"

void CPPToJavaArchiveExtractCallback::Init()
{
	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	_getStreamMethodID = GetMethodId("getStream",
			"(IL" EXTRACTASKMODE_CLASS ";)L" SEQUENTIALOUTSTREAM_CLASS ";");

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	_prepareOperationMethodID = GetMethodId("prepareOperation",
			"(L" EXTRACTASKMODE_CLASS ";)Z");

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	_setOperationResultMethodID = GetMethodId("setOperationResult",
			"(L" EXTRACTOPERATIONRESULT_CLASS ";)V");

	_extractOperationResultClass = GetClass(EXTRACTOPERATIONRESULT_CLASS);

	// public static ExtractOperationResult getOperationResult(int index)
	_extractOperationResultGetOperationResultMethodID = 
			GetStaticMethodId(_extractOperationResultClass, "getOperationResult", "(I)L" EXTRACTOPERATIONRESULT_CLASS ";");
	
	_extractAskModeClass = GetClass(EXTRACTASKMODE_CLASS);

	// public static ExtractAskMode getExtractAskModeByIndex(int index)
	_extractAskModeGetExtractAskModeByIndexMethodID = 
			GetStaticMethodId(_extractAskModeClass, "getExtractAskModeByIndex", "(I)L" EXTRACTASKMODE_CLASS ";");
}

/*
 STDMETHODIMP JavaArchiveExtractCallbackImpl::CryptoGetTextPassword(BSTR *password)
 {
 printf("> Asked for password!\n");
 *password = L"test1";
 return S_OK;
 }
 */

STDMETHODIMP CPPToJavaArchiveExtractCallback::GetStream(UInt32 index, ISequentialOutStream **outStream,
		Int32 askExtractMode)
{
	jobject askExtractModeObject = _env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID,
			(jint)askExtractMode);
	
	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	_env->ExceptionClear();
	jobject result = _env->CallObjectMethod(_javaImplementation, _getStreamMethodID, (jint)index, askExtractModeObject);
	if (_env->ExceptionCheck())
	{
		return S_FALSE;
	}

	if (result == NULL)
	{
		*outStream = NULL;
		return S_OK;
	}
	
	CMyComPtr<ISequentialOutStream> outStreamComPtr = new CPPToJavaSequentialOutStream(_env, result);
	*outStream = outStreamComPtr.Detach();
	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::PrepareOperation(Int32 askExtractMode)
{
	jobject askExtractModeObject = _env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID, (jint)askExtractMode);

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	_env->ExceptionClear();
	jboolean result = _env->CallBooleanMethod(_javaImplementation, _prepareOperationMethodID, askExtractModeObject);

	if (_env->ExceptionCheck() || !result)
	{
		return S_FALSE;
	}

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::SetOperationResult(Int32 resultEOperationResult)
{
	jobject resultEOperationResultObject = _env->CallStaticObjectMethod(_extractOperationResultClass, _extractOperationResultGetOperationResultMethodID, (jint)resultEOperationResult);

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	_env->ExceptionClear();
	_env->CallVoidMethod(_javaImplementation, _setOperationResultMethodID, resultEOperationResultObject);

	if (_env->ExceptionCheck())
	{
		return S_FALSE;
	}

	return S_OK;
}
