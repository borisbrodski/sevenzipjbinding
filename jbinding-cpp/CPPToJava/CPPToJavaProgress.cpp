#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaProgress.h"

STDMETHODIMP CPPToJavaProgress::SetCompleted(const UInt64 * completeValue) {
    TRACE_OBJECT_CALL("SetCompleted");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    _iProgress->setCompleted(jniEnvInstance, _javaImplementation, (jlong) (*completeValue));

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaProgress::SetTotal(UINT64 total) {
    TRACE_OBJECT_CALL("SetTotal");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    _iProgress->setTotal(jniEnvInstance, _javaImplementation, (jlong) total);

    return jniEnvInstance.exceptionCheck() ? S_FALSE : S_OK;
}

