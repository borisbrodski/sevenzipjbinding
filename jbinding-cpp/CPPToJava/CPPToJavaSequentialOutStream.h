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
  // Set if the Java object implements IReusableBufferOutStream: Write() then hands it the same byte[] (_buffer, a
  // global reference grown on demand) with the chunk length instead of a new array per call (issue #69).
  jni::IReusableBufferOutStream *_iReusableBufferOutStream;
  jbyteArray _buffer;
  jsize _bufferLength;

public:
  CPPToJavaSequentialOutStream(JBindingSession &jbindingSession,
                               JNIEnv *initEnv,
                               jobject javaSequentialOutStreamImpl)
      : CPPToJavaAbstract(jbindingSession, initEnv,
                          javaSequentialOutStreamImpl),
        _iSequentialOutStream(jni::ISequentialOutStream::_getInstanceFromObject(
            initEnv, javaSequentialOutStreamImpl)),
        _iReusableBufferOutStream(
            jni::IReusableBufferOutStream::_isInstance(initEnv, javaSequentialOutStreamImpl)
                ? jni::IReusableBufferOutStream::_getInstanceFromObject(initEnv, javaSequentialOutStreamImpl)
                : NULL),
        _buffer(NULL), _bufferLength(0) {
    TRACE_OBJECT_CREATION("CPPToJavaSequentialOutStream")
  }

  virtual ~CPPToJavaSequentialOutStream() {
    if (_buffer) {
      JNIEnvInstance jniEnvInstance(_jbindingSession);
      jniEnvInstance->DeleteGlobalRef(_buffer);
    }
  }

  Z7_IFACE_COM7_IMP_NONFINAL(ISequentialOutStream)

private:
  Z7_COM_UNKNOWN_IMP_1(ISequentialOutStream)
};

#endif /*CPPTOJAVASEQUENTIALOUTSTREAM_H_*/
