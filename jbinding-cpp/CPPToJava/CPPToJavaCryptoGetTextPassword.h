#ifndef CPPTOJAVACRYPTOGETTEXTPASSWORD_H_
#define CPPTOJAVACRYPTOGETTEXTPASSWORD_H_

#include "Common/MyCom.h"
#include "CPPToJavaAbstract.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaCryptoGetTextPassword :
    public virtual CPPToJavaAbstract,
    public virtual ICryptoGetTextPassword,
    public virtual CMyUnknownImp
{
private:
    jni::ICryptoGetTextPassword * _iCryptoGetTextPassword;

public:
    CPPToJavaCryptoGetTextPassword(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject cryptoGetTextPassword)
        : CPPToJavaAbstract(jbindingSession, initEnv, cryptoGetTextPassword),
          _iCryptoGetTextPassword(jni::ICryptoGetTextPassword::_getInstanceFromObject(initEnv, cryptoGetTextPassword))
    {
        TRACE_OBJECT_CREATION("CPPToJavaCryptoGetTextPassword")
    }

    virtual ~CPPToJavaCryptoGetTextPassword() {}

public:
    Z7_IFACE_COM7_IMP_NONFINAL(ICryptoGetTextPassword)

private:
    Z7_COM_UNKNOWN_IMP_1(ICryptoGetTextPassword)
};

#endif /* CPPTOJAVACRYPTOGETTEXTPASSWORD_H_ */
