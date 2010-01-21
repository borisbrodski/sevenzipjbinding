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

struct JOut {
    JNIEnv * _env;
    ostream & _stream;
    JOut(JNIEnv * env, ostream & stream) : _env(env), _stream(stream) {}
};

JOut operator<< (ostream & stream, JNIEnv * env) {
    return JOut(env, stream);
}
ostream & operator<< (JOut jout, jstring str) {
    char const * s = jout._env->GetStringUTFChars(str, NULL);
    jout._stream << s;
    jout._env->ReleaseStringUTFChars(str, s);
    return jout._stream;
}

void test(JNIEnv * env) {
    //m.myMethod(env);
    jobject classLong = env->FindClass("java/lang/Long");
    std::cout << "o=" << classLong << std::endl;
    jobject classInteger = env->FindClass("java/lang/Integer");
    std::cout << "o=" << classInteger << std::endl;

    JObject & objectLong = JObject::getInstanceFromObject(env, classLong);

    JObject & objectInteger = JObject::getInstanceFromObject(env, classInteger);

    jstring str = objectLong.toString()(env, classLong);
    std::cout << "Result: '" << env << str << "'" << std::endl;

    str = objectInteger.toString()(env, classInteger);
    std::cout << "Result: '" << env << str << "'" << std::endl;



    //o.getVersion(env);

//    jobject o = InArchiveImpl::_class.newInstance(env);
//
//    env->SetLongField(o, InArchiveImpl::bindingSession.getJFieldID(env), 123);
//    env->CallVoidMethod(o, InArchiveImpl::test.getJMethodID(env));
}
