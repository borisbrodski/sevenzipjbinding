#ifndef __JAVA_IN_STREAM_H__INCLUDED__

#include "CPPToJavaSequentialInStream.h"

class CPPToJavaInStream :
    public CPPToJavaAbstract,
    public IInStream,
    public CMyUnknownImp
{
    jni::ISeekableStream * _iSeekableStream;
    CPPToJavaSequentialInStream _sequentialInStream;

public:

    CPPToJavaInStream(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject inStream)
        : CPPToJavaAbstract(jbindingSession, initEnv, inStream),
          _iSeekableStream(jni::ISeekableStream::_getInstanceFromObject(initEnv, inStream)),
          _sequentialInStream(jbindingSession, initEnv, inStream)
    {
        TRACE_OBJECT_CREATION("CPPToJavaInStream")
    }

    virtual ~CPPToJavaInStream() {}

    Z7_IFACE_COM7_IMP_NONFINAL(IInStream)
    Z7_IFACE_COM7_IMP_NONFINAL(ISequentialInStream)



private:
    Z7_COM_UNKNOWN_IMP_2(IInStream, ISequentialInStream)
};

#define __JAVA_IN_STREAM_H__INCLUDED__
#endif // __JAVA_IN_STREAM_H__INCLUDED__
