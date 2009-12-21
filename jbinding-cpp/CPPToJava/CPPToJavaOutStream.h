#ifndef __JAVA_OUT_STREAM_H__INCLUDED__

#include "CPPToJavaSequentialOutStream.h"

class CPPToJavaOutStream : public virtual IOutStream, public CPPToJavaSequentialOutStream
{
private:
	jmethodID _seekMethodID;

public:
	CPPToJavaOutStream(CMyComPtr<NativeMethodContext> nativeMethodContext, JNIEnv * initEnv, jobject inStream) :
		CPPToJavaSequentialOutStream(nativeMethodContext, initEnv, inStream)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaOutStream")

		_seekMethodID = GetMethodId(initEnv, "seek", "(JI)J");
		classname = "CPPToJavaOutStream";
	}

	STDMETHOD(Write)(const void *data, UInt32 size, UInt32 *processedSize)
	{
		TRACE1("WRITE(size=%i)", (int)size)
		HRESULT result = CPPToJavaSequentialOutStream::Write(data, size, processedSize);
#ifdef TRACE_ON
		if (processedSize) {
			TRACE3("WRITE: size=%i, was written:%i, result:%i", (int)size, (int)*processedSize, (int)result);
		}
#endif
		return result;
	}

	STDMETHOD(QueryInterface)(REFGUID refguid, void ** p)
	{
		return CPPToJavaSequentialOutStream::QueryInterface(refguid, p);
	}

	STDMETHOD_(ULONG, AddRef)()
	{
		return CPPToJavaSequentialOutStream::AddRef();
	}

	STDMETHOD_(ULONG, Release)()
	{
        return CPPToJavaSequentialOutStream::Release();
	}

	STDMETHOD(Seek)(Int64 offset, UInt32 seekOrigin, UInt64 *newPosition);

};

#define __JAVA_OUT_STREAM_H__INCLUDED__
#endif // __JAVA_OUT_STREAM_H__INCLUDED__
