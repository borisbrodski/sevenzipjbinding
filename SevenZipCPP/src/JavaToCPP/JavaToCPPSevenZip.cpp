#include "StdAfx.h"

#include "jnitools.h"
#include "CLSIDs.h"

#include "SevenZipJBinding.h"

#include "Java/all.h"
#include "CPPToJava/CPPToJavaInStream.h"

CreateObjectFunc createObjectFunc;

/*
class TestInStream : public IInStream, public CMyUnknownImp
{
private:
	FILE * file;

public:
	MY_UNKNOWN_IMP

	TestInStream()
	{
		file
				= fopen(
						"D:\\Private\\workspace\\SevenZipJava\\TestArchives\\TestContent.7z",
						"rb");
		if (file == NULL)
		{
			printf("ERROR OPENING FILE!\n");
			fflush(stdout);
			exit(1);
		}
		printf("File opened!\n");
		fflush(stdout);
	}

	STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize)
	{
		printf("Reading %u!\n", size);
		*processedSize = fread(data, 1, size, file);
//		*(char *)data = 'A';
		printf("processedSize = %u\n", *processedSize);
		fflush(stdout);
		return S_OK;

	}

	STDMETHOD(Seek)(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition)
	{
		// printf("Seek(%i,%i)");
		fseek(file, offset, seekOrigin);
		if (newPosition != NULL)
		{
			*newPosition = ftello64(file);
		}
		return S_OK;
	}

};

class TestSequentialOutStream : public ISequentialOutStream,
	public CMyUnknownImp
{
	MY_UNKNOWN_IMP

	STDMETHOD(Write)(const void *data, UInt32 size, UInt32 *processedSize)
	{
		printf("Writing %u bytes\n", size);
		for (UInt32 i = 0; i < size; i++)
		{
			fputc(((char *)data)[i], stdout);
		}
		*processedSize = size;
		fflush(stdout);
		return S_OK;
	}

};
class TestArchiveExctractCallback : public IArchiveExtractCallback,
	public CMyUnknownImp
{
	MY_UNKNOWN_IMP

	STDMETHOD(SetTotal)(UInt64 total)
	{
		printf("SetTotal(%i)\n", (int)total);
		fflush(stdout);
		return S_OK;
	}

	STDMETHOD(SetCompleted)(const UInt64 *completeValue)
	{
		printf("SetCompleted(%i)\n", (int)(*completeValue));
		fflush(stdout);
		return S_OK;
	}

	STDMETHOD(GetStream)(UInt32 index, ISequentialOutStream **outStream,
			Int32 askExtractMode)
	{
		printf("GetStream(%u, %s, %i)!\n", index, outStream == NULL ? "NULL" : "os", askExtractMode);
		fflush(stdout);
		
		if (askExtractMode != 0)
		{
			*outStream = NULL;
			return S_OK;
		}
		
		if (outStream != NULL)
		{
			CMyComPtr<ISequentialOutStream> out = new TestSequentialOutStream;
			*outStream = out.Detach();
		}
		return S_OK;
	}

	// GetStream OUT: S_OK - OK, S_FALSE - skeep this file
	STDMETHOD(PrepareOperation)(Int32 askExtractMode)
	{
		printf("PrepareOperation(%i)\n", askExtractMode);
		fflush(stdout);
		return S_OK;
	}
	STDMETHOD(SetOperationResult)(Int32 resultEOperationResult)
	{
		printf("SetOperationResult(%i)\n", resultEOperationResult);
		fflush(stdout);
		return S_OK;
	}
};
*/

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeInitSevenZipLibrary
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sf_sevenzip_SevenZip_nativeInitSevenZipLibrary
  (JNIEnv * env, jclass thiz)
{
	char * msg = load7ZipLibrary(&createObjectFunc);

	if (msg)
	{
		return env->NewStringUTF(msg);
	}
	return NULL;
}

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    openArchiveTest
 * Signature: (Lnet/sf/sevenzip/SequentialInStream;)Lnet/sf/sevenzip/SevenZip;
 */
JNIEXPORT jobject JNICALL Java_net_sf_sevenzip_SevenZip_nativeOpenArchive
(JNIEnv * env, jclass clazz, jint format, jobject sequentialInStream)
{
	// Test format
	if (format < 0 || format >= guidsCount)
	{
		ThrowSevenZipException(env, "Format %i out of range. There are only %i formats availible", format, guidsCount);
		return NULL;
	}

	CMyComPtr<IInArchive> archive;

	if (createObjectFunc(&guids[format], &IID_IInArchive, (void **)&archive) != S_OK)
	{
		fatal("Can not get class object");
	}

	//	printf("Opening archive file in format %i (%i)... ", (int)format, (size_t)(void*)archive);
	//	fflush(stdout);

	CMyComPtr<IInStream> jis = new CPPToJavaInStream(env, sequentialInStream);

	if (archive->Open(jis.Detach(), 0, 0) != S_OK)
	{
		ThrowSevenZipException(env, "Archive file (format: %i) can't be opened", format);
		return NULL;
	}
	
//	printf("EXTRACTIING.....\n");
//	fflush(stdout);
//
//	UInt32 indices = 4;
//	HRESULT hr = archive->Extract(&indices, 1, 0, new TestArchiveExctractCallback);
//	printf("HRESULT: 0x%X\n", (int)hr);
//	fflush(stdout);
//	exit(0);

	jobject InArchiveImplObject = GetSimpleInstance(env, IN_ARCHIVE_IMPL);
	SetIntegerAttribute(env, InArchiveImplObject, IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
			(size_t)(void*)(archive.Detach()));

	return InArchiveImplObject;
}

