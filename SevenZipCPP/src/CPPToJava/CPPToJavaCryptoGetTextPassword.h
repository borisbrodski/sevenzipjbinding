#ifndef CPPTOJAVACRYPTOGETTEXTPASSWORD_H_
#define CPPTOJAVACRYPTOGETTEXTPASSWORD_H_

#include "CPPToJavaAbstract.h"

class CPPToJavaCryptoGetTextPassword : public CPPToJavaAbstract, public virtual ICryptoGetTextPassword,
    public CMyUnknownImp
{

private:
    jmethodID _cryptoGetTextPasswordMethodID;

public:
    MY_UNKNOWN_IMP

    CPPToJavaCryptoGetTextPassword(CMyComPtr<JNICallState> jniCallState, JNIEnv * initEnv, jobject progress) :
        CPPToJavaAbstract(jniCallState, initEnv, progress)
    {
        TRACE_OBJECT_CREATION("CPPToJavaCryptoGetTextPassword")
        
        classname = "CPPToJavaCryptoGetTextPassword";
        
        // public String cryptoGetTextPassword()
        _cryptoGetTextPasswordMethodID = GetMethodId(initEnv, "cryptoGetTextPassword", "()" JAVA_STRING_T);
    }

    STDMETHOD(CryptoGetTextPassword)(BSTR *password);
};

#endif /*CPPTOJAVACRYPTOGETTEXTPASSWORD_H_*/
