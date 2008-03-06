#include "StdAfx.h"
#include <windows.h>

#include <jni.h>
#include "jnitools.h"

static int initialized = 0;

static jclass g_IntegerClass;
static jmethodID g_IntegerValueOf;

static jclass g_LongClass;
static jmethodID g_LongValueOf;

static jclass g_DoubleClass;
static jmethodID g_DoubleValueOf;

static jclass g_BooleanClass;
static jmethodID g_BooleanValueOf;

static jclass g_StringClass;

static jclass g_DateClass;
static jmethodID g_DateConstructor;

static void localinit(JNIEnv * env)
{
	if (initialized)
	{
		return;
	}

	g_IntegerClass = env->FindClass("java/lang/Integer");
	FATALIF(g_IntegerClass == NULL, "Can't find Integer class");
	g_IntegerClass = (jclass)env->NewGlobalRef(g_IntegerClass);

	g_IntegerValueOf = env->GetStaticMethodID(g_IntegerClass, "valueOf",
			"(I)Ljava/lang/Integer;");
	FATALIF(g_IntegerValueOf == NULL, "Can't find Integer.valueOf() method");

	g_LongClass = env->FindClass("java/lang/Long");
	FATALIF(g_LongClass == NULL, "Can't find Long class");
	g_LongClass = (jclass)env->NewGlobalRef(g_LongClass);
	g_LongValueOf = env->GetStaticMethodID(g_LongClass, "valueOf",
			"(J)Ljava/lang/Long;");
	FATALIF(g_LongValueOf == NULL, "Can't find Long.valueOf() method");

	g_DoubleClass = env->FindClass("java/lang/Double");
	FATALIF(g_DoubleClass == NULL, "Can't find Double class");
	g_DoubleClass = (jclass)env->NewGlobalRef(g_DoubleClass);
	g_DoubleValueOf = env->GetStaticMethodID(g_DoubleClass, "valueOf",
			"(D)Ljava/lang/Double;");
	FATALIF(g_DoubleValueOf == NULL, "Can't find Double.valueOf() method");

	g_BooleanClass = env->FindClass("java/lang/Boolean");
	FATALIF(g_BooleanClass == NULL, "Can't find Boolean class");
	g_BooleanClass = (jclass)env->NewGlobalRef(g_BooleanClass);
	g_BooleanValueOf = env->GetStaticMethodID(g_BooleanClass, "valueOf",
			"(Z)Ljava/lang/Boolean;");
	FATALIF(g_BooleanValueOf == NULL, "Can't find Boolean.valueOf() method");

	g_StringClass = env->FindClass("java/lang/String");
	FATALIF(g_StringClass == NULL, "Can't find String class");
	g_StringClass = (jclass)env->NewGlobalRef(g_StringClass);

	g_DateClass = env->FindClass("java/util/Date");
	FATALIF(g_DateClass == NULL, "Can't find java.util.Date class");
	g_DateClass = (jclass)env->NewGlobalRef(g_DateClass);
	
	g_DateConstructor = env->GetMethodID(g_DateClass, "<init>", "(J)V");
	FATALIF(g_DateConstructor == NULL, "Can't find constructor java.util.Date(long)");

	initialized = 1;
}

/**
 * Fatal error
 */
void fatal(char * fmt, ...)
{
	va_list args;
	va_start(args, fmt);
	fputs("FATAL ERROR: ", stdout);
	vprintf(fmt, args);
	va_end(args);

	fputc('\n', stdout);
	fflush(stdout);

	exit(-1);
}

/**
 * Put name of the java class 'clazz'into the buffer 'buffer'
 * Return: buffer
 */
char * GetJavaClassName(JNIEnv * env, jclass clazz, char * buffer, int size)
{
	jclass reflectionClass = env->GetObjectClass(clazz);
	jmethodID id = env->GetMethodID(reflectionClass, "getName",
			"()Ljava/lang/String;");
	FATALIF(id == NULL, "Method Class.getName() can't be found");

	jstring string = (jstring)env->CallNonvirtualObjectMethod(clazz,
			reflectionClass, id);
	FATALIF(string == NULL, "CallNonvirtualObjectMethod() returns NULL");

	const char * cstr = env->GetStringUTFChars(string, NULL);
	strncpy(buffer, cstr, size);
	env->ReleaseStringUTFChars(string, cstr);

	return buffer;
}

/**
 * Create instance of class 'classname' using default constructor. 
 */
