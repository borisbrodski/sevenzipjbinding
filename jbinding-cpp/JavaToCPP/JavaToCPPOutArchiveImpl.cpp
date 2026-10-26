#include "SevenZipJBinding.h"

#include "JNITools.h"

#include "net_sf_sevenzipjbinding_impl_OutArchiveImpl.h"
#include "CodecTools.h"

#include "CPPToJava/CPPToJavaOutStream.h"
#include "CPPToJava/CPPToJavaSequentialOutStream.h"
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
    jniNativeCallContext.captureContextClassLoader();
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

	// Only a Java IOutStream gets the seekable wrapper. A plain ISequentialOutStream used to be wrapped as
	// IOutStream too, so 7-Zip's first Seek() looked up a seek() method the object doesn't have and aborted
	// the JVM ("Method not found: seek()"). Without IOutStream 7-Zip writes zip, tar, gzip, bzip2 and xz
	// sequentially and fails 7z with E_NOTIMPL, which is reported below.
	bool isSeekableOutStream = jni::IOutStream::_isInstance(env, outStream);
	CMyComPtr<ISequentialOutStream> cppToJavaOutStream;
	if (isSeekableOutStream) {
		cppToJavaOutStream = new CPPToJavaOutStream(jbindingSession, env, outStream);
	} else {
		cppToJavaOutStream = new CPPToJavaSequentialOutStream(jbindingSession, env, outStream);
	}

	CPPToJavaArchiveUpdateCallback * cppToJavaArchiveUpdateCallback = new CPPToJavaArchiveUpdateCallback(
	        jbindingSession, env,
	        archiveUpdateCallback,
	        isInArchiveAttached,
	        archiveFormatIndex,
	        thiz);

	CMyComPtr<IArchiveUpdateCallback> cppToJavaArchiveUpdateCallbackPtr = cppToJavaArchiveUpdateCallback;

	HRESULT hresult  = outArchive->UpdateItems(cppToJavaOutStream, numberOfItems,
			cppToJavaArchiveUpdateCallback);
	if (hresult == E_NOTIMPL && !isSeekableOutStream) {
		jniEnvInstance.reportError(hresult, "Error creating '%S' archive with %i items: this archive format "
				"needs a seekable output stream. Pass an IOutStream (for example RandomAccessFileOutStream or "
				"ByteArrayStream) instead of a plain ISequentialOutStream",
				(const wchar_t*) codecTools.codecs.Formats[archiveFormatIndex].Name,
				(int) numberOfItems);
	} else if (hresult) {
		jniEnvInstance.reportError(hresult, "Error creating '%S' archive with %i items",
				(const wchar_t*) codecTools.codecs.Formats[archiveFormatIndex].Name,
				(int) numberOfItems);
	}

	cppToJavaArchiveUpdateCallback->freeOutItem(jniEnvInstance);
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_OutArchiveImpl
 * Method:    nativeSetProperties
 * Signature: (IIILjava/lang/String;ZLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_OutArchiveImpl_nativeSetProperties
  (JNIEnv * env, jobject thiz, jint compressionLevel, jint headerEncryption, jint threadCount,
   jstring solidSpec, jboolean solidSpecDefined, jstring dictionarySizeSpec) {
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

    const wchar_t *names[5];
    NWindows::NCOM::CPropVariant propValues[5];
    UInt32 size = 0;
    AString description; // The properties as sent, to tell in the error message which one 7-Zip rejected

    if (compressionLevel != -1) {
        names[size] = L"X";
        propValues[size++] = (UInt32)compressionLevel;
        description += "X=";
        description.Add_UInt32((UInt32)compressionLevel);
    }
    if (headerEncryption != -1) {
        names[size] = L"HE";
        propValues[size++] = (headerEncryption != 0);
        description.Add_Space_if_NotEmpty();
        description += headerEncryption ? "HE=on" : "HE=off";
    }
    if (threadCount >= 0) {
        names[size] = L"MT";
        description.Add_Space_if_NotEmpty();
        // Zero means to use the count of available processors.
        if (threadCount == 0) {
            propValues[size++] = true;
            description += "MT=on";
        } else {
            propValues[size++] = (UInt32)threadCount;
            description += "MT=";
            description.Add_UInt32((UInt32)threadCount);
        }
    }
    if (solidSpecDefined) {
        names[size] = L"S";
        description.Add_Space_if_NotEmpty();
        if (solidSpec == NULL) {
            propValues[size++] = false;
            description += "S=off";
        } else {
            UString solidSpecString(FromJChar(env, solidSpec));
            propValues[size++] = solidSpecString;
            description += "S=";
            for (unsigned i = 0; i < solidSpecString.Len(); i++) {
                description.Add_Char((char)solidSpecString[i]); // Built by OutArchive7zImpl: ASCII only
            }
        }
    }
    if (dictionarySizeSpec != NULL) {
        // "0d": dictionary size of the first (main) compression method.
        names[size] = L"0d";
        UString dictionarySizeString(FromJChar(env, dictionarySizeSpec));
        propValues[size++] = dictionarySizeString;
        description.Add_Space_if_NotEmpty();
        description += "0d=";
        for (unsigned i = 0; i < dictionarySizeString.Len(); i++) {
            description.Add_Char((char)dictionarySizeString[i]); // Built by OutArchiveImpl: ASCII only
        }
    }

    result = setProperties->SetProperties(names, propValues, size);
    if (result) {
        TRACE("Error setting archive properties (" << description.Ptr() << "). Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error setting archive properties: %s", description.Ptr());
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
