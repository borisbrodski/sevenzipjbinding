#include <iostream>
#include <string>
#include <sstream>
#include <inttypes.h>

#include "SevenZipJBinding.h"

#include "CHeadCacheInStream.h"

#ifdef NATIVE_JUNIT_TEST_SUPPORT

using namespace std;

#define TEST_BUFFER_CONTENT(i)  ((unsigned char)((i) % 0xFF))


class CGeneratorInStream : public IInStream, public CMyUnknownImp {
    UInt64 _size;
    UInt64 _pos;
    UInt32 * _readCount;  // TODO IMPLEMENT THIS.
public:
    CGeneratorInStream(UInt64 size) : _size(size), _pos(0) {
    	_readCount = new UInt32[size];
    	memset(_readCount, 0, sizeof(UInt32) * size);
    };
    virtual ~CGeneratorInStream() {
    	delete [] _readCount;
    }

	UInt32 * GetReadCount() {
		return _readCount;
	}

    MY_UNKNOWN_IMP2(ISequentialInStream, IInStream)
    STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize) {
    	if (processedSize) {
    		*processedSize = 0;
    	}
        if (!size) {
            return S_OK;
        }

        UInt32 toRead;
        if (rand() % 3 == 0) {
           toRead = size;
        } else {
           toRead = ((rand() << 1 | rand()) % size) + 1;
        }
        unsigned char * buffer = (unsigned char *)data;
        for (UInt32 i = 0; i < toRead; i++) {
			if (_pos + i >= _size) {
				toRead = i;
				break;
			}
			_readCount[_pos + i]++;
            buffer[i] = TEST_BUFFER_CONTENT(_pos + i);
        }
        _pos += toRead;
    	if (processedSize) {
    		*processedSize = toRead;
    	}
    	return S_OK;
    }
    STDMETHOD(Seek)(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition) {
    	switch (seekOrigin) {
    	case SEEK_SET:
    		_pos = offset;
    		break;
    	case SEEK_CUR:
    		_pos += offset;
    		break;
    	case SEEK_END:
    		_pos = _size + offset;
    	}
    	if (newPosition) {
    		*newPosition = _pos;
    	}
    	return S_OK;
    }
};


const char * checkReadCount(CMyComPtr<CGeneratorInStream> inStream, UInt64 expectedSize, UInt32 expectedCount) {
	UInt32 * count = inStream->GetReadCount();
	for (UInt64 i = 0; i < expectedSize; i++) {
		if (count[i] != expectedCount) {
			printf("Invalid read count at pos %u. Expected %u, Get %u\n", (UInt32)i, (UInt32)expectedCount, count[i]);
			return "Invalid read count";
		}
	}
	return NULL;
}
const char * testReadCountInStream(CMyComPtr<CGeneratorInStream> inStream, UInt64 expectedSize) {
	const char * err = checkReadCount(inStream, expectedSize, 0);
	if (err) {
		return err;
	}

	unsigned char * buffer = new unsigned char[expectedSize];

	UInt32 toRead = expectedSize;
	unsigned char * buffer_pos = buffer;
	do {
		UInt32 read;
		HRESULT res = inStream->Read((void *)buffer, toRead, &read);
		if (res != S_OK || read == 0 || read > toRead) {
			delete[] buffer;
			return "Error reading stream";
		}
		toRead -= read;
		buffer_pos += read;
	} while (toRead > 0);
	err = checkReadCount(inStream, expectedSize, 1);
	if (err) {
		delete[] buffer;
		return err;
	}

	delete[] buffer;
	return NULL;
}

const char * testReadOverTheEndOfStream(CMyComPtr<CGeneratorInStream> inStream, UInt64 expectedSize) {
	unsigned char * buffer = new unsigned char[expectedSize * 2];

	UInt32 toRead = expectedSize * 2;
	unsigned char * buffer_pos = buffer;
	do {
		UInt32 read;
		HRESULT res = inStream->Read((void *)buffer, toRead, &read);
		if (res != S_OK || read > toRead) {
			delete[] buffer;
			return "Error reading stream";
		}
		if (read == 0) {
			break;
		}
		toRead -= read;
		buffer_pos += read;
		if (toRead < expectedSize) {
			delete[] buffer;
			return "Read to many bytes (1)";
		}
	} while (toRead > 0);
	if (toRead != expectedSize) {
		delete[] buffer;
		return "Read to many bytes (2)";
	}

	delete[] buffer;
	return NULL;
}


