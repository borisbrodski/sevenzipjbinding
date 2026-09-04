#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveExtractCallback.h"
#include "CPPToJavaSequentialOutStream.h"

/*
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

 // public void prepareOperation(ExtractAskMode extractAskMode);
 _prepareOperationMethodID = GetMethodId(initEnv, "prepareOperation",
 "(" EXTRACTASKMODE_CLASS_T ")V");

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
 */

STDMETHODIMP CPPToJavaArchiveExtractCallback::CryptoGetTextPassword(BSTR *password) noexcept {
    TRACE_OBJECT_CALL("CryptoGetTextPassword");

    if (_cryptoGetTextPasswordImpl) {
        return _cryptoGetTextPasswordImpl->CryptoGetTextPassword(password);
    }

    // No password callback available - return empty password
    *password = SysAllocString(L"");
    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::GetStream(UInt32 index,
                                                        ISequentialOutStream **outStream,
                                                        Int32 askExtractMode) noexcept {
    TRACE_OBJECT_CALL("GetStream");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (outStream) {
        *outStream = NULL;
    }

    jobject askExtractModeObject = jni::ExtractAskMode::getExtractAskModeByIndex(jniEnvInstance,
            (jint) askExtractMode);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    // public SequentialOutStream getStream(int index, ExtractAskMode extractAskMode);
    jobject result = _iArchiveExtractCallback->getStream(jniEnvInstance, _javaImplementation,
            (jint) index, askExtractModeObject);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    if (result == NULL) {
        *outStream = NULL;
        return S_OK;
    }

    CMyComPtr<ISequentialOutStream> outStreamComPtr = new CPPToJavaSequentialOutStream(
            _jbindingSession, jniEnvInstance, result);
    jniEnvInstance->DeleteLocalRef(result);
    *outStream = outStreamComPtr.Detach();

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::PrepareOperation(Int32 askExtractMode) noexcept {
    TRACE_OBJECT_CALL("PrepareOperation");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jobject askExtractModeObject = jni::ExtractAskMode::getExtractAskModeByIndex(jniEnvInstance,
            (jint) askExtractMode);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    // public void prepareOperation(ExtractAskMode extractAskMode);
    _iArchiveExtractCallback->prepareOperation(jniEnvInstance, _javaImplementation,
            askExtractModeObject);

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::SetOperationResult(Int32 resultEOperationResult) noexcept {
    TRACE_OBJECT_CALL("SetOperationResult");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jobject resultEOperationResultObject = jni::ExtractOperationResult::getOperationResult(
            jniEnvInstance, (jint) resultEOperationResult);

    if (jniEnvInstance.exceptionCheck()) {
    	return S_FALSE;
    }

    // public void setOperationResult(ExtractOperationResult extractOperationResult);
    _iArchiveExtractCallback->setOperationResult(jniEnvInstance, _javaImplementation,
            resultEOperationResultObject);

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::ReportExtractResult(UInt32 indexType, UInt32 index, Int32 opRes) noexcept {
    TRACE_OBJECT_CALL("ReportExtractResult");
    
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    // Convert indexType to Java enum
    jobject indexTypeObject = jni::ReportExtractResultIndexType::getIndexType(jniEnvInstance, (jint) indexType);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    // Convert opRes to ExtractOperationResult enum
    jobject opResObject = jni::ExtractOperationResult::getOperationResult(jniEnvInstance, (jint) opRes);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    // Call the Java method: reportExtractResult(ReportExtractResultIndexType indexType, int index, ExtractOperationResult extractOperationResult)
    _iArchiveExtractCallback->reportExtractResult(jniEnvInstance, _javaImplementation,
            indexTypeObject, (jint) index, opResObject);

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

