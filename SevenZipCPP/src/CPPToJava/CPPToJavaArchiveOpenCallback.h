#ifndef CPPTOJAVAARCHIVEOPENCALLBACK_H_
#define CPPTOJAVAARCHIVEOPENCALLBACK_H_

#include "CPPToJavaAbstract.h"

class CPPToJavaArchiveOpenCallback : public CPPToJavaAbstract, public virtual IArchiveOpenCallback, public CMyUnknownImp
{

private:
    jmethodID _setTotalMethodID;
    jmethodID _setCompletedMethodID;

public:
    MY_UNKNOWN_IMP

    CPPToJavaArchiveOpenCallback(CMyComPtr<NativeMethodContext> nativeMethodContext, JNIEnv * initEnv, jobject archiveOpenCallback) :
        CPPToJavaAbstract(nativeMethodContext, initEnv, archiveOpenCallback)
    {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenCallback")

        classname = "CPPToJavaArchiveOpenCallback";
        
        // 	public void setTotal(Long files, Long bytes);
        _setTotalMethodID = GetMethodId(initEnv, "setTotal", "(" JAVA_LONG_T JAVA_LONG_T ")V");

        // public void setCompleted(Long files, Long bytes);
        _setCompletedMethodID = GetMethodId(initEnv, "setCompleted", "(" JAVA_LONG_T JAVA_LONG_T ")V");
    }

    STDMETHOD(SetTotal)(const UInt64 *files, const UInt64 *bytes);
    STDMETHOD(SetCompleted)(const UInt64 *files, const UInt64 *bytes);

};

#endif /*CPPTOJAVAARCHIVEOPENCALLBACK_H_*/
