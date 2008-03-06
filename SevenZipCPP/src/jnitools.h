#ifndef __JNITOOLS_H__INCLUDED__


#define FATALIF(cond, fmt) { if (cond) fatal(fmt); }
#define FATALIF1(cond, fmt, p1) { if (cond) fatal(fmt, p1); }
#define FATALIF2(cond, fmt, p1, p2) { if (cond) fatal(fmt, p1, p2); }
#define FATALIF3(cond, fmt, p1, p2, p3) { if (cond) fatal(fmt, p1, p2, p3); }
#define FATALIF4(cond, fmt, p1, p2, p3, p4) { if (cond) fatal(fmt, p1, p2, p3, p4); }

#define SEVEN_ZIP_EXCEPTION "net/sf/sevenzip/SevenZipException"

/**
 * Fatal error
 */
void fatal(char * fmt, ...);

#define CHECK_HRESULT(call, msg)	CHECK_HRESULT5(call, msg, NULL, NULL, NULL, NULL, NULL)
#define CHECK_HRESULT1(call, msg, p1)	CHECK_HRESULT5(call, msg, p1, NULL, NULL, NULL, NULL)
#define CHECK_HRESULT2(call, msg, p1, p2)	CHECK_HRESULT5(call, msg, p1, p2, NULL, NULL, NULL)
#define CHECK_HRESULT3(call, msg, p1, p2, p3)	CHECK_HRESULT5(call, msg, p1, p2, p3, NULL, NULL)
#define CHECK_HRESULT4(call, msg, p1, p2, p3, p4)	CHECK_HRESULT5(call, msg, p1, p2, p3, p4, NULL)
#define CHECK_HRESULT5(call, msg, p1, p2, p3, p4, p5)						\
		{ 																	\
			HRESULT hr = call;												\
			if (hr)															\
			{																\
				ThrowSevenZipException(env,									\
					msg, hr, p1, p2, p3, p4, p5);							\
			}																\
		}


/**
 * Create instance of class 'clazz' using default constructor. 
 */
jobject GetSimpleInstance(JNIEnv * env, jclass clazz);

/**
 * Create instance of class 'classname' using default constructor. 
 */
jobject GetSimpleInstance(JNIEnv * env, char * classname);

/**
 * Put name of the java class 'clazz'into the buffer 'buffer'
 * Return: buffer
 */
char * GetJavaClassName(JNIEnv * env, jclass clazz, char * buffer, int size);

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, char * fmt, ...);

/**
 * Set integer attribute "attribute" of object "object" with value "value"
 */
void SetIntegerAttribute(JNIEnv * env, jobject object, char * attribute, int value);

/**
 * Convert PropVariant into java object: Integer, Double, String
 */
jobject PropVariantToObject(JNIEnv * env, PROPVARIANT * propVariant);

/**
 * Return Java-Class corresponding to the PropVariant Type 'vt'
 */
jclass VarTypeToJavaType(JNIEnv * env, VARTYPE vt);

#define __JNITOOLS_H__INCLUDED__
#endif // __JNITOOLS_H__INCLUDED__
