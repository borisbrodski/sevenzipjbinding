#ifndef __JAVA_OUT_STREAM_H__INCLUDED__

#include "CPPToJavaSequentialOutStream.h"

class CPPToJavaOutStream : public virtual IOutStream, public CPPToJavaSequentialOutStream
{
private:
	jmethodID _seekMethodID;
	jmethodID _setSizeMethodID;

public:
	CPPToJavaOutStream(CMyComPtr<NativeMethodContext> nativeMethodContext, JNIEnv * initEnv, jobject inStream) :
		CPPToJavaSequentialOutStream(nativeMethodContext, initEnv, inStream)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaOutStream")

		_seekMethodID = GetMethodId(initEnv, "seek", "(JI)J");
		_setSizeMethodID = GetMethodId(initEnv, "setSize", "(J)V");
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

	STDMETHOD(QueryInterface)(REFGUID iid, void ** outObject)
	{
	    if (iid == IID_IOutStream)
	    {
	        *outObject = (void *)(IOutStream *)this;
	        AddRef();
	        return S_OK;
	    }

		return CPPToJavaSequentialOutStream::QueryInterface(iid, outObject);
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

	STDMETHOD(SetSize)(Int64 newSize);
};

#define __JAVA_OUT_STREAM_H__INCLUDED__
#endif // __JAVA_OUT_STREAM_H__INCLUDED__
