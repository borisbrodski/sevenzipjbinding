#include "SevenZipJBinding.h"
#include "JNITools.h"
#include "net_sf_sevenzipjbinding_impl_InArchiveImpl.h"
#include "CPPToJava/CPPToJavaArchiveExtractCallback.h"
#include "CodecTools.h"
#include "UnicodeHelper.h"

#include "JavaStatInfos/JavaPackageSevenZip.h"


static IInArchive * GetArchive(JNIEnv * env, jobject thiz) {
    jlong pointer = jni::InArchiveImpl::sevenZipArchiveInstance_Get(env, thiz);
    FATALIF(!pointer, "GetArchive() : pointer == NULL");

    return (IInArchive *) (void *) (size_t) pointer;
}

static JBindingSession & GetJBindingSession(JNIEnv * env, jobject thiz) {
    jlong pointer = jni::InArchiveImpl::jbindingSession_Get(env, thiz);
    FATALIF(!pointer, "GetJBindingSession() : pointer == NULL");

    return *((JBindingSession *) (void *) (size_t) pointer);
}

static IInStream * GetInStream(JNIEnv * env, jobject thiz) {
    jlong pointer = jni::InArchiveImpl::sevenZipInStreamInstance_Get(env, thiz);
    FATALIF(!pointer, "GetInStream() : pointer == NULL.");

    return (IInStream *) (void *) (size_t) pointer;
}

