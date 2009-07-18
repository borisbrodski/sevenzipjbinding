
#include "SevenZipJBinding.h"
#include "JNITools.h"
#include "net_sf_sevenzipjbinding_impl_InArchiveImpl.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "CPPToJava/CPPToJavaArchiveExtractCallback.h"
#include "JNICallState.h"


static bool initialized = 0;
static jfieldID g_ObjectAttributeFieldID;
static jfieldID g_InStreamAttributeFieldID;
static jclass g_PropertyInfoClazz;
static jfieldID g_PropertyInfo_name;
static jfieldID g_PropertyInfo_propID;
static jfieldID g_PropertyInfo_varType;
static jclass g_PropIDClazz;
static jmethodID g_PropID_getPropIDByIndex;

static void localinit(JNIEnv * env, jobject thiz)
{
	char classname[256];

	if (initialized)
	{
		return;
	}

	jclass clazz = env->GetObjectClass(thiz);
	FATALIF(clazz == NULL, "Can't get class from object");

	g_ObjectAttributeFieldID = env->GetFieldID(clazz, IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE, "J");
	FATALIF2(g_ObjectAttributeFieldID == NULL, "Field '%s' in the class '%s' was not found", IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
			GetJavaClassName(env, clazz, classname, sizeof(classname)));

	g_InStreamAttributeFieldID = env->GetFieldID(clazz,	IN_STREAM_IMPL_OBJ_ATTRIBUTE, "J");
	FATALIF2(g_InStreamAttributeFieldID == NULL, "Field '%s' in the class '%s' was not found", IN_STREAM_IMPL_OBJ_ATTRIBUTE,
			GetJavaClassName(env, clazz, classname, sizeof(classname)));

	// Initialize PropVariant
	g_PropertyInfoClazz = env->FindClass(PROPERTYINFO_CLASS);
	FATALIF1(g_PropertyInfoClazz == NULL, "Can't find class '%s'", PROPERTYINFO_CLASS);
	g_PropertyInfoClazz = (jclass)env->NewGlobalRef(g_PropertyInfoClazz);

	g_PropertyInfo_name = env->GetFieldID(g_PropertyInfoClazz, "name",
			"L" JAVA_STRING ";");
	FATALIF1(g_PropertyInfo_name == NULL, "Can't find attribute 'name' in the class %s", PROPERTYINFO_CLASS);

	g_PropertyInfo_propID = env->GetFieldID(g_PropertyInfoClazz, "propID",
	"L" PROPID_CLASS ";");
	FATALIF1(g_PropertyInfo_propID == NULL, "Can't find attribute 'propID' in the class %s", PROPERTYINFO_CLASS);

	g_PropertyInfo_varType = env->GetFieldID(g_PropertyInfoClazz, "varType",
			"Ljava/lang/Class;");
	FATALIF1(g_PropertyInfo_varType == NULL, "Can't find attribute 'varType' in the class %s", PROPERTYINFO_CLASS);

	// Initialize PropID
	g_PropIDClazz = env->FindClass(PROPID_CLASS);
	FATALIF1(g_PropIDClazz == NULL, "Can't find class '%s'", PROPID_CLASS);
	g_PropIDClazz = (jclass)env->NewGlobalRef(g_PropIDClazz);

	g_PropID_getPropIDByIndex = env->GetStaticMethodID(g_PropIDClazz,
			"getPropIDByIndex", "(I)" PROPID_CLASS_T);
	FATALIF1(g_PropID_getPropIDByIndex == NULL, "Can't method 'getPropIDByIndex(int)' in class '%s'", PROPID_CLASS);

	initialized = 1;
}

static IInArchive * GetArchive(JNIEnv * env, jobject thiz)
{
	jlong pointer;

	localinit(env, thiz);

	pointer = env->GetLongField(thiz, g_ObjectAttributeFieldID);

	if (!pointer)
	{
	    TRACE("GetArchive() : pointer == NULL. Throwing exception");
        throw SevenZipException("Can't preform action. Archive already closed.");
	}

	return (IInArchive *)(void *)(size_t)pointer;
}

static CPPToJavaInStream * GetInStream(JNIEnv * env, jobject thiz)
{
	jlong pointer;

	localinit(env, thiz);

	pointer = env->GetLongField(thiz, g_InStreamAttributeFieldID);

	if (!pointer)
	{
        throw SevenZipException("Can't perform action. InStream==NULL.");
	}

//    TRACE1("Getting STREAM: 0x%08X", (unsigned int)(Object *)(CPPToJavaInStream *)(void *)pointer);

    return (CPPToJavaInStream *)(void *)(size_t)pointer;
}

static void SetArchive(JNIEnv * env, jobject thiz, size_t pointer)
{
	localinit(env, thiz);

	env->SetLongField(thiz, g_ObjectAttributeFieldID, (jlong)pointer);
}

