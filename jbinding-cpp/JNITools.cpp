#include "SevenZipJBinding.h"
#include "JNITools.h"
#include "UnicodeHelper.h"

// TODO Refactor to use java static info

// TODO Synchronize access to this variable or call the init method from the global initialization routing (better)
static bool initialized = 0;

//static jclass g_NumberClass;

static jclass g_IntegerClass;
static jmethodID g_IntegerValueOf;
static jmethodID g_IntegerIntValue;

static jclass g_LongClass;
static jmethodID g_LongValueOf;
static jmethodID g_LongLongValue;

static jclass g_DoubleClass;
static jmethodID g_DoubleValueOf;

static jclass g_BooleanClass;
static jmethodID g_BooleanValueOf;
static jmethodID g_BooleanBooleanValue;

static jclass g_StringClass;

static jclass g_DateClass;
static jmethodID g_DateConstructor;

static void localinit(JNIEnv * env) {
    if (initialized) {
        return;
    }

    //	g_NumberClass = env->FindClass(JAVA_NUMBER);
    //	#ifdef __ANDROID_API__
    //	if (g_NumberClass == nullptr) {
    //	g_NumberClass = findClass(env, JAVA_NUMBER);
    //	}
    //	#endif
    //	FATALIF(g_NumberClass == NULL, "Can't find Number class");
    //	g_NumberClass = (jclass) env->NewGlobalRef(g_NumberClass);

    // class: Integer
    g_IntegerClass = env->FindClass(JAVA_INTEGER);
#ifdef __ANDROID_API__
    if (g_IntegerClass == nullptr) {
        g_IntegerClass = findClass(env, JAVA_INTEGER);
    }
#endif
    FATALIF(g_IntegerClass == NULL, "Can't find Integer class");
    g_IntegerClass = (jclass) env->NewGlobalRef(g_IntegerClass);

    g_IntegerValueOf = env->GetStaticMethodID(g_IntegerClass, "valueOf", "(I)Ljava/lang/Integer;");
    FATALIF(g_IntegerValueOf == NULL, "Can't find Integer.valueOf() method");

    g_IntegerIntValue = env->GetMethodID(g_IntegerClass, "intValue", "()I");
    FATALIF(g_IntegerIntValue == NULL, "Can't find Integer.intValue() method");

    // class: Long
    g_LongClass = env->FindClass(JAVA_LONG);
#ifdef __ANDROID_API__
    if (g_LongClass == nullptr) {
        g_LongClass = findClass(env, JAVA_LONG);
    }
#endif
    FATALIF(g_LongClass == NULL, "Can't find Long class");
    g_LongClass = (jclass) env->NewGlobalRef(g_LongClass);
    g_LongValueOf = env->GetStaticMethodID(g_LongClass, "valueOf", "(J)L" JAVA_LONG ";");
    FATALIF(g_LongValueOf == NULL, "Can't find Long.valueOf() method");

    g_LongLongValue = env->GetMethodID(g_IntegerClass, "longValue", "()J");
    FATALIF(g_LongLongValue == NULL, "Can't find Long.longValue() method");

    // class: Double
    g_DoubleClass = env->FindClass(JAVA_DOUBLE);
#ifdef __ANDROID_API__
    if (g_DoubleClass == nullptr) {
        g_DoubleClass = findClass(env, JAVA_DOUBLE);
    }
#endif
    FATALIF(g_DoubleClass == NULL, "Can't find Double class");
    g_DoubleClass = (jclass) env->NewGlobalRef(g_DoubleClass);
    g_DoubleValueOf = env->GetStaticMethodID(g_DoubleClass, "valueOf", "(D)Ljava/lang/Double;");
    FATALIF(g_DoubleValueOf == NULL, "Can't find Double.valueOf() method");

    g_BooleanClass = env->FindClass(JAVA_BOOLEAN);
#ifdef __ANDROID_API__
    if (g_BooleanClass == nullptr) {
        g_BooleanClass = findClass(env, JAVA_BOOLEAN);
    }
#endif
    FATALIF(g_BooleanClass == NULL, "Can't find Boolean class");

    g_BooleanClass = (jclass) env->NewGlobalRef(g_BooleanClass);
    g_BooleanValueOf = env->GetStaticMethodID(g_BooleanClass, "valueOf", "(Z)Ljava/lang/Boolean;");
    FATALIF(g_BooleanValueOf == NULL, "Can't find Boolean.valueOf() method");

    g_BooleanBooleanValue = env->GetMethodID(g_BooleanClass, "booleanValue", "()Z");
    FATALIF(g_BooleanBooleanValue == NULL, "Can't find Boolean.booleanValue() method");

    // class: String
    g_StringClass = env->FindClass(JAVA_STRING);
#ifdef __ANDROID_API__
    if (g_StringClass == nullptr) {
        g_StringClass = findClass(env, JAVA_STRING);
    }
#endif
    FATALIF(g_StringClass == NULL, "Can't find String class");
    g_StringClass = (jclass) env->NewGlobalRef(g_StringClass);

    g_DateClass = env->FindClass("java/util/Date");
#ifdef __ANDROID_API__
    if (g_DateClass == nullptr) {
        g_DateClass = findClass(env, "java/util/Date");
    }
#endif
    FATALIF(g_DateClass == NULL, "Can't find java.util.Date class");
    g_DateClass = (jclass) env->NewGlobalRef(g_DateClass);

    g_DateConstructor = env->GetMethodID(g_DateClass, "<init>", "(J)V");
    FATALIF(g_DateConstructor == NULL, "Can't find constructor java.util.Date(long)");

    initialized = 1;
}

