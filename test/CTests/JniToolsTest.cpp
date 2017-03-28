#include <iostream>
#include <string>
#include <sstream>

#include "SevenZipJBinding.h"
#include "JavaStaticInfo.h"

#ifdef NATIVE_JUNIT_TEST_SUPPORT

using namespace std;
using namespace jni;

// TODO Test "ByteArray"

JT_BEGIN_CLASS("net/sf/sevenzipjbinding/junit/jnitools", JTestAbstractClass)
	JT_CLASS_FINAL_METHOD(Long, privateLongMethod, JT_INT(i,_))
	JT_CLASS_FINAL_METHOD(String, privateStringMethod, JT_INT(i,_))
	JT_CLASS_FINAL_METHOD(Void, privateVoidMethod, JT_INT(i,_))

	JT_CLASS_FINAL_METHOD(Long, privateFinalLongMethod, JT_INT(i,_))
	JT_CLASS_FINAL_METHOD(String, privateFinalStringMethod, JT_INT(i,_))
	JT_CLASS_FINAL_METHOD(Void, privateFinalVoidMethod, JT_INT(i,_))

	JT_CLASS_FINAL_METHOD_OBJECT("Ljava/util/List;", privateFinalListMethod, JT_INT(i,_)) // TODO Test it!!

	JT_CLASS_STATIC_METHOD(Long, privateStaticLongMethod, JT_INT(i,_))
	JT_CLASS_STATIC_METHOD(String, privateStaticStringMethod, JT_INT(i,_))
	JT_CLASS_STATIC_METHOD(Void, privateStaticVoidMethod, JT_INT(i,_))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", privateStaticListMethod, JT_INT(i,_)) // TODO Test it!!

	JT_CLASS_VIRTUAL_METHOD(Long, protectedVirtualLongMethod, JT_INT(i, _))
	JT_CLASS_VIRTUAL_METHOD(String, protectedVirtualStringMethod, JT_INT(i, _))
	JT_CLASS_VIRTUAL_METHOD(Void, protectedVirtualVoidMethod, JT_INT(i, _))

	JT_FIELD(Long, privateLongField)
	JT_FIELD(String, privateStringField)

	JT_STATIC_FIELD(Long, privateStaticLongField)
	JT_STATIC_FIELD(String, privateStaticStringField)
JT_END_CLASS
JT_BEGIN_CLASS("net/sf/sevenzipjbinding/junit/jnitools", JTestFinalClass)
	JT_CLASS_VIRTUAL_METHOD(Long, protectedVirtualLongMethod, JT_INT(i, _))
	JT_CLASS_VIRTUAL_METHOD(String, protectedVirtualStringMethod, JT_INT(i, _))
	JT_CLASS_VIRTUAL_METHOD(Void, protectedVirtualVoidMethod, JT_INT(i, _))

	JT_FIELD(Class, privateClassField)
	JT_FIELD(Long, id)

	JT_FIELD_OBJECT(privateJTestFinalClassField, "Lnet/sf/sevenzipjbinding/junit/jnitools/JTestFinalClass;")
	JT_FIELD_OBJECT(privateJTestAbstractClassField, "Lnet/sf/sevenzipjbinding/junit/jnitools/JTestAbstractClass;")
JT_END_CLASS

JT_BEGIN_INTERFACE("net/sf/sevenzipjbinding/junit/jnitools", Interface1)
	JT_INTERFACE_METHOD(Long, longMethod, JT_INT(i, _))
	JT_INTERFACE_METHOD(String, stringMethod, JT_INT(i, _))
	JT_INTERFACE_METHOD(Void, voidMethod, JT_INT(i, _))
	JT_INTERFACE_METHOD(Void, voidMethodFromInterface2, JT_INT(i, _))
JT_END_INTERFACE

#define JT_MY_CLASS(name, param_spec)   \
    JT_PARAM(Object, "Lnet/sf/sevenzipjbinding/junit/jnitools/ParamSpecTest$MyClass;", name, param_spec)

