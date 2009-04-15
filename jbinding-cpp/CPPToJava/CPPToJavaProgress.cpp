#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaProgress.h"


STDMETHODIMP CPPToJavaProgress::SetCompleted(const UInt64 * completeValue)
{
    TRACE_OBJECT_CALL("SetCompleted");

    JNIInstance jniInstance(_nativeMethodContext);

    jniInstance.PrepareCall();
    jniInstance.GetEnv()->CallVoidMethod(_javaImplementation, _setCompletedMethodID, (jlong)(*completeValue));

    return jniInstance.IsExceptionOccurs() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaProgress::SetTotal(UINT64 total)
{
    TRACE_OBJECT_CALL("SetTotal");

    JNIInstance jniInstance(_nativeMethodContext);

    jniInstance.PrepareCall();
    jniInstance.GetEnv()->CallVoidMethod(_javaImplementation, _setTotalMethodID, (jlong)total);

    return jniInstance.IsExceptionOccurs() ? S_FALSE : S_OK ;
}

