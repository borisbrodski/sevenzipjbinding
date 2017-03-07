#ifndef CHEADCACHEINSTREAM_H_
#define CHEADCACHEINSTREAM_H_

#include "7zip/IStream.h"
#include "Common/MyCom.h"

class CHeadCacheInStream : public IInStream, public CMyUnknownImp {
    CMyComPtr<IInStream> _inStream;
    UInt64 _inStreamPos;
	UInt64 _inStreamSize;

    Byte * _cacheBuffer;
    UInt64 _cacheSize;
	UInt64 _cacheInitializedSize;
    UInt64 _pos;

public:
    CHeadCacheInStream(CMyComPtr<IInStream> inStream, UInt64 cacheSize) :
		_inStream(inStream),
		_inStreamPos(0),
		_inStreamSize(-1), // Lazy initialized
    	_cacheBuffer(NULL),
		_cacheSize(cacheSize),
		_cacheInitializedSize(0),
		_pos(0)
   	{}
    virtual ~CHeadCacheInStream() {
		if (_cacheBuffer) {
			free(_cacheBuffer);
		}
	}

    MY_UNKNOWN_IMP2(ISequentialInStream, IInStream);

    STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize);
    STDMETHOD(Seek)(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition);
    STDMETHOD(Init)(bool readEntireCache);
private:
	STDMETHOD(InStreamSeekAndRead)(void *data, UInt32 size, UInt32 *processedSize);
	STDMETHOD(ReadIntoCache)(UInt64 newCacheInitializedSize);
	STDMETHOD(ReadFromCache)(void *data, UInt32 size, UInt32 *processedSize);
};

#endif

