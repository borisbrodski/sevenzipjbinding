#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaCryptoGetTextPassword.h"
#include "UnicodeHelper.h"

STDMETHODIMP CPPToJavaCryptoGetTextPassword::CryptoGetTextPassword(BSTR * password) {
    TRACE_OBJECT_CALL("CryptoGetTextPassword");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (password) {
        *password = NULL;
    }

    jstring passwordString = (jstring) _iCryptoGetTextPassword->cryptoGetTextPassword(
            jniEnvInstance, _javaImplementation);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    if (!passwordString) {
        jniEnvInstance.reportError("Password is 'null'");
        return S_FALSE;
    }

    if (password) {
        StringToBstr(UString(FromJChar(jniEnvInstance, passwordString)), password);
    }

    if (passwordString) {
        jniEnvInstance->DeleteLocalRef(passwordString);
    }

    return S_OK;
}