const char * testInStream(CMyComPtr<IInStream> inStream, UInt64 expectedSize) {
#define WITH_HRESULT(cmd) { HRESULT res = (cmd); if (res != S_OK) {printf("Unexpected HRESULT: 0x%X", res); return "Unexpected HRESULT"; }}
    UInt64 actualSize;
    WITH_HRESULT(inStream->Seek(0L, SEEK_END, &actualSize));
    if (actualSize != expectedSize) {
        printf("Expected size: %u, Actual size: %u\n", (UInt32)expectedSize, (UInt32)actualSize);
        return "Stream size mismatch";
    }
    UInt64 pos = expectedSize;
    unsigned char * data = new unsigned char[expectedSize + 1];
    for (UInt32 i = 0; i < 10000; i++) {
        UInt64 targetPos = actualSize ? (rand() << 1 | rand()) % actualSize : 0;
        UInt64 newPosition;
        int origin = rand() % 3;
        switch (origin) {
        case 0:
            WITH_HRESULT(inStream->Seek(targetPos, SEEK_SET, &newPosition));
            break;
        case 1:
            WITH_HRESULT(inStream->Seek(targetPos - pos, SEEK_CUR, &newPosition));
            break;
        case 2:
            WITH_HRESULT(inStream->Seek(targetPos - actualSize, SEEK_END, &newPosition));
            break;
        }
        if (newPosition != targetPos) {
            printf("Seek to unexpected position. Expected pos: %u, Actual pos: %u (origin: %i)\n", (UInt32)targetPos, (UInt32)newPosition, origin);
            delete [] data;
            return "Stream size mismatch";
        }
        pos = newPosition;
        if (pos < expectedSize) {
            UInt32 size = (rand() << 1 | rand()) % (expectedSize - pos + 1);
            UInt32 toRead = size;
            UInt32 read = 0;
            memset(data, 0, expectedSize + 1);
            unsigned char * buffer = data;
            while (TRUE) {
                UInt32 processedSize = (UInt32)-1;
                WITH_HRESULT(inStream->Read((void *)buffer, toRead, &processedSize));
                if (processedSize > toRead) {
                    printf("Read more, that requested. Read %u, requested: %u\n", processedSize, toRead);
                    delete [] data;
                    return "Read more, that requested";
                }
                if (!processedSize) {
                    break;
                }
                for (UInt32 i = 0; i < size + 1 - read; i++) {
                    unsigned char expectedByte = i < processedSize ? TEST_BUFFER_CONTENT(pos + read + i) : 0;
                    if (expectedByte != buffer[i]) {
                        printf("Data error. At position %u (buffer pos: %u) read: %i, expected %i\n", (UInt32)(pos + read + i), i, buffer[i], expectedByte);
                        delete [] data;
                        return "Data error";
                    }
                }
                read += processedSize;
                buffer += processedSize;
                toRead -= processedSize;

            };

            if (read != size) {
                printf("Read wrong amount of data at pos %u. Expected: %u, read: %u\n", (UInt32)pos, size, read);
                delete [] data;
                return "Read wrong amount of data";
            }
            pos += size;
            UInt64 positionAfterRead = 0;
            WITH_HRESULT(inStream->Seek(0, SEEK_CUR, &positionAfterRead));
            if (positionAfterRead != pos) {
                printf("Invalid current position. Expected: %u, current: %u\n", (UInt32)pos, (UInt32)positionAfterRead);
                printf("%u", (UInt32)pos);
                delete [] data;
                return "Invalid position after read";
            }
        }
    }

	// Read outsize of the stream
	for (UInt32 pos = expectedSize; pos < expectedSize + 10; pos++) {
		UInt64 newPos;
		WITH_HRESULT(inStream->Seek(expectedSize, SEEK_SET, &newPos));
		if (newPos != expectedSize) {
			printf("Move pos outside of the stream. Expected: %u, current: %u\n", (UInt32)expectedSize, (UInt32)newPos);
			delete [] data;
			return "Moved pos outside of the stream";
		}

		unsigned char buffer[3];
		UInt32 read;
		WITH_HRESULT(inStream->Read(&buffer, 3, &read));
		if (read != 0) {
			printf("Read outside of the stream. Expected read bytes: 0, read: %u\n", read);
			delete [] data;
			return "Read outside of the stream";
		}
	}

    delete [] data;
    return NULL;
}

