#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaCryptoGetTextPassword.h"

STDMETHODIMP CPPToJavaCryptoGetTextPassword::CryptoGetTextPassword(BSTR * password)
{
    TRACE_OBJECT_CALL("CryptoGetTextPassword")
    
    JNIEnv * env = BeginCPPToJavaCall();

    env->ExceptionClear();
    jstring passwordString = (jstring)env->CallObjectMethod(_javaImplementation, _cryptoGetTextPasswordMethodID);
    if (env->ExceptionCheck())
    {
        SaveLastOccurredException(env);
        
        EndCPPToJavaCall();
        return S_FALSE;
    }

    if (password)
    {
        const jchar * passwordJChars = env->GetStringChars(passwordString, NULL);
        CMyComBSTR passwordBSTR((LPCWSTR)passwordJChars);
        env->ReleaseStringChars(passwordString, passwordJChars);

        *password = passwordBSTR.Detach();
    }
    
    EndCPPToJavaCall();
    return S_OK;
}
