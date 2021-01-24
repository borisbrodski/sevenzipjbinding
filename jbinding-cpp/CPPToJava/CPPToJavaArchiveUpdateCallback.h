#ifndef CPPTOJAVAARCHIVEUPDATECALLBACK_H_
#define CPPTOJAVAARCHIVEUPDATECALLBACK_H_

#include "CPPToJavaAbstract.h"
#include "CPPToJavaProgress.h"
#include "CodecTools.h"

#include "7zip/IPassword.h"

class CPPToJavaArchiveUpdateCallback : public virtual IArchiveUpdateCallback,
        public CPPToJavaProgress,
		public ICryptoGetTextPassword2,
		public ICryptoGetTextPassword {

private:
    jni::IOutCreateCallback * _iOutCreateCallback;
    jobject _outItem;
    int _outItemLastIndex;
    int _archiveFormatIndex;
    jobject _outArchive;
    bool _isInArchiveAttached;
    bool _isICryptoGetTextPasswordImplemented;
    jni::ICryptoGetTextPassword * _cryptoGetTextPassword;

public:
    CPPToJavaArchiveUpdateCallback(JBindingSession & jbindingSession, JNIEnv * initEnv,
                                   jobject archiveUpdateCallback, bool isInArchiveAttached,
                                   int archiveFormatIndex, jobject outArchive) :
        CPPToJavaProgress(jbindingSession, initEnv, archiveUpdateCallback),
            _iOutCreateCallback(jni::IOutCreateCallback::_getInstanceFromObject(
                        initEnv, archiveUpdateCallback)),
				_outItemLastIndex(-1),
				_archiveFormatIndex(archiveFormatIndex),
				_outItem(NULL),
				_outArchive(outArchive),
				_isInArchiveAttached(isInArchiveAttached),
                _isICryptoGetTextPasswordImplemented(-1),
                _cryptoGetTextPassword(NULL) {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenCallback")

        _isICryptoGetTextPasswordImplemented = jni::ICryptoGetTextPassword::_isInstance(initEnv, _javaImplementation);
		JNIEnvInstance jniEnvInstance(_jbindingSession);
#ifdef __ANDROID_API__
        _outArchive = jniEnvInstance->NewGlobalRef(outArchive);
#endif
    }

#ifdef __ANDROID_API__
    ~CPPToJavaArchiveUpdateCallback() {
        JNIEnvInstance jniEnvInstance(_jbindingSession);
        jniEnvInstance->DeleteGlobalRef(_outArchive);
    }
#endif
    STDMETHOD(GetUpdateItemInfo)(UInt32 index, Int32 *newData, /*1 - new data, 0 - old data */
    Int32 *newProperties, /* 1 - new properties, 0 - old properties */
    UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
    );

    STDMETHOD(GetProperty)(UInt32 index, PROPID propID, PROPVARIANT *value);
    STDMETHOD(GetStream)(UInt32 index, ISequentialInStream **inStream);
    STDMETHOD(SetOperationResult)(Int32 operationResult);
    STDMETHOD(CryptoGetTextPassword)(BSTR *password);
    STDMETHOD(CryptoGetTextPassword2)(Int32 *passwordIsDefined, BSTR *password);

    STDMETHOD(SetTotal)(UInt64 total) {
        TRACE_OBJECT_CALL("SetTotal");
        return CPPToJavaProgress::SetTotal(total);
    }

    STDMETHOD(SetCompleted)(const UInt64 *completeValue) {
        TRACE_OBJECT_CALL("SetCompleted");
        return CPPToJavaProgress::SetCompleted(completeValue);
    }

    STDMETHOD(QueryInterface)(REFGUID refguid, void ** p) throw() {
        TRACE_OBJECT_CALL("QueryInterface");

        if ((refguid == IID_ICryptoGetTextPassword) || (refguid == IID_ICryptoGetTextPassword2))
        {
            if (_isICryptoGetTextPasswordImplemented)
            {
            	if (refguid == IID_ICryptoGetTextPassword)
                    *p = (void *)(ICryptoGetTextPassword *)this;
            	else if (refguid == IID_ICryptoGetTextPassword2)
                    *p = (void *)(ICryptoGetTextPassword2 *)this;
            	else
                    return E_NOINTERFACE;
                AddRef();
                return S_OK;
            }
            return E_NOINTERFACE;
        }

        return CPPToJavaProgress::QueryInterface(refguid, p);
    }

    STDMETHOD_(ULONG, AddRef)() throw() {
        TRACE_OBJECT_CALL("AddRef");
        return CPPToJavaProgress::AddRef();
    }

    STDMETHOD_(ULONG, Release)() {
        TRACE_OBJECT_CALL("Release");
        return CPPToJavaProgress::Release();
    }

    void freeOutItem(JNIEnvInstance & jniEnvInstance);


private:
    LONG getOrUpdateOutItem(JNIEnvInstance & jniEnvInstance, int index);
};

#endif /*CPPTOJAVAARCHIVEUPDATECALLBACK_H_*/
