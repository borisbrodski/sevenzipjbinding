/*
 * JBindingTest.cpp
 *
 *  Created on: Jan 31, 2010
 *      Author: boris
 */
#include <iostream>
#include <sstream>

#include <vector>

#include "SevenZipJBinding.h"
#include "Common/MyCom.h"
#include "Windows/Thread.h"

#include "JBindingTools.h"

#ifdef NATIVE_JUNIT_TEST_SUPPORT

JT_BEGIN_INTERFACE("net/sf/sevenzipjbinding/junit/jbindingtools", Callback1)
/*    */JT_INTERFACE_METHOD(String, test, JT_INT(i, _))
JT_END_INTERFACE

JT_BEGIN_CLASS("net/sf/sevenzipjbinding/junit/jbindingtools", JBindingTest)
/*    */JT_CLASS_STATIC_METHOD(String, simpleCallbackMethod, JT_INT(i, _))
JT_END_CLASS

JT_BEGIN_CLASS("net/sf/sevenzipjbinding/junit/jbindingtools", ExceptionHandlingTest)
/*    */JT_CLASS_STATIC_METHOD(String, recursiveCallbackMethod,
        /*    */JT_STRING(path, JT_INT(depth, JT_INT(width, JT_INT(mtwidth, JT_BOOLEAN(useException,
                                                JT_BOOLEAN(customErrorMessage, JT_INT(widthIndex,JT_INT(mtwidthindex,_)))))))))
