#ifndef UNIVERSALARCHIVEOPENCALLBACK_
#define UNIVERSALARCHIVEOPENCALLBACK_

#include "SevenZipJBinding.h"
#include "CPPToJava/CPPToJavaArchiveOpenCallback.h"
#include "CPPToJava/CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJava/CPPToJavaCryptoGetTextPassword.h"
#include "CPPToJava/CPPToJavaInStream.h"

#include <set>

class UniversalArchiveOpencallback : public virtual IArchiveOpenCallback,
                                     public virtual IArchiveOpenVolumeCallback,
                                     public virtual ICryptoGetTextPassword,
                                     public virtual CMyUnknownImp,
                                     public Object
{
private:
    JBindingSession * _jbindingSession;
    JNIEnv * _initEnv;
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

    STDMETHOD(SetTotal)(const UInt64 *files, const UInt64 *bytes) noexcept Z7_override
    {
        TRACE_OBJECT_CALL("SetTotal")
        TRACE("UniversalArchiveOpencallback::SetTotal")

        return _archiveOpenCallback->SetTotal(files, bytes);
    }
    STDMETHOD(SetCompleted)(const UInt64 *files, const UInt64 *bytes) noexcept Z7_override
    {
        TRACE_OBJECT_CALL("SetCompleted")
        TRACE("UniversalArchiveOpencallback::SetCompleted")

        return _archiveOpenCallback->SetCompleted(files, bytes);
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value) noexcept Z7_override
    {
        TRACE_OBJECT_CALL("GetProperty")
        TRACE("UniversalArchiveOpencallback::GetProperty")

        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetProperty(propID, value);
        }
        // No IArchiveOpenVolumeCallback implemented - return empty variant (not NULL!)
        // VT_EMPTY allows 7-zip to continue during codec auto-detection
        if (value)
        {
            value->vt = VT_EMPTY;
        }
        return S_OK;
    }

   STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream) noexcept Z7_override
    {
        TRACE_OBJECT_CALL("GetStream")
        TRACE("UniversalArchiveOpencallback::GetStream")

        if (_archiveOpenVolumeCallback)
        {
            return _archiveOpenVolumeCallback->GetStream(name, inStream);
        }

        if (inStream)
        {
            *inStream = NULL;
        }

        // No IArchiveOpenVolumeCallback implemented by Java.
        if (_simulateArchiveOpenVolumeCallback) {
            // Archive is a CAB (or other format requiring volumes).
            // 7-zip called GetStream() meaning it needs another volume.
            // Since no callback was provided, we must return an error.
            TRACE("Volume requested but IArchiveOpenVolumeCallback not implemented - returning E_FAIL")
            return E_FAIL;  // This will cause SevenZipException to be thrown
        }

        // For non-CAB archives or during initial detection, allow 7-zip to continue.
        // Return S_OK with NULL stream - this allows codec auto-detection.

        return S_OK;
    }

    STDMETHOD(CryptoGetTextPassword)(BSTR *password) noexcept Z7_override
    {
        TRACE_OBJECT_CALL("CryptoGetTextPassword")
        TRACE("UniversalArchiveOpencallback::CryptoGetTextPassword")

        if (_cryptoGetTextPassword)
        {
            return _cryptoGetTextPassword->CryptoGetTextPassword(password);
        }
        return E_NOINTERFACE;
    }

private:
    Z7_COM_UNKNOWN_IMP_3(IArchiveOpenCallback, IArchiveOpenVolumeCallback, ICryptoGetTextPassword)
};
#endif /*UNIVERSALARCHIVEOPENCALLBACK_*/
