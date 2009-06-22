#ifndef CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_
#define CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_

#include "CPPToJavaAbstract.h"
#include "CPPToJava/CPPToJavaInStream.h"

class CPPToJavaArchiveOpenVolumeCallback : public CPPToJavaAbstract, //
    public virtual IArchiveOpenVolumeCallback, public CMyUnknownImp
{

private:
    jmethodID _getPropertyMethodID;
    jmethodID _getStreamMethodID;

	jclass _propIDClass;
	jmethodID _propIDGetPropIDByIndexMethodID;
	CPPToJavaInStream * lastVolume;

	void Init(JNIEnv * initEnv);

public:
    MY_UNKNOWN_IMP

    CPPToJavaArchiveOpenVolumeCallback(CMyComPtr<NativeMethodContext> nativeMethodContext, JNIEnv * initEnv,
    		jobject javaImplementation, CPPToJavaInStream * lastVolume) :
        CPPToJavaAbstract(nativeMethodContext, initEnv, javaImplementation)
    {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenVolumeCallback")

		Init(initEnv);
		classname = "CPPToJavaArchiveOpenVolumeCallback";
		this->lastVolume = lastVolume;
    }

    STDMETHOD(GetProperty)(PROPID propID, PROPVARIANT *value);
    STDMETHOD(GetStream)(const wchar_t *name, IInStream **inStream);

    ~CPPToJavaArchiveOpenVolumeCallback()
    {
	    TRACE_OBJECT_CALL("~CPPToJavaArchiveOpenVolumeCallback");

	    JNIInstance jniInstance(_nativeMethodContext);
	    JNIEnv * env = jniInstance.GetEnv();

		env->DeleteGlobalRef(_propIDClass);
    }
};

#endif /*CPPTOJAVAARCHIVEOPENVOLUMECALLBACK_H_*/