jobject GetSimpleInstance(JNIEnv * env, char * classname)
{
	jclass clazz = env->FindClass(classname);
	FATALIF1(clazz == NULL, "Class '%s' wasn't found", classname);

//	printf(">> Class: %X (%s)\n", clazz, classname);
//	fflush(stdout);
//
	return GetSimpleInstance(env, clazz);
}

/**
 * Create instance of class 'clazz' using default constructor. 
 */
jobject GetSimpleInstance(JNIEnv * env, jclass clazz)
{
	jmethodID defaultConstructor = env->GetMethodID(clazz, "<init>", "()V");

	char classname[256];
	FATALIF1(defaultConstructor == NULL, "Class '%s' has no default constructor",
			GetJavaClassName(env, clazz, classname, sizeof(classname)));

	return env->NewObject(clazz, defaultConstructor);
}

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, char * fmt, ...)
{
	jclass exceptionClass = env->FindClass(SEVEN_ZIP_EXCEPTION);
	FATALIF(exceptionClass == NULL, "SevenZipException class '" SEVEN_ZIP_EXCEPTION "' can't be found");

	char buffer[64 * 1024];
	va_list args;
	va_start(args, fmt);
	_vsnprintf(buffer, sizeof(buffer), fmt, args);
	va_end(args);

	buffer[sizeof(buffer) - 1] = '\0';

	env->ThrowNew(exceptionClass, buffer);
}

/**
 * Set integer attribute "attribute" of object "object" with value "value"
 */
void SetIntegerAttribute(JNIEnv * env, jobject object, char * attribute,
		int value)
{
	char classname[256];

	jclass clazz = env->GetObjectClass(object);
	FATALIF(clazz == NULL, "Can't get class from object");

	jfieldID fieldID = env->GetFieldID(clazz, attribute, "I");
	FATALIF2(fieldID == NULL, "Field '%s' in the class '%s' was not found", attribute,
			GetJavaClassName(env, clazz, classname, sizeof(classname)));

	env->SetIntField(object, fieldID, value);

}

static jobject BooleanToObject(JNIEnv * env, int value)
{
	jobject result = env->CallStaticObjectMethod(g_BooleanClass,
			g_BooleanValueOf, (jint)value);
	FATALIF1(result == NULL, "Error getting Boolean object for value %i", value);
	return result;
}

static jobject IntToObject(JNIEnv * env, int value)
{
	jobject result = env->CallStaticObjectMethod(g_IntegerClass,
			g_IntegerValueOf, (jint)value);
	FATALIF1(result == NULL, "Error getting Integer object for value %i", value);
	return result;
}

static jobject LongToObject(JNIEnv * env, LONGLONG value)
{
	jobject result = env->CallStaticObjectMethod(g_LongClass, g_LongValueOf,
			(jlong)value);
	FATALIF1(result == NULL, "Error getting Long object for value %li", value);
	return result;
}

static jobject DoubleToObject(JNIEnv * env, double value)
{
	jobject result = env->CallStaticObjectMethod(g_DoubleClass,
			g_DoubleValueOf, (jint)value);
	FATALIF1(result == NULL, "Error getting Double object for value %f", value);
	return result;
}

static jobject BSTRToObject(JNIEnv * env, BSTR value)
{
	CMyComBSTR str(value);
	return env->NewString((jchar *)(BSTR)str, str.Length());
}

static jobject FILETIMEToObject(JNIEnv * env, FILETIME filetime)
{
	LONGLONG time = (((LONGLONG)filetime.dwHighDateTime) << 32) | filetime.dwLowDateTime;
	LONGLONG javaTime = (time - (((LONGLONG)0x19db1de)<<32 | 0xd53e8000)) / 10000;
	
	jobject dateObject = env->NewObject(g_DateClass, g_DateConstructor, (jlong)javaTime);
	FATALIF(dateObject == NULL, "Error creating instance of java.util.Date using Date(long) constructor");
	return dateObject;
}

/**
 * Convert PropVariant into java object: Integer, Double, String
 */
