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
		return CPPToJavaSequentialInStream::AddRef();
	}

	STDMETHOD_(ULONG, Release)()
	{
		return CPPToJavaSequentialInStream::Release();
	}

	STDMETHOD(Seek)(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition);
};

#define __JAVA_IN_STREAM_H__INCLUDED__
#endif // __JAVA_IN_STREAM_H__INCLUDED__
