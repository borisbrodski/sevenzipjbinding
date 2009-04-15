#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaCryptoGetTextPassword.h"

STDMETHODIMP CPPToJavaCryptoGetTextPassword::CryptoGetTextPassword(BSTR * password)
{
    TRACE_OBJECT_CALL("CryptoGetTextPassword");

    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    jniInstance.PrepareCall();
    jstring passwordString = (jstring)env->CallObjectMethod(_javaImplementation, _cryptoGetTextPasswordMethodID);
    if (jniInstance.IsExceptionOccurs())
    {
        return S_FALSE;
    }

    if (password)
    {
        const jchar * passwordJChars = env->GetStringChars(passwordString, NULL);
        CMyComBSTR passwordBSTR((LPCWSTR)passwordJChars);
        env->ReleaseStringChars(passwordString, passwordJChars);

        *password = passwordBSTR;
    }

    return S_OK;
}
