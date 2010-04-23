#include "SevenZipJBinding.h"

#include "JNITools.h"

#include "net_sf_sevenzipjbinding_impl_OutArchiveImpl.h"
#include "CodecTools.h"

#include "CPPToJava/CPPToJavaOutStream.h"
#include "CPPToJava/CPPToJavaArchiveUpdateCallback.h"

#include "JNICallState.h"

// void updateItemsNative(int archiveFormatIndex, IOutStream outStream, int numberOfItems,
//                        IArchiveUpdateCallback archiveUpdateCallback)

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    updateItemsNative
 * Signature: (ILnet/sf/sevenzipjbinding/ISequentialOutStream;ILnet/sf/sevenzipjbinding/IArchiveUpdateCallback;)V
 */
JBINDING_JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_updateItemsNative(
                                                                                                   JNIEnv * env,
                                                                                                   jobject,
                                                                                                   jint archiveFormatIndex,
                                                                                                   jobject outStream,
                                                                                                   jint numberOfItems,
                                                                                                   jobject archiveUpdateCallback) {
    TRACE("OutArchiveImpl.updateItemsNative()");

    JBindingSession jbindingSession(env);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IOutArchive> outArchive;
    HRESULT hresult = CodecTools::codecs.CreateOutArchive(archiveFormatIndex, outArchive);
    if (hresult) {
        jniEnvInstance.reportError(hresult, "Error creating OutArchive for archive format %S",
                (const wchar_t*) CodecTools::codecs.Formats[archiveFormatIndex].Name);
        return;
    }

    CMyComPtr<IOutStream> cppToJavaOutStream = new CPPToJavaOutStream(jbindingSession, env,
            outStream);

    CMyComPtr<IArchiveUpdateCallback> cppToJavaArchiveUpdateCallback =
            new CPPToJavaArchiveUpdateCallback(jbindingSession, env, archiveUpdateCallback,
                    false);

    hresult = outArchive->UpdateItems(cppToJavaOutStream, numberOfItems,
            cppToJavaArchiveUpdateCallback);

    if (hresult) {
        jniEnvInstance.reportError(hresult, "Error creating '%S' archive with %i items",
                (const wchar_t*) CodecTools::codecs.Formats[archiveFormatIndex].Name,
                (int) numberOfItems);
    }

    return;
}
