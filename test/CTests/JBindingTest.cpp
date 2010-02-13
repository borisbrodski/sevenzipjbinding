/*
 * JBindingTest.cpp
 *
 *  Created on: Jan 31, 2010
 *      Author: boris
 */
#include <iostream>

#include <vector>

#include "SevenZipJBinding.h"
#include "Common/MyCom.h"
#include "Windows/Thread.h"

#include "JBindingTools.h"

BEGIN_JINTERFACE(Callback1)
/*    */JINTERFACE_METHOD(String, test, "(I)")
END_JINTERFACE

BEGIN_JCLASS("net/sf/sevenzipjbinding/junit/jbinding", JBindingTest)
/*    */JCLASS_STATIC_METHOD(String, simpleCallbackMethod, "(I)")
/*    */JCLASS_STATIC_METHOD(String, recursiveCallbackMethod, "(IZ)")
END_JCLASS

class SimpleIUnknownClass : public CMyUnknownImp, public Object, public IUnknown {
    int _index;
    int &_instanceCount;
public:
    MY_UNKNOWN_IMP
    SimpleIUnknownClass(int &instanceCount, int index) :
        _instanceCount(instanceCount), _index(index) {
        TRACE_OBJECT_CREATION("SimpleIUnknownClass")
        _instanceCount++;
    }
    int getIndex() {
        TRACE_OBJECT_CALL("getIndex")
        return _index;
    }
    ~SimpleIUnknownClass() {
        TRACE_OBJECT_CALL("~SimpleIUnknownClass")
        _instanceCount--;
    }
};

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jbinding_JBindingTest_checkAddingRemovingObjects(JNIEnv * env,
                                                                            jclass thiz,
                                                                            jint objectCount) {
    JBindingSession jbindingSession(env);

    std::vector<SimpleIUnknownClass *> objects(objectCount);
    std::vector<CMyComPtrWrapper<SimpleIUnknownClass> > objectMyComPtrs(objectCount);

    int instanceCount = 0;

    for (int i = 0; i < objectCount; i++) {
        SimpleIUnknownClass * obj = new SimpleIUnknownClass(instanceCount, i);
        objects[i] = obj;
        objectMyComPtrs[i] = obj;
        jbindingSession.addObject(obj);
    }

    // Check objects are alive
    if (instanceCount != objectCount) {
        return env->NewStringUTF("Object count doesn't match instance count");
    }
    for (int i = 0; i < objectCount; i++) {
        if (objects[i]->getIndex() != i || objectMyComPtrs[i]->getIndex() != i) {
            return env->NewStringUTF("Wrong object index");
        }
    }

    objectMyComPtrs.clear();

    // Check objects are alive
    if (instanceCount != objectCount) {
        return env->NewStringUTF("Object count doesn't match instance count");
    }
    for (int i = 0; i < objectCount; i++) {
        if (objects[i]->getIndex() != i || objectMyComPtrs[i]->getIndex() != i) {
            return env->NewStringUTF("Wrong object index");
        }
    }

    jbindingSession.closeSession(env);

    if (instanceCount != 0) {
        return env->NewStringUTF("Not all objects were destroyed as expected.");
    }

    return env->NewStringUTF("OK");
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jbinding_JBindingTest_callSimpleCallbackMethod(JNIEnv * env,
                                                                                  jclass thiz,
                                                                                  jint parameter) {
    JBindingSession jbindingSession(env);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    jstring value = jni::JBindingTest::simpleCallbackMethod(jniEnvInstance, parameter);
    if (jniEnvInstance.exceptionCheck()) {
        return env->NewStringUTF("Exception");
    }

    return value;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jbinding_JBindingTest_callRecursiveCallbackMethod(JNIEnv * env,
                                                                                     jclass thiz,
                                                                                     jint parameter,
                                                                                     jboolean useException) {
    JBindingSession jbindingSession(env);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);

    jstring value = jni::JBindingTest::recursiveCallbackMethod(jniEnvInstance, parameter, useException);
    if (jniEnvInstance.exceptionCheck()) {
        return env->NewStringUTF("Exception");
    }

    return value;
}

class CPPToJavaSimpleClass : public AbstractJavaCallback<jni::Callback1> {
public:
    CPPToJavaSimpleClass(JBindingSession & jbindingSession, JNIEnv * env, jobject implementation) :
            AbstractJavaCallback<jni::Callback1>(jbindingSession, env, implementation) {
        TRACE_OBJECT_CREATION("CPPToJavaSimpleClass");
    }
    char * callback1(long num) {
        JNIEnvInstance envInstance(_jbindingSession);


        jstring result =_javaClass.test(envInstance, _implementation, jint(num));
        if (envInstance.exceptionCheck()) {
            return strdup("EXCEPTION");
        }

        jboolean isCopy;
        char const * resultString = envInstance->GetStringUTFChars(result, &isCopy);
        TRACE("Result = " << resultString)
        char * resultStringCopy = strdup(resultString);
        envInstance->ReleaseStringUTFChars(result, resultString);
        return resultStringCopy;
    }
};


static THREAD_FUNC_DECL MFThread(void *threadInfo) {
    for (int i = 0; i < 10; i++) {
        std::cout << "T: " << i << std::endl;
    }
    return 0;
}

NWindows::CThread Thread;

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jbinding_JBindingTest_singleCallSessionWithCallback1(
                                                                                        JNIEnv * env,
                                                                                        jclass thiz,
                                                                                        jobject object, jlong number) {
    JBindingSession jbindingSession(env);
    JNINativeCallContext nativeCallContext(jbindingSession, env);

    // Thread.Create(MFThread, NULL);
    //for (int i = 0; i < 10; i++) {
    //    std::cout << "X: " << i << std::endl;
    // }

    CPPToJavaSimpleClass callback(jbindingSession, env, object);

    char * result = callback.callback1(number);

    return env->NewStringUTF(result);
}
