#ifndef ARCHIVEEXTRACTCALLBACK_H_
#define ARCHIVEEXTRACTCALLBACK_H_

#include "7zip/UI/Common/ArchiveExtractCallback.h"

#include "JavaSequentialOutStream.h"


class JavaArchiveExtractCallback : public IArchiveExtractCallback, public CMyUnknownImp,
			public ICryptoGetTextPassword {
private:
	JavaSequentialOutStream * jsos;
	
public:
	JavaArchiveExtractCallback(JavaSequentialOutStream * jsos) {
		this->jsos = jsos;
	}
	
	MY_UNKNOWN_IMP1(ICryptoGetTextPassword)

	STDMETHOD(GetStream)(UInt32 index, ISequentialOutStream **outStream,
			Int32 askExtractMode);
	// GetStream OUT: S_OK - OK, S_FALSE - skeep this file
	STDMETHOD(PrepareOperation)(Int32 askExtractMode);
	STDMETHOD(SetOperationResult)(Int32 resultEOperationResult);
	STDMETHOD(SetTotal)(UInt64 total);
	STDMETHOD(SetCompleted)(const UInt64* completed);

	STDMETHOD(CryptoGetTextPassword)(BSTR *password);

	
	virtual ~JavaArchiveExtractCallback() {
	}
};



#endif /*ARCHIVEEXTRACTCALLBACK_H_*/
