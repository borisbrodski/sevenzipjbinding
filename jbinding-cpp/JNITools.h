#ifndef __JNITOOLS_H__INCLUDED__





#define CHECK_HRESULT(jniNativeCallContext, call, msg)	CHECK_HRESULT5(jniNativeCallContext, call, msg, NULL, NULL, NULL, NULL, NULL)
#define CHECK_HRESULT1(jniNativeCallContext, call, msg, p1)	CHECK_HRESULT5(jniNativeCallContext, call, msg, p1, NULL, NULL, NULL, NULL)
#define CHECK_HRESULT2(jniNativeCallContext, call, msg, p1, p2)	CHECK_HRESULT5(jniNativeCallContext, call, msg, p1, p2, NULL, NULL, NULL)
#define CHECK_HRESULT3(jniNativeCallContext, call, msg, p1, p2, p3)	CHECK_HRESULT5(jniNativeCallContext, call, msg, p1, p2, p3, NULL, NULL)
#define CHECK_HRESULT4(jniNativeCallContext, call, msg, p1, p2, p3, p4)	CHECK_HRESULT5(jniNativeCallContext, call, msg, p1, p2, p3, p4, NULL)
#define CHECK_HRESULT5(jniNativeCallContext, call, msg, p1, p2, p3, p4, p5)	\
		{ 																	\
			HRESULT hr = call;												\
			if (hr != S_OK)													\
			{																\
				(jniNativeCallContext).reportError(                         \
				        hr, msg, p1, p2, p3, p4, p5);					    \
			}																\
		}

#include "JavaStaticInfo.h"
#include "JBindingTools.h"

// TODO Check all methods here and remove not used

/**
 * Put name of the java class 'clazz'into the buffer 'buffer'
 * Return: buffer
 */
char * GetJavaClassName(JNIEnv * env, jclass clazz, char * buffer, size_t size);

/**
 * Set long attribute "attribute" of object "object" with value "value"
 */
void SetLongAttribute(JNIEnv * env, jobject object, const char * attribute, jlong value);

/**
 * Convert PropVariant into java object: Integer, Double, String
 */
jobject PropVariantToObject(JNIEnvInstance & jniEnvInstance, NWindows::NCOM::CPropVariant * propVariant);

/**
 * Convert PropVariant into java string
 */
jstring PropVariantToString(JNIEnv * env, PROPID propID, const PROPVARIANT &propVariant);

/**
 * Return Java-Class corresponding to the PropVariant Type 'vt'
 */
jclass VarTypeToJavaType(JNIEnvInstance & jniEnvInstance, VARTYPE vt);

void ObjectToPropVariant(JNIEnvInstance & jniEnvInstance, jobject object, PROPVARIANT * propVariant);


/**
 * Get java.lang.Boolean object from boolean value
 */
jobject BooleanToObject(JNIEnv * env, bool value);

/**
 * Get java.lang.Integer object from int value
 */
jobject IntToObject(JNIEnv * env, jint value);

/**
 * Get java.lang.Long object from long value
 */
jobject LongToObject(JNIEnv * env, LONGLONG value);

/**
 * Get java.lang.Double object from double value
 */
jobject DoubleToObject(JNIEnv * env, double value);

/**
 * Get java.lang.String object from BSTR string
 */
jobject BSTRToObject(JNIEnv * env, BSTR value);

/**
 * Get java.util.Date object from date in FILETIME format
 */
jobject FILETIMEToObject(JNIEnv * env, FILETIME filetime);

/**
 * Fill FILETIME from java.util.Date object
 */
bool ObjectToFILETIME(JNIEnvInstance & jniEnvInstance, jobject obj, FILETIME & filetime);

#define __JNITOOLS_H__INCLUDED__
#endif // __JNITOOLS_H__INCLUDED__