/**
 * Put name of the java class 'clazz'into the buffer 'buffer'
 * Return: buffer
 */
char * GetJavaClassName(JNIEnv * env, jclass clazz, char * buffer, size_t size) {
    jclass reflectionClass = env->GetObjectClass(clazz);
    jmethodID id = env->GetMethodID(reflectionClass, "getName", "()Ljava/lang/String;");
    FATALIF(id == NULL, "Method Class.getName() can't be found");

    jobject string = env->CallNonvirtualObjectMethod(clazz, reflectionClass, id);
    FATALIF(string == NULL, "CallNonvirtualObjectMethod() returns NULL");
#ifdef __ANDROID_API__
    env->DeleteLocalRef(reflectionClass);
#endif

    const char * cstr = env->GetStringUTFChars((jstring) string, NULL);
    strncpy(buffer, cstr, size);
    env->ReleaseStringUTFChars((jstring) string, cstr);
#ifdef __ANDROID_API__
    env->DeleteLocalRef(string);
#endif

    return buffer;
}

/**
 * Create instance of class 'clazz' using default constructor.
 */
jobject GetSimpleInstance(JNIEnv * env, jclass clazz) {
    jmethodID defaultConstructor = env->GetMethodID(clazz, "<init>", "()V");

    char classname[256];
    FATALIF1(defaultConstructor == NULL, "Class '%s' has no default constructor",
            GetJavaClassName(env, clazz, classname, sizeof(classname)));

    return env->NewObject(clazz, defaultConstructor);
}

/**
 * Set long attribute "attribute" of object "object" with value "value"
 */
void SetLongAttribute(JNIEnv * env, jobject object, const char * attribute, jlong value) {
    char classname[256];

    jclass clazz = env->GetObjectClass(object);
    FATALIF(clazz == NULL, "Can't get class from object");

    jfieldID fieldID = env->GetFieldID(clazz, attribute, "J");
    FATALIF2(fieldID == NULL, "Field '%s' in the class '%s' was not found", attribute,
            GetJavaClassName(env, clazz, classname, sizeof(classname)));
#ifdef __ANDROID_API__
    env->DeleteLocalRef(clazz);
#endif

    env->SetLongField(object, fieldID, value);
}

/**
 * Get java.lang.Boolean object from boolean value
 */
jobject BooleanToObject(JNIEnv * env, bool value) {
    localinit(env);

    jobject result = env->CallStaticObjectMethod(g_BooleanClass, g_BooleanValueOf,
            (jboolean) (value != VARIANT_FALSE));
    FATALIF1(result == NULL, "Error getting Boolean object for value %i", value);
    return result;
}

