/*
 * JavaStaticInfo.cpp
 *
 *  Created on: Jan 14, 2010
 *      Author: boris
 */

#include <sstream>

#include "JObjectList.h"
#include "SevenZipJBinding.h"
#include "JNISession.h"

#define JSF_DEFINE_VARIABLES 1
#include "JavaStaticInfo.h"

#include "JavaStatInfos/InArchiveImpl.h"
#include "JavaStatInfos/PropertyInfo.h"


#include <iostream>
using namespace std;

jobject JavaFinalClass::newInstance(JNIEnv * env) {
    ensureJClassLoaded(env);
    jmethodID defaultConstructor = env->GetMethodID(_jclass, "<init>", "()V");

    char classname[256];
    FATALIF1(defaultConstructor == NULL, "Class '%s' has no default constructor",
            GetJavaClassName(env, _jclass, classname, sizeof(classname)));

    return env->NewObject(_jclass, defaultConstructor);
}

void test(JNIEnv * env) {
    //m.myMethod(env);
    jclass classLong = env->FindClass("java/lang/Long");
    jmethodID longValueOf = env->GetStaticMethodID(classLong, "valueOf", "(J)Ljava/lang/Long;");
    jobject long123 = env->CallStaticObjectMethod(classLong, longValueOf, (jlong)123);

    JObject & objectLong = JObject::getInstanceFromObject(env, long123);
    std::cout << "long123 = " << env << objectLong.toString(env, long123) << std::endl;
    std::cout << "long123 = " << env << objectLong.toString(env, long123) << std::endl;


    jclass classInteger = env->FindClass("java/lang/Integer");
    jmethodID integerValueOf = env->GetStaticMethodID(classInteger, "valueOf", "(I)Ljava/lang/Integer;");
    jobject integer234 = env->CallStaticObjectMethod(classInteger, integerValueOf, (jint)234);

    JObject & objectInteger = JObject::getInstanceFromObject(env, integer234);
    std::cout << "integer234 = " << env << objectInteger.toString(env, integer234) << std::endl;
    std::cout << "integer234 = " << env << objectInteger.toString(env, integer234) << std::endl;


    JObject & objectLong2 = JObject::getInstanceFromObject(env, long123);
    std::cout << "long123 = " << env << objectLong2.toString(env, long123) << std::endl;
    std::cout << "long123 = " << env << objectLong2.toString(env, long123) << std::endl;

    JObject & objectLong3 = JObject::getInstanceFromObject(env, long123);
    std::cout << "long123 = " << env << objectLong2.toString(env, long123) << std::endl;
    std::cout << "long123 = " << env << objectLong2.toString(env, long123) << std::endl;

    JObject & objectInteger2 = JObject::getInstanceFromObject(env, integer234);
    std::cout << "integer234 = " << env << objectInteger2.toString(env, integer234) << std::endl;
    std::cout << "integer234 = " << env << objectInteger2.toString(env, integer234) << std::endl;

    JObject & objectInteger3 = JObject::getInstanceFromObject(env, integer234);
    std::cout << "integer234 = " << env << objectInteger3.toString(env, integer234) << std::endl;
    std::cout << "integer234 = " << env << objectInteger3.toString(env, integer234) << std::endl;

//    exit(0);
/*
    std::cout << "o=" << classLong << std::endl;
    jobject classInteger = env->FindClass("java/lang/Integer");
    std::cout << "o=" << classInteger << std::endl;

    JObject & objectLong = JObject::getInstanceFromObject(env, classLong);

    JObject & objectInteger = JObject::getInstanceFromObject(env, classInteger);

    jstring str = objectLong.toString()(env, classLong);
    std::cout << "Result: '" << env << str << "'" << std::endl;

    str = objectInteger.toString()(env, classInteger);
    std::cout << "Result: '" << env << str << "'" << std::endl;
*/


    //o.getVersion(env);

//    jobject o = InArchiveImpl::_class.newInstance(env);
//
//    env->SetLongField(o, InArchiveImpl::bindingSession.getJFieldID(env), 123);
//    env->CallVoidMethod(o, InArchiveImpl::test.getJMethodID(env));
}

void checkString(std::stringstream & errmsg, JNIEnv * env, char const * expectedValue, jstring actualValue) {
    std::stringstream jstringstream;

    jstringstream << env << actualValue;
    if (jstringstream.str() != expectedValue) {
        errmsg << "ERROR: Expected '" << expectedValue << "' get '" << jstringstream.str() << "'" << std::endl;
    }
}

void checkLong(std::stringstream & errmsg, jlong expectedValue, jlong actualValue) {
    if (actualValue != expectedValue) {
        errmsg << "ERROR: Expected '" << expectedValue << "' get '" << actualValue << "'" << std::endl;
    }
}

JBINDING_JNIEXPORT jstring JNICALL Java_net_sf_sevenzipjbinding_junit_jnitools_JNIToolsNativeInterface_testAbstractClass
(JNIEnv * env, jobject thiz, jobject jTestAbstractClass) {

    std::stringstream errmsg;

    // --- Private methods ---

    jlong longResult = JTestAbstractClass::privateLongMethod(env, jTestAbstractClass, jlong(1));
    checkLong(errmsg, jlong(1001), longResult);

    jstring stringResult = JTestAbstractClass::privateStringMethod(env, jTestAbstractClass, jint(2));
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

    longResult = JTestAbstractClass::protectedVirtualLongMethod(env,jTestAbstractClass, jlong(10));
    checkLong(errmsg, jlong(3010), longResult);

    stringResult = JTestAbstractClass::protectedVirtualStringMethod(env,jTestAbstractClass,jint(11));
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