int CompareIndicies(const void *pi1, const void * pi2)
{
	UInt32 i1 = *(UInt32*)pi1;
	UInt32 i2 = *(UInt32*)pi2;
	return i1 > i2 ? 1 : (i1 < i2 ? -1 : 0);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeExtract
 * Signature: ([IZLnet/sf/sevenzip/IArchiveExtractCallback;)V
 */
JBINDING_JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeExtract
(JNIEnv * env, jobject thiz, jintArray indicesArray, jboolean testMode, jobject archiveExtractCallbackObject)
{
    TRACE1("InArchiveImpl::nativeExtract(). ThreadID=%lu",  (long unsigned int)PlatformGetCurrentThreadId());

    NativeMethodContext nativeMethodContext(env);

	TRY;

    JNIInstance jniInstance(&nativeMethodContext);

	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	if (archive == NULL)
	{
        TRACE("Archive==NULL. Do nothing...");
	    return;
	}

	CPPToJavaInStream * inStream = GetInStream(env, thiz);
	inStream->SetNativMethodContext(&nativeMethodContext);

	jint * indices = env->GetIntArrayElements(indicesArray, NULL);

	qsort(indices, env->GetArrayLength(indicesArray), 4, &CompareIndicies);

	CMyComPtr<IArchiveExtractCallback> archiveExtractCallback = new CPPToJavaArchiveExtractCallback(&nativeMethodContext, env, archiveExtractCallbackObject);

	TRACE1("Extracting %i items", (int)env->GetArrayLength(indicesArray))
	HRESULT result = 0;
	result = archive->Extract((UInt32*)indices, env->GetArrayLength(indicesArray), (Int32)testMode,
	        archiveExtractCallback);

	archiveExtractCallback.Release();

	env->ReleaseIntArrayElements(indicesArray, indices, JNI_ABORT);
    inStream->ClearNativeMethodContext();

	if (result)
	{
	    TRACE1("Extraction error. Result: 0x%08X", result);
	    nativeMethodContext.ThrowSevenZipException(result, "Error extracting %i element(s). Result: %X", env->GetArrayLength(indicesArray), result);
	}
	else
	{
	    TRACE("Extraction succeeded")
	}

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, ;);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfItems
 * Signature: ()I
 */
JBINDING_JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetNumberOfItems
(JNIEnv * env, jobject thiz)
{
    TRACE1("InArchiveImpl::nativeGetNumberOfItems(). ThreadID=%lu",  (long unsigned int)PlatformGetCurrentThreadId());

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	CPPToJavaInStream * p = GetInStream(env, thiz);

	CMyComPtr<CPPToJavaInStream> inStream(p);

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...");
        return 0;
    }

    UInt32 result;

	CHECK_HRESULT(nativeMethodContext, archive->GetNumberOfItems(&result), "Error getting number of items from archive");

	inStream->ClearNativeMethodContext();

	TRACE1("Returning: %u", result)

	return result;

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, 0);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeClose
 * Signature: ()V
 */
JBINDING_JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeClose
(JNIEnv * env, jobject thiz)
{
    TRACE1("InArchiveImpl::nativeClose(). ThreadID=%lu",  (long unsigned int)PlatformGetCurrentThreadId());

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
	CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

	inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...");
        return;
    }

    CHECK_HRESULT(nativeMethodContext, archive->Close(), "Error closing archive");

    archive->Release();
    inStream->Release();

    SetArchive(env, thiz, 0);

    TRACE("Archive closed")

    CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, ;);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfArchiveProperties
 * Signature: ()I
 */
JBINDING_JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetNumberOfArchiveProperties
(JNIEnv * env, jobject thiz)
{
    TRACE("InArchiveImpl::GetNumberOfArchiveProperties()");

    NativeMethodContext nativeMethodContext(env);
    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
	{
        TRACE("Archive==NULL. Do nothing...");
	    return 0;
	}

	UInt32 result;

	CHECK_HRESULT(nativeMethodContext, archive->GetNumberOfArchiveProperties(&result), "Error getting number of archive properties");

	inStream->ClearNativeMethodContext();

	return result;

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, 0);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetArchivePropertyInfo
 * Signature: (I)Lnet/sf/sevenzip/PropertyInfo;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetArchivePropertyInfo
(JNIEnv * env, jobject thiz, jint index)
{
    TRACE("InArchiveImpl::nativeGetArchivePropertyInfo()");

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...")
        return NULL;
    }

	VARTYPE type;
	CMyComBSTR name;
	PROPID propID;

	CHECK_HRESULT1(nativeMethodContext, archive->GetArchivePropertyInfo(index, &name, &propID, &type), "Error getting archive property info with index %i", index);

	jobject propertInfo = GetSimpleInstance(env, g_PropertyInfoClazz); //);

	jstring javaName;
	if (&name == NULL)
	{
		javaName = env->NewStringUTF("");
	}
	else
	{
		javaName = env->NewString((jchar *)(BSTR)name, name.Length());
	}
	jobject javaType = VarTypeToJavaType(&jniInstance, type);

	jobject propIDObject = env->CallStaticObjectMethod(g_PropIDClazz, g_PropID_getPropIDByIndex, propID);
	env->SetObjectField(propertInfo, g_PropertyInfo_propID, propIDObject);

	env->SetObjectField(propertInfo, g_PropertyInfo_name, javaName);
	env->SetObjectField(propertInfo, g_PropertyInfo_varType, javaType);

	inStream->ClearNativeMethodContext();

	return propertInfo;

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetArchiveProperty
 * Signature: (J)Lnet/sf/sevenzip/PropVariant;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetArchiveProperty
