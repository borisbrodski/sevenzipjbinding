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
		TRACE("WRITE(size=" << size << ")")
		HRESULT result = CPPToJavaSequentialOutStream::Write(data, size, processedSize);
#ifdef TRACE_ON
		if (processedSize) {
			TRACE("WRITE: size=" << size << ", was written:" << *processedSize << ", result:" << result);
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
