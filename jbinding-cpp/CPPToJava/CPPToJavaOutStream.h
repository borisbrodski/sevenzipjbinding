#ifndef __JAVA_OUT_STREAM_H__INCLUDED__
#define __JAVA_OUT_STREAM_H__INCLUDED__

#include "CPPToJavaSequentialOutStream.h"

class CPPToJavaOutStream :
    public CPPToJavaAbstract,
    public virtual IOutStream,
    public CMyUnknownImp {
private:
    CPPToJavaSequentialOutStream _sequentialOutStream;
    jni::IOutStream * _iOutStream;
    jni::ISeekableStream * _iSeekableStream;

public:
    CPPToJavaOutStream(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject inStream) :
            CPPToJavaAbstract(jbindingSession, initEnv, inStream), //
            _sequentialOutStream(jbindingSession, initEnv, inStream), //
            _iOutStream(jni::IOutStream::_getInstanceFromObject(initEnv, inStream)), //
            _iSeekableStream(jni::ISeekableStream::_getInstanceFromObject(initEnv, inStream)) {
        TRACE_OBJECT_CREATION("CPPToJavaOutStream")
    }

    Z7_IFACE_COM7_IMP_NONFINAL(IOutStream)
    Z7_IFACE_COM7_IMP_NONFINAL(ISequentialOutStream)

private:
    Z7_COM_UNKNOWN_IMP_2(IOutStream, ISequentialOutStream)
};

#endif // __JAVA_OUT_STREAM_H__INCLUDED__
