#include "SevenZipJBinding.h"
#include "CHeadCacheInStream.h"

STDMETHODIMP CHeadCacheInStream::ReadIntoCache(UInt64 newCacheInitializedSize) {
	HRESULT res;
	if (newCacheInitializedSize <= _cacheInitializedSize) {
		return S_OK;
	}

	if (_inStreamPos != _cacheInitializedSize) {
		res = _inStream->Seek(_cacheInitializedSize, SEEK_SET, NULL);
		if (res != S_OK) {
			return res;
		}
		_inStreamPos = _cacheInitializedSize;
	}

	do {
		UInt32 wasRead;
		res = _inStream->Read(_cacheBuffer + _cacheInitializedSize, newCacheInitializedSize - _cacheInitializedSize, &wasRead);
		if (res != S_OK) {
			return res;
		}
		if (wasRead == 0) {
			return S_FALSE;
		}
		_cacheInitializedSize += wasRead;
	} while (newCacheInitializedSize > _cacheInitializedSize);

	return S_OK;
}

STDMETHODIMP CHeadCacheInStream::ReadFromCache(void *data, UInt32 size, UInt32 *processedSize) {
	HRESULT res;
	if (_pos + size > _cacheInitializedSize) {
		res = ReadIntoCache(MIN(_pos + size, _cacheSize));
		if (res != S_OK) {
			return res;
		}
	}
	if (_pos >= _cacheInitializedSize) {
		return S_FALSE;
	}
	UInt64 willRead = MIN(size, _cacheInitializedSize - _pos);
	memcpy(data, _cacheBuffer + _pos, willRead);
	if (processedSize) {
		*processedSize = willRead;
	}
	_pos += willRead;
	return S_OK;
}

STDMETHODIMP CHeadCacheInStream::InStreamSeekAndRead(void *data, UInt32 size, UInt32 *processedSize) {
	HRESULT res;
	if (_pos != _inStreamPos) {
		res = _inStream->Seek(_pos, SEEK_SET, NULL);
		if (res != S_OK) {
			return res;
		}
	}
	UInt32 myProcessedSize;
	res = _inStream->Read(data, size, &myProcessedSize);
	if (res != S_OK) {
		return res;
	}
	if (myProcessedSize == 0) {
		return S_FALSE;
	}
	_pos += myProcessedSize;
	_inStreamPos = _pos;
	if (processedSize) {
		*processedSize = myProcessedSize;
	}
	return S_OK;
}

STDMETHODIMP CHeadCacheInStream::Read(void *data, UInt32 size, UInt32 *processedSize) {
	if (_inStreamSize == -1) {
		HRESULT res = InitSize();
		if (res != S_OK) {
			return res;
		}
	}

	if (_cacheBuffer == NULL && _cacheSize > 0) {
		UInt64 size = _inStreamSize;
		_cacheSize = MIN(_cacheSize, size);
		_cacheBuffer = (Byte *)malloc(_cacheSize);
		if (_cacheBuffer == NULL) {
			return E_OUTOFMEMORY;
		}
	}
	UInt64 streamSize = _inStreamSize;
	if (_pos >= streamSize || size == 0) {
		if (processedSize) {
			*processedSize = 0;
		}
		return S_OK;
	}
	if (_pos < _cacheSize) {
		size = MIN(size, streamSize - _pos);
		return ReadFromCache(data, MIN(size, _cacheSize - _pos), processedSize);
	}
	return InStreamSeekAndRead(data, size, processedSize);
}
STDMETHODIMP CHeadCacheInStream::InitSize() {
	if (_inStreamSize == -1) {
		HRESULT res = _inStream->Seek(0, SEEK_END, &_inStreamSize);
		if (res != S_OK) {
			return res;
		}
		_inStreamPos = _inStreamSize;
	}

	return S_OK;
}

STDMETHODIMP CHeadCacheInStream::Seek(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition) {
	switch (seekOrigin) {
		case SEEK_SET:
			_pos = offset;
			break;
		case SEEK_CUR:
			_pos += offset;
			break;
		case SEEK_END:
			if (_inStreamSize == -1) {
				HRESULT res = InitSize();
				if (res != S_OK) {
					return res;
				}
			}
			_pos = _inStreamSize + offset;
	}
	if (newPosition) {
		*newPosition = _pos;
	}
	return S_OK;
}
