#include <iostream>
#include <string>
#include <sstream>

#include "SevenZipJBinding.h"
#include "JavaStaticInfo.h"

using namespace std;
using namespace jni;



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

void checkNull(std::stringstream & errmsg, bool expectedNull, jobject actualValue) {
    if (expectedNull ^ !actualValue) {
        errmsg << "ERROR: Expected " << (expectedNull ? "null" : "not null") << std::endl;
    }
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsNativeInterface_testInterface1(
                                                                                   JNIEnv * env,
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
    checkLong(errmsg, jlong(12017), longResult);

    jstring stringResult = interface1->stringMethod(env, interface1Impl, jint(18 + offset));
    checkString(errmsg, env, "Interface.I = 18", stringResult);
    env->DeleteLocalRef(stringResult);

    interface1->voidMethod(env, interface1Impl, jint(19 + offset));

    char const * errmsgstring = errmsg.str().c_str();
    if (*errmsgstring) {
        return env->NewStringUTF(errmsgstring);
    }
    return NULL;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsNativeInterface_testAbstractClass(
                                                                                      JNIEnv * env,
                                                                                      jobject thiz,
                                                                                      jobject jTestAbstractClass) {

    std::stringstream errmsg;

    // --- Private methods ---

    jlong longResult = JTestAbstractClass::privateLongMethod(env, jTestAbstractClass, jlong(1));
    checkLong(errmsg, jlong(1001), longResult);

    jstring stringResult =
            JTestAbstractClass::privateStringMethod(env, jTestAbstractClass, jint(2));
    checkString(errmsg, env, "I1 = 2", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateVoidMethod(env, jTestAbstractClass, jint(3));

    // --- Final methods ---

    longResult = JTestAbstractClass::privateFinalLongMethod(env, jTestAbstractClass, jlong(4));
    checkLong(errmsg, jlong(1004), longResult);

    stringResult = JTestAbstractClass::privateFinalStringMethod(env, jTestAbstractClass, jint(5));
    checkString(errmsg, env, "I2 = 5", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateFinalVoidMethod(env, jTestAbstractClass, jint(6));

    // --- Static methods ---

    longResult = JTestAbstractClass::privateStaticLongMethod(env, jlong(7));
    checkLong(errmsg, jlong(2007), longResult);

    stringResult = JTestAbstractClass::privateStaticStringMethod(env, jint(8));
    checkString(errmsg, env, "I3 = 8", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateStaticVoidMethod(env, jint(9));

    // --- Virtual methods ---

    longResult = JTestAbstractClass::protectedVirtualLongMethod(env, jTestAbstractClass, jlong(10));
    checkLong(errmsg, jlong(3010), longResult);

    stringResult = JTestAbstractClass::protectedVirtualStringMethod(env, jTestAbstractClass, jint(
            11));
    checkString(errmsg, env, "I4 = 11", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::protectedVirtualVoidMethod(env, jTestAbstractClass, jint(12));

    // --- Private fields ---
    longResult = JTestAbstractClass::privateLongField_Get(env, jTestAbstractClass);
    checkLong(errmsg, jlong(-1), longResult);

    JTestAbstractClass::privateLongField_Set(env, jTestAbstractClass, jlong(13));

    stringResult = JTestAbstractClass::privateStringField_Get(env, jTestAbstractClass);
    checkString(errmsg, env, "-1", stringResult);
    env->DeleteLocalRef(stringResult);

    JTestAbstractClass::privateStringField_Set(env, jTestAbstractClass, env->NewStringUTF("14"));

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

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsNativeInterface_testJTestFinalClassNewInstance(
                                                                                                   JNIEnv * env,
                                                                                                   jobject thiz) {
    return JTestFinalClass::newInstance(env);
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsNativeInterface_testFinalClass(
                                                                                   JNIEnv * env,
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
