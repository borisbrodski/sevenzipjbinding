#ifndef JAVAPROGRESSIMPL_H_
#define JAVAPROGRESSIMPL_H_

#include "JavaInterface.h"

class JavaProgressImpl : public JavaInterface, public virtual IProgress,
	public CMyUnknownImp
{

private:
	jmethodID SetTotalMethodID;
	jmethodID SetCompletedMethodID;
	void Init();

public:
	MY_UNKNOWN_IMP

	JavaProgressImpl(JNIEnv * env, jobject progress) :
		JavaInterface(env, progress)
	{
		Init();
	}

	STDMETHOD(SetTotal)(UInt64 total) PURE;
	STDMETHOD(SetCompleted)(const UInt64 *completeValue) PURE;
};

#endif /*JAVAPROGRESSIMPL_H_*/
