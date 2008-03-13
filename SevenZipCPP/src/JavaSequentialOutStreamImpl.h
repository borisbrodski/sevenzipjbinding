#ifndef JAVASEQUENTIALOUTSTREAMIMPL_H_
#define JAVASEQUENTIALOUTSTREAMIMPL_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"
#include "JavaInterface.h"

class JavaSequentialOutStreamImpl : public JavaInterface,
	public ISequentialOutStream, public CMyUnknownImp
{
private:
	jmethodID WriteMethodID;
	void Init();
	
public:
	MY_UNKNOWN_IMP
	
	JavaSequentialOutStreamImpl(JNIEnv * env, jobject javaSequentialOutStreamImpl) :
		JavaInterface(env, javaSequentialOutStreamImpl)
	{
		Init();
	}

	STDMETHOD(Write)(const void *data, UInt32 size, UInt32 *processedSize);
};


#endif /*JAVASEQUENTIALOUTSTREAMIMPL_H_*/
