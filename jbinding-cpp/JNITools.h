#ifndef __JNITOOLS_H__INCLUDED__

#define JAVA_MAKE_SIGNATURE_TYPE(classname) "L" classname ";"

#define JAVA_OBJECT "java/lang/Object"
#define JAVA_OBJECT_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_OBJECT)

#define JAVA_BYTE "java/lang/Byte"
#define JAVA_BYTE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_BYTE)

#define JAVA_CHARACTER "java/lang/Character"
#define JAVA_CHARACTER_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_CHARACTER)

#define JAVA_SHORT "java/lang/Short"
#define JAVA_SHORT_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_SHORT)

#define JAVA_NUMBER "java/lang/Number"
#define JAVA_NUMBER_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_NUMBER)

#define JAVA_INTEGER "java/lang/Integer"
#define JAVA_INTEGER_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_INTEGER)

#define JAVA_LONG "java/lang/Long"
#define JAVA_LONG_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_LONG)

#define JAVA_FLOAT "java/lang/Float"
#define JAVA_FLOAT_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_FLOAT)

#define JAVA_DOUBLE "java/lang/Double"
#define JAVA_DOUBLE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_DOUBLE)

#define JAVA_BOOLEAN "java/lang/Boolean"
#define JAVA_BOOLEAN_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_BOOLEAN)

#define JAVA_STRING "java/lang/String"
#define JAVA_STRING_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_STRING)

#define JAVA_THROWABLE "java/lang/Throwable"
#define JAVA_THROWABLE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_THROWABLE)

#define JAVA_DATE "java/util/Date"
#define JAVA_DATE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_DATE)


#define JBINDING_JNIEXPORT extern "C" JNIEXPORT

#define FATAL fatal
#define FATALIF(cond, fmt) { if (cond) fatal(fmt); }
#define FATALIF1(cond, fmt, p1) { if (cond) fatal(fmt, p1); }
#define FATALIF2(cond, fmt, p1, p2) { if (cond) fatal(fmt, p1, p2); }
#define FATALIF3(cond, fmt, p1, p2, p3) { if (cond) fatal(fmt, p1, p2, p3); }
#define FATALIF4(cond, fmt, p1, p2, p3, p4) { if (cond) fatal(fmt, p1, p2, p3, p4); }

#define CHECK_HRESULT(nativeMethodContext, call, msg)	CHECK_HRESULT5(nativeMethodContext, call, msg, NULL, NULL, NULL, NULL, NULL)
#define CHECK_HRESULT1(nativeMethodContext, call, msg, p1)	CHECK_HRESULT5(nativeMethodContext, call, msg, p1, NULL, NULL, NULL, NULL)
#define CHECK_HRESULT2(nativeMethodContext, call, msg, p1, p2)	CHECK_HRESULT5(nativeMethodContext, call, msg, p1, p2, NULL, NULL, NULL)
#define CHECK_HRESULT3(nativeMethodContext, call, msg, p1, p2, p3)	CHECK_HRESULT5(nativeMethodContext, call, msg, p1, p2, p3, NULL, NULL)
#define CHECK_HRESULT4(nativeMethodContext, call, msg, p1, p2, p3, p4)	CHECK_HRESULT5(nativeMethodContext, call, msg, p1, p2, p3, p4, NULL)
#define CHECK_HRESULT5(nativeMethodContext, call, msg, p1, p2, p3, p4, p5)	\
		{ 																	\
			HRESULT hr = call;												\
			if (hr != S_OK)													\
			{																\
				(nativeMethodContext).ThrowSevenZipException(               \
				        hr, msg, p1, p2, p3, p4, p5);					    \
			}																\
		}

class JNIInstance;

// TODO Check all methods here and remove not used

/**
 * Create instance of class 'classname' using default constructor.
 */
jobject GetSimpleInstance(JNIEnv * env, const char * classname);

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
jobject PropVariantToObject(JNIInstance * jniInstance, NWindows::NCOM::CPropVariant * propVariant);

/**
 * Convert PropVariant into java string
 */
jstring PropVariantToString(JNIEnv * env, PROPID propID, const PROPVARIANT &propVariant);

/**
 * Return Java-Class corresponding to the PropVariant Type 'vt'
 */
jclass VarTypeToJavaType(JNIInstance * jniInstance, VARTYPE vt);

void ObjectToPropVariant(JNIInstance * jniInstance, jobject object, PROPVARIANT * propVariant);


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

#define __JNITOOLS_H__INCLUDED__
#endif // __JNITOOLS_H__INCLUDED__
