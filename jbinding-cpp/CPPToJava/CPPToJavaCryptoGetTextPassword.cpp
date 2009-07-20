#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaCryptoGetTextPassword.h"
#include "UnicodeHelper.h"

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
        //CMyComBSTR passwordBSTR((OLECHAR*)(const wchar_t*)UnicodeHelper(passwordJChars));
        //CMyComBSTR passwordBSTR(L"TestXXX");

        //printf("PASSWORD: '%S'\n", (BSTR)passwordBSTR);
        //fflush(stdout);
        StringToBstr(UString(UnicodeHelper(passwordJChars)), password);//passwordBSTR.MyCopy();
        env->ReleaseStringChars(passwordString, passwordJChars);
    }

    if (passwordString)
    {
    	env->DeleteLocalRef(passwordString);
    }

    return S_OK;
}
