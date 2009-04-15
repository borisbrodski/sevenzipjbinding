//============================================================================
// Name        : SevenZipBinding.cpp
// Author      :
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <stdlib.h>

#include "SevenZipJBinding.h"

#include "Windows/DLL.h"



/**
 * Fatal error
 */
void fatal(const char * fmt, ...) {
	va_list args;
	va_start(args, fmt);
	fputs("FATAL ERROR: ", stdout);
	vprintf(fmt, args);
	va_end(args);

	fputc('\n', stdout);
	fflush(stdout);

	exit(-1);
}

/**
 * Load 7-Zip DLL.
 *
 * Return: NULL - ok, else error message
 */
const char * load7ZipLibrary(CreateObjectFunc * createObjectFunc) {

	NWindows::NDLL::CLibrary library;
	if (!library.Load(TEXT(SEVENZIPJBINDING_LIBRARY_NAME_FILENAME))) {
		return "Can not load library";
	}
	*createObjectFunc =
			(CreateObjectFunc) library.GetProcAddress("CreateObject");

	if (NULL == *createObjectFunc) {
		return "Not a 7-Zip Library. Missing 'CreateObject' export name";
	}

//	HINSTANCE lib = LoadLibraryA(DLLFILENAME);
//	if (NULL == lib)
//	return "Error loading 7-Zip library: " DLLFILENAME;

//	*createObjectFunc = (CreateObjectFunc) GetProcAddress(lib, "CreateObject");
// CreateObjectFunc
//	if (NULL == *createObjectFunc)
//		return "Not a 7-Zip Library. Missing 'CreateObject' export name";

	return NULL;
}

