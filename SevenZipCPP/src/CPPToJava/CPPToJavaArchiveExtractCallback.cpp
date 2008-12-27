#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaArchiveExtractCallback.h"
#include "CPPToJavaSequentialOutStream.h"

void CPPToJavaArchiveExtractCallback::Init(JNIEnv * initEnv)
{
    TRACE_OBJECT_CALL("Init")
    
    _cryptoGetTextPasswordImpl = NULL;
    
    jclass cryptoGetTextPasswordClass = initEnv->FindClass(CRYPTOGETTEXTPASSWORD_CLASS);
    FATALIF(cryptoGetTextPasswordClass == NULL,
            "Can't find class " CRYPTOGETTEXTPASSWORD_CLASS);

    if (initEnv->IsInstanceOf(_javaImplementation, cryptoGetTextPasswordClass))
    {
        CMyComPtr<ICryptoGetTextPassword> cryptoGetTextPasswordComPtr = 
            new CPPToJavaCryptoGetTextPassword(_jniCallState, initEnv, _javaImplementation);
        _cryptoGetTextPasswordImpl = cryptoGetTextPasswordComPtr.Detach();
    }
    
    
	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	_getStreamMethodID = GetMethodId(initEnv, "getStream",
			"(I" EXTRACTASKMODE_CLASS_T ")" SEQUENTIALOUTSTREAM_CLASS_T);

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	_prepareOperationMethodID = GetMethodId(initEnv, "prepareOperation",
			"(" EXTRACTASKMODE_CLASS_T ")Z");

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	_setOperationResultMethodID = GetMethodId(initEnv, "setOperationResult",
			"(" EXTRACTOPERATIONRESULT_CLASS_T ")V");

	_extractOperationResultClass = GetClass(initEnv, EXTRACTOPERATIONRESULT_CLASS);

	// public static ExtractOperationResult getOperationResult(int index)
	_extractOperationResultGetOperationResultMethodID = 
			GetStaticMethodId(initEnv, _extractOperationResultClass,
			        "getOperationResult", "(I)" EXTRACTOPERATIONRESULT_CLASS_T);
	
	_extractAskModeClass = GetClass(initEnv, EXTRACTASKMODE_CLASS);

	// public static ExtractAskMode getExtractAskModeByIndex(int index)
	_extractAskModeGetExtractAskModeByIndexMethodID = 
			GetStaticMethodId(initEnv, _extractAskModeClass, "getExtractAskModeByIndex", 
			        "(I)" EXTRACTASKMODE_CLASS_T);
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::GetStream(UInt32 index, ISequentialOutStream **outStream,
		Int32 askExtractMode)
{
    TRACE_OBJECT_CALL("GetStream")
    
    JNIEnv * env = BeginCPPToJavaCall();
    
	jobject askExtractModeObject = env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID,
			(jint)askExtractMode);
	
	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	env->ExceptionClear();
	jobject result = env->CallObjectMethod(_javaImplementation, _getStreamMethodID, (jint)index, askExtractModeObject);
	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}

	if (result == NULL)
	{
		*outStream = NULL;
		
        EndCPPToJavaCall();
		return S_OK;
	}
	
	CMyComPtr<ISequentialOutStream> outStreamComPtr = new CPPToJavaSequentialOutStream(_jniCallState, env, result);
	*outStream = outStreamComPtr.Detach();

	EndCPPToJavaCall();
	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::PrepareOperation(Int32 askExtractMode)
{
    TRACE_OBJECT_CALL("PrepareOperation")
    
    JNIEnv * env = BeginCPPToJavaCall();
    
	jobject askExtractModeObject = env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID, (jint)askExtractMode);

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	env->ExceptionClear();
	jboolean result = env->CallBooleanMethod(_javaImplementation, _prepareOperationMethodID, askExtractModeObject);

	if (env->ExceptionCheck() || !result)
	{
        SaveLastOccurredException(env);

        EndCPPToJavaCall();
		return S_FALSE;
	}

    EndCPPToJavaCall();
	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::SetOperationResult(Int32 resultEOperationResult)
{
    TRACE_OBJECT_CALL("SetOperationResult")
    
    JNIEnv * env = BeginCPPToJavaCall();

    jobject resultEOperationResultObject = env->CallStaticObjectMethod(_extractOperationResultClass, _extractOperationResultGetOperationResultMethodID, (jint)resultEOperationResult);

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	env->ExceptionClear();
	env->CallVoidMethod(_javaImplementation, _setOperationResultMethodID, resultEOperationResultObject);

	if (env->ExceptionCheck())
	{
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
		return S_FALSE;
	}

    EndCPPToJavaCall();
	return S_OK;
}
