#include "StdAfx.h"

#include "JavaArchiveExtractCallback.h"

STDMETHODIMP JavaArchiveExtractCallback::CryptoGetTextPassword(BSTR *password)
{
	printf("> Asked for password!\n");
	*password = L"test1";
	return S_OK;
}


STDMETHODIMP JavaArchiveExtractCallback::GetStream(UInt32 index, ISequentialOutStream **outStream,
		Int32 askExtractMode)
{
	printf("GetStream(%i)\n", index);
	*outStream = jsos;
	return S_OK;
}


STDMETHODIMP JavaArchiveExtractCallback::PrepareOperation(Int32 askExtractMode)
{
	printf("PrepareOperation(%i)\n", askExtractMode);
	return S_OK;
}

STDMETHODIMP JavaArchiveExtractCallback::SetOperationResult(Int32 resultEOperationResult)
{
	printf("SetOperationResult(%i)\n", resultEOperationResult);
	return S_OK;
}

STDMETHODIMP JavaArchiveExtractCallback::SetTotal(UInt64 total)
{
	return S_OK;
}

STDMETHODIMP JavaArchiveExtractCallback::SetCompleted(const UInt64* completed)
{
	return S_OK;
}