const char * testHeadCacheInStream(UInt32 streamSize, UInt32 cacheSize, bool readEntireCache) {
    CGeneratorInStream * generator = new CGeneratorInStream(streamSize);
    CMyComPtr<IInStream> generatorInStream = generator;
	CHeadCacheInStream * headCacheInStream = new CHeadCacheInStream(generatorInStream, cacheSize);
    CMyComPtr<IInStream> inStream = headCacheInStream;

	headCacheInStream->Init(readEntireCache);

    const char * err = testInStream(inStream, streamSize);
	if (err) {
		return err;
	}
	UInt32 * count = generator->GetReadCount();
	for (UInt32 i = 0; i < streamSize; i++) {
		if (i < cacheSize) {
			if (count[i] != 1) {
				printf("Invalid read count in the cached area of the stream, Pos: %u. Expected 1, Get: %u\n", i, count[i]);
				return "Invalid read count in the cached area of the stream. Expected 1";
			}
		} else {
			if (count[i] == 0) {
				printf("Invalid read count in the non-cached area of the stream, Pos: %u. Expected non-null, Get: %u\n", i, count[i]);
				return "Invalid read count in the non-cached area of the stream. Expected non-null";
			}
		}
	}
	return NULL;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_CHeadCacheInStreamTest_nativeSimpleTests(JNIEnv * env, jobject thiz, jboolean printTraceLog) {

#define RUN(caption, test) {								\
        if (printTraceLog) 									\
            printf("Test: " caption "\n");					\
        const char * msg = (test);							\
        if (msg) {											\
            printf("FAILED TEST: " caption "\n");			\
            return env->NewStringUTF(msg);					\
        }													\
    }

    RUN("CGeneratorInStream, empty", testInStream(CMyComPtr<IInStream>(new CGeneratorInStream(0)), 0));
    RUN("CGeneratorInStream, size 1", testInStream(CMyComPtr<IInStream>(new CGeneratorInStream(1)), 1));
    RUN("CGeneratorInStream, size 100", testInStream(CMyComPtr<IInStream>(new CGeneratorInStream(100)), 100));

    RUN("CGeneratorInStream ReadOverTheEndOfStream test, size 100", testReadOverTheEndOfStream(CMyComPtr<CGeneratorInStream>(new CGeneratorInStream(100)), 100));
    RUN("CGeneratorInStream ReadCount test, size 100", testReadCountInStream(CMyComPtr<CGeneratorInStream>(new CGeneratorInStream(100)), 100));
	

    RUN("CHeadCacheInStream, empty (no cache)", testHeadCacheInStream(0, 0, false))
    RUN("CHeadCacheInStream, empty (no cache, reb)", testHeadCacheInStream(0, 0, true))
    RUN("CHeadCacheInStream, empty (1 cache)", testHeadCacheInStream(0, 1, false))
    RUN("CHeadCacheInStream, empty (1 cache, reb)", testHeadCacheInStream(0, 1, true))
    RUN("CHeadCacheInStream, empty (100 cache)", testHeadCacheInStream(0, 100, false))
    RUN("CHeadCacheInStream, empty (100 cache, reb)", testHeadCacheInStream(0, 100, true))
	RUN("CHeadCacheInStream, size 1 (no cache)", testHeadCacheInStream(1, 0, false))
	RUN("CHeadCacheInStream, size 1 (no cache, reb)", testHeadCacheInStream(1, 0, true))
	RUN("CHeadCacheInStream, size 1 (full cache)", testHeadCacheInStream(1, 1, false))
	RUN("CHeadCacheInStream, size 1 (full cache, reb)", testHeadCacheInStream(1, 1, true))
	RUN("CHeadCacheInStream, size 1 (100 cache)", testHeadCacheInStream(1, 100, false))
	RUN("CHeadCacheInStream, size 1 (100 cache, reb)", testHeadCacheInStream(1, 100, true))
    RUN("CHeadCacheInStream, size 100 (no cache)", testHeadCacheInStream(100, 0, false))
    RUN("CHeadCacheInStream, size 100 (no cache, reb)", testHeadCacheInStream(100, 0, true))
    RUN("CHeadCacheInStream, size 100 (1/2 cache)", testHeadCacheInStream(100, 50, false))
    RUN("CHeadCacheInStream, size 100 (1/2 cache, reb)", testHeadCacheInStream(100, 50, true))
    RUN("CHeadCacheInStream, size 100 (full cache)", testHeadCacheInStream(100, 100, false))
    RUN("CHeadCacheInStream, size 100 (full cache, reb)", testHeadCacheInStream(100, 100, true))
    RUN("CHeadCacheInStream, size 100 (200 cache)", testHeadCacheInStream(100, 200, false))
    RUN("CHeadCacheInStream, size 100 (200 cache, reb)", testHeadCacheInStream(100, 200, true))

    return NULL;
}

#endif

