#include <iostream>
#include <string>
#include <sstream>

#include "SevenZipJBinding.h"
#include "JavaStaticInfo.h"

#ifdef NATIVE_JUNIT_TEST_SUPPORT

using namespace std;
using namespace jni;

BEGIN_JCLASS("net/sf/sevenzipjbinding/junit/jnitools", JTestAbstractClass)
/*    */JCLASS_FINAL_METHOD(Long, privateLongMethod, "(I)")
/*    */JCLASS_FINAL_METHOD(String, privateStringMethod, "(I)")
/*    */JCLASS_FINAL_METHOD(Void, privateVoidMethod, "(I)")

/*    */JCLASS_FINAL_METHOD(Long, privateFinalLongMethod, "(I)")
/*    */JCLASS_FINAL_METHOD(String, privateFinalStringMethod, "(I)")
/*    */JCLASS_FINAL_METHOD(Void, privateFinalVoidMethod, "(I)")

/*    */JCLASS_STATIC_METHOD(Long, privateStaticLongMethod, "(I)")
/*    */JCLASS_STATIC_METHOD(String, privateStaticStringMethod, "(I)")
/*    */JCLASS_STATIC_METHOD(Void, privateStaticVoidMethod, "(I)")

/*    */JCLASS_VIRTUAL_METHOD(Long, protectedVirtualLongMethod, "(I)")
/*    */JCLASS_VIRTUAL_METHOD(String, protectedVirtualStringMethod, "(I)")
/*    */JCLASS_VIRTUAL_METHOD(Void, protectedVirtualVoidMethod, "(I)")

/*    */JCLASS_FIELD(Long, privateLongField)
/*    */JCLASS_FIELD(String, privateStringField)

/*    */JCLASS_STATIC_FIELD(Long, privateStaticLongField)
/*    */JCLASS_STATIC_FIELD(String, privateStaticStringField)
END_JCLASS
BEGIN_JCLASS("net/sf/sevenzipjbinding/junit/jnitools", JTestFinalClass)
/*    */JCLASS_VIRTUAL_METHOD(Long, protectedVirtualLongMethod, "(I)")
/*    */JCLASS_VIRTUAL_METHOD(String, protectedVirtualStringMethod, "(I)")
/*    */JCLASS_VIRTUAL_METHOD(Void, protectedVirtualVoidMethod, "(I)")

/*    */JCLASS_FIELD(Class, privateClassField)
/*    */JCLASS_FIELD(Long, id)

/*    */JCLASS_FIELD_OBJECT(privateJTestFinalClassField, "Lnet/sf/sevenzipjbinding/junit/jnitools/JTestFinalClass;")
/*    */JCLASS_FIELD_OBJECT(privateJTestAbstractClassField, "Lnet/sf/sevenzipjbinding/junit/jnitools/JTestAbstractClass;")
END_JCLASS

BEGIN_JINTERFACE(Interface1)
/*    */JINTERFACE_METHOD(Long, longMethod, "(I)")
/*    */JINTERFACE_METHOD(String, stringMethod, "(I)")
/*    */JINTERFACE_METHOD(Void, voidMethod, "(I)")
END_JINTERFACE

void checkString(std::stringstream & errmsg, JNIEnv * env, char const * expectedValue,
                 jstring actualValue) {
    std::stringstream jstringstream;

    jstringstream << env << actualValue;
    string expectedString = string("\"") + expectedValue + '"';
    if (jstringstream.str() != expectedString) {
        errmsg << "ERROR: Expected '" << expectedString << "' get '" << jstringstream.str() << "'"
                << std::endl;
    }
}

void checkLong(std::stringstream & errmsg, jlong expectedValue, jlong actualValue) {
    if (actualValue != expectedValue) {
        errmsg << "ERROR: Expected '" << expectedValue << "' get '" << actualValue << "'"
                << std::endl;
    }
}

void checkException(std::stringstream & errmsg, JNIEnv * env) {
    jni::prepareExceptionCheck(env);
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        errmsg << "Unexpected exception" << std::endl;
    }
}