jobject PropVariantToObject(JNIEnv * env, PROPVARIANT * propVariant)
{
	localinit(env);

	if (propVariant->vt & VT_VECTOR || propVariant->vt & VT_ARRAY
			|| propVariant->vt & VT_BYREF)
	{
		ThrowSevenZipException(
				env,
				"Vector, array or byref flags of PropVariant are not supported. VarType: %i",
				propVariant->vt);
		return NULL;
	}

	switch (propVariant->vt)
	{
	case VT_EMPTY:
	case VT_NULL:
		return NULL;
	case VT_I1:
		return IntToObject(env, propVariant->cVal);

	case VT_I2:
		return IntToObject(env, propVariant->iVal);

	case VT_I4:
		return IntToObject(env, propVariant->lVal);

	case VT_I8:
		return LongToObject(env, propVariant->hVal.QuadPart);

	case VT_UI1:
		return IntToObject(env, propVariant->bVal);

	case VT_UI2:
		return IntToObject(env, propVariant->uiVal);

	case VT_UI4:
		return IntToObject(env, propVariant->ulVal);

	case VT_UI8:
		return LongToObject(env, propVariant->uhVal.QuadPart);

	case VT_R4:
		return DoubleToObject(env, (double)propVariant->fltVal);

	case VT_R8:
		return DoubleToObject(env, propVariant->dblVal);

	case VT_BOOL:
		return BooleanToObject(env, propVariant->boolVal);

	case VT_BSTR:
		return BSTRToObject(env, propVariant->bstrVal);
		
	case VT_FILETIME:
		return FILETIMEToObject(env, propVariant->filetime);
		
	case VT_LPSTR:
	case VT_LPWSTR:


	case VT_CY:
	case VT_DATE:
	case VT_DISPATCH:
	case VT_ERROR:
	case VT_VARIANT:
	case VT_UNKNOWN:
	case VT_DECIMAL:
	case VT_VOID:
	case VT_HRESULT:
	case VT_PTR:
	case VT_SAFEARRAY:
	case VT_CARRAY:
	case VT_USERDEFINED:
	case VT_RECORD:
	case VT_INT_PTR:
	case VT_UINT_PTR:
	case VT_BLOB:
	case VT_STREAM:
	case VT_STORAGE:
	case VT_STREAMED_OBJECT:
	case VT_STORED_OBJECT:
	case VT_BLOB_OBJECT:
	case VT_INT:
	case VT_UINT:
	case VT_CF:
	case VT_CLSID:
	case VT_BSTR_BLOB:

		ThrowSevenZipException(env,
				"Unsupported PropVariant type. VarType: %i", propVariant->vt);

	};

	return NULL;
}

/**
 * Return Java-Class corresponding to the PropVariant Type 'vt'
 */
jclass VarTypeToJavaType(JNIEnv * env, VARTYPE vt)
{
	localinit(env);

	if (vt & VT_VECTOR || vt & VT_ARRAY || vt & VT_BYREF)
	{
		ThrowSevenZipException(
				env,
				"Vector, array or byref flags of PropVariant are not supported. VarType: %i",
				vt);
		return NULL;
	}

	switch (vt)
	{
	case VT_EMPTY:
	case VT_NULL:
		return NULL;
	case VT_I1:
	case VT_I2:
	case VT_I4:
	case VT_UI1:
	case VT_UI2:
	case VT_UI4:
		return g_IntegerClass;

	case VT_I8:
	case VT_UI8:
		return g_LongClass;

	case VT_R4:
	case VT_R8:
		return g_DoubleClass;

	case VT_BOOL:
		return g_BooleanClass;

	case VT_BSTR:
	case VT_LPSTR:
	case VT_LPWSTR:
		return g_StringClass;

	case VT_FILETIME:
		return g_DateClass;
		
	case VT_CY:
	case VT_DATE:
	case VT_DISPATCH:
	case VT_ERROR:
	case VT_VARIANT:
	case VT_UNKNOWN:
	case VT_DECIMAL:
	case VT_VOID:
	case VT_HRESULT:
	case VT_PTR:
	case VT_SAFEARRAY:
	case VT_CARRAY:
	case VT_USERDEFINED:
	case VT_RECORD:
	case VT_INT_PTR:
	case VT_UINT_PTR:
	case VT_BLOB:
	case VT_STREAM:
	case VT_STORAGE:
	case VT_STREAMED_OBJECT:
	case VT_STORED_OBJECT:
	case VT_BLOB_OBJECT:
	case VT_INT:
	case VT_UINT:
	case VT_CF:
	case VT_CLSID:
	case VT_BSTR_BLOB:

		ThrowSevenZipException(env,
				"Unsupported PropVariant type. VarType: %i", vt);

	};

	return NULL;
}

