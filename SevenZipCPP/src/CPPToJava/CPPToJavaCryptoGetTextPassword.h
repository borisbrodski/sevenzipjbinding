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

    CPPToJavaCryptoGetTextPassword(JNIEnv * env, jobject progress) :
        CPPToJavaAbstract(env, progress)
    {
        // public String cryptoGetTextPassword()
        _cryptoGetTextPasswordMethodID = GetMethodId("cryptoGetTextPassword", "()" JAVA_STRING_T);
    }

    STDMETHOD(CryptoGetTextPassword)(BSTR *password);
};

#endif /*CPPTOJAVACRYPTOGETTEXTPASSWORD_H_*/
