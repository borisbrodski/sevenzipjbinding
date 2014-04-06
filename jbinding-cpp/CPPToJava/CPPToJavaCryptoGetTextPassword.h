#ifndef CPPTOJAVACRYPTOGETTEXTPASSWORD_H_
#define CPPTOJAVACRYPTOGETTEXTPASSWORD_H_

#include "CPPToJavaAbstract.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaCryptoGetTextPassword : public CPPToJavaAbstract, public virtual ICryptoGetTextPassword,
    public CMyUnknownImp
{

private:
    jni::ICryptoGetTextPassword * _iCryptoGetTextPassword;
public:
    MY_UNKNOWN_IMP

    CPPToJavaCryptoGetTextPassword(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject cryptoGetTextPassword) :
        CPPToJavaAbstract(jbindingSession, initEnv, cryptoGetTextPassword),
                _iCryptoGetTextPassword(jni::ICryptoGetTextPassword::_getInstanceFromObject(initEnv, cryptoGetTextPassword))
    {
        TRACE_OBJECT_CREATION("CPPToJavaCryptoGetTextPassword")
    }

    STDMETHOD(CryptoGetTextPassword)(BSTR *password);
};

#endif /*CPPTOJAVACRYPTOGETTEXTPASSWORD_H_*/
