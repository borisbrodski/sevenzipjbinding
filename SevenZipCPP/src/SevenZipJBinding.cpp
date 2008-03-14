//============================================================================
// Name        : SevenZipBinding.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include "StdAfx.h"

#include "jnitools.h"
#include "SevenZipJBinding.h"

static struct
{
	HRESULT errCode;
	char * message;
} SevenZipErrorMessages [] =
{
{ S_OK, "OK" }, // ((HRESULT)0x00000000L)
		{ S_FALSE, "FALSE" }, // ((HRESULT)0x00000001L)
		{ E_NOTIMPL, "Not implemented" }, // ((HRESULT)0x80004001L)
		{ E_NOINTERFACE, "No interface" }, // ((HRESULT)0x80004002L)
		{ E_ABORT, "Abort" }, // ((HRESULT)0x80004004L)
		{ E_FAIL, "Fail" }, // ((HRESULT)0x80004005L)
		{ STG_E_INVALIDFUNCTION, "Invalid function" }, // ((HRESULT)0x80030001L)
		{ E_OUTOFMEMORY, "Out of memory" }, // ((HRESULT)0x8007000EL)
		{ E_INVALIDARG, "Invalid argument" }, // ((HRESULT)0x80070057L)
		{ 0, NULL },
};

/*
 * Return error message from error code
 */
char * getSevenZipErrorMessage(HRESULT hresult)
{
	for (int i = 0; SevenZipErrorMessages[i].message != NULL; i++)
	{
		if (SevenZipErrorMessages[i].errCode == hresult)
		{
			return SevenZipErrorMessages[i].message;
		}
	}
	return "Unknown error code";
}

/**
 * Load 7-Zip DLL.
 * 
 * Return: NULL - ok, else error message
 */
char * load7ZipLibrary(CreateObjectFunc * createObjectFunc)
{
	HINSTANCE lib = LoadLibraryA(DLLFILENAME);

	if (NULL == lib)
		return "Error loading 7-Zip library: " DLLFILENAME;

	*createObjectFunc = (CreateObjectFunc)GetProcAddress(lib, "CreateObject");

	if (NULL == *createObjectFunc)
		return "Not a 7-Zip Library. Missing 'CreateObject' export name";

	return NULL;
}

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, char * fmt, ...)
{
	jclass exceptionClass = env->FindClass(SEVEN_ZIP_EXCEPTION);
	FATALIF(exceptionClass == NULL, "SevenZipException class '" SEVEN_ZIP_EXCEPTION "' can't be found");

	char buffer[64 * 1024];
	va_list args;
	va_start(args, fmt);
	_vsnprintf(buffer, sizeof(buffer), fmt, args);
	va_end(args);

	buffer[sizeof(buffer) - 1] = '\0';

	env->ThrowNew(exceptionClass, buffer);
}

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, HRESULT hresult, char * fmt, ...)
{
	jclass exceptionClass = env->FindClass(SEVEN_ZIP_EXCEPTION);
	FATALIF(exceptionClass == NULL, "SevenZipException class '" SEVEN_ZIP_EXCEPTION "' can't be found");

	char buffer[64 * 1024];
	
	snprintf(buffer, sizeof(buffer), "HRESULT: 0x%X (%s). ", (int)hresult, getSevenZipErrorMessage(hresult));
	int beginIndex = strlen(buffer);
	
	va_list args;
	va_start(args, fmt);
	_vsnprintf(&buffer[beginIndex], sizeof(buffer) - beginIndex, fmt, args);
	va_end(args);

	buffer[sizeof(buffer) - 1] = '\0';

	env->ThrowNew(exceptionClass, buffer);
}

