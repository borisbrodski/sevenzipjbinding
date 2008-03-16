#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaProgress.h"


STDMETHODIMP CPPToJavaProgress::SetCompleted(const UInt64 * completeValue)
{
	_env->ExceptionClear();
	_env->CallVoidMethod(_javaImplementation, _setCompletedMethodID, (jlong)(*completeValue));
	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
		return S_FALSE;
	}
	
	return S_OK;
}

STDMETHODIMP CPPToJavaProgress::SetTotal(UINT64 total)
{
	_env->ExceptionClear();
	_env->CallVoidMethod(_javaImplementation, _setTotalMethodID, (jlong)total);
	if (_env->ExceptionCheck())
	{
        SaveLastOccurredException(_env);
		return S_FALSE;
	}
	
	return S_OK;
}

