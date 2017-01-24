#ifndef __JAVA_IN_STREAM_H__INCLUDED__

#include "CPPToJavaSequentialInStream.h"

class CPPToJavaInStream : public virtual IInStream, public CPPToJavaSequentialInStream
{
private:
    jni::ISeekableStream * _iSeekableStream;
public:
	CPPToJavaInStream(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject inStream) :
		CPPToJavaSequentialInStream(jbindingSession, initEnv, inStream),
		        _iSeekableStream(jni::ISeekableStream::_getInstanceFromObject(initEnv, inStream))
	{
	    TRACE_OBJECT_CREATION("CPPToJavaInStream")

	}

	virtual ~CPPToJavaInStream() {}

	STDMETHOD(Read)(void *data, UInt32 size, UInt32 *processedSize)
	{
		TRACE("READ(size=" << size << ")")
		HRESULT result = CPPToJavaSequentialInStream::Read(data, size, processedSize);
#ifdef TRACE_ON
		if (processedSize) {
			TRACE("READ: size=" << size << ", was read:" << *processedSize << ", result:" << result);
		}
#endif
		return result;
	}

	STDMETHOD(QueryInterface)(REFGUID iid, void ** outObject) throw()
	{
		if (iid == IID_IInStream)
	    {
	        *outObject = (void *)(IInStream *)this;
	        AddRef();
	        return S_OK;
	    }
		return CPPToJavaSequentialInStream::QueryInterface(iid, outObject);
	}

	STDMETHOD_(ULONG, AddRef)() throw()
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
