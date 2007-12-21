#ifndef JAVASEQUENTIALINSTREAM_H_
#define JAVASEQUENTIALINSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"

class JavaSequentialInStream :
	public ISequentialInStream,
	public CMyUnknownImp {
		
private:
	jobject SequentialInStream;
	jmethodID ReadMethodID;
	JNIEnv * env;
	void Init(JNIEnv * env, jobject sequentialInStream);
	
public:
	MY_UNKNOWN_IMP

	JavaSequentialInStream(JNIEnv * env, jobject sequentialInStream)
	{
		Init(env, sequentialInStream);
	}
	
	STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize);
	/*
	Out: if size != 0, return_value = S_OK and (*processedSize == 0),
	  then there are no more bytes in stream.
	if (size > 0) && there are bytes in stream, 
	this function must read at least 1 byte.
	This function is allowed to read less than number of remaining bytes in stream.
	You must call Read function in loop, if you need exact amount of data
	*/
};


#endif /*JAVASEQUENTIALINSTREAM_H_*/
