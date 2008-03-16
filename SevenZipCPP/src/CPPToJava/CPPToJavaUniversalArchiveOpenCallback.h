#ifndef CPPTOJAVAUNIVERSALARCHIVEOPENCALLBACK_
#define CPPTOJAVAUNIVERSALARCHIVEOPENCALLBACK_

#include "CPPToJavaArchiveOpenCallback.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaCryptoGetTextPassword.h"

class CPPToJavaUniversalArchiveOpencallback :
    public IArchiveOpenCallback,
    public IArchiveOpenVolumeCallback,
    public ICryptoGetTextPassword,
    public CMyUnknownImp
{
private:
    IArchiveOpenCallback * _archiveOpenCallback;
    IArchiveOpenVolumeCallback * _archiveOpenVolumeCallback;
    ICryptoGetTextPassword * _cryptoGetTextPassword;
    
    void Init(JNIEnv * env, jobject archiveOpenCallbackImpl);

public:
    
    CPPToJavaUniversalArchiveOpencallback(JNIEnv * env, jobject archiveOpenCallbackImpl)
    {
        Init(env, archiveOpenCallbackImpl);
    }
    
    virtual ~CPPToJavaUniversalArchiveOpencallback()
    {
        _archiveOpenCallback->Release();
        if (_archiveOpenVolumeCallback)
        {
            _archiveOpenVolumeCallback->Release();
        }
        
        if (_cryptoGetTextPassword)
        {
            _cryptoGetTextPassword->Release();
        }
    }

    STDMETHOD(QueryInterface)(REFGUID iid, void **outObject);
    
    STDMETHOD_(ULONG, AddRef)()
    {
        return ++__m_RefCount;
    }
    STDMETHOD_(ULONG, Release)()
    {
        if (--__m_RefCount != 0)
            return __m_RefCount;
        delete this;
        return 0;
    }

    STDMETHOD(SetTotal)(const UInt64 *files, const UInt64 *bytes)
    {
        return _archiveOpenCallback->SetTotal(files, bytes);
    }
    STDMETHOD(SetCompleted)(const UInt64 *files, const UInt64 *bytes)
    {
        return _archiveOpenCallback->SetCompleted(files, bytes);
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value)
    {
        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetProperty(propID, value);
        }
        return E_NOINTERFACE;
    }
    STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream)
    {
        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetStream(name, inStream);
        }
        return E_NOINTERFACE;
    }

    STDMETHOD(CryptoGetTextPassword)(BSTR *password)
    {
        if (_cryptoGetTextPassword)
        {
            return _cryptoGetTextPassword->CryptoGetTextPassword(password);
        }
        return E_NOINTERFACE;
    }
};
#endif /*CPPTOJAVAUNIVERSALARCHIVEOPENCALLBACK_*/