int CompareIndicies(const void *pi1, const void * pi2) {
    UInt32 i1 = *(UInt32*) pi1;
    UInt32 i2 = *(UInt32*) pi2;
    return i1 > i2 ? 1 : (i1 < i2 ? -1 : 0);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeExtract
 * Signature: ([IZLnet/sf/sevenzip/IArchiveExtractCallback;)V
 */
JBINDING_JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeExtract(
                                                                                              JNIEnv * env,
                                                                                              jobject thiz,
                                                                                              jintArray indicesArray,
                                                                                              jboolean testMode,
                                                                                              jobject archiveExtractCallbackObject) {
    TRACE("InArchiveImpl::nativeExtract(). ThreadID=" << PlatformGetCurrentThreadId());

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return;
    }

    jint * indices = NULL;
    UInt32 indicesCount = (UInt32) -1;
    UInt32 numberOfItems;
    HRESULT result = archive->GetNumberOfItems((UInt32*) &numberOfItems);
    if (result != S_OK) {
        TRACE("Error getting number of items from archive. Result: 0x" << std::hex << result)
        jniNativeCallContext.reportError(result, "Error getting number of items from archive");
        return;
    }
    if (indicesArray) {
        indices = env->GetIntArrayElements(indicesArray, NULL);

        indicesCount = env->GetArrayLength(indicesArray);

        jint lastIndex = -1;
        int sortNeeded = false;
        for (UInt32 i = 0; i < indicesCount; i++) {
            if (indices[i] < 0 || indices[i] >= numberOfItems) {
                TRACE("Passed index for the extraction is incorrect: " << indices[i] << " (Count of items in archive: " << numberOfItems << ")")
                jniNativeCallContext.reportError(
                        result,
                        "Passed index for the extraction is incorrect: %i (Count of items in archive: %i)",
                        indices[i], numberOfItems);
                return;
            }
            if (lastIndex > indices[i])
                sortNeeded = true;
            lastIndex = indices[i];
        }
        if (sortNeeded)
            qsort(indices, indicesCount, 4, &CompareIndicies);
    }

    CMyComPtr<IArchiveExtractCallback> archiveExtractCallback =
            new CPPToJavaArchiveExtractCallback(jbindingSession, env, archiveExtractCallbackObject);

    TRACE("Extracting " << indicesCount << " items")
    result = archive->Extract((UInt32*) indices, indicesCount, (Int32) testMode,
            archiveExtractCallback);

    archiveExtractCallback.Release();

    if (indicesArray)
        env->ReleaseIntArrayElements(indicesArray, indices, JNI_ABORT);
    else
        delete[] indices;

    if (result) {
        TRACE("Extraction error. Result: 0x" << std::hex << result);
        if (indicesCount == -1) {
            jniNativeCallContext.reportError(result, "Error extracting all items");
        } else {
            jniNativeCallContext.reportError(result, "Error extracting %i item(s)", indicesCount);
        }
    } else {
        TRACE("Extraction succeeded")
    }

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfItems
 * Signature: ()I
 */
JBINDING_JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetNumberOfItems(
                                                                                                       JNIEnv * env,
                                                                                                       jobject thiz) {
    TRACE("InArchiveImpl::nativeGetNumberOfItems(). ThreadID=" << PlatformGetCurrentThreadId());

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return 0;
    }

    UInt32 result;

    CHECK_HRESULT(jniNativeCallContext, archive->GetNumberOfItems(&result), "Error getting number of items from archive");

    TRACE("Returning: " << result)

    return result;

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeClose
 * Signature: ()V
 */
JBINDING_JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeClose(
                                                                                            JNIEnv * env,
                                                                                            jobject thiz) {
    TRACE("InArchiveImpl::nativeClose(). ThreadID=" << PlatformGetCurrentThreadId());

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    {
        JNINativeCallContext jniNativeCallContext(jbindingSession, env);
        JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

        CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
        CMyComPtr<IInStream> inStream(GetInStream(env, thiz));

        if (archive == NULL) {
            TRACE("Archive==NULL. Do nothing...");
            return;
        }

        CHECK_HRESULT(jniNativeCallContext, archive->Close(), "Error closing archive");

        archive->Release();
        inStream->Release();

        jni::InArchiveImpl::sevenZipArchiveInstance_Set(env, thiz, 0);
        jni::InArchiveImpl::jbindingSession_Set(env, thiz, 0);
        jni::InArchiveImpl::sevenZipInStreamInstance_Set(env, thiz, 0);

    }

    delete &jbindingSession;

    TRACE("InArchive closed")
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfArchiveProperties
 * Signature: ()I
 */
JBINDING_JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetNumberOfArchiveProperties(
                                                                                                                   JNIEnv * env,
                                                                                                                   jobject thiz) {
    TRACE("InArchiveImpl::GetNumberOfArchiveProperties()");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return 0;
    }

    UInt32 result;

    CHECK_HRESULT(jniNativeCallContext, archive->GetNumberOfArchiveProperties(&result), "Error getting number of archive properties");

    return result;

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetArchivePropertyInfo
 * Signature: (I)Lnet/sf/sevenzip/PropertyInfo;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetArchivePropertyInfo(
                                                                                                                JNIEnv * env,
                                                                                                                jobject thiz,
                                                                                                                jint index) {
    TRACE("InArchiveImpl::nativeGetArchivePropertyInfo()");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...")
        return NULL;
    }

    VARTYPE type;
    CMyComBSTR name;
    PROPID propID;

    CHECK_HRESULT1(jniNativeCallContext, archive->GetArchivePropertyInfo(index, &name, &propID, &type), "Error getting archive property info with index %i", index);

    jobject propertInfo = jni::PropertyInfo::_newInstance(env);
    if (jniEnvInstance.exceptionCheck()) {
        return NULL;
    }

    jstring javaName;
    if (((const wchar_t *)name) == NULL) {
        javaName = env->NewStringUTF("");
    } else {
        javaName = ToJChar(name).toNewString(env);
    }
    jclass javaType = VarTypeToJavaType(jniEnvInstance, type);

    jobject propIDObject = jni::PropID::getPropIDByIndex(env, propID);
    if (jniEnvInstance.exceptionCheck()) {
        return NULL;
    }

    jni::PropertyInfo::propID_Set(env, propertInfo, propIDObject);
    jni::PropertyInfo::name_Set(env, propertInfo, javaName);
#ifdef __ANDROID_API__
    env->DeleteLocalRef(javaName);
#endif
    jni::PropertyInfo::varType_Set(env, propertInfo, javaType);

    return propertInfo;

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetArchiveProperty
 * Signature: (J)Lnet/sf/sevenzip/PropVariant;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetArchiveProperty(
                                                                                                            JNIEnv * env,
                                                                                                            jobject thiz,
                                                                                                            jint propID) {
    TRACE("InArchiveImpl::nativeGetArchiveProperty");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...")
        return NULL;
    }

    NWindows::NCOM::CPropVariant PropVariant;

    CHECK_HRESULT1(jniNativeCallContext, archive->GetArchiveProperty(propID, &PropVariant), "Error getting property mit Id: %lu", propID);

    return PropVariantToObject(jniEnvInstance, &PropVariant);

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetStringArchiveProperty
 * Signature: (I)Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetStringArchiveProperty(
                                                                                                                  JNIEnv * env,
                                                                                                                  jobject thiz,
                                                                                                                  jint propID) {
    TRACE("InArchiveImpl::nativeGetStringArchiveProperty");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    NWindows::NCOM::CPropVariant PropVariant;

    CHECK_HRESULT1(jniNativeCallContext, archive->GetArchiveProperty(propID, &PropVariant), "Error getting property mit Id: %lu", propID);

    return PropVariantToString(env, propID, PropVariant);

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfProperties
 * Signature: ()I
 */
JBINDING_JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetNumberOfProperties(
                                                                                                            JNIEnv * env,
                                                                                                            jobject thiz) {
    TRACE("InArchiveImpl::nativeGetNumberOfProperties");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return 0;
    }

    UInt32 result;

    CHECK_HRESULT(jniNativeCallContext, archive->GetNumberOfProperties(&result), "Error getting number of properties");

    return result;

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetProperty
 * Signature: (IJ)Ljava/lang/Object;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetProperty(
                                                                                                     JNIEnv * env,
                                                                                                     jobject thiz,
                                                                                                     jint index,
                                                                                                     jint propID) {
    TRACE("InArchiveImpl::nativeGetProperty");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    NWindows::NCOM::CPropVariant propVariant;

    CHECK_HRESULT2(jniNativeCallContext, archive->GetProperty(index, propID, &propVariant), "Error getting property with propID=%lu for item %i", propID, index);

    return PropVariantToObject(jniEnvInstance, &propVariant);

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetStringProperty
 * Signature: (II)Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetStringProperty(
                                                                                                           JNIEnv * env,
                                                                                                           jobject thiz,
                                                                                                           jint index,
                                                                                                           jint propID) {
    TRACE("InArchiveImpl::nativeGetStringProperty");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    NWindows::NCOM::CPropVariant propVariant;

    CHECK_HRESULT2(jniNativeCallContext, archive->GetProperty(index, propID, &propVariant), "Error getting property with propID=%lu for item %i", propID, index);

    return PropVariantToString(env, propID, propVariant);

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetPropertyInfo
 * Signature: (I)Lnet/sf/sevenzip/PropertyInfo;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetPropertyInfo(
                                                                                                         JNIEnv * env,
                                                                                                         jobject thiz,
                                                                                                         jint index) {
    TRACE("InArchiveImpl::nativeGetPropertyInfo");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    if (archive == NULL) {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    VARTYPE type;
    CMyComBSTR name;
    PROPID propID;

    CHECK_HRESULT1(jniNativeCallContext, archive->GetPropertyInfo(index, &name, &propID, &type), "Error getting property info with index %i", index);

    jobject propertInfo = jni::PropertyInfo::_newInstance(env);
    if (jniEnvInstance.exceptionCheck()) {
        return NULL;
    }

    jstring javaName;
    if (((const wchar_t *)name) == NULL) {
        javaName = env->NewStringUTF("");
    } else {
        javaName = ToJChar(name).toNewString(env);
    }
    jclass javaType = VarTypeToJavaType(jniEnvInstance, type);

    jobject propIDObject = jni::PropID::getPropIDByIndex(env, propID);
    if (jniEnvInstance.exceptionCheck()) {
        return NULL;
    }

    jni::PropertyInfo::propID_Set(env, propertInfo, propIDObject);
    jni::PropertyInfo::name_Set(env, propertInfo, javaName);
#ifdef __ANDROID_API__
    env->DeleteLocalRef(javaName);
#endif
    jni::PropertyInfo::varType_Set(env, propertInfo, javaType);

    return propertInfo;
}

/*
 * Class:     net_sf_sevenzipjbinding_impl_InArchiveImpl
 * Method:    nativeConnectOutArchive
 * Signature: (Lnet/sf/sevenzipjbinding/impl/OutArchiveImpl;Lnet/sf/sevenzipjbinding/ArchiveFormat;)V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeConnectOutArchive
  (JNIEnv * env, jobject thiz, jobject outArchiveImpl, jobject archiveFormat) {

    TRACE("InArchiveImpl::nativeConnectOutArchive");

    JBindingSession & jbindingSession = GetJBindingSession(env, thiz);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

    int archiveFormatIndex = codecTools.getArchiveFormatIndex(jniEnvInstance, archiveFormat);

    if (archiveFormatIndex < 0 || codecTools.codecs.Formats[archiveFormatIndex].CreateOutArchive == NULL) {
        jniEnvInstance.reportError("Internal error during creating OutArchive. Archive format index: %i",
        		archiveFormatIndex);
        return;
    }

    CMyComPtr<IOutArchive> outArchive;

    HRESULT hresult = archive->QueryInterface(IID_IOutArchive, (void**)&outArchive);
    if (hresult) {
        jniEnvInstance.reportError(hresult, "Error connecting OutArchive to the InArchive for archive format %S",
                (const wchar_t*) codecTools.codecs.Formats[archiveFormatIndex].Name);
        return;
    }

    jni::OutArchiveImpl::sevenZipArchiveInstance_Set(env, outArchiveImpl, //
            (jlong) (size_t) (void*) (outArchive.Detach()));

    jni::OutArchiveImpl::jbindingSession_Set(env, outArchiveImpl, //
            (jlong) (size_t) (void*) (&jbindingSession));
}
