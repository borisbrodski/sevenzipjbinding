#include "StdAfx.h"

#include "jnitools.h"
#include "CLSIDs.h"

#include "SevenZipJBinding.h"

#include "Java/all.h"
#include "JavaInStream.h"


CreateObjectFunc createObjectFunc;


// Load 7-Zip DLL.
// Return: NULL - ok, else error message
static char * load7ZipLibrary(CreateObjectFunc * createObjectFunc)
{
	HINSTANCE lib = LoadLibraryA(DLLFILENAME);

	if (NULL == lib)
		return "Error loading 7-Zip library: " DLLFILENAME;

	*createObjectFunc = (CreateObjectFunc)GetProcAddress(lib, "CreateObject");

	if (NULL == *createObjectFunc)
		return "Not a 7-Zip Library. Missing 'CreateObject' export name";

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
	
	JavaInStream * jis = new JavaInStream(env, sequentialInStream);

	CMyComPtr<IInArchive> archive;

	if (createObjectFunc(&guids[format], &IID_IInArchive, (void **)&archive) != S_OK)
	{
		fatal("Can not get class object");
	}

//	printf("Opening archive file in format %i (%i)... ", (int)format, (size_t)(void*)archive);
//	fflush(stdout);

	if (archive->Open(jis, 0, 0) != S_OK)
	{
		ThrowSevenZipException(env, "Archive file (format: %i) can't be opened", format);
		return NULL;
	}

	jobject InArchiveImplObject = GetSimpleInstance(env, IN_ARCHIVE_IMPL);
	SetIntegerAttribute(env, InArchiveImplObject, IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
			(size_t)(void*)(archive.Detach()));

	
	return InArchiveImplObject;
}


/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    initSevenZipLibrary
 * Signature: ()V
 */
JNIEXPORT jstring JNICALL Java_net_sf_sevenzip_SevenZip_initSevenZipLibrary
(JNIEnv * env, jclass clazz)
{
	char * msg = load7ZipLibrary(&createObjectFunc);

	if (msg)
	{
    	return env->NewStringUTF(msg);
	}
	return NULL;
}
