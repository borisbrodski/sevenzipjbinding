#ifndef CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_
#define CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_

#include "CPPToJavaCryptoGetTextPassword.h"
#include "CPPToJavaProgress.h"
#include "Common/MyCom.h"

class CPPToJavaArchiveExtractCallback : public virtual IArchiveExtractCallback,
                                        public virtual ICryptoGetTextPassword,
                                        public virtual IArchiveExtractCallbackMessage2,
                                        public virtual CPPToJavaAbstract,
                                        public virtual CMyUnknownImp {
private:
  ICryptoGetTextPassword *_cryptoGetTextPasswordImpl;
  jni::IArchiveExtractCallback *_iArchiveExtractCallback;
  CPPToJavaProgress _progress;

public:
  CPPToJavaArchiveExtractCallback(JBindingSession &jbindingSession,
                                  JNIEnv *initEnv,
                                  jobject archiveExtractCallbackImpl)
      : CPPToJavaAbstract(jbindingSession, initEnv, archiveExtractCallbackImpl),
        _progress(jbindingSession, initEnv, archiveExtractCallbackImpl),
        _iArchiveExtractCallback(
            jni::IArchiveExtractCallback::_getInstanceFromObject(
                initEnv, archiveExtractCallbackImpl)) {
    TRACE_OBJECT_CREATION("CPPToJavaArchiveExtractCallback")

    jclass cryptoGetTextPasswordClass =
        initEnv->FindClass(CRYPTOGETTEXTPASSWORD_CLASS);
    FATALIF(cryptoGetTextPasswordClass == NULL,
            "Can't find class " CRYPTOGETTEXTPASSWORD_CLASS);

    if (initEnv->IsInstanceOf(_javaImplementation,
                              cryptoGetTextPasswordClass)) {
      CMyComPtr<ICryptoGetTextPassword> cryptoGetTextPasswordComPtr =
          new CPPToJavaCryptoGetTextPassword(_jbindingSession, initEnv,
                                             _javaImplementation);
      _cryptoGetTextPasswordImpl = cryptoGetTextPasswordComPtr.Detach();
    } else {
      _cryptoGetTextPasswordImpl = NULL;
    }
  }

  virtual ~CPPToJavaArchiveExtractCallback() {
    TRACE_OBJECT_CALL("~CPPToJavaArchiveExtractCallback");

    if (_cryptoGetTextPasswordImpl) {
      _cryptoGetTextPasswordImpl->Release();
    }
  }

public:
  STDMETHOD(SetTotal)(UInt64 total) noexcept Z7_override {
    TRACE_OBJECT_CALL("SetTotal");
    return _progress.SetTotal(total);
  }

  STDMETHOD(SetCompleted)(const UInt64 *completeValue) noexcept Z7_override {
    TRACE_OBJECT_CALL("SetCompleted");
    return _progress.SetCompleted(completeValue);
  }

  // STDMETHOD(GetStream)(UInt32 index, ISequentialOutStream **outStream, Int32
  // askExtractMode);
  //
  // /*
  //  * FROM 7-ZIP:
  //  *
  //  * GetStream OUT: S_OK - OK, S_FALSE - skeep this file
  //  *
  //  */
  // STDMETHOD(PrepareOperation)(Int32 askExtractMode);
  // STDMETHOD(SetOperationResult)(Int32 resultEOperationResult);

public:
  // Z7_COM_QI_BEGIN2(IArchiveExtractCallback)
  // // Z7_COM_QI_ENTRY(ICryptoGetTextPassword)
  // Z7_COM_QI_END
  // Z7_COM_ADDREF_RELEASE

  Z7_IFACE_COM7_IMP_NONFINAL(IArchiveExtractCallback)
  Z7_IFACE_COM7_IMP_NONFINAL(ICryptoGetTextPassword)
  Z7_IFACE_COM7_IMP_NONFINAL(IArchiveExtractCallbackMessage2)

private:
  Z7_COM_UNKNOWN_IMP_4(IArchiveExtractCallback, ICryptoGetTextPassword,
                       IProgress, IArchiveExtractCallbackMessage2)
};

#endif /*CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_*/
