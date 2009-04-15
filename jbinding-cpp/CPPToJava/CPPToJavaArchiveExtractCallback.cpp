#include "SevenZipJBinding.h"

#include "JNITools.h"
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
            new CPPToJavaCryptoGetTextPassword(_nativeMethodContext, initEnv, _javaImplementation);
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
    TRACE_OBJECT_CALL("GetStream");
    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

	jobject askExtractModeObject = env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID,
			(jint)askExtractMode);

	// public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
	jniInstance.PrepareCall();
	jobject result = env->CallObjectMethod(_javaImplementation, _getStreamMethodID, (jint)index, askExtractModeObject);
	if (jniInstance.IsExceptionOccurs())
	{
		return S_FALSE;
	}

	if (result == NULL)
	{
		*outStream = NULL;

        EndCPPToJavaCall();
		return S_OK;
	}

	CMyComPtr<ISequentialOutStream> outStreamComPtr = new CPPToJavaSequentialOutStream(_nativeMethodContext, env, result);
	*outStream = outStreamComPtr.Detach();

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::PrepareOperation(Int32 askExtractMode)
{
    TRACE_OBJECT_CALL("PrepareOperation");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

	jobject askExtractModeObject = env->CallStaticObjectMethod(_extractAskModeClass, _extractAskModeGetExtractAskModeByIndexMethodID, (jint)askExtractMode);

	// public boolean prepareOperation(ExtractAskMode extractAskMode);
	jniInstance.PrepareCall();
	jboolean result = env->CallBooleanMethod(_javaImplementation, _prepareOperationMethodID, askExtractModeObject);

	return jniInstance.IsExceptionOccurs() || !result ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::SetOperationResult(Int32 resultEOperationResult)
{
    TRACE_OBJECT_CALL("SetOperationResult");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    jobject resultEOperationResultObject = env->CallStaticObjectMethod(_extractOperationResultClass, _extractOperationResultGetOperationResultMethodID, (jint)resultEOperationResult);

	// public void setOperationResult(ExtractOperationResult extractOperationResult);
	jniInstance.PrepareCall();
	env->CallVoidMethod(_javaImplementation, _setOperationResultMethodID, resultEOperationResultObject);

	return jniInstance.IsExceptionOccurs() ? S_FALSE : S_OK;
}
