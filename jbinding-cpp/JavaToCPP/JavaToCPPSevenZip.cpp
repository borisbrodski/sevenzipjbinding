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

#include "JavaStatInfos/InArchiveImpl.h"

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

void test(JNIEnv * env); // TODO Remove this

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeInitSevenZipLibrary
 * Signature: ()Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_SevenZip_nativeInitSevenZipLibrary(JNIEnv * env, jclass thiz) {

    TRACE("7-zip library initialized (TODO)")

    CodecTools::init();

    test(env);
    //if (msg) {
    //	TRACE1("Error initializing 7-zip library: %s", msg)
    //	return env->NewStringUTF(msg);
    //}

    return NULL;
}

/* TODO Delete me
void setArchiveFormat(JNIEnv * env, jobject inArchiveImplObject, const UString & formatNameString) {
    jclass c = env->GetObjectClass(inArchiveImplObject);
    jmethodID methodId = env->GetMethodID(c, "setArchiveFormat", "(Ljava/lang/String;)V");

    jstring jstring = env->NewString(UnicodeHelper(formatNameString), formatNameString.Length());
    env->CallVoidMethod(inArchiveImplObject, methodId, jstring);
    env->ExceptionClear();
    return;
}
*/

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

    NativeMethodContext nativeMethodContext(env);

    TRY

        JNIInstance jniInstance(&nativeMethodContext);

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
        }

        CMyComPtr<IInArchive> archive;
        CMyComPtr<CPPToJavaInStream> stream = new CPPToJavaInStream(&nativeMethodContext, env,
                inStream);

        CMyComPtr<IArchiveOpenCallback> archiveOpenCallback;

        if (archiveOpenCallbackImpl) {
            TRACE("Using archive open callback")

            archiveOpenCallback = new UniversalArchiveOpencallback(&nativeMethodContext, env,
                    archiveOpenCallbackImpl, (CPPToJavaInStream *) stream);
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
                nativeMethodContext.ThrowSevenZipException(result,
                        "Archive file (format: %S) can't be opened",
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

                nativeMethodContext.ThrowSevenZipException(
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

        if (nativeMethodContext.WillExceptionBeThrown()) {
            archive->Close();
            return NULL;
        }

        TRACE("Archive opened")

        jobject InArchiveImplObject = InArchiveImpl::_class.newInstance(env);

        jstring jstringFormatNameString = env->NewString(UnicodeHelper(formatNameString), formatNameString.Length());
        InArchiveImpl::setArchiveFormat(env, InArchiveImplObject, jstringFormatNameString);
        if (jniInstance.IsExceptionOccurs()) {
            archive->Close();
            return NULL;
        }

        SetLongAttribute(env, InArchiveImplObject, IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
                (jlong) (size_t) (void*) (archive.Detach()));

        SetLongAttribute(env, InArchiveImplObject, IN_STREAM_IMPL_OBJ_ATTRIBUTE,
                (jlong) (size_t) (void*) (stream));

        stream->ClearNativeMethodContext();

        stream.Detach();

        return InArchiveImplObject;

    CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
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

    NativeMethodContext nativeMethodContext(env);

    TRY

        JNIInstance jniInstance(&nativeMethodContext);

        UString formatNameString;
        int index = CodecTools::getIndexByName(env, formatName, formatNameString);

        if (checkForOutArchive && CodecTools::codecs.Formats[index].CreateOutArchive == NULL) {
            return -1;
        }

        return (jint) index;

    CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}
