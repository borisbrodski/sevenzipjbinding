#ifndef CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_
#define CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_

#include "CPPToJavaAbstract.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaArchiveOpenVolumeCallback :
    public CPPToJavaAbstract, //
    public virtual IArchiveOpenVolumeCallback,
    public CMyUnknownImp {

private:
    jni::IArchiveOpenVolumeCallback * _iArchiveOpenVolumeCallback;
public:
    CPPToJavaArchiveOpenVolumeCallback(JBindingSession & jbindingSession, JNIEnv * initEnv,
                                       jobject archiveOpenColumeCallback) :
        CPPToJavaAbstract(jbindingSession, initEnv, archiveOpenColumeCallback),
                _iArchiveOpenVolumeCallback(jni::IArchiveOpenVolumeCallback::_getInstanceFromObject(
                        initEnv, archiveOpenColumeCallback)) {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenVolumeCallback")
    }

public:
    Z7_COM_UNKNOWN_IMP_1(IArchiveOpenVolumeCallback)

private:
    Z7_IFACE_COM7_IMP_NONFINAL(IArchiveOpenVolumeCallback)
};

#endif /*CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_*/
