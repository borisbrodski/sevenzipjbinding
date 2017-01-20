#include "StdAfx.h"
#include "Common/MyInitGuid.h"
#include "../C/7zVersion.h"

#include "SevenZipJBinding.h"

#include "JNITools.h"

#include "net_sf_sevenzipjbinding_SevenZip.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "UniversalArchiveOpenCallback.h"
#include "CodecTools.h"

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

	codecTools.init();

    jni::OutOfMemoryError::_initialize(env);

    //if (msg) {
    //	TRACE1("Error initializing 7-zip library: %s", msg)
    //	return env->NewStringUTF(msg);
    //}

    return NULL;
}

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    nativeGetVersionMajor
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeGetVersionMajor(JNIEnv *, jclass) {
    TRACE("SevenZip.nativeGetVersionMajor()")

    return MY_VER_MAJOR;
}

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    nativeGetVersionMinor
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeGetVersionMinor(JNIEnv *, jclass) {
    TRACE("SevenZip.nativeGetVersionMinor()")

    return MY_VER_MINOR;
}

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    nativeGetVersionBuild
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeGetVersionBuild(JNIEnv *, jclass) {
    TRACE("SevenZip.nativeGetVersionBuild()")

    return MY_VER_BUILD;
}

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    nativeGetVersionVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeGetVersionVersion(JNIEnv * env, jclass) {
    TRACE("SevenZip.nativeGetVersionVersion()")

    return env->NewStringUTF(MY_VERSION);
}

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    nativeGetVersionDate
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeGetVersionDate(JNIEnv * env, jclass) {
    TRACE("SevenZip.nativeGetVersionDate()")

    return env->NewStringUTF(MY_DATE);
}

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    nativeGetVersionCopyright
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeGetVersionCopyright(JNIEnv * env, jclass) {
    TRACE("SevenZip.nativeGetVersionCopyright()")

    return env->NewStringUTF(MY_COPYRIGHT);
}


JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeOpenArchive(
                                                                                           JNIEnv * env,
                                                                                           jclass thiz,
                                                                                           jobject archiveFormat,
                                                                                           jobject inStream,
                                                                                           jobject archiveOpenCallbackImpl) {
    TRACE("SevenZip.nativeOpenArchive()")

    JBindingSession & jbindingSession = *(new JBindingSession(env));
    DeleteInErrorCase<JBindingSession> deleteInErrorCase(jbindingSession);

    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    int index = -1;
    UString formatNameString;
    if (archiveFormat) {
        index = codecTools.getArchiveFormatIndex(env, archiveFormat);
        if (index == -1) {
            codecTools.getArchiveFormatName(env, archiveFormat, formatNameString);
            jniNativeCallContext.reportError("Not registered archive format: '%S'",
                    (const wchar_t*) formatNameString);
            deleteInErrorCase.setErrorCase();
            return NULL;
        }
        formatNameString = codecTools.codecs.Formats[index].Name;
    }

    CMyComPtr<IInArchive> archive;
    CMyComPtr<CPPToJavaInStream> stream = new CPPToJavaInStream(jbindingSession, env, inStream);

    UniversalArchiveOpencallback * universalArchiveOpencallback = new UniversalArchiveOpencallback(jbindingSession, env, archiveOpenCallbackImpl);
	CMyComPtr<IArchiveOpenCallback> archiveOpenCallback = universalArchiveOpencallback;

    UInt64 maxCheckStartPosition = 4 * 1024 * 1024; // Advice from Igor Pavlov

    if (index != -1) {
        // Use one specified codec
    	codecTools.codecs.CreateInArchive(index, archive);
        if (!archive) {
            fatal("Can't get InArchive class for codec %S", (const wchar_t *) formatNameString);
        }

        TRACE("Opening using codec " << codecTools.codecs.Formats[index].Name);

        universalArchiveOpencallback->setSimulateArchiveOpenVolumeCallback(codecTools.isCabArchive(index));

        HRESULT result = archive->Open(stream, &maxCheckStartPosition, archiveOpenCallback);

        if (result != S_OK) {
            TRACE("Result = 0x" << std::hex << result << ", throwing exception...")
            jniEnvInstance.reportError(result, "Archive file (format: %S) can't be opened",
                    (const wchar_t *) formatNameString);
            deleteInErrorCase.setErrorCase();
            return NULL;
        }
    } else {
        // Try all known codecs
        TRACE("Iterating through all available codecs...")
        bool success = false;
        for (int i = 0; i < codecTools.codecs.Formats.Size(); i++) {
            TRACE("Trying codec " << codecTools.codecs.Formats[i].Name);

            stream->Seek(0, STREAM_SEEK_SET, NULL);

            codecTools.codecs.CreateInArchive(i, archive);
            if (!archive) {
                continue;
            }

            universalArchiveOpencallback->setSimulateArchiveOpenVolumeCallback(codecTools.isCabArchive(i));

            HRESULT result = archive->Open(stream, &maxCheckStartPosition, archiveOpenCallback);
            if (result != S_OK) {
                continue;
            }

            formatNameString = codecTools.codecs.Formats[i].Name;
            success = true;
            break;
        }

        if (!success) {
            TRACE("Success=false, throwing exception...")

            jniEnvInstance.reportError(
                    "Archive file can't be opened with any of the registered codecs");
            deleteInErrorCase.setErrorCase();
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
        deleteInErrorCase.setErrorCase();
        return NULL;
    }

    TRACE("Archive opened")

    jobject inArchiveImplObject = jni::InArchiveImpl::_newInstance(env);
    if (jniEnvInstance.exceptionCheck()) {
        archive->Close();
        deleteInErrorCase.setErrorCase();
        return NULL;
    }

    jstring jstringFormatNameString = ToJChar(formatNameString).toNewString(env);
    jni::InArchiveImpl::setArchiveFormat(env, inArchiveImplObject, jstringFormatNameString);
    env->DeleteLocalRef(jstringFormatNameString);
    if (jniEnvInstance.exceptionCheck()) {
        archive->Close();
        deleteInErrorCase.setErrorCase();
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

    stream.Detach();

    return inArchiveImplObject;
}

/*
 * Class:     net_sf_sevenzipjbinding_SevenZip
 * Method:    nativeCreateArchive
 * Signature: (Lnet/sf/sevenzipjbinding/impl/OutArchiveImpl;Lnet/sf/sevenzipjbinding/ArchiveFormat;)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_SevenZip_nativeCreateArchive(
                                                                                 JNIEnv * env,
                                                                                 jclass thiz,
                                                                                 jobject outArchiveImpl,
                                                                                 jobject archiveFormat) {
    TRACE("SevenZip.nativeCreateArchive()")

    JBindingSession & jbindingSession = *(new JBindingSession(env));
    DeleteInErrorCase<JBindingSession> deleteInErrorCase(jbindingSession);

    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    int archiveFormatIndex = codecTools.getArchiveFormatIndex(jniEnvInstance, archiveFormat);

    if (archiveFormatIndex < 0 || codecTools.codecs.Formats[archiveFormatIndex].CreateOutArchive == NULL) {
        jniEnvInstance.reportError("Internal error during creating OutArchive. Archive format index: %i",
        		archiveFormatIndex);
        deleteInErrorCase.setErrorCase();
        return;
    }

    CMyComPtr<IOutArchive> outArchive;
    HRESULT hresult = codecTools.codecs.CreateOutArchive(archiveFormatIndex, outArchive);
    if (hresult) {
        jniEnvInstance.reportError(hresult, "Error creating OutArchive for archive format %S",
                (const wchar_t*) codecTools.codecs.Formats[archiveFormatIndex].Name);
        deleteInErrorCase.setErrorCase();
        return;
    }

    jni::OutArchiveImpl::sevenZipArchiveInstance_Set(env, outArchiveImpl, //
            (jlong) (size_t) (void*) (outArchive.Detach()));

    jni::OutArchiveImpl::jbindingSession_Set(env, outArchiveImpl, //
            (jlong) (size_t) (void*) (&jbindingSession));

    jni::OutArchiveImpl::archiveFormat_Set(env, outArchiveImpl, archiveFormat);
}

#ifdef ANDROID_NDK
// 'FindClass' start in the "system" class loader in the threads created by native code.
// So attempts to find app-specific classes will fail.
// I make 'Class SevenZip.findClass(String)' java static method to find class with
// app class loader.
// Check https://developer.android.com/training/articles/perf-jni.html
// FAQ: Why didn't FindClass find my class?
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env = NULL;
    FATALIF(vm->GetEnv((void**) (&env), JNI_VERSION_1_6) != JNI_OK, "Can't get JNIEnv");
    FATALIF(env == NULL, "Can't get JNIEnv");

    jclass sevenZipClass = env->FindClass(SEVEN_ZIP_PACKAGE "/SevenZip");
    FATALIF(sevenZipClass == NULL, "Can't find " SEVEN_ZIP_PACKAGE "/SevenZip class");
    sevenZipClass = (jclass) env->NewGlobalRef(sevenZipClass);

    jmethodID findClassMethodID = env->GetStaticMethodID(sevenZipClass, "findClass",
            "(Ljava/lang/String;)Ljava/lang/Class;");

    InitFindClass(sevenZipClass, findClassMethodID);

    return JNI_VERSION_1_6;
}
#endif
