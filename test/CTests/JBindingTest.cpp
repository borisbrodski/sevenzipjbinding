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

#include "JBindingTools.h"

class SimpleIUnknownClass : public CMyUnknownImp, public Object, public IUnknown {
    int _index;
    int &_instanceCount;
public:
    MY_UNKNOWN_IMP SimpleIUnknownClass(int &instanceCount, int index) :
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
Java_net_sf_sevenzipjbinding_junit_jbinding_JBindingTest_singleCallSession1(JNIEnv * env,
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

