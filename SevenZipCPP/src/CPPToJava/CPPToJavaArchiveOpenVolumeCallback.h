#ifndef CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_
#define CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_

#include "CPPToJavaAbstract.h"

class CPPToJavaArchiveOpenVolumeCallback : public CPPToJavaAbstract, //
    public virtual IArchiveOpenVolumeCallback, public CMyUnknownImp
{

private:
    jmethodID _getPropertyMethodID;
    jmethodID _getStreamMethodID;

public:
    MY_UNKNOWN_IMP

    CPPToJavaArchiveOpenVolumeCallback(CMyComPtr<JNICallState> jniCallState, JNIEnv * initEnv, jobject progress) :
        CPPToJavaAbstract(jniCallState, initEnv, progress)
    {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenVolumeCallback")
        
        // public Object getProperty(PropID propID);
        _getPropertyMethodID = GetMethodId(initEnv, "getProperty", "(" PROPID_CLASS_T ")" JAVA_OBJECT_T);

        // public IInStream getStream(String filename);
        _getStreamMethodID = GetMethodId(initEnv, "getStream", "(" JAVA_STRING_T ")" INSTREAM_CLASS_T);
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value);
    STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream);
};

#endif /*CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_*/
