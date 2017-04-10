#ifndef CPPTOJAVASEQUENTIALINSTREAM_H_
#define CPPTOJAVASEQUENTIALINSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"

#include "CPPToJavaAbstract.h"

#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaSequentialInStream :
	public CPPToJavaAbstract, //
	public virtual ISequentialInStream, //
	public CMyUnknownImp {

	jni::ISequentialInStream * _iSequentialInStream;
public:
	MY_UNKNOWN_IMP1(ISequentialInStream)

	CPPToJavaSequentialInStream(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject sequentialInStream)
		: CPPToJavaAbstract(jbindingSession, initEnv, sequentialInStream),
		  _iSequentialInStream(jni::ISequentialInStream::_getInstanceFromObject(initEnv, sequentialInStream))
	{
	    TRACE_OBJECT_CREATION("CPPToJavaSequentialInStream")
	}

	virtual ~CPPToJavaSequentialInStream() {}

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
