#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaCryptoGetTextPassword.h"

STDMETHODIMP CPPToJavaCryptoGetTextPassword::CryptoGetTextPassword(BSTR * password)
{
    _env->ExceptionClear();
    jstring passwordString = (jstring)_env->CallObjectMethod(_javaImplementation, _cryptoGetTextPasswordMethodID);
    if (_env->ExceptionCheck())
    {
        SaveLastOccurredException(_env);
        return S_FALSE;
    }

    if (password)
    {
        const jchar * passwordJChars = _env->GetStringChars(passwordString, NULL);
        CMyComBSTR passwordBSTR((LPCWSTR)passwordJChars);
        _env->ReleaseStringChars(passwordString, passwordJChars);

        *password = passwordBSTR.Detach();
    }
    return S_OK;
}
