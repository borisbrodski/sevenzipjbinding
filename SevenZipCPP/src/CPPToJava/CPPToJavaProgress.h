#ifndef CPPTOJAVAPROGRESS_H_
#define CPPTOJAVAPROGRESS_H_

#include "CPPToJavaAbstract.h"

class CPPToJavaProgress : public CPPToJavaAbstract, public virtual IProgress,
	public CMyUnknownImp
{

private:
	jmethodID _setTotalMethodID;
	jmethodID _setCompletedMethodID;
	
public:
	MY_UNKNOWN_IMP

	CPPToJavaProgress(JNIEnv * env, jobject progress) :
		CPPToJavaAbstract(env, progress)
	{
		_setTotalMethodID = GetMethodId("setCompleted", "(J)V");
		_setCompletedMethodID = GetMethodId("setTotal", "(J)V");
	}

	STDMETHOD(SetTotal)(UInt64 total) PURE;
	STDMETHOD(SetCompleted)(const UInt64 *completeValue) PURE;
};

#endif /*CPPTOJAVAPROGRESS_H_*/
