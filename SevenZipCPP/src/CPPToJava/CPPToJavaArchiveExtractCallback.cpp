#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaArchiveExtractCallback.h"
#include "CPPToJavaSequentialOutStream.h"

void CPPToJavaArchiveExtractCallback::Init()
{
    _cryptoGetTextPasswordImpl = NULL;
    
    jclass cryptoGetTextPasswordClass = _env->FindClass(CRYPTOGETTEXTPASSWORD_CLASS);
    FATALIF(cryptoGetTextPasswordClass == NULL,
            "Can't find class " CRYPTOGETTEXTPASSWORD_CLASS);

    if (_env->IsInstanceOf(_javaImplementation, cryptoGetTextPasswordClass))
    {
        CMyComPtr<ICryptoGetTextPassword> cryptoGetTextPasswordComPtr = 
            new CPPToJavaCryptoGetTextPassword(_env, _javaImplementation);
        _cryptoGetTextPasswordImpl = cryptoGetTextPasswordComPtr.Detach();
    }
    
    
	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	_getStreamMethodID = GetMethodId("getStream",
			"(I" EXTRACTASKMODE_CLASS_T ")" SEQUENTIALOUTSTREAM_CLASS_T);

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	_prepareOperationMethodID = GetMethodId("prepareOperation",
			"(" EXTRACTASKMODE_CLASS_T ")Z");

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	_setOperationResultMethodID = GetMethodId("setOperationResult",
			"(" EXTRACTOPERATIONRESULT_CLASS_T ")V");

	_extractOperationResultClass = GetClass(EXTRACTOPERATIONRESULT_CLASS);

	// public static ExtractOperationResult getOperationResult(int index)
	_extractOperationResultGetOperationResultMethodID = 
			GetStaticMethodId(_extractOperationResultClass,
			        "getOperationResult", "(I)" EXTRACTOPERATIONRESULT_CLASS_T);
	
	_extractAskModeClass = GetClass(EXTRACTASKMODE_CLASS);

	// public static ExtractAskMode getExtractAskModeByIndex(int index)
	_extractAskModeGetExtractAskModeByIndexMethodID = 
			GetStaticMethodId(_extractAskModeClass, "getExtractAskModeByIndex", 
			        "(I)" EXTRACTASKMODE_CLASS_T);
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::GetStream(UInt32 index, ISequentialOutStream **outStream,
		Int32 askExtractMode)
{
//    if (askExtractMode) {
//        *outStream = NULL;
//        return S_OK;
//    }
    
	jobject askExtractModeObject = _env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID,
			(jint)askExtractMode);
	
	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	_env->ExceptionClear();
	jobject result = _env->CallObjectMethod(_javaImplementation, _getStreamMethodID, (jint)index, askExtractModeObject);
	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
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
//    return S_OK;
    
	jobject askExtractModeObject = _env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID, (jint)askExtractMode);

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	_env->ExceptionClear();
	jboolean result = _env->CallBooleanMethod(_javaImplementation, _prepareOperationMethodID, askExtractModeObject);

	if (_env->ExceptionCheck() || !result)
	{
        SaveLastOccurredException(_env);
		return S_FALSE;
	}

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::SetOperationResult(Int32 resultEOperationResult)
{
//    return S_OK;

    jobject resultEOperationResultObject = _env->CallStaticObjectMethod(_extractOperationResultClass, _extractOperationResultGetOperationResultMethodID, (jint)resultEOperationResult);

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	_env->ExceptionClear();
	_env->CallVoidMethod(_javaImplementation, _setOperationResultMethodID, resultEOperationResultObject);

	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
		return S_FALSE;
	}

	return S_OK;
}
