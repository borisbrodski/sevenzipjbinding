#include "StdAfx.h"

#include "Common/MyInitGuid.h"


#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CLSIDs.h"

#include "Java/net_sf_sevenzip_SevenZip.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "UniversalArchiveOpenCallback.h"

#include "JNICallState.h"

using namespace NWindows;
using namespace NFile;


#include "7zip/UI/Common/LoadCodecs.h"

//CreateObjectFunc createObjectFunc;
STDAPI CreateCoder(const GUID *clsid, const GUID *iid, void **outObject);
STDAPI CreateArchiver(const GUID *classID, const GUID *iid, void **outObject);

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeInitSevenZipLibrary
 * Signature: ()Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL Java_net_sf_sevenzip_SevenZip_nativeInitSevenZipLibrary(
		JNIEnv * env, jclass thiz) {
	//const char * msg = load7ZipLibrary(&createObjectFunc);

	//if (msg) {
	//	TRACE1("Error initializing 7-zip library: %s", msg)
	//	return env->NewStringUTF(msg);
	//}

	TRACE("7-zip library initialized (TODO)")

	return NULL;
}

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeOpenArchive
 * Signature: (ILnet/sf/sevenzip/IInStream;Lnet/sf/sevenzip/IArchiveOpenCallback;)Lnet/sf/sevenzip/IInArchive;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzip_SevenZip_nativeOpenArchive(JNIEnv * env,
		jclass thiz, jstring formatName, jobject inStream,
		jobject archiveOpenCallbackImpl) {
	TRACE("SevenZip.nativeOpenArchive()")

	NativeMethodContext nativeMethodContext(env);

	TRY

	JNIInstance jniInstance(&nativeMethodContext);

	// Test format
	if (format < 0 || format >= guidsCount)
	{
		nativeMethodContext.ThrowSevenZipException("Format %i out of range. There are only %i formats availible", format, guidsCount);
		return NULL;
	}

	CMyComPtr<IInArchive> archive;


	CCodecs *codecs = new CCodecs;
	CMyComPtr<
	    #ifdef EXTERNAL_CODECS
	    ICompressCodecsInfo
	    #else
	    IUnknown
	    #endif
	    > compressCodecsInfo = codecs;

	HRESULT result = codecs->Load();
	if (result != S_OK)
		fatal("result != S_OK");



	UString s(L"zip");

	int index = codecs->FindFormatForArchiveType(s);
	if (index == -1) {
        fatal("Not registered archive format: '%S'", (const wchar_t*)s);
	}

	fatal("Success: %i", formatIndices.Size());

	/*
    CMyComPtr<IOutArchive> outArchive;


	TRACE2("Using format: %i, fnc: 0x%08X", format, createObjectFunc)

	CCodecs *codecs = new CCodecs;
	CMyComPtr<
	    #ifdef EXTERNAL_CODECS
	    ICompressCodecsInfo
	    #else
	    IUnknown
	    #endif
	    > compressCodecsInfo = codecs;
	  HRESULT result = codecs->Load();
	  if (result != S_OK)
			fatal("result != S_OK");

*/

/*
	for (int i = 0; i < 20; i++) {
	  printf("Name: '%S'\n", (const wchar_t*)(codecs->GetCodecName(i)));
	  fflush(stdout);
	}
*/
	/*
	  UString s(L"zip");

	CIntVector formatIndices;
	if (codecs->FindFormatForArchiveType(s) == -1)
	{
		fatal("Can't get class object");
	}

	fatal("Success!");
*/
	return NULL;
/*
	if (CreateArchiver(&guids[format], &IID_IInArchive, (void **)&archive) != S_OK)
	{
		fatal("Can't get class object");
	}

	TRACE2("Opening archive file in format %i (%i)... ", (int)format, (size_t)(void*)archive)

	CMyComPtr<IInStream> jis = NULL; // new CPPToJavaInStream(env, inStream);

	CMyComPtr<IArchiveOpenCallback> archiveOpenCallback;
	if (archiveOpenCallbackImpl)
	{
		TRACE("Using archive open callback")

		archiveOpenCallback = new UniversalArchiveOpencallback(&nativeMethodContext, env, archiveOpenCallbackImpl);
	}
	CMyComPtr<CPPToJavaInStream> stream = new CPPToJavaInStream(&nativeMethodContext, env, inStream);

	TRACE("Opening...")

	UInt64 maxCheckStartPosition = 0;
	HRESULT openResult = archive->Open((IInStream *)stream, &maxCheckStartPosition, archiveOpenCallback);
	if (openResult != S_OK)
	{
		TRACE1("Result = 0x%08X, throwing exception...", (int)openResult)

		nativeMethodContext.ThrowSevenZipException(openResult, "Archive file (format: %i) can't be opened", format);
		return NULL;
	}

	TRACE("Archive opened")

	jobject InArchiveImplObject = GetSimpleInstance(env, IN_ARCHIVE_IMPL);

	SetIntegerAttribute(env, InArchiveImplObject, IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
			(size_t)(void*)(archive.Detach()));

	SetIntegerAttribute(env, InArchiveImplObject, IN_STREAM_IMPL_OBJ_ATTRIBUTE,
			(size_t)(void*)(stream));

	stream->ClearNativeMethodContext();
	stream.Detach();

	return InArchiveImplObject;

*/
	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

