#ifndef CPPTOJAVAABSTRACT_H_
#define CPPTOJAVAABSTRACT_H_

#include <stack>
#include "Debug.h"
#include "JBindingTools.h"

class CPPToJavaAbstract : public Object {
protected:
    JBindingSession & _jbindingSession;
    jobject _javaImplementation;

protected:
    CPPToJavaAbstract(JBindingSession & jbindingSession, JNIEnv * initEnv,
                      jobject javaImplementation) :
        _jbindingSession(jbindingSession) {
        TRACE_OBJECT_CREATION("CPPToJavaAbstract");

        _javaImplementation = initEnv->NewGlobalRef(javaImplementation);
    }

    virtual ~CPPToJavaAbstract() {
        TRACE_OBJECT_CALL("~CPPToJavaAbstract");

        JNIEnvInstance jniEnvInstance(_jbindingSession);
        jniEnvInstance->DeleteGlobalRef(_javaImplementation);
    }
};

#endif /*CPPTOJAVAABSTRACT_H_*/

