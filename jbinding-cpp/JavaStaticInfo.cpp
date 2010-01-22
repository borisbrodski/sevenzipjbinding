/*
 * JavaStaticInfo.cpp
 *
 *  Created on: Jan 14, 2010
 *      Author: boris
 */

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

    exit(0);
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
