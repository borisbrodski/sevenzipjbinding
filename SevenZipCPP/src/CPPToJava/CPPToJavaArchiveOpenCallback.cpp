#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaArchiveOpenCallback.h"


STDMETHODIMP CPPToJavaArchiveOpenCallback::SetCompleted(const UInt64 *files, const UInt64 *bytes)
{
    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;
    
    if (files)
    {
        filesLongObject = LongToObject(_env, *files);
    }
    
    if (bytes)
    {
        bytesLongObject = LongToObject(_env, *bytes);
    }
    
	_env->ExceptionClear();
	_env->CallVoidMethod(_javaImplementation, _setCompletedMethodID, filesLongObject, bytesLongObject);
	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
		return S_FALSE;
	}
	
	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenCallback::SetTotal(const UInt64 *files, const UInt64 *bytes)
{
    jobject filesLongObject = NULL;
    jobject bytesLongObject = NULL;
    
    if (files)
    {
        filesLongObject = LongToObject(_env, *files);
    }
    
    if (bytes)
    {
        bytesLongObject = LongToObject(_env, *bytes);
    }

	_env->ExceptionClear();
	_env->CallVoidMethod(_javaImplementation, _setTotalMethodID, filesLongObject, bytesLongObject);
	if (_env->ExceptionCheck())
	{
	    SaveLastOccurredException(_env);
		return S_FALSE;
	}
	
	return S_OK;
}

