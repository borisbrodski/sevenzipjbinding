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

    CPPToJavaArchiveOpenVolumeCallback(JNIEnv * env, jobject progress) :
        CPPToJavaAbstract(env, progress)
    {
        // public Object getProperty(PropID propID);
        _getPropertyMethodID = GetMethodId("getProperty", "(" PROPID_CLASS_T ")" JAVA_OBJECT_T);

        // public IInStream getStream(String filename);
        _getStreamMethodID = GetMethodId("getStream", "(" JAVA_STRING_T ")" INSTREAM_CLASS_T);
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value);
    STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream);
};

#endif /*CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_*/
