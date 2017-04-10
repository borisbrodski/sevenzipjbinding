#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveOpenCallback.h"

STDMETHODIMP CPPToJavaArchiveOpenCallback::SetCompleted(const UInt64 *files, const UInt64 *bytes) {
    TRACE_OBJECT_CALL("SetCompleted");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;

    if (files) {
        filesLongObject = LongToObject(jniEnvInstance, *files);
    }

    if (bytes) {
        bytesLongObject = LongToObject(jniEnvInstance, *bytes);
    }

    _iArchiveOpenCallback->setCompleted(jniEnvInstance, _javaImplementation, filesLongObject,
            bytesLongObject);

    if (filesLongObject) {
        jniEnvInstance->DeleteLocalRef(filesLongObject);
    }

    if (bytesLongObject) {
        jniEnvInstance->DeleteLocalRef(bytesLongObject);
    }

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenCallback::SetTotal(const UInt64 *files, const UInt64 *bytes) {
    TRACE_OBJECT_CALL("SetTotal");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;

    if (files) {
        filesLongObject = LongToObject(jniEnvInstance, *files);
    }

    if (bytes) {
        bytesLongObject = LongToObject(jniEnvInstance, *bytes);
    }

    _iArchiveOpenCallback->setTotal(jniEnvInstance, _javaImplementation, filesLongObject, bytesLongObject);

    if (filesLongObject) {
        jniEnvInstance->DeleteLocalRef(filesLongObject);
    }

    if (bytesLongObject) {
        jniEnvInstance->DeleteLocalRef(bytesLongObject);
    }

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

