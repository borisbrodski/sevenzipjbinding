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
 * Method:    setLevelNative
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeSetLevel
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
    propValues[0] = (unsigned int)level;

    CRecordVector<const wchar_t *> names;
    names.Add(L"X");

    result = setProperties->SetProperties(&names.Front(), propValues, names.Size());
    if (result) {
        TRACE("Error setting 'Level' property. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error setting 'Level' property.");
        return;
    }
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    nativeSetHeaderEncryption
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeSetHeaderEncryption
  (JNIEnv * env, jobject thiz, jboolean enabled) {
    TRACE("OutArchiveImpl::nativeSetHeaderEncryption(). ThreadID=" << PlatformGetCurrentThreadId());

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
    propValues[0] = (bool)enabled;

    CRecordVector<const wchar_t *> names;
    names.Add(L"HE"); // See 7zHandlerOut.cpp:823

    result = setProperties->SetProperties(&names.Front(), propValues, names.Size());
    if (result) {
        TRACE("Error setting 'Header Encryption' property. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error setting 'Header Encryption' property.");
        return;
    }
}
/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    nativeSetSolidSpec
 * Signature: (Ljava/land/String;)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeSetSolidSpec(JNIEnv * env, jobject thiz, jstring solidSpec) {
    TRACE("OutArchiveImpl::nativeSetSolidSpec(). ThreadID=" << PlatformGetCurrentThreadId());

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
    if (solidSpec == NULL) {
		// printf("[SolidSpec:false]");fflush(stdout);
        propValues[0] = false;
    } else {
		// printf("[SolidSpec:%S]", UString(UnicodeHelper(jchars)).GetBuffer(100000));fflush(stdout);
        propValues[0] = UString(FromJChar(env, solidSpec));
    }
    CRecordVector<const wchar_t *> names;
    names.Add(L"S");

    result = setProperties->SetProperties(&names.Front(), propValues, names.Size());
    if (result) {
        TRACE("Error setting 'Solid' property. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error setting 'Solid' property.");
        return;
    }
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    nativeSetMultithreading
 * Signature: (Ljava/land/String;)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeSetMultithreading(JNIEnv * env, jobject thiz, jint threadCount) {
    TRACE("OutArchiveImpl::nativeSetMultithreading(). ThreadID=" << PlatformGetCurrentThreadId());

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
	// printf("[MT:%i]", (int)threadCount);fflush(stdout);
    if (threadCount) {
        propValues[0] = (UInt32)threadCount;
    } else {
    	// Use count of available processors
        propValues[0] = true;
    }
    CRecordVector<const wchar_t *> names;
    names.Add(L"MT");

    result = setProperties->SetProperties(&names.Front(), propValues, names.Size());
    if (result) {
        TRACE("Error setting 'Multithreading' property. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error setting 'Multithreading' property.");
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
