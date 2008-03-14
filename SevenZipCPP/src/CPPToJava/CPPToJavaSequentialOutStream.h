#ifndef CPPTOJAVASEQUENTIALOUTSTREAM_H_
#define CPPTOJAVASEQUENTIALOUTSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"
#include "CPPToJavaAbstract.h"

class CPPToJavaSequentialOutStream : public CPPToJavaAbstract,
	public ISequentialOutStream, public CMyUnknownImp
{
private:
	jmethodID _writeMethodID;
	
public:
	MY_UNKNOWN_IMP
	
	CPPToJavaSequentialOutStream(JNIEnv * env, jobject javaSequentialOutStreamImpl) :
		CPPToJavaAbstract(env, javaSequentialOutStreamImpl)
	{
		// public int write(byte[] data);
		_writeMethodID = GetMethodId("write", "([B)I");
	}

	STDMETHOD(Write)(const void *data, UInt32 size, UInt32 *processedSize);
};


#endif /*CPPTOJAVASEQUENTIALOUTSTREAM_H_*/
