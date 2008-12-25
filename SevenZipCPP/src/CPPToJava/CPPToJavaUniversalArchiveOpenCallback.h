#ifndef CPPTOJAVAUNIVERSALARCHIVEOPENCALLBACK_
#define CPPTOJAVAUNIVERSALARCHIVEOPENCALLBACK_

#include "VM.h"
#include "CPPToJavaArchiveOpenCallback.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaCryptoGetTextPassword.h"

class CPPToJavaUniversalArchiveOpencallback :
    public IArchiveOpenCallback,
    public IArchiveOpenVolumeCallback,
    public ICryptoGetTextPassword,
    public CMyUnknownImp,
    public Object
{
private:
    IArchiveOpenCallback * _archiveOpenCallback;
    IArchiveOpenVolumeCallback * _archiveOpenVolumeCallback;
    ICryptoGetTextPassword * _cryptoGetTextPassword;
    
    void Init(VM * vm, JNIEnv * initEnv, jobject archiveOpenCallbackImpl);

public:
    
    CPPToJavaUniversalArchiveOpencallback(CMyComPtr<VM> vm, JNIEnv * initEnv, jobject archiveOpenCallbackImpl)
    {
        TRACE_OBJECT_CREATION("CPPToJavaUniversalArchiveOpencallback")
        Init(vm, initEnv, archiveOpenCallbackImpl);
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
        
        // TRACE_OBJECT_DESTRUCTION
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
        TRACE_OBJECT_CALL("SetTotal")
        
        return _archiveOpenCallback->SetTotal(files, bytes);
    }
    STDMETHOD(SetCompleted)(const UInt64 *files, const UInt64 *bytes)
    {
        TRACE_OBJECT_CALL("SetCompleted")
        
        return _archiveOpenCallback->SetCompleted(files, bytes);
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value)
    {
        TRACE_OBJECT_CALL("GetProperty")
        
        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetProperty(propID, value);
        }
        return E_NOINTERFACE;
    }
    STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream)
    {
        TRACE_OBJECT_CALL("GetStream")
        
        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetStream(name, inStream);
        }
        return E_NOINTERFACE;
    }

    STDMETHOD(CryptoGetTextPassword)(BSTR *password)
    {
        TRACE_OBJECT_CALL("CryptoGetTextPassword")
        
        if (_cryptoGetTextPassword)
        {
            return _cryptoGetTextPassword->CryptoGetTextPassword(password);
        }
        return E_NOINTERFACE;
    }
};
#endif /*CPPTOJAVAUNIVERSALARCHIVEOPENCALLBACK_*/
