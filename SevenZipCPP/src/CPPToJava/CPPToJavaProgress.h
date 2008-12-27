#ifndef CPPTOJAVAPROGRESS_H_
#define CPPTOJAVAPROGRESS_H_

#include "CPPToJavaAbstract.h"

class CPPToJavaProgress : public CPPToJavaAbstract, public virtual IProgress
	, public CMyUnknownImp
{

private:
	jmethodID _setTotalMethodID;
	jmethodID _setCompletedMethodID;
	
public:
	MY_UNKNOWN_IMP
/*
	STDMETHOD_(ULONG, AddRef)()
    {
        TRACE2("CPPToJavaProgress::AddRef(). Counter before: %i => %i", (int)__m_RefCount,(int)__m_RefCount + 1);
        return ++__m_RefCount;
    } 
    STDMETHOD_(ULONG, Release)()
    {
        TRACE2("CPPToJavaProgress::Release(). Counter before: %i => %i", (int)__m_RefCount, (int)__m_RefCount - 1);
        if (--__m_RefCount != 0)  
            return __m_RefCount; delete this; return 0;
    }
    
    STDMETHOD(QueryInterface)(REFGUID, void **)
    {
        return E_NOINTERFACE;
    }
*/

	CPPToJavaProgress(CMyComPtr<JNICallState> jniCallState, JNIEnv * initEnv, jobject progress) :
		CPPToJavaAbstract(jniCallState, initEnv, progress)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaProgress")
	    
	    classname = "CPPToJavaProgress";
		_setTotalMethodID = GetMethodId(initEnv, "setTotal", "(J)V");
		_setCompletedMethodID = GetMethodId(initEnv, "setCompleted", "(J)V");
	}

	STDMETHOD(SetTotal)(UInt64 total) PURE;
	STDMETHOD(SetCompleted)(const UInt64 *completeValue) PURE;
};

#endif /*CPPTOJAVAPROGRESS_H_*/
