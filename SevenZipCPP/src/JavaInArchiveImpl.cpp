#include "StdAfx.h"

#include "jnitools.h"

#include "SevenZipJBinding.h"

#include "Java/all.h"

static int initialized = 0;
static jfieldID g_ObjectAttributeFieldID;
static jclass g_PropertyInfoClazz;
static jfieldID g_PropertyInfo_name;
static jfieldID g_PropertyInfo_propID;
static jfieldID g_PropertyInfo_varType;

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

	g_PropertyInfo_propID = env->GetFieldID(g_PropertyInfoClazz, "propID", "I");
	FATALIF1(g_PropertyInfo_propID == NULL, "Can't find attribute 'propID' in the class %s", PROPERTYINFO_CLASS);

	g_PropertyInfo_varType = env->GetFieldID(g_PropertyInfoClazz, "varType",
			"Ljava/lang/Class;");
	FATALIF1(g_PropertyInfo_varType == NULL, "Can't find attribute 'varType' in the class %s", PROPERTYINFO_CLASS);

	initialized = 1;
}

static IInArchive * GetArchive(JNIEnv * env, jobject thiz)
{
	jint pointer;

	localinit(env, thiz);

	pointer = env->GetIntField(thiz, g_ObjectAttributeFieldID);

	return (IInArchive *)(void *)pointer;
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
	CMyComPtr<IInArchive> archive(
			GetArchive(env, thiz));

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

	env->SetObjectField(propertInfo, g_PropertyInfo_name, javaName);
	env->SetIntField(propertInfo, g_PropertyInfo_propID, propID);
	env->SetObjectField(propertInfo, g_PropertyInfo_varType, javaType);

	//	printf("%%%%%%%% Type: %i\n", t);
	//	printf("%%%%%%%% PropID: %i\n", id);
	//	printf("%%%%%%%% Name len: %i\n", name.Length());
	//	fflush(stdout);

	return propertInfo;
}