JT_BEGIN_CLASS("net/sf/sevenzipjbinding/junit/jnitools", ParamSpecTest)
	JT_CLASS_STATIC_METHOD(String, stringMethodWithNoParameters, _)
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWithNoParameters, _)
	JT_CLASS_STATIC_METHOD(String, stringMethodWith1Parameter, JT_LONG(l1, _))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith1Parameter, JT_LONG(l1, _))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith2Parameters, JT_INT(l1, JT_STRING(s2, _)))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith2Parameters, JT_INT(l1, JT_STRING(s2, _)))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith3Parameters, JT_BOOLEAN(b1, JT_MY_CLASS(m2, JT_INT(i3, _))))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith3Parameters, JT_BOOLEAN(b1, JT_MY_CLASS(m2, JT_INT(i3, _))))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith4Parameters, JT_MY_CLASS(m1, JT_INT(i2, JT_STRING(s3, JT_BOOLEAN(b4, _)))))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith4Parameters, JT_MY_CLASS(m1, JT_INT(i2, JT_STRING(s3, JT_BOOLEAN(b4, _)))))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith5Parameters, JT_BOOLEAN(b1, JT_STRING(s2, JT_LONG(l3, JT_INT(i4, JT_MY_CLASS(m5, _))))))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith5Parameters, JT_BOOLEAN(b1, JT_STRING(s2, JT_LONG(l3, JT_INT(i4, JT_MY_CLASS(m5, _))))))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith6Parameters, JT_INT(i1, JT_MY_CLASS(m2, JT_LONG(l3, JT_INT(i4, JT_BOOLEAN(b5, JT_MY_CLASS(m6, _)))))))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith6Parameters, JT_INT(i1, JT_MY_CLASS(m2, JT_LONG(l3, JT_INT(i4, JT_BOOLEAN(b5, JT_MY_CLASS(m6, _)))))))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith7Parameters, JT_STRING(s1, JT_INT(i2, JT_LONG(l3, JT_MY_CLASS(m4, JT_INT(i5, JT_BOOLEAN(b6, JT_LONG(l7, _))))))))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith7Parameters, JT_STRING(s1, JT_INT(i2, JT_LONG(l3, JT_MY_CLASS(m4, JT_INT(i5, JT_BOOLEAN(b6, JT_LONG(l7, _))))))))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith8Parameters, JT_STRING(s1, JT_INT(i2, JT_LONG(l3, JT_LONG(l4, JT_INT(i5, JT_BOOLEAN(b6, JT_BOOLEAN(b7, JT_INT(i8, _)))))))))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith8Parameters, JT_STRING(s1, JT_INT(i2, JT_LONG(l3, JT_LONG(l4, JT_INT(i5, JT_BOOLEAN(b6, JT_BOOLEAN(b7, JT_INT(i8, _)))))))))
	JT_CLASS_STATIC_METHOD(String, stringMethodWith9Parameters, JT_MY_CLASS(m1, JT_STRING(s2, JT_INT(i3, JT_MY_CLASS(m4, JT_BOOLEAN(b5, JT_INT(i6, JT_STRING(s7, JT_LONG(l8, JT_BOOLEAN(b9, _))))))))))
	JT_CLASS_STATIC_METHOD_OBJECT("Ljava/util/List;", listMethodWith9Parameters, JT_MY_CLASS(m1, JT_STRING(s2, JT_INT(i3, JT_MY_CLASS(m4, JT_BOOLEAN(b5, JT_INT(i6, JT_STRING(s7, JT_LONG(l8, JT_BOOLEAN(b9, _))))))))))

	JT_CLASS_CONSTRUCTOR(_)
	JT_CLASS_CONSTRUCTOR(JT_LONG(l1, _))
	JT_CLASS_CONSTRUCTOR(JT_INT(l1, JT_STRING(s2, _)))
	JT_CLASS_CONSTRUCTOR(JT_BOOLEAN(b1, JT_MY_CLASS(m2, JT_INT(i3, _))))
	JT_CLASS_CONSTRUCTOR(JT_MY_CLASS(m1, JT_INT(i2, JT_STRING(s3, JT_BOOLEAN(b4, _)))))
	JT_CLASS_CONSTRUCTOR(JT_BOOLEAN(b1, JT_STRING(s2, JT_LONG(l3, JT_INT(i4, JT_MY_CLASS(m5, _))))))
	JT_CLASS_CONSTRUCTOR(JT_INT(i1, JT_MY_CLASS(m2, JT_LONG(l3, JT_INT(i4, JT_BOOLEAN(b5, JT_MY_CLASS(m6, _)))))))
	JT_CLASS_CONSTRUCTOR(JT_STRING(s1, JT_INT(i2, JT_LONG(l3, JT_MY_CLASS(m4, JT_INT(i5, JT_BOOLEAN(b6, JT_LONG(l7, _))))))))
	JT_CLASS_CONSTRUCTOR(JT_STRING(s1, JT_INT(i2, JT_LONG(l3, JT_LONG(l4, JT_INT(i5, JT_BOOLEAN(b6, JT_BOOLEAN(b7, JT_INT(i8, _)))))))))
	JT_CLASS_CONSTRUCTOR(JT_MY_CLASS(m1, JT_STRING(s2, JT_INT(i3, JT_MY_CLASS(m4, JT_BOOLEAN(b5, JT_INT(i6, JT_STRING(s7, JT_LONG(l8, JT_BOOLEAN(b9, _))))))))))
