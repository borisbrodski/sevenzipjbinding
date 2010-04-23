// TODO Remove me
/*
#include <stdlib.h>

#include "SevenZipJBinding.h"
#include "SevenZipException.h"


SevenZipException::SevenZipException(const char * fmt, ...)
{
    TRACE_OBJECT_CREATION("SevenZipException")

    va_list args;
    va_start(args, fmt);

    _message = (char *)malloc(EXCEPTION_MESSAGE_MAX_SIZE);
    vsnprintf(_message, EXCEPTION_MESSAGE_MAX_SIZE, fmt, args);
    _message[EXCEPTION_MESSAGE_MAX_SIZE - 1] = 0;
    va_end(args);

    TRACE("Exception with message '" << _message << "' created.");
    _hresult = 0;
}

SevenZipException::SevenZipException(HRESULT hresult, char * fmt, ...)
{
    va_list args;
    va_start(args, fmt);

    TRACE_OBJECT_CREATION("SevenZipException")

    _message = (char *)malloc(EXCEPTION_MESSAGE_MAX_SIZE);
    vsnprintf(_message, EXCEPTION_MESSAGE_MAX_SIZE, fmt, args);
    _message[EXCEPTION_MESSAGE_MAX_SIZE - 1] = 0;
    va_end(args);

    TRACE("Exception with message '" << _message << "' created. HResult: " << _hresult);
    _hresult = hresult;
}
*/
