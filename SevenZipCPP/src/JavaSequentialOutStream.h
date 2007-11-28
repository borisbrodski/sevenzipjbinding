#ifndef JAVASEQUENTIALOUTSTREAM_H_
#define JAVASEQUENTIALOUTSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"

class JavaSequentialOutStream :
	public ISequentialOutStream,
	public CMyUnknownImp {
public:
	MY_UNKNOWN_IMP

	STDMETHOD(Write)(const void *data, UInt32 size, UInt32 *processedSize);
};


#endif /*JAVASEQUENTIALOUTSTREAM_H_*/
