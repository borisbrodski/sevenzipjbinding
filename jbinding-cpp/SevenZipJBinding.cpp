//============================================================================
// Name        : SevenZipBinding.cpp
// Author      :
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================


#include <stdlib.h>

#include "SevenZipJBinding.h"

// #include "Common/MyInitGuid.h"

// #include "Windows/DLL.h"

/*
#include "Common/CommandLineParser.h"
#include "Common/MyException.h"
#include "Common/IntToString.h"
#include "Common/ListFileUtils.h"
#include "Common/StdInStream.h"
#include "Common/StdOutStream.h"
#include "Common/StringConvert.h"
#include "Common/StringToInt.h"
#include "Common/Wildcard.h"

#include "Windows/FileDir.h"
#include "Windows/FileName.h"
#include "Windows/Defs.h"
#include "Windows/Error.h"
#ifdef _WIN32
#include "Windows/MemoryLock.h"
#endif

#include "7zip/IPassword.h"
#include "7zip/ICoder.h"
#include "7zip/UI/Common/UpdateAction.h"
#include "7zip/UI/Common/Update.h"
#include "7zip/UI/Common/Extract.h"
#include "7zip/UI/Common/ArchiveCommandLine.h"
#include "7zip/UI/Common/ExitCode.h"
#ifdef EXTERNAL_CODECS
#include "../Common/LoadCodecs.h"
#endif

#include "7zip/Compress/LZMA_Alone/LzmaBenchCon.h"

#include "7zip/UI/Console/List.h"
#include "7zip/UI/Console/OpenCallbackConsole.h"
#include "7zip/UI/Console/ExtractCallbackConsole.h"
#include "7zip/UI/Console/UpdateCallbackConsole.h"

#include "7zip/MyVersion.h"
*/
/*
#if defined( _WIN32) && defined( _7ZIP_LARGE_PAGES)
extern "C"
{
#include "../../../../C/Alloc.h"
}
#endif

#include "myPrivate.h"
#include "Windows/System.h"
*/

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

	TRACE_PRINT_OBJECTS

	// exit(-1);

	printf("Crash jvm to get a stack trace\n");
	fflush(stdout);
	int * i = NULL;
	*i = 0;
}

