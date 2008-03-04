#ifndef __JAVA_IN_STREAM_H__INCLUDED__

#include "JavaSequentialInStream.h"

class JavaInStream : public virtual IInStream, private JavaSequentialInStream
{
private:
	void Init();
	jmethodID SeekMethodID;

public:
	JavaInStream(JNIEnv * env, jobject inStream) :
		JavaSequentialInStream(env, inStream)
	{
		Init();
	}

	STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize)
	{
		return JavaSequentialInStream::Read(data, size, processedSize);
	}

	STDMETHOD(QueryInterface)(REFGUID refguid, void ** p)
	{
		return JavaSequentialInStream::QueryInterface(refguid, p);
	}

	STDMETHOD_(ULONG, AddRef)()
	{
		return JavaSequentialInStream::AddRef();
	}

	STDMETHOD_(ULONG, Release)()
	{
		return JavaSequentialInStream::Release();
	}

	STDMETHOD(Seek)(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition);
};

#define __JAVA_IN_STREAM_H__INCLUDED__
#endif // __JAVA_IN_STREAM_H__INCLUDED__