(JNIEnv * env, jobject thiz, jint propID)
{
    TRACE("InArchiveImpl::nativeGetArchiveProperty");

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...")
        return NULL;
    }

    NWindows::NCOM::CPropVariant PropVariant;

	CHECK_HRESULT1(nativeMethodContext, archive->GetArchiveProperty(propID, &PropVariant), "Error getting property mit Id: %lu", propID);

	inStream->ClearNativeMethodContext();

	return PropVariantToObject(&jniInstance, &PropVariant);

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetStringArchiveProperty
 * Signature: (I)Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetStringArchiveProperty
  (JNIEnv * env, jobject thiz, jint propID)
{
    TRACE("InArchiveImpl::nativeGetStringArchiveProperty");

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    NWindows::NCOM::CPropVariant PropVariant;

    CHECK_HRESULT1(nativeMethodContext, archive->GetArchiveProperty(propID, &PropVariant), "Error getting property mit Id: %lu", propID);

    inStream->ClearNativeMethodContext();

    return PropVariantToString(env, propID, PropVariant);

    CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfProperties
 * Signature: ()I
 */
JBINDING_JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetNumberOfProperties
(JNIEnv * env, jobject thiz)
{
    TRACE("InArchiveImpl::nativeGetNumberOfProperties");

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...");
        return 0;
    }

    UInt32 result;

	CHECK_HRESULT(nativeMethodContext, archive->GetNumberOfProperties(&result), "Error getting number of properties");

	inStream->ClearNativeMethodContext();

	return result;

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, 0);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetProperty
 * Signature: (IJ)Ljava/lang/Object;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetProperty
(JNIEnv * env, jobject thiz, jint index, jint propID)
{
    TRACE("InArchiveImpl::nativeGetProperty");

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    NWindows::NCOM::CPropVariant propVariant;

//    TRACE3("Index: %i, PropID: %i, archive: 0x%08X", index, propID, (unsigned int)(Object *)(CPPToJavaInStream *)(void*)(*(&archive)))
    CHECK_HRESULT2(nativeMethodContext, archive->GetProperty(index, propID, &propVariant), "Error getting property with propID=%lu for item %i", propID, index);

    inStream->ClearNativeMethodContext();

	return PropVariantToObject(&jniInstance, &propVariant);

	CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetStringProperty
 * Signature: (II)Ljava/lang/String;
 */
JBINDING_JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetStringProperty
    (JNIEnv * env, jobject thiz, jint index, jint propID)
{
    TRACE("InArchiveImpl::nativeGetStringProperty");

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    NWindows::NCOM::CPropVariant propVariant;

    CHECK_HRESULT2(nativeMethodContext, archive->GetProperty(index, propID, &propVariant), "Error getting property with propID=%lu for item %i", propID, index);

    inStream->ClearNativeMethodContext();

    return PropVariantToString(env, propID, propVariant);

    CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetPropertyInfo
 * Signature: (I)Lnet/sf/sevenzip/PropertyInfo;
 */
JBINDING_JNIEXPORT jobject JNICALL Java_net_sf_sevenzipjbinding_impl_InArchiveImpl_nativeGetPropertyInfo
(JNIEnv * env, jobject thiz, jint index)
{
    TRACE("InArchiveImpl::nativeGetPropertyInfo");

    NativeMethodContext nativeMethodContext(env);

    TRY;

    JNIInstance jniInstance(&nativeMethodContext);
    CMyComPtr<IInArchive> archive(GetArchive(env, thiz));
    CMyComPtr<CPPToJavaInStream> inStream(GetInStream(env, thiz));

    inStream->SetNativMethodContext(&nativeMethodContext);

    if (archive == NULL)
    {
        TRACE("Archive==NULL. Do nothing...");
        return NULL;
    }

    VARTYPE type;
	CMyComBSTR name;
	PROPID propID;

	CHECK_HRESULT1(nativeMethodContext, archive->GetPropertyInfo(index, &name, &propID, &type), "Error getting property info with index %i", index);

	jobject propertInfo = GetSimpleInstance(env, g_PropertyInfoClazz); //);

	jstring javaName;
	if (&name == NULL)
	{
		javaName = env->NewStringUTF("");
	}
	else
	{
		javaName = env->NewString((jchar *)(BSTR)name, name.Length());
	}
	jobject javaType = VarTypeToJavaType(&jniInstance, type);

	jobject propIDObject = env->CallStaticObjectMethod(g_PropIDClazz, g_PropID_getPropIDByIndex, propID);
	env->SetObjectField(propertInfo, g_PropertyInfo_propID, propIDObject);

	env->SetObjectField(propertInfo, g_PropertyInfo_name, javaName);
	env->SetObjectField(propertInfo, g_PropertyInfo_varType, javaType);

	inStream->ClearNativeMethodContext();

	return propertInfo;

    CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, NULL);
}
