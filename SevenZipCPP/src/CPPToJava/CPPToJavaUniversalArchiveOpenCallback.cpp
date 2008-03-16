#include "StdAfx.h"

#include "jnitools.h"
#include "CPPToJavaUniversalArchiveOpenCallback.h"

void CPPToJavaUniversalArchiveOpencallback::Init(JNIEnv * env, jobject archiveOpenCallbackImpl)
{
    CMyComPtr<IArchiveOpenCallback> archiveOpenCallbackComPtr = new CPPToJavaArchiveOpenCallback(env, archiveOpenCallbackImpl);
    _archiveOpenCallback = archiveOpenCallbackComPtr.Detach();
    
    _archiveOpenVolumeCallback = NULL;
    _cryptoGetTextPassword = NULL;
    
    jclass cryptoGetTextPasswordClass = env->FindClass(CRYPTOGETTEXTPASSWORD_CLASS);
    FATALIF(cryptoGetTextPasswordClass == NULL,
            "Can't find class " CRYPTOGETTEXTPASSWORD_CLASS);

    jclass archiveOpenVolumeCallbackClass = env->FindClass(ARCHIVEOPENVOLUMECALLBACK_CLASS);
    FATALIF(cryptoGetTextPasswordClass == NULL,
            "Can't find class " ARCHIVEOPENVOLUMECALLBACK_CLASS);

    if (env->IsInstanceOf(archiveOpenCallbackImpl, cryptoGetTextPasswordClass))
    {
        CMyComPtr<ICryptoGetTextPassword> cryptoGetTextPasswordComPtr = 
            new CPPToJavaCryptoGetTextPassword(env, archiveOpenCallbackImpl);
        _cryptoGetTextPassword = cryptoGetTextPasswordComPtr.Detach();
    }
    
    if (env->IsInstanceOf(archiveOpenCallbackImpl, archiveOpenVolumeCallbackClass))
    {
        CMyComPtr<IArchiveOpenVolumeCallback> archiveOpenVolumeCallbackComPtr = 
            new CPPToJavaArchiveOpenVolumeCallback(env, archiveOpenCallbackImpl);
        _archiveOpenVolumeCallback = archiveOpenVolumeCallbackComPtr.Detach();
    }
}

STDMETHODIMP(CPPToJavaUniversalArchiveOpencallback::QueryInterface)(REFGUID iid, void **outObject)
{
    if (iid == IID_IArchiveOpenCallback)
    {
        *outObject = (void *)(IArchiveOpenCallback *)this;
        AddRef();
        return S_OK;
    }

    if (iid == IID_IArchiveOpenVolumeCallback && _archiveOpenVolumeCallback)
    {
        *outObject = (void *)(IArchiveOpenVolumeCallback *)this;
        AddRef();
        return S_OK;
    }

    if (iid == IID_ICryptoGetTextPassword && _cryptoGetTextPassword)
    {
        *outObject = (void *)(ICryptoGetTextPassword *)this;
        AddRef();
        return S_OK;
    }

    return E_NOINTERFACE;
}