void checkNull(std::stringstream & errmsg, bool expectedNull, jobject actualValue) {
    if (expectedNull ^ !actualValue) {
        errmsg << "ERROR: Expected " << (expectedNull ? "null" : "not null") << std::endl;
    }
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_nativeInterface1(JNIEnv * env,
                                                                          jobject thiz,
                                                                          jobject interface1Impl,
                                                                          jint offset,
                                                                          jboolean fromClass) {
    std::stringstream errmsg;

    Interface1 * interface1;

    if (fromClass) {
        jclass interface1ImplClass = env->GetObjectClass(interface1Impl);
        MY_ASSERT(interface1ImplClass)

        interface1 = &Interface1::getInstance(env, interface1ImplClass);

    } else {
        interface1 = &Interface1::getInstanceFromObject(env, interface1Impl);
    }

    jlong longResult = interface1->longMethod(env, interface1Impl, jint(17 + offset));
    checkException(errmsg, env);
    checkLong(errmsg, jlong(12017), longResult);

    jstring stringResult = interface1->stringMethod(env, interface1Impl, jint(18 + offset));
    checkException(errmsg, env);
    checkString(errmsg, env, "Interface.I = 18", stringResult);
    env->DeleteLocalRef(stringResult);

    interface1->voidMethod(env, interface1Impl, jint(19 + offset));
    checkException(errmsg, env);

    char const * errmsgstring = errmsg.str().c_str();
    if (*errmsgstring) {
        return env->NewStringUTF(errmsgstring);
    }
    return NULL;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_nativeAbstractClassStatic(
                                                                                   JNIEnv * env,
                                                                                   jobject thiz,
                                                                                   jobject jTestAbstractClass) {
    std::stringstream errmsg;

    // --- Static methods ---

    jlong longResult = JTestAbstractClass::privateStaticLongMethod(env, jlong(7));
    checkException(errmsg, env);
    checkLong(errmsg, jlong(2007), longResult);

    jstring stringResult = JTestAbstractClass::privateStaticStringMethod(env, jint(8));
    checkException(errmsg, env);
    checkString(errmsg, env, "I3 = 8", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateStaticVoidMethod(env, jint(9));
    checkException(errmsg, env);

    // --- Private static fields ---
    longResult = JTestAbstractClass::privateStaticLongField_Get(env);
    checkLong(errmsg, jlong(-1), longResult);

    JTestAbstractClass::privateStaticLongField_Set(env, jlong(15));

    stringResult = JTestAbstractClass::privateStaticStringField_Get(env);
    checkString(errmsg, env, "-1", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateStaticStringField_Set(env, env->NewStringUTF("16"));

    char const * errmsgstring = errmsg.str().c_str();
    if (*errmsgstring) {
        return env->NewStringUTF(errmsgstring);
    }
    return NULL;
}
JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_nativeAbstractClass(
                                                                             JNIEnv * env,
                                                                             jobject thiz,
                                                                             jobject jTestAbstractClass) {

    std::stringstream errmsg;

    // --- Private methods ---

    jlong longResult = JTestAbstractClass::privateLongMethod(env, jTestAbstractClass, jlong(1));
    checkException(errmsg, env);
    checkLong(errmsg, jlong(1001), longResult);

    jstring stringResult =
            JTestAbstractClass::privateStringMethod(env, jTestAbstractClass, jint(2));
    checkException(errmsg, env);
    checkString(errmsg, env, "I1 = 2", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateVoidMethod(env, jTestAbstractClass, jint(3));
    checkException(errmsg, env);

    // --- Final methods ---

    longResult = JTestAbstractClass::privateFinalLongMethod(env, jTestAbstractClass, jlong(4));
    checkException(errmsg, env);
    checkLong(errmsg, jlong(1004), longResult);

    stringResult = JTestAbstractClass::privateFinalStringMethod(env, jTestAbstractClass, jint(5));
    checkException(errmsg, env);
    checkString(errmsg, env, "I2 = 5", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateFinalVoidMethod(env, jTestAbstractClass, jint(6));
    checkException(errmsg, env);

    // --- Virtual methods ---

    longResult = JTestAbstractClass::protectedVirtualLongMethod(env, jTestAbstractClass, jlong(10));
    checkException(errmsg, env);
    checkLong(errmsg, jlong(3010), longResult);

    stringResult = JTestAbstractClass::protectedVirtualStringMethod(env, jTestAbstractClass, jint(
            11));
    checkException(errmsg, env);
    checkString(errmsg, env, "I4 = 11", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::protectedVirtualVoidMethod(env, jTestAbstractClass, jint(12));
    checkException(errmsg, env);

    // --- Private fields ---
    longResult = JTestAbstractClass::privateLongField_Get(env, jTestAbstractClass);
    checkLong(errmsg, jlong(-1), longResult);

    JTestAbstractClass::privateLongField_Set(env, jTestAbstractClass, jlong(13));

    stringResult = JTestAbstractClass::privateStringField_Get(env, jTestAbstractClass);
    checkString(errmsg, env, "-1", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateStringField_Set(env, jTestAbstractClass, env->NewStringUTF("14"));

    char const * errmsgstring = errmsg.str().c_str();
    if (*errmsgstring) {
        return env->NewStringUTF(errmsgstring);
    }
    return NULL;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_nativeJTestFinalClassNewInstance(
                                                                                          JNIEnv * env,
                                                                                          jobject thiz) {
    return JTestFinalClass::newInstance(env);
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_nativeFinalClass(JNIEnv * env,
                                                                          jobject thiz,
                                                                          jobject jTestFinalClass) {
    std::stringstream errmsg;

    jclass clazz = JTestFinalClass::privateClassField_Get(env, jTestFinalClass);
    checkNull(errmsg, false, clazz);

    JTestFinalClass::privateClassField_Set(env, jTestFinalClass, NULL);

    // --- Test Field: privateJTestFinalClassField ---
    jobject object = JTestFinalClass::privateJTestFinalClassField_Get(env, jTestFinalClass);
    checkNull(errmsg, true, object);

    jobject jTestFinalClass2 = JTestFinalClass::newInstance(env);
    JTestFinalClass::id_Set(env, jTestFinalClass2, jlong(200));
    JTestFinalClass::privateJTestFinalClassField_Set(env, jTestFinalClass, jTestFinalClass2);

    // --- Test Field: privateJTestFinalClassField ---
    object = JTestFinalClass::privateJTestAbstractClassField_Get(env, jTestFinalClass);
    checkNull(errmsg, true, object);

    jTestFinalClass2 = JTestFinalClass::newInstance(env);
    JTestFinalClass::id_Set(env, jTestFinalClass2, jlong(300));
    JTestFinalClass::privateJTestAbstractClassField_Set(env, jTestFinalClass, jTestFinalClass2);

    char const * errmsgstring = errmsg.str().c_str();
    if (*errmsgstring) {
        return env->NewStringUTF(errmsgstring);
    }
    return NULL;
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_abstractClassIsInstance(JNIEnv * env,
                                                                                 jobject thiz,
                                                                                 jobject object) {
    return jni::JTestAbstractClass::isInstance(env, object);
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_finalClassIsInstance(JNIEnv * env,
                                                                              jobject thiz,
                                                                              jobject object) {
    return jni::JTestFinalClass::isInstance(env, object);
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_abstractClassIsAssignableFromInstanceOf(
                                                                                                 JNIEnv * env,
                                                                                                 jobject thiz,
                                                                                                 jclass clazz) {
    return jni::JTestAbstractClass::isAssingableFromInstanceOf(env, clazz);
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_finalClassIsAssignableFromInstanceOf(
                                                                                              JNIEnv * env,
                                                                                              jobject thiz,
                                                                                              jclass clazz) {
    return jni::JTestFinalClass::isAssingableFromInstanceOf(env, clazz);
}

#endif
