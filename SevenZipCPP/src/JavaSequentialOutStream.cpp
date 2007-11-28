#include "StdAfx.h"

#include "JavaSequentialOutStream.h"


STDMETHODIMP JavaSequentialOutStream::Write(const void *data, UInt32 size, UInt32 *processedSize)
{
	
	fwrite(data, 1, size, stdout);
	*processedSize = size;
	
	return S_OK;
}

