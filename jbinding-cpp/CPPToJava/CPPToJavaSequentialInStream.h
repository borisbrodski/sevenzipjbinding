#ifndef CPPTOJAVASEQUENTIALINSTREAM_H_
#define CPPTOJAVASEQUENTIALINSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"

#include "CPPToJavaAbstract.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaSequentialInStream :
    public CPPToJavaAbstract,
    public ISequentialInStream,
    public CMyUnknownImp
{
    jni::ISequentialInStream * _iSequentialInStream;

public:
    CPPToJavaSequentialInStream(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject sequentialInStream)
        : CPPToJavaAbstract(jbindingSession, initEnv, sequentialInStream),
          _iSequentialInStream(jni::ISequentialInStream::_getInstanceFromObject(initEnv, sequentialInStream))
    {
        TRACE_OBJECT_CREATION("CPPToJavaSequentialInStream")
    }

    virtual ~CPPToJavaSequentialInStream() {}

public:
    Z7_IFACE_COM7_IMP_NONFINAL(ISequentialInStream)

private:
    Z7_COM_UNKNOWN_IMP_1(ISequentialInStream)
};

#endif /* CPPTOJAVASEQUENTIALINSTREAM_H_ */

