#ifndef CPPTOJAVASEQUENTIALOUTSTREAM_H_
#define CPPTOJAVASEQUENTIALOUTSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "CPPToJavaAbstract.h"
#include "Common/MyCom.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaSequentialOutStream : public CPPToJavaAbstract,
                                     public ISequentialOutStream,
                                     public CMyUnknownImp {
private:
  jni::ISequentialOutStream *_iSequentialOutStream;

public:
  CPPToJavaSequentialOutStream(JBindingSession &jbindingSession,
                               JNIEnv *initEnv,
                               jobject javaSequentialOutStreamImpl)
      : CPPToJavaAbstract(jbindingSession, initEnv,
                          javaSequentialOutStreamImpl),
        _iSequentialOutStream(jni::ISequentialOutStream::_getInstanceFromObject(
            initEnv, javaSequentialOutStreamImpl)) {
    TRACE_OBJECT_CREATION("CPPToJavaSequentialOutStream")
  }

  virtual ~CPPToJavaSequentialOutStream() {}

  Z7_IFACE_COM7_IMP_NONFINAL(ISequentialOutStream)

private:
  Z7_COM_UNKNOWN_IMP_1(ISequentialOutStream)
};

#endif /*CPPTOJAVASEQUENTIALOUTSTREAM_H_*/
