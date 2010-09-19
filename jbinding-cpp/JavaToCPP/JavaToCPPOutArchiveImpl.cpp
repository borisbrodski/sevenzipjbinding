#include "SevenZipJBinding.h"

#include "JNITools.h"

#include "net_sf_sevenzipjbinding_impl_OutArchiveImpl.h"
#include "CodecTools.h"

#include "CPPToJava/CPPToJavaOutStream.h"
#include "CPPToJava/CPPToJavaArchiveUpdateCallback.h"

#include "JNICallState.h"

// void updateItemsNative(int archiveFormatIndex, IOutStream outStream, int numberOfItems,
//                        IArchiveUpdateCallback archiveUpdateCallback)

static JBindingSession & GetJBindingSession(JNIEnv * env, jobject thiz) {
    jlong pointer = jni::OutArchiveImpl::jbindingSession_Get(env, thiz);
    FATALIF(!pointer, "GetJBindingSession() : pointer == NULL");

    return *((JBindingSession *) (void *) (size_t) pointer);
}

static IOutArchive * GetArchive(JNIEnv * env, jobject thiz) {
    jlong pointer = jni::OutArchiveImpl::sevenZipArchiveInstance_Get(env, thiz);
    FATALIF(!pointer, "GetArchive() : pointer == NULL");

    return (IOutArchive *) (void *) (size_t) pointer;
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    updateItemsNative
 * Signature: (ILnet/sf/sevenzipjbinding/ISequentialOutStream;ILnet/sf/sevenzipjbinding/IArchiveUpdateCallback;)V
 */
JBINDING_JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_updateItemsNative(
                                                                                                   JNIEnv * env,
                                                                                                   jobject thiz,
                                                                                                   jint archiveFormatIndex,
                                                                                                   jobject outStream,
                                                                                                   jint numberOfItems,
                                                                                                   jobject archiveUpdateCallback) {
    TRACE("OutArchiveImpl.updateItemsNative()");

    JBindingSession & jbindingSession(GetJBindingSession(env, thiz));
{
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IOutArchive> outArchive(GetArchive(env, thiz));

    CMyComPtr<IOutStream> cppToJavaOutStream = new CPPToJavaOutStream(jbindingSession, env,
            outStream);

    CMyComPtr<IArchiveUpdateCallback> cppToJavaArchiveUpdateCallback =
            new CPPToJavaArchiveUpdateCallback(jbindingSession, env, archiveUpdateCallback,
                    false);

    HRESULT hresult  = outArchive->UpdateItems(cppToJavaOutStream, numberOfItems,
            cppToJavaArchiveUpdateCallback);

    if (hresult) {
        jniEnvInstance.reportError(hresult, "Error creating '%S' archive with %i items",
                (const wchar_t*) CodecTools::codecs.Formats[archiveFormatIndex].Name,
                (int) numberOfItems);
    }
    outArchive->Release();
}
    delete &jbindingSession;

    return;
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    setLevelNative
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_setLevelNative
  (JNIEnv * env, jobject thiz, jint level) {
    TRACE("OutArchiveImpl::setLevelNative(). ThreadID=" << PlatformGetCurrentThreadId());

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IOutArchive> outArchive(GetArchive(env, thiz));
    // TODO Delete this and all other such ifs, also in J2CppInArchive.cpp, since this is already tested in GetArchive()
    if (outArchive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return;
    }

    // TODO Move query interface to the central location in J2C+SevenZip.cpp
    CMyComPtr<ISetProperties> setProperties;
    HRESULT result = outArchive->QueryInterface(IID_ISetProperties, (void**)&setProperties);
    if (result != S_OK) {
        TRACE("Error getting IID_ISetProperties interface. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error getting IID_ISetProperties interface.");
        return;
    }

    const int size = 1;
    NWindows::NCOM::CPropVariant *propValues = new NWindows::NCOM::CPropVariant[size];
    propValues[0] = (unsigned int)0;

    CRecordVector<const wchar_t *> names;
    names.Add(L"X");

    result = setProperties->SetProperties(&names.Front(), propValues, names.Size());
    if (result) {
        TRACE("Error setting 'Level' property. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error setting 'Level' property.");
        return;
    }
}
