#ifndef CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_
#define CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_

#include "CPPToJavaAbstract.h"
#include "CPPToJava/CPPToJavaInStream.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaArchiveOpenVolumeCallback : public CPPToJavaAbstract, //
        public virtual IArchiveOpenVolumeCallback, public CMyUnknownImp {

private:
    jni::IArchiveOpenVolumeCallback * _iArchiveOpenVolumeCallback;
public:
    MY_UNKNOWN_IMP

    CPPToJavaArchiveOpenVolumeCallback(JBindingSession & jbindingSession, JNIEnv * initEnv,
                                       jobject archiveOpenColumeCallback) :
        CPPToJavaAbstract(jbindingSession, initEnv, archiveOpenColumeCallback),
                _iArchiveOpenVolumeCallback(jni::IArchiveOpenVolumeCallback::_getInstanceFromObject(
                        initEnv, archiveOpenColumeCallback)) {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenVolumeCallback")
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value);
    STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream);
};

#endif /*CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_*/
