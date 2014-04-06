#ifndef CPPTOJAVAPROGRESS_H_
#define CPPTOJAVAPROGRESS_H_

#include "CPPToJavaAbstract.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaProgress : public CPPToJavaAbstract, public virtual IProgress
	, public CMyUnknownImp
{

private:
    jni::IProgress * _iProgress;
public:
	MY_UNKNOWN_IMP

	CPPToJavaProgress(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject progress) :
		CPPToJavaAbstract(jbindingSession, initEnv, progress),
		        _iProgress(jni::IProgress::_getInstanceFromObject(initEnv, progress))
	{
	    TRACE_OBJECT_CREATION("CPPToJavaProgress")
	}

	STDMETHOD(SetTotal)(UInt64 total) PURE;
	STDMETHOD(SetCompleted)(const UInt64 *completeValue) PURE;
};

#endif /*CPPTOJAVAPROGRESS_H_*/