/**
 * Get java.lang.Integer object from int value
 */
jobject IntToObject(JNIEnv * env, jint value) {
    localinit(env);

    jobject result = env->CallStaticObjectMethod(g_IntegerClass, g_IntegerValueOf, value);
    FATALIF1(result == NULL, "Error getting Integer object for value %i", value);
    return result;
}

/**
 * Get java.lang.Long object from long value
 */
jobject LongToObject(JNIEnv * env, LONGLONG value) {
    localinit(env);

    jobject result = env->CallStaticObjectMethod(g_LongClass, g_LongValueOf, (jlong) value);
    FATALIF1(result == NULL, "Error getting Long object for value %li", value);
    return result;
}

/**
 * Get java.lang.Double object from double value
 */
jobject DoubleToObject(JNIEnv * env, double value) {
    localinit(env);

    jobject result = env->CallStaticObjectMethod(g_DoubleClass, g_DoubleValueOf, (jint) value);
    FATALIF1(result == NULL, "Error getting Double object for value %f", value);
    return result;
}

/**
 * Get java.lang.String object from BSTR string
 */
jobject BSTRToObject(JNIEnv * env, BSTR value) {
    localinit(env);

    return ToJChar(value).toNewString(env);
}

/**
 * Get java.util.Date object from date in FILETIME format
 */
jobject FILETIMEToObject(JNIEnv * env, FILETIME filetime) {
    localinit(env);


    LONGLONG time = (((LONGLONG) filetime.dwHighDateTime) << 32) | filetime.dwLowDateTime;
    LONGLONG javaTime = (time - FILETIME_TO_JAVATIME_SHIFT) / FILETIME_TO_JAVATIME_FACTOR;

    jobject dateObject = env->NewObject(g_DateClass, g_DateConstructor, (jlong) javaTime);
    FATALIF(dateObject == NULL, "Error creating instance of java.util.Date using Date(long) constructor");
    return dateObject;
}

bool ObjectToFILETIME(JNIEnvInstance & jniEnvInstance, jobject obj, FILETIME & filetime) {
    LONGLONG javaTime = (LONGLONG)(jni::Date::getTime(jniEnvInstance, obj));
    if (jniEnvInstance.exceptionCheck()) {
        return false;
    }
    LONGLONG time = javaTime * FILETIME_TO_JAVATIME_FACTOR + FILETIME_TO_JAVATIME_SHIFT;

    filetime.dwHighDateTime = (DWORD)(time >> 32);
    filetime.dwLowDateTime = (DWORD)(time);

    return true;
}

/**
 * Convert PropVariant into java string
 */
jstring PropVariantToString(JNIEnv * env, PROPID propID, const PROPVARIANT &propVariant) {

    UString string;
    ConvertPropertyToString(string, propVariant, propID, true);
    return ToJChar(string).toNewString(env);
}

void ObjectToPropVariant(JNIEnvInstance & jniEnvInstance, jobject object, PROPVARIANT * propVariant) {

    localinit(jniEnvInstance);
    NWindows::NCOM::CPropVariant cPropVariant;
    if (object) {
        if (jniEnvInstance->IsInstanceOf(object, g_IntegerClass)) {
            jint value = jniEnvInstance->CallIntMethod(object, g_IntegerIntValue);
            cPropVariant = (Int32) value;
        } else if (jniEnvInstance->IsInstanceOf(object, g_StringClass)) {
            cPropVariant = UString(FromJChar(jniEnvInstance, (jstring)object));
        } else if (jniEnvInstance->IsInstanceOf(object, g_BooleanClass)) {
            jboolean value = jniEnvInstance->CallBooleanMethod(object, g_BooleanBooleanValue);
            cPropVariant = (bool) value;
        } else if (jniEnvInstance->IsInstanceOf(object, g_LongClass)) {
            jlong value = jniEnvInstance->CallLongMethod(object, g_LongLongValue);
            cPropVariant = (UInt64) value;
        } else if (jniEnvInstance->IsInstanceOf(object, g_DateClass)) {
            FILETIME filetime;
            ObjectToFILETIME(jniEnvInstance, object, filetime);
            cPropVariant = filetime;
        } else {
            jniEnvInstance.reportError("Can't convert object to PropVariant"); // TODO Improve error message by giving name of the class
        }

    }

    cPropVariant.Detach(propVariant);
}

