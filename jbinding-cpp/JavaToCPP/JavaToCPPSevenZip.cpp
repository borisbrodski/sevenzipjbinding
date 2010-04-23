#include "StdAfx.h"
#include "Common/MyInitGuid.h"

#include "SevenZipJBinding.h"

#include "JNITools.h"

#include "net_sf_sevenzipjbinding_SevenZip.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "UniversalArchiveOpenCallback.h"
#include "CodecTools.h"

#include "JNICallState.h"

#include "iostream"

#include "JavaStatInfos/JavaPackageSevenZip.h"

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
    if (!::GetVersionEx(&versionInfo)) {
        return false;
    }
    return (versionInfo.dwPlatformId == VER_PLATFORM_WIN32_NT);
}
#endif
#endif

#define JAVA_STATIC_EXTERN extern
#include "JavaStaticInfo.h"

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeInitSevenZipLibrary
 * Signature: ()Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_SevenZip_nativeInitSevenZipLibrary(JNIEnv * env, jclass thiz) {

    TRACE("7-zip library initialized (TODO)")

    CodecTools::init();

    //if (msg) {
    //	TRACE1("Error initializing 7-zip library: %s", msg)
    //	return env->NewStringUTF(msg);
    //}

    return NULL;
}

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeOpenArchive
 * Signature: (ILnet/sf/sevenzip/IInStream;Lnet/sf/sevenzip/IArchiveOpenCallback;)Lnet/sf/sevenzip/IInArchive;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeOpenArchive(
                                                                                           JNIEnv * env,
                                                                                           jclass thiz,
                                                                                           jstring formatName,
                                                                                           jobject inStream,
                                                                                           jobject archiveOpenCallbackImpl) {
    TRACE("SevenZip.nativeOpenArchive()")

    JBindingSession & jbindingSession = *(new JBindingSession(env));
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

#ifdef TRACE_ON
    for (int i = 0; i < CodecTools::codecs.Formats.Size(); i++) {
        TRACE("Available codec: " << CodecTools::codecs.Formats[i].Name)
    }
#endif // TRACE_ON
    //for (int i = 0; i < SevenZipJBinding::codecs.Formats.Size(); i++) {
    //	printf("Available codec: '%S'\n", (const wchar_t*)SevenZipJBinding::codecs.Formats[i].Name);
    //	fflush(stdout);
    //}

    int index = -1;
    UString formatNameString;
    if (formatName) {
        index = CodecTools::getIndexByName(env, formatName, formatNameString);
        if (index == -1) {
            jniNativeCallContext.reportError("Not registered archive format: '%S'",
                    (const wchar_t*) formatNameString);
            return NULL;
        }
    }

    CMyComPtr<IInArchive> archive;
    CMyComPtr<CPPToJavaInStream> stream = new CPPToJavaInStream(jbindingSession, env, inStream);

    CMyComPtr<IArchiveOpenCallback> archiveOpenCallback;

    if (archiveOpenCallbackImpl) {
        TRACE("Using archive open callback")

        archiveOpenCallback = new UniversalArchiveOpencallback(jbindingSession, env,
                archiveOpenCallbackImpl);
    }

    UInt64 maxCheckStartPosition = 4 * 1024 * 1024; // Advice from Igor Pavlov

    if (index != -1) {
        // Use one specified codec
        CodecTools::codecs.CreateInArchive(index, archive);
        if (!archive) {
            fatal("Can't get InArchive class for codec %S", (const wchar_t *) formatNameString);
        }

        TRACE("Opening using codec " << CodecTools::codecs.Formats[index].Name);
        HRESULT result = archive->Open(stream, &maxCheckStartPosition, archiveOpenCallback);

        if (result != S_OK) {
            TRACE("Result = 0x" << std::hex << result << ", throwing exception...")
            jniEnvInstance.reportError(result, "Archive file (format: %S) can't be opened",
                    (const wchar_t *) formatNameString);
            return NULL;
        }
    } else {
        // Try all known codecs
        TRACE("Iterating through all available codecs...")
        bool success = false;
        for (int i = 0; i < CodecTools::codecs.Formats.Size(); i++) {
            TRACE("Trying codec " << CodecTools::codecs.Formats[i].Name);

            stream->Seek(0, STREAM_SEEK_SET, NULL);

            CodecTools::codecs.CreateInArchive(i, archive);
            if (!archive) {
                continue;
            }
            HRESULT result = archive->Open(stream, &maxCheckStartPosition, archiveOpenCallback);
            if (result != S_OK) {
                continue;
            }

            formatNameString = CodecTools::codecs.Formats[i].Name;
            success = true;
            break;
        }

        if (!success) {
            TRACE("Success=false, throwing exception...")

            jniEnvInstance.reportError(
                    "Archive file can't be opened with none of the registered codecs");
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
     {name
     TRACE1("Result = 0x%08X, throwing exception...", (int)openResult)

     nativeMethodContext.ThrowSevenZipException(openResult, "Archive file (format: %i) can't be opened", format);
     return NULL;
     }
     */

    if (jniNativeCallContext.willExceptionBeThrown()) {
        archive->Close();
        return NULL;
    }

    TRACE("Archive opened")

    jobject inArchiveImplObject = jni::InArchiveImpl::_newInstance(env);
    jni::expectExceptionCheck(env);

    jstring jstringFormatNameString = env->NewString(UnicodeHelper(formatNameString),
            formatNameString.Length());
    jni::InArchiveImpl::setArchiveFormat(env, inArchiveImplObject, jstringFormatNameString);
    if (jniEnvInstance.exceptionCheck()) {
        archive->Close();
        return NULL;
    }

    jni::InArchiveImpl::sevenZipArchiveInstance_Set(env, inArchiveImplObject, //
            (jlong) (size_t) (void*) (archive.Detach()));

    jni::InArchiveImpl::jbindingSession_Set(env, inArchiveImplObject, //
            (jlong) (size_t) (void*) (&jbindingSession));

    jni::InArchiveImpl::sevenZipInStreamInstance_Set(env, inArchiveImplObject, //
            (jlong) (size_t) (void*) (stream));

    // SetLongAttribute(env, inArchiveImplObject, IN_STREAM_IMPL_OBJ_ATTRIBUTE,
    //        (jlong) (size_t) (void*) (stream));

    stream.Detach(); // TODO Join with previous statement

    return inArchiveImplObject;

    //CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

// private static native int getSevenZipCCodersArchiveFormatIndex(String archiveFormat, boolean checkForOutArchive)

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    getSevenZipCCodersArchiveFormatIndex
 * Signature: (Ljava/lang/String;Z)I
 */
JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_SevenZip_getSevenZipCCodersArchiveFormatIndex(
                                                                                                  JNIEnv * env,
                                                                                                  jclass thiz,
                                                                                                  jstring formatName,
                                                                                                  jboolean checkForOutArchive) {
    TRACE("SevenZip.getSevenZipCCodersArchiveFormatIndex()")

    UString formatNameString;
    int index = CodecTools::getIndexByName(env, formatName, formatNameString);

    if (checkForOutArchive && CodecTools::codecs.Formats[index].CreateOutArchive == NULL) {
        return -1;
    }

    return (jint) index;
}
