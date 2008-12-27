#ifndef CPPTOJAVASEQUENTIALINSTREAM_H_
#define CPPTOJAVASEQUENTIALINSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"

#include "CPPToJavaAbstract.h"

class CPPToJavaSequentialInStream :
	public CPPToJavaAbstract, //
	public virtual ISequentialInStream, //
	public CMyUnknownImp {
		
private:
	jmethodID _readMethodID;

public:
	MY_UNKNOWN_IMP

	CPPToJavaSequentialInStream(CMyComPtr<JNICallState> jniCallState, JNIEnv * initEnv, jobject sequentialInStream)
		: CPPToJavaAbstract(jniCallState, initEnv, sequentialInStream)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaSequentialInStream")
	    
		_readMethodID = GetMethodId(initEnv, "read", "([B[I)I");
		classname = "CPPToJavaSequentialInStream";
	}
	
	/*
	 * FROM 7-ZIP:
	 * Out: if size != 0, return_value = S_OK and (*processedSize == 0),
	 * then there are no more bytes in stream.
	 * if (size > 0) && there are bytes in stream, 
	 * this function must read at least 1 byte.
	 * This function is allowed to read less than number of remaining bytes in stream.
	 * You must call Read function in loop, if you need exact amount of data
	 */
	STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize);
};


#endif /*CPPTOJAVASEQUENTIALINSTREAM_H_*/
