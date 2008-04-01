#ifndef __JAVA_IN_STREAM_H__INCLUDED__

#include "StdAfx.h"
#include "CPPToJavaSequentialInStream.h"

class CPPToJavaInStream : public virtual IInStream, private CPPToJavaSequentialInStream
{
private:
	jmethodID _seekMethodID;

public:
	CPPToJavaInStream(JNIEnv * env, jobject inStream) :
		CPPToJavaSequentialInStream(env, inStream)
	{
		_seekMethodID = GetMethodId("seek", "(JI[J)I");
//		printf("Creating InStream\n");
//		fflush(stdout);
	}

	virtual ~CPPToJavaInStream()
	{
//        printf("Deleting InStream!\n");
//        fflush(stdout);
	}
	
	STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize)
	{
		return CPPToJavaSequentialInStream::Read(data, size, processedSize);
	}

	STDMETHOD(QueryInterface)(REFGUID refguid, void ** p)
	{
		return CPPToJavaSequentialInStream::QueryInterface(refguid, p);
	}

	STDMETHOD_(ULONG, AddRef)()
	{
		int counter = CPPToJavaSequentialInStream::AddRef();
//	    printf("CPPToJavaInStream: AddRef (changed to: %i)\n", counter);
//	    fflush(stdout);
		return counter;
	}

	STDMETHOD_(ULONG, Release)()
	{
		int counter = CPPToJavaSequentialInStream::Release();
//        rintf("CPPToJavaInStream: Release (changed to: %i)\n", counter);
//        fflush(stdout);
        return counter;
	}

	STDMETHOD(Seek)(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition);
};

#define __JAVA_IN_STREAM_H__INCLUDED__
#endif // __JAVA_IN_STREAM_H__INCLUDED__
