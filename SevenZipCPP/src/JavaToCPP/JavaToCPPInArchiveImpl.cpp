#include "StdAfx.h"

#include "jnitools.h"

#include "SevenZipJBinding.h"

#include "Java/all.h"

#include "CPPToJava/CPPToJavaArchiveExtractCallback.h"

static int initialized = 0;
static jfieldID g_ObjectAttributeFieldID;
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

	g_ObjectAttributeFieldID = env->GetFieldID(clazz, 
	IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE, "I");
	FATALIF2(g_ObjectAttributeFieldID == NULL, "Field '%s' in the class '%s' was not found", IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE,
			GetJavaClassName(env, clazz, classname, sizeof(classname)));

	// Initialize PropVariant
	g_PropertyInfoClazz = env->FindClass(PROPERTYINFO_CLASS);
	FATALIF1(g_PropertyInfoClazz == NULL, "Can't find class '%s'", PROPERTYINFO_CLASS);
	g_PropertyInfoClazz = (jclass)env->NewGlobalRef(g_PropertyInfoClazz);

	g_PropertyInfo_name = env->GetFieldID(g_PropertyInfoClazz, "name",
			"Ljava/lang/String;");
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
			"getPropIDByIndex", "(I)L" PROPID_CLASS ";");
	FATALIF1(g_PropID_getPropIDByIndex == NULL, "Can't method 'getPropIDByIndex(int)' in class '%s'", PROPID_CLASS);

	initialized = 1;
}

static IInArchive * GetArchive(JNIEnv * env, jobject thiz)
{
	jint pointer;

	localinit(env, thiz);

	pointer = env->GetIntField(thiz, g_ObjectAttributeFieldID);

	return (IInArchive *)(void *)pointer;
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
JNIEXPORT void JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeExtract
(JNIEnv * env, jobject thiz, jintArray indicesArray, jboolean testMode, jobject archiveExtractCallbackObject)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	jint * indices = env->GetIntArrayElements(indicesArray, NULL);

	qsort(indices, env->GetArrayLength(indicesArray), 4, &CompareIndicies);
//		printf("Extracting indicies count: %i\n", env->GetArrayLength(indicesArray));
//		for (int i = 0; i < env->GetArrayLength(indicesArray); i++)
//		{
//			printf("Index: %i\n", ((UInt32*)indices)[i]);
//		}
//		fflush(stdout);
	//	UInt32 index = 4;
	//	int result = archive->Extract(&index, 1, (Int32)testMode, 
	int result = archive->Extract((UInt32*)indices, env->GetArrayLength(indicesArray), (Int32)testMode,
			new CPPToJavaArchiveExtractCallback(env, archiveExtractCallbackObject));
	env->ReleaseIntArrayElements(indicesArray, indices, JNI_ABORT);

	if (result)
	{
		ThrowSevenZipException(env, result, "Error extracting %i element(s). Result: %X", env->GetArrayLength(indicesArray), result);
	}
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfItems
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeGetNumberOfItems
(JNIEnv * env, jobject thiz)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	UInt32 result;

	CHECK_HRESULT(archive->GetNumberOfItems(&result), "Error getting number of items from archive");

	return result;
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeClose
(JNIEnv * env, jobject thiz)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	CHECK_HRESULT(archive->Close(), "Error closing archive");
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfArchiveProperties
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeGetNumberOfArchiveProperties
(JNIEnv * env, jobject thiz)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	UInt32 result;

	CHECK_HRESULT(archive->GetNumberOfArchiveProperties(&result), "Error getting number of archive properties");

	return result;
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetArchivePropertyInfo
 * Signature: (I)Lnet/sf/sevenzip/PropertyInfo;
 */
JNIEXPORT jobject JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeGetArchivePropertyInfo
(JNIEnv * env, jobject thiz, jint index)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	VARTYPE type;
	CMyComBSTR name;
	unsigned long propID;

	CHECK_HRESULT1(archive->GetArchivePropertyInfo(index, &name, &propID, &type), "Error getting archive property info with index %i", index);

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
	jobject javaType = VarTypeToJavaType(env, type);

	jobject propIDObject = env->CallStaticObjectMethod(g_PropIDClazz, g_PropID_getPropIDByIndex, propID);
	env->SetObjectField(propertInfo, g_PropertyInfo_propID, propIDObject);

	env->SetObjectField(propertInfo, g_PropertyInfo_name, javaName);
	env->SetObjectField(propertInfo, g_PropertyInfo_varType, javaType);

	return propertInfo;

}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetArchiveProperty
 * Signature: (J)Lnet/sf/sevenzip/PropVariant;
 */
JNIEXPORT jobject JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeGetArchiveProperty
(JNIEnv * env, jobject thiz, jint propID)
{
	CMyComPtr<IInArchive> archive(
			GetArchive(env, thiz));

	PROPVARIANT PropVariant;
	memset(&PropVariant, 0, sizeof(PropVariant));

	CHECK_HRESULT1(archive->GetArchiveProperty(propID, &PropVariant), "Error getting property mit Id: %lu", propID);

	printf("%%%%%%%% Type: %i\n", PropVariant.vt);
	fflush(stdout);

	return PropVariantToObject(env, &PropVariant);
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetNumberOfProperties
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeGetNumberOfProperties
(JNIEnv * env, jobject thiz)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	UInt32 result;

	CHECK_HRESULT(archive->GetNumberOfProperties(&result), "Error getting number of properties");

	return result;
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetProperty
 * Signature: (IJ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeGetProperty
(JNIEnv * env, jobject thiz, jint index, jint propID)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	PROPVARIANT PropVariant;
	memset(&PropVariant, 0, sizeof(PropVariant));

	CHECK_HRESULT2(archive->GetProperty(index, propID, &PropVariant), "Error getting property with propID=%lu for item %i", propID, index);

	return PropVariantToObject(env, &PropVariant);;
}

/*
 * Class:     net_sf_sevenzip_impl_InArchiveImpl
 * Method:    nativeGetPropertyInfo
 * Signature: (I)Lnet/sf/sevenzip/PropertyInfo;
 */
JNIEXPORT jobject JNICALL Java_net_sf_sevenzip_impl_InArchiveImpl_nativeGetPropertyInfo
(JNIEnv * env, jobject thiz, jint index)
{
	CMyComPtr<IInArchive> archive(GetArchive(env, thiz));

	VARTYPE type;
	CMyComBSTR name;
	unsigned long propID;

	CHECK_HRESULT1(archive->GetPropertyInfo(index, &name, &propID, &type), "Error getting property info with index %i", index);

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
	jobject javaType = VarTypeToJavaType(env, type);

	jobject propIDObject = env->CallStaticObjectMethod(g_PropIDClazz, g_PropID_getPropIDByIndex, propID);
	env->SetObjectField(propertInfo, g_PropertyInfo_propID, propIDObject);

	env->SetObjectField(propertInfo, g_PropertyInfo_name, javaName);
	env->SetObjectField(propertInfo, g_PropertyInfo_varType, javaType);

	return propertInfo;
}