/**
 * Convert PropVariant into java object: Integer, Double, String and Date
 */
jobject PropVariantToObject(JNIEnvInstance & jniEnvInstance, NWindows::NCOM::CPropVariant * propVariant) {
    localinit(jniEnvInstance);

    switch (propVariant->vt) {
    case VT_EMPTY:
    case VT_NULL:
    case VT_VOID:
        return NULL;

    case VT_I1:
        return IntToObject(jniEnvInstance, propVariant->cVal);

    case VT_I2:
        return IntToObject(jniEnvInstance, propVariant->iVal);

    case VT_INT: // TODO Check this: Variant 'VT_INT'
    case VT_I4:
        return IntToObject(jniEnvInstance, propVariant->lVal);

    case VT_I8:
        return LongToObject(jniEnvInstance, propVariant->hVal.QuadPart);

    case VT_UI8:
        return LongToObject(jniEnvInstance, propVariant->uhVal.QuadPart);

    case VT_UI1:
        return IntToObject(jniEnvInstance, propVariant->bVal);

    case VT_UI2:
        return IntToObject(jniEnvInstance, propVariant->uiVal);

    case VT_UINT: // TODO Check this: Variant 'VT_UINT'
    case VT_UI4:
        return IntToObject(jniEnvInstance, propVariant->ulVal);

    case VT_BOOL:
        return BooleanToObject(jniEnvInstance, propVariant->boolVal);

    case VT_BSTR:
        return BSTRToObject(jniEnvInstance, propVariant->bstrVal);

    case VT_DATE:
    case VT_FILETIME:
        return FILETIMEToObject(jniEnvInstance, propVariant->filetime);

    case VT_R4:
        // Not supported by MyWindows.cpp yet
        //		 return DoubleToObject(env, (double) propVariant->fltVal);
    case VT_R8:
        // Not supported by MyWindows.cpp yet
        //		 return DoubleToObject(env, propVariant->dblVal);
    case VT_CY:
    case VT_DISPATCH:
    case VT_DECIMAL:
    case VT_HRESULT:
    case VT_ERROR:
    case VT_VARIANT:
    case VT_UNKNOWN:

    default:
        jniEnvInstance.reportError("Unsupported PropVariant type. VarType: %i",
                propVariant->vt);

    };

    return NULL;
}

/**
 * Return Java-Class corresponding to the PropVariant Type 'vt'
 */
jclass VarTypeToJavaType(JNIEnvInstance & jniEnvInstance, VARTYPE vt) {
    localinit(jniEnvInstance);

    switch (vt) {

    case VT_EMPTY:
    case VT_NULL:
    case VT_VOID:
        return NULL;

    case VT_I2:
    case VT_I4:
    case VT_I1:
    case VT_UI1:
    case VT_UI2:
    case VT_UI4:
    case VT_INT:
    case VT_UINT:
        return g_IntegerClass;

    case VT_I8:
    case VT_UI8:
        return g_LongClass;

    case VT_BOOL:
        return g_BooleanClass;

    case VT_BSTR:
        return g_StringClass;

    case VT_DATE:
    case VT_FILETIME:
        return g_DateClass;

    case VT_R4:
    case VT_R8:
        // Not supported by MyWindows.cpp yet
        //		return g_DoubleClass;
    case VT_CY:
    case VT_DISPATCH:
    case VT_DECIMAL:
    case VT_HRESULT:
    case VT_ERROR:
    case VT_VARIANT:
    case VT_UNKNOWN:

    default:
        jniEnvInstance.reportError("Unsupported PropVariant type. VarType: %i", vt);

    };

    return NULL;
}