JT_END_CLASS

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
Java_net_sf_sevenzipjbinding_junit_jbindingtools_JBindingTest_checkAddingRemovingObjects(
                                                                                         JNIEnv * env,
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
Java_net_sf_sevenzipjbinding_junit_jbindingtools_JBindingTest_callSimpleCallbackMethod(
                                                                                       JNIEnv * env,
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

class ThreadHelper {
    bool _error;
    std::stringstream & _sstream;
    PlatformCriticalSection _criticalSection;
public:
    ThreadHelper(std::stringstream & sstream) :
        _sstream(sstream), _error(false) {
    }

    void reportError() {
        _criticalSection.Enter();
        _error = true;
        _criticalSection.Leave();
    }
    bool isError() {
        return _error;
    }
};

struct CallRecursiveCallbackMethodMTParameter {
    ThreadHelper * _threadHelper;
    JBindingSession * _jbindingSession;
    jclass _thiz;
    jstring _path;
    jint _depth;
    jint _width;
    jint _mtwidth;
    jboolean _useException;
    jboolean _customErrorMessage;
    jint _mtwidthindex;
    jstring _message;

    CallRecursiveCallbackMethodMTParameter() : _message(NULL) {}
};

static THREAD_FUNC_DECL callRecursiveCallbackMethodMT(void * parameterPtr) {
    CallRecursiveCallbackMethodMTParameter *parameter =
    (CallRecursiveCallbackMethodMTParameter *) parameterPtr;

    {
        JNIEnvInstance jniEnvInstance(*parameter->_jbindingSession);

        jstring value = jni::ExceptionHandlingTest::recursiveCallbackMethod(jniEnvInstance,
                parameter->_path, parameter->_depth, parameter->_width, parameter->_mtwidth,
                parameter->_useException, parameter->_customErrorMessage, -1,
                parameter->_mtwidthindex);
        if (jniEnvInstance.exceptionCheck()) {
            parameter->_threadHelper->reportError();
        } else {
            parameter->_message = (jstring) jniEnvInstance->NewGlobalRef(value);
        }
    }
    return 0;
}

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_ExceptionHandlingTest_callRecursiveCallbackMethod(
        JNIEnv * env,
        jclass thiz,
        jstring path,
        jint depth,
        jint width,
        jint mtwidth,
        jboolean useException,
        jboolean customErrorMessage) {
    JBindingSession jbindingSession(env);
    JNINativeCallContext jniNativeCallContext(jbindingSession, env);
    JNIEnvInstance jniEnvInstance(jbindingSession, jniNativeCallContext, env);
#ifdef __ANDROID_API__
    jstring _path = (jstring) jniEnvInstance->NewGlobalRef(path);

#endif
    std::stringstream sstream;

    bool error = false;

    if (width > 1) {
        sstream << "(";
    }
    for (int i = 0; i < width; i++) {
#ifdef __ANDROID_API__
        jstring value = jni::ExceptionHandlingTest::recursiveCallbackMethod(jniEnvInstance, _path, depth,
                width, mtwidth, useException, customErrorMessage, i, -1);
#else
        jstring value = jni::ExceptionHandlingTest::recursiveCallbackMethod(jniEnvInstance, path, depth,
                width, mtwidth, useException, customErrorMessage, i, -1);
#endif
        error |= jniEnvInstance.exceptionCheck();

        if (i) {
            sstream << ",";
        }

        sstream << env << jstringNoQuotes() << value;
    }

    if (width > 1) {
        sstream << ")";
    }

    if (mtwidth > 0) {
        ThreadHelper threadHelper(sstream);
        NWindows::CThread * threads = new NWindows::CThread[mtwidth];
        CallRecursiveCallbackMethodMTParameter * parameters =
        new CallRecursiveCallbackMethodMTParameter[mtwidth];
        for (int i = 0; i < mtwidth; i++) {
            parameters[i]._threadHelper = &threadHelper;
            parameters[i]._jbindingSession = &jbindingSession;
            parameters[i]._thiz = thiz;
#ifdef __ANDROID_API__
            parameters[i]._path = _path;
#else
            parameters[i]._path = path;
#endif
            parameters[i]._depth = depth;
            parameters[i]._width = width;
            parameters[i]._mtwidth = mtwidth;
            parameters[i]._useException = useException;
            parameters[i]._customErrorMessage = customErrorMessage;
            parameters[i]._mtwidthindex = i;
            HRESULT hr;
            for (int j = 0; j < 100; j++) {
                hr = threads[i].Create(callRecursiveCallbackMethodMT, &parameters[i]);
                if (!hr) {
                    break;
                }

                PlatformSleep(1);
            }
            if (hr) {
                fatal("Can't start new thread. Error: 0x%08X", hr);
            }
        }
        for (int i = 0; i < mtwidth;) {
            threads[i].Wait();
            i++;
        }
        for (int i = 0; i < mtwidth; i++) {
            if (parameters[i]._message) {
                sstream  << env << jstringNoQuotes() << parameters[i]._message;
            } else {
                MY_ASSERT(threadHelper.isError());
                sstream << "<message==NULL>";
            }
            env->DeleteGlobalRef(parameters[i]._message);
        }
        delete[] threads;
        delete[] parameters;

        error |= threadHelper.isError();
    }

    if (error) {
        if (customErrorMessage) {
            if (depth % 2) {
                jniNativeCallContext.reportError("Error: depth=%i, width=%i", (int) depth, (int) width);
                jniNativeCallContext.reportError("Following error reports should be ignored!");
            } else {
                // TODO Test this
                jniEnvInstance.reportError("Error: depth=%i, width=%i", (int) depth, (int) width);
                jniEnvInstance.reportError("Following error reports should be ignored!");
            }
        }
#ifdef __ANDROID_API__
        jniEnvInstance->DeleteGlobalRef(_path);
#endif
        return NULL;
    }

#ifdef __ANDROID_API__
    jniEnvInstance->DeleteGlobalRef(_path);
#endif
    return env->NewStringUTF(sstream.str().c_str());
}

class CPPToJavaSimpleClass : public AbstractJavaCallback<jni::Callback1> {
public:
    CPPToJavaSimpleClass(JBindingSession & jbindingSession, JNIEnv * env, jobject implementation) :
        AbstractJavaCallback<jni::Callback1> (jbindingSession, env, implementation) {
        TRACE_OBJECT_CREATION("CPPToJavaSimpleClass");
    }
    char * callback1(long num) {
        JNIEnvInstance envInstance(_jbindingSession);

        jstring result = _javaClass->test(envInstance, _implementation, jint(num));
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

NWindows::CThread Thread;

JBINDING_JNIEXPORT jstring JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_JBindingTest_singleCallSessionWithCallback1(
                                                                                             JNIEnv * env,
                                                                                             jclass thiz,
                                                                                             jobject object,
                                                                                             jlong number) {
    JBindingSession jbindingSession(env);
    JNINativeCallContext nativeCallContext(jbindingSession, env);

    CPPToJavaSimpleClass callback(jbindingSession, env, object);

    char * result = callback.callback1(number);

    return env->NewStringUTF(result);
}

#endif
