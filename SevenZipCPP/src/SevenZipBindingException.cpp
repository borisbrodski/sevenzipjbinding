#include "StdAfx.h"

#include "SevenZipBindingException.h"

SevenZipBindingException::SevenZipBindingException(char * fmt, ...)
{
    va_list args;
    va_start(args, fmt);

    TRACE_OBJECT_CREATION("SevenZipBindingException")
    
    message = (char *)malloc(EXCEPTION_MESSAGE_MAX_SIZE);
    vsnprintf(message, EXCEPTION_MESSAGE_MAX_SIZE, fmt, args);
    va_end(args);
}
