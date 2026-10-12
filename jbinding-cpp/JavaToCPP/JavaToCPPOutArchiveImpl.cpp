#include "SevenZipJBinding.h"

#include "JNITools.h"

#include "net_sf_sevenzipjbinding_impl_OutArchiveImpl.h"
#include "CodecTools.h"

#include "CPPToJava/CPPToJavaOutStream.h"
#include "CPPToJava/CPPToJavaArchiveUpdateCallback.h"

#include "UnicodeHelper.h"
#include "UserTrace.h"

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
 * Signature: (ILnet/sf/sevenzipjbinding/ISequentialOutStream;ILnet/sf/sevenzipjbinding/IArchiveUpdateCallback;Z)V
 */
JBINDING_JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeUpdateItems(
                                                                                                   JNIEnv * env,
                                                                                                   jobject thiz,
                                                                                                   jobject outStream,
                                                                                                   jint numberOfItems,
                                                                                                   jobject archiveUpdateCallback) {
    TRACE("OutArchiveImpl.updateItemsNative()");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

	CMyComPtr<IOutArchive> outArchive(GetArchive(env, thiz));

    jobject archiveFormat = jni::OutArchiveImpl::archiveFormat_Get(env, thiz);
    int archiveFormatIndex = codecTools.getArchiveFormatIndex(jniEnvInstance, archiveFormat);
    jboolean isInArchiveAttached = jni::OutArchiveImpl::inArchive_Get(env, thiz) != NULL;

	if (isUserTraceEnabled(jniEnvInstance, thiz)) {
	    if (isInArchiveAttached) {
	        userTrace(jniEnvInstance, thiz, UString(L"Updating ") << (UInt32)numberOfItems << L" items");
	    } else {
	        userTrace(jniEnvInstance, thiz, UString(L"Compressing ") << (UInt32)numberOfItems << L" items");
	    }
	}

	CMyComPtr<IOutStream> cppToJavaOutStream = new CPPToJavaOutStream(jbindingSession, env,
			outStream);

	CPPToJavaArchiveUpdateCallback * cppToJavaArchiveUpdateCallback = new CPPToJavaArchiveUpdateCallback(
	        jbindingSession, env,
	        archiveUpdateCallback,
	        isInArchiveAttached,
	        archiveFormatIndex,
	        thiz);

	CMyComPtr<IArchiveUpdateCallback> cppToJavaArchiveUpdateCallbackPtr = cppToJavaArchiveUpdateCallback;

	HRESULT hresult  = outArchive->UpdateItems(cppToJavaOutStream, numberOfItems,
			cppToJavaArchiveUpdateCallback);
	if (hresult) {
		jniEnvInstance.reportError(hresult, "Error creating '%S' archive with %i items",
				(const wchar_t*) codecTools.codecs.Formats[archiveFormatIndex].Name,
				(int) numberOfItems);
	}

	cppToJavaArchiveUpdateCallback->freeOutItem(jniEnvInstance);
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    nativeSetProperties
 * Signature: (IIILjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeSetProperties
  (JNIEnv * env, jobject thiz, jint compressionLevel, jint headerEncryption, jint threadCount,
   jstring solidSpec, jboolean solidSpecDefined) {
    TRACE("OutArchiveImpl::nativeSetProperties(). ThreadID=" << PlatformGetCurrentThreadId());

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

    const wchar_t *names[4];
    NWindows::NCOM::CPropVariant propValues[4];
    UInt32 size = 0;

    if (compressionLevel != -1) {
        names[size] = L"X";
        propValues[size++] = (UInt32)compressionLevel;
    }
    if (headerEncryption != -1) {
        names[size] = L"HE";
        propValues[size++] = (headerEncryption != 0);
    }
    if (threadCount >= 0) {
        names[size] = L"MT";
        // Zero means to use the count of available processors.
        if (threadCount == 0) {
            propValues[size++] = true;
        } else {
            propValues[size++] = (UInt32)threadCount;
        }
    }
    if (solidSpecDefined) {
        names[size] = L"S";
        if (solidSpec == NULL) {
            propValues[size++] = false;
        } else {
            propValues[size++] = UString(FromJChar(env, solidSpec));
        }
    }

    result = setProperties->SetProperties(names, propValues, size);
    if (result) {
        TRACE("Error setting archive properties. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error setting archive properties.");
        return;
    }
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    nativeClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeClose
  (JNIEnv * env, jobject thiz) {

    TRACE("InArchiveImpl::nativeClose(). ThreadID=" << PlatformGetCurrentThreadId());

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    {
        JNINativeCallContext jniNativeCallContext(jbindingSession, env);
        JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

        CMyComPtr<IOutArchive> outArchive(GetArchive(env, thiz));

        outArchive->Release();

        jni::OutArchiveImpl::sevenZipArchiveInstance_Set(env, thiz, 0);
        jni::OutArchiveImpl::jbindingSession_Set(env, thiz, 0);

        TRACE("sevenZipArchiveInstance and jbindingSession references cleared, outArchive released")
    }
    delete &jbindingSession;

    TRACE("OutArchive closed")
}
