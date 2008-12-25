#include "StdAfx.h"

#include "jnitools.h"
#include "CLSIDs.h"

#include "SevenZipJBinding.h"

#include "Java/all.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "CPPToJava/CPPToJavaUniversalArchiveOpenCallback.h"

#include "VM.h"

CreateObjectFunc createObjectFunc;


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
        TRACE1("Error initializing 7-zip library: %s", msg)
        return env->NewStringUTF(msg);
    }
    
    TRACE("7-zip library initialized")
    
    return NULL;
}

/*
 * Class:     net_sf_sevenzip_SevenZip
 * Method:    nativeOpenArchive
 * Signature: (ILnet/sf/sevenzip/IInStream;Lnet/sf/sevenzip/IArchiveOpenCallback;)Lnet/sf/sevenzip/IInArchive;
 */
JNIEXPORT jobject JNICALL Java_net_sf_sevenzip_SevenZip_nativeOpenArchive
(JNIEnv * env, jclass thiz, jint format, jobject inStream, jobject archiveOpenCallbackImpl)
{
    TRACE("SevenZip.nativeOpenArchive()")
    
    CMyComPtr<VM> vm = new VM(env);
    
    // Test format
    if (format < 0 || format >= guidsCount)
    {
        ThrowSevenZipException(env, "Format %i out of range. There are only %i formats availible", format, guidsCount);
        return NULL;
    }

    CMyComPtr<IInArchive> archive;

    if (createObjectFunc(&guids[format], &IID_IInArchive, (void **)&archive) != S_OK)
    {
        fatal("Can't get class object");
    }

    TRACE2("Opening archive file in format %i (%i)... ", (int)format, (size_t)(void*)archive)

    CMyComPtr<IInStream> jis = NULL; // new CPPToJavaInStream(env, inStream);

    CMyComPtr<IArchiveOpenCallback> archiveOpenCallback;
    if (archiveOpenCallbackImpl)
    {
        TRACE("Using archive open callback")
        
        archiveOpenCallback = new CPPToJavaUniversalArchiveOpencallback(vm, env, archiveOpenCallbackImpl);
    }
    IInStream * stream = new CPPToJavaInStream(vm, env, inStream);
    
    TRACE("Opening...")
    
    UInt64 maxCheckStartPosition = 0;
    HRESULT openResult = archive->Open(stream, &maxCheckStartPosition, archiveOpenCallback);
    if (openResult != S_OK)
    {
        TRACE1("Result = 0x%08X, throwing exception...", (int)openResult)
        
        ThrowSevenZipException(env, openResult, "Archive file (format: %i) can't be opened", format);
        return NULL;
    }

    TRACE("Archive opened")
    
    jobject InArchiveImplObject = GetSimpleInstance(env, IN_ARCHIVE_IMPL);
    SetIntegerAttribute(env, InArchiveImplObject, IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
            (size_t)(void*)(archive.Detach()));

    return InArchiveImplObject;
}