JT_END_CLASS

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

void checkNoExceptions(std::stringstream & errmsg, JNIEnv * env) {
    jni::prepareExceptionCheck(env);
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        errmsg << "ERROR: Unexpected exception occurred" << std::endl;
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

        interface1 = Interface1::_getInstance(env, interface1ImplClass);

    } else {
        interface1 = Interface1::_getInstanceFromObject(env, interface1Impl);
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

    if (interface1->_voidMethodFromInterface2_exists(env)) {
        interface1->voidMethodFromInterface2(env, interface1Impl, jint(20 + offset));
        checkException(errmsg, env);
    }

    char const * errmsgstring = errmsg.str().c_str();
    if (*errmsgstring) {
        return env->NewStringUTF(errmsgstring);
    }
    return NULL;
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_nativeInterfaceIsInstance(JNIEnv * env,
                                                                                   jobject thiz,
                                                                                   jobject object) {
	return jni::Interface1::_isInstance(env, object);
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
    jobject object = JTestFinalClass::_newInstance(env);
    jni::prepareExceptionCheck(env); // No exception check actually needed. The pending exception will be thrown later in java.
    return object;
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

    jobject jTestFinalClass2 = JTestFinalClass::_newInstance(env);
    checkNoExceptions(errmsg, env);
    JTestFinalClass::id_Set(env, jTestFinalClass2, jlong(200));
    JTestFinalClass::privateJTestFinalClassField_Set(env, jTestFinalClass, jTestFinalClass2);

    // --- Test Field: privateJTestFinalClassField ---
    object = JTestFinalClass::privateJTestAbstractClassField_Get(env, jTestFinalClass);
    checkNull(errmsg, true, object);

    jTestFinalClass2 = JTestFinalClass::_newInstance(env);
    checkNoExceptions(errmsg, env);
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
    return jni::JTestAbstractClass::_isInstance(env, object);
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_finalClassIsInstance(JNIEnv * env,
                                                                              jobject thiz,
                                                                              jobject object) {
    return jni::JTestFinalClass::_isInstance(env, object);
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_abstractClassIsAssignableFromInstanceOf(
                                                                                                 JNIEnv * env,
                                                                                                 jobject thiz,
                                                                                                 jclass clazz) {
    return jni::JTestAbstractClass::_isAssingableFromInstanceOf(env, clazz);
}

JBINDING_JNIEXPORT jboolean JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsTest_finalClassIsAssignableFromInstanceOf(
                                                                                              JNIEnv * env,
                                                                                              jobject thiz,
                                                                                              jclass clazz) {
    return jni::JTestFinalClass::_isAssingableFromInstanceOf(env, clazz);
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWithNoParameters(
                                                                                             JNIEnv * env,
                                                                                             jclass) {
    jstring string = jni::ParamSpecTest::stringMethodWithNoParameters(env);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWithNoParameters(
                                                                                           JNIEnv * env,
                                                                                           jclass) {
    jobject object = jni::ParamSpecTest::listMethodWithNoParameters(env);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWithNoParameters(
                                                                                           JNIEnv * env,
                                                                                           jclass) {
    jobject object = jni::ParamSpecTest::newInstance(env);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith1Parameter(
                                                                                           JNIEnv * env,
                                                                                           jclass,
                                                                                           jlong l1) {
    jstring string = jni::ParamSpecTest::stringMethodWith1Parameter(env, l1);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith1Parameter(
                                                                                         JNIEnv * env,
                                                                                         jclass,
                                                                                         jlong l1) {
    jobject object = jni::ParamSpecTest::listMethodWith1Parameter(env, l1);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith1Parameter(
                                                                                         JNIEnv * env,
                                                                                         jclass,
                                                                                         jlong l1) {
    jobject object = jni::ParamSpecTest::newInstance(env, l1);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith2Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jint i1,
                                                                                            jstring s2) {
    jstring string = jni::ParamSpecTest::stringMethodWith2Parameters(env, i1, s2);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith2Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jint i1,
                                                                                          jstring s2) {
    jobject object = jni::ParamSpecTest::listMethodWith2Parameters(env, i1, s2);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith2Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jint i1,
                                                                                          jstring s2) {
    jobject object = jni::ParamSpecTest::newInstance(env, i1, s2);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith3Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jboolean b1,
                                                                                            jobject m2,
                                                                                            jint i3) {
    jstring string = jni::ParamSpecTest::stringMethodWith3Parameters(env, b1, m2, i3);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith3Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jboolean b1,
                                                                                          jobject m2,
                                                                                          jint i3) {
    jobject object = jni::ParamSpecTest::listMethodWith3Parameters(env, b1, m2, i3);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith3Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jboolean b1,
                                                                                          jobject m2,
                                                                                          jint i3) {
    jobject object = jni::ParamSpecTest::newInstance(env, b1, m2, i3);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith4Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jobject m1,
                                                                                            jint i2,
                                                                                            jstring s3,
                                                                                            jboolean b4) {
    jstring string = jni::ParamSpecTest::stringMethodWith4Parameters(env, m1, i2, s3, b4);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith4Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jobject m1,
                                                                                          jint i2,
                                                                                          jstring s3,
                                                                                          jboolean b4) {
    jobject object = jni::ParamSpecTest::listMethodWith4Parameters(env, m1, i2, s3, b4);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith4Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jobject m1,
                                                                                          jint i2,
                                                                                          jstring s3,
                                                                                          jboolean b4) {
    jobject object = jni::ParamSpecTest::newInstance(env, m1, i2, s3, b4);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith5Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jboolean b1,
                                                                                            jstring s2,
                                                                                            jlong l3,
                                                                                            jint i4,
                                                                                            jobject m5) {
    jstring string = jni::ParamSpecTest::stringMethodWith5Parameters(env, b1, s2, l3, i4, m5);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith5Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jboolean b1,
                                                                                          jstring s2,
                                                                                          jlong l3,
                                                                                          jint i4,
                                                                                          jobject m5) {
    jobject object = jni::ParamSpecTest::listMethodWith5Parameters(env, b1, s2, l3, i4, m5);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith5Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jboolean b1,
                                                                                          jstring s2,
                                                                                          jlong l3,
                                                                                          jint i4,
                                                                                          jobject m5) {
    jobject object = jni::ParamSpecTest::newInstance(env, b1, s2, l3, i4, m5);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith6Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jint i1,
                                                                                            jobject m2,
                                                                                            jlong l3,
                                                                                            jint i4,
                                                                                            jboolean b5,
                                                                                            jobject m6) {
    jstring string = jni::ParamSpecTest::stringMethodWith6Parameters(env, i1, m2, l3, i4, b5, m6);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith6Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jint i1,
                                                                                          jobject m2,
                                                                                          jlong l3,
                                                                                          jint i4,
                                                                                          jboolean b5,
                                                                                          jobject m6) {
    jobject object = jni::ParamSpecTest::listMethodWith6Parameters(env, i1, m2, l3, i4, b5, m6);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith6Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jint i1,
                                                                                          jobject m2,
                                                                                          jlong l3,
                                                                                          jint i4,
                                                                                          jboolean b5,
                                                                                          jobject m6) {
    jobject object = jni::ParamSpecTest::newInstance(env, i1, m2, l3, i4, b5, m6);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith7Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jstring s1,
                                                                                            jint i2,
                                                                                            jlong l3,
                                                                                            jobject m4,
                                                                                            jint i5,
                                                                                            jboolean b6,
                                                                                            jlong l7) {
    jstring string = jni::ParamSpecTest::stringMethodWith7Parameters(env, s1, i2, l3, m4, i5, b6,
            l7);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith7Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jstring s1,
                                                                                          jint i2,
                                                                                          jlong l3,
                                                                                          jobject m4,
                                                                                          jint i5,
                                                                                          jboolean b6,
                                                                                          jlong l7) {
    jobject object = jni::ParamSpecTest::listMethodWith7Parameters(env, s1, i2, l3, m4, i5, b6, l7);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith7Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jstring s1,
                                                                                          jint i2,
                                                                                          jlong l3,
                                                                                          jobject m4,
                                                                                          jint i5,
                                                                                          jboolean b6,
                                                                                          jlong l7) {
    jobject object = jni::ParamSpecTest::newInstance(env, s1, i2, l3, m4, i5, b6, l7);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith8Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jstring s1,
                                                                                            jint i2,
                                                                                            jlong l3,
                                                                                            jlong l4,
                                                                                            jint i5,
                                                                                            jboolean b6,
                                                                                            jboolean b7,
                                                                                            jint i8) {
    jstring string = jni::ParamSpecTest::stringMethodWith8Parameters(env, s1, i2, l3, l4, i5, b6,
            b7, i8);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith8Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jstring s1,
                                                                                          jint i2,
                                                                                          jlong l3,
                                                                                          jlong l4,
                                                                                          jint i5,
                                                                                          jboolean b6,
                                                                                          jboolean b7,
                                                                                          jint i8) {
    jobject object = jni::ParamSpecTest::listMethodWith8Parameters(env, s1, i2, l3, l4, i5, b6, b7,
            i8);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith8Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jstring s1,
                                                                                          jint i2,
                                                                                          jlong l3,
                                                                                          jlong l4,
                                                                                          jint i5,
                                                                                          jboolean b6,
                                                                                          jboolean b7,
                                                                                          jint i8) {
    jobject object = jni::ParamSpecTest::newInstance(env, s1, i2, l3, l4, i5, b6, b7,
            i8);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeStringMethodWith9Parameters(
                                                                                            JNIEnv * env,
                                                                                            jclass,
                                                                                            jobject m1,
                                                                                            jstring s2,
                                                                                            jint i3,
                                                                                            jobject m4,
                                                                                            jboolean b5,
                                                                                            jint i6,
                                                                                            jstring s7,
                                                                                            jlong l8,
                                                                                            jboolean b9) {
    jstring string = jni::ParamSpecTest::stringMethodWith9Parameters(env, m1, s2, i3, m4, b5, i6,
            s7, l8, b9);
    jni::prepareExceptionCheck(env);
    return string;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeListMethodWith9Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jobject m1,
                                                                                          jstring s2,
                                                                                          jint i3,
                                                                                          jobject m4,
                                                                                          jboolean b5,
                                                                                          jint i6,
                                                                                          jstring s7,
                                                                                          jlong l8,
                                                                                          jboolean b9) {
    jobject object = jni::ParamSpecTest::listMethodWith9Parameters(env, m1, s2, i3, m4, b5, i6, s7,
            l8, b9);
    jni::prepareExceptionCheck(env);
    return object;
}

JBINDING_JNIEXPORT jobject JNICALL
Java_net_sf_sevenzipjbinding_junit_jnitools_ParamSpecTest_nativeConstructWith9Parameters(
                                                                                          JNIEnv * env,
                                                                                          jclass,
                                                                                          jobject m1,
                                                                                          jstring s2,
                                                                                          jint i3,
                                                                                          jobject m4,
                                                                                          jboolean b5,
                                                                                          jint i6,
                                                                                          jstring s7,
                                                                                          jlong l8,
                                                                                          jboolean b9) {
    jobject object = jni::ParamSpecTest::newInstance(env, m1, s2, i3, m4, b5, i6, s7,
            l8, b9);
    jni::prepareExceptionCheck(env);
    return object;
}

#endif
