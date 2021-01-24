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
 #ifdef __ANDROID_API__
 if (cryptoGetTextPasswordClass == nullptr) {
 cryptoGetTextPasswordClass = findClass(initEnv, CRYPTOGETTEXTPASSWORD_CLASS);
 }
 #endif
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

STDMETHODIMP CPPToJavaArchiveExtractCallback::GetStream(UInt32 index,
                                                        ISequentialOutStream **outStream,
                                                        Int32 askExtractMode) {
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
#ifdef __ANDROID_API__
    jniEnvInstance->DeleteLocalRef(askExtractModeObject);
#endif
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    if (result == NULL) {
        *outStream = NULL;
        return S_OK;
    }

    CMyComPtr<ISequentialOutStream> outStreamComPtr = new CPPToJavaSequentialOutStream(
            _jbindingSession, jniEnvInstance, result);
#ifdef __ANDROID_API__
    jniEnvInstance->DeleteLocalRef(result);
#endif
    *outStream = outStreamComPtr.Detach();

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::PrepareOperation(Int32 askExtractMode) {
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
#ifdef __ANDROID_API__
    jniEnvInstance->DeleteLocalRef(askExtractModeObject);
#endif

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaArchiveExtractCallback::SetOperationResult(Int32 resultEOperationResult) {
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
#ifdef __ANDROID_API__
    jniEnvInstance->DeleteLocalRef(resultEOperationResultObject);
#endif

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}
