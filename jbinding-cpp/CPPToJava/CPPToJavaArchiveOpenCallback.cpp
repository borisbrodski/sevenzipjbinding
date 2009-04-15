#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveOpenCallback.h"


STDMETHODIMP CPPToJavaArchiveOpenCallback::SetCompleted(const UInt64 *files, const UInt64 *bytes)
{
    TRACE_OBJECT_CALL("SetCompleted");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;

    if (files)
    {
        filesLongObject = LongToObject(env, *files);
    }

    if (bytes)
    {
        bytesLongObject = LongToObject(env, *bytes);
    }

	jniInstance.PrepareCall();
	env->CallVoidMethod(_javaImplementation, _setCompletedMethodID, filesLongObject, bytesLongObject);
	return jniInstance.IsExceptionOccurs() ? S_FALSE : S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenCallback::SetTotal(const UInt64 *files, const UInt64 *bytes)
{
    TRACE_OBJECT_CALL("SetTotal");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;

    if (files)
    {
        filesLongObject = LongToObject(env, *files);
    }

    if (bytes)
    {
        bytesLongObject = LongToObject(env, *bytes);
    }

    jniInstance.PrepareCall();
	env->CallVoidMethod(_javaImplementation, _setTotalMethodID, filesLongObject, bytesLongObject);
	return jniInstance.IsExceptionOccurs() ? S_FALSE : S_OK;
}

