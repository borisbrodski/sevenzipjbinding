#ifndef CPPTOJAVAARCHIVEOPENCALLBACK_H_
#define CPPTOJAVAARCHIVEOPENCALLBACK_H_

#include "CPPToJavaAbstract.h"

#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaArchiveOpenCallback : public CPPToJavaAbstract, public virtual IArchiveOpenCallback, public CMyUnknownImp
{
    jni::IArchiveOpenCallback * _iArchiveOpenCallback;
public:
    MY_UNKNOWN_IMP

    CPPToJavaArchiveOpenCallback(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject archiveOpenCallback) :
        CPPToJavaAbstract(jbindingSession, initEnv, archiveOpenCallback),
                _iArchiveOpenCallback(jni::IArchiveOpenCallback::_getInstanceFromObject(initEnv, archiveOpenCallback))
    {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenCallback")
    }

    STDMETHOD(SetTotal)(const UInt64 *files, const UInt64 *bytes);
    STDMETHOD(SetCompleted)(const UInt64 *files, const UInt64 *bytes);
};

#endif /*CPPTOJAVAARCHIVEOPENCALLBACK_H_*/
