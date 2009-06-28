#include "StdAfx.h"
#include "Common/MyInitGuid.h"

#include "SevenZipJBinding.h"


#include "JNITools.h"

#include "net_sf_sevenzipjbinding_SevenZip.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "UniversalArchiveOpenCallback.h"

#include "JNICallState.h"

using namespace NWindows;
using namespace NFile;

#ifdef MINGW
DEFINE_GUID(IID_IUnknown,
0x00000000, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x46);
#endif // MINGW

#include "7zip/UI/Common/LoadCodecs.h"
#include "UnicodeHelper.h"

#ifdef _WIN32
#ifndef _UNICODE
bool g_IsNT = false;
static inline bool IsItWindowsNT()
{
  OSVERSIONINFO versionInfo;
  versionInfo.dwOSVersionInfoSize = sizeof(versionInfo);
  if (!::GetVersionEx(&versionInfo))
    return false;
  return (versionInfo.dwPlatformId == VER_PLATFORM_WIN32_NT);
}
#endif
#endif


//CreateObjectFunc createObjectFunc;
STDAPI CreateCoder(const GUID *clsid, const GUID *iid, void **outObject);
STDAPI CreateArchiver(const GUID *classID, const GUID *iid, void **outObject);

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeInitSevenZipLibrary
 * Signature: ()Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeInitSevenZipLibrary(
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
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeOpenArchive(JNIEnv * env,
		jclass thiz, jstring formatName, jobject inStream,
		jobject archiveOpenCallbackImpl) {
	TRACE("SevenZip.nativeOpenArchive()")

	NativeMethodContext nativeMethodContext(env);

	TRY

	JNIInstance jniInstance(&nativeMethodContext);

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
		fatal("codecs->Load() return error: 0x%08X", result);

	for (int i = 0; i < codecs->Formats.Size(); i++) {
		TRACE1("Available codec: '%S'", (const wchar_t*)codecs->Formats[i].Name)
	}

	int index = -1;
	UString formatNameString;
	if (formatName)
	{
		const jchar * formatNameJChars = env->GetStringChars(formatName, NULL);
		formatNameString = UnicodeHelper(formatNameJChars);
		env->ReleaseStringChars(formatName, formatNameJChars);

		TRACE1("Format: '%S'\n", (const wchar_t*)formatNameString);
		index = codecs->FindFormatForArchiveType(formatNameString);
		if (index == -1) {
			fatal("Not registered archive format: '%S'", (const wchar_t*)formatNameString);
		}
	}

	CMyComPtr<IInArchive> archive;
	CMyComPtr<CPPToJavaInStream> stream = new CPPToJavaInStream(&nativeMethodContext, env, inStream);

	CMyComPtr<IArchiveOpenCallback> archiveOpenCallback;

	if (archiveOpenCallbackImpl)
	{
		TRACE("Using archive open callback")

		archiveOpenCallback = new UniversalArchiveOpencallback(&nativeMethodContext, env,
				archiveOpenCallbackImpl, (CPPToJavaInStream *)stream);
	}

	if (index != -1) {
		// Use one specified codec
		codecs->CreateInArchive(index, archive);
	    if (!archive) {
	    	fatal("Can't get InArchive class for codec %S",  (const wchar_t *)formatNameString);
	    }

		TRACE1("Opening using codec %S", (const wchar_t*)codecs->Formats[index].Name);

		HRESULT result = archive->Open(stream, NULL, archiveOpenCallback);

	    if (result != S_OK) {
			TRACE1("Result = 0x%08X, throwing exception...", (int)result)
			nativeMethodContext.ThrowSevenZipException(result, "Archive file (format: %S) can't be opened", (const wchar_t *)formatNameString);

			return NULL;
		}
	} else {
		// Try all known codecs
		TRACE("Iterating through all available codecs...")
		bool success = false;
		for (int i = 0; i < codecs->Formats.Size(); i++) {
			TRACE1("Trying codec %S", (const wchar_t*)codecs->Formats[i].Name);

			stream->Seek(0, STREAM_SEEK_SET, NULL);

			codecs->CreateInArchive(i, archive);
		    if (!archive) {
		    	continue;
		    }

		    HRESULT result = archive->Open(stream, NULL, archiveOpenCallback);
		    if (result != S_OK) {
		    	continue;
			}

		    success = true;
		    break;
		}

		if (!success) {
			TRACE("Success=false, throwing exception...")

			nativeMethodContext.ThrowSevenZipException("Archive file can't be opened with none of the registered codecs");
			return NULL;
		}

	}

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
*/

	TRACE("Archive opened")

	jobject InArchiveImplObject = GetSimpleInstance(env, IN_ARCHIVE_IMPL);

	SetIntegerAttribute(env, InArchiveImplObject, IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
			(size_t)(void*)(archive.Detach()));

	SetIntegerAttribute(env, InArchiveImplObject, IN_STREAM_IMPL_OBJ_ATTRIBUTE,
			(size_t)(void*)(stream));

	stream->ClearNativeMethodContext();

	stream.Detach();

	return InArchiveImplObject;

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

