#ifndef UNIVERSALARCHIVEOPENCALLBACK_
#define UNIVERSALARCHIVEOPENCALLBACK_

#include "SevenZipJBinding.h"
#include "CPPToJava/CPPToJavaArchiveOpenCallback.h"
#include "CPPToJava/CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJava/CPPToJavaCryptoGetTextPassword.h"
#include "CPPToJava/CPPToJavaInStream.h"

class UniversalArchiveOpencallback :
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
    bool _simulateArchiveOpenVolumeCallback;

    void Init(JBindingSession & jbindingSession, JNIEnv * initEnv,
    		jobject archiveOpenCallbackImpl);

public:

    UniversalArchiveOpencallback(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject archiveOpenCallbackImpl)
    {
        TRACE_OBJECT_CREATION("UniversalArchiveOpencallback")
        Init(jbindingSession, initEnv, archiveOpenCallbackImpl);
    }

    virtual ~UniversalArchiveOpencallback()
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

    void setSimulateArchiveOpenVolumeCallback(bool value) {
        _simulateArchiveOpenVolumeCallback = value;
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
        TRACE("UniversalArchiveOpencallback::SetTotal")

        return _archiveOpenCallback->SetTotal(files, bytes);
    }
    STDMETHOD(SetCompleted)(const UInt64 *files, const UInt64 *bytes)
    {
        TRACE_OBJECT_CALL("SetCompleted")
        TRACE("UniversalArchiveOpencallback::SetCompleted")

        return _archiveOpenCallback->SetCompleted(files, bytes);
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value)
    {
        TRACE_OBJECT_CALL("GetProperty")
        TRACE("UniversalArchiveOpencallback::GetProperty")

        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetProperty(propID, value);
        }
        // TODO Generate exception explaining situation:
        // IArchiveOpenCallback implementation must also implement IArchiveOpenVolumeCallback
        return E_NOINTERFACE;
    }
    STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream)
    {
        TRACE_OBJECT_CALL("GetStream")
        TRACE("UniversalArchiveOpencallback::GetStream")

        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetStream(name, inStream);
        }
        // TODO Generate exception explaining situation:
        // IArchiveOpenCallback implementation must also implement IArchiveOpenVolumeCallback
        return E_NOINTERFACE;
    }

    STDMETHOD(CryptoGetTextPassword)(BSTR *password)
    {
        TRACE_OBJECT_CALL("CryptoGetTextPassword")
        TRACE("UniversalArchiveOpencallback::CryptoGetTextPassword")

        if (_cryptoGetTextPassword)
        {
            return _cryptoGetTextPassword->CryptoGetTextPassword(password);
        }
        return E_NOINTERFACE;
    }
};
#endif /*UNIVERSALARCHIVEOPENCALLBACK_*/
