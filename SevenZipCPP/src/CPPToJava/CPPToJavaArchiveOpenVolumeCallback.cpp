#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaInStream.h"

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetProperty(PROPID propID, PROPVARIANT *value)
{
	_env->ExceptionClear();
	jobject result = _env->CallObjectMethod(_javaImplementation, _getPropertyMethodID, (jint)propID);
	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
		return S_FALSE;
	}
	
	// TODO Convert object to Variant
	
	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetStream(const wchar_t *name, IInStream **inStream)
{
    jstring nameString = _env->NewString((jchar *)name, (jsize)wcslen(name));
    
	_env->ExceptionClear();
	jobject inStreamImpl = _env->CallObjectMethod(_javaImplementation, _getStreamMethodID, nameString);
	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
		return S_FALSE;
	}

	if (inStream)
	{
	    CMyComPtr<IInStream> inStreamComPtr = new CPPToJavaInStream(_env, inStreamImpl);
	    *inStream = inStreamComPtr.Detach();
	}
	return S_OK;
}

