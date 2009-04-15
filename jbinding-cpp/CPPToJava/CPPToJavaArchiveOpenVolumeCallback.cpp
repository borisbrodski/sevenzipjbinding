#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaInStream.h"

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetProperty(PROPID propID, PROPVARIANT *value)
{
    TRACE_OBJECT_CALL("GetProperty");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

	jniInstance.PrepareCall();
	jobject result = env->CallObjectMethod(_javaImplementation, _getPropertyMethodID, (jint)propID);
	if (jniInstance.IsExceptionOccurs())
	{
		return S_FALSE;
	}

	// TODO Convert object to Variant

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetStream(const wchar_t *name, IInStream **inStream)
{
    TRACE_OBJECT_CALL("GetStream");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    jstring nameString = env->NewString((jchar *)name, (jsize)wcslen(name));

	jniInstance.PrepareCall();
	jobject inStreamImpl = env->CallObjectMethod(_javaImplementation, _getStreamMethodID, nameString);
	if (jniInstance.IsExceptionOccurs())
	{
		return S_FALSE;
	}

	if (inStream)
	{
	    CMyComPtr<IInStream> inStreamComPtr = new CPPToJavaInStream(_nativeMethodContext, env, inStreamImpl);
	    *inStream = inStreamComPtr.Detach();
	}

	return S_OK;
}

