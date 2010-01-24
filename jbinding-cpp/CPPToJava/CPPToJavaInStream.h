#ifndef __JAVA_IN_STREAM_H__INCLUDED__

#include "CPPToJavaSequentialInStream.h"

class CPPToJavaInStream : public virtual IInStream, public CPPToJavaSequentialInStream
{
private:
	jmethodID _seekMethodID;
	CPPToJavaInStream * _nextInStream;
	CPPToJavaInStream * _previousInStream;

public:
	CPPToJavaInStream(CMyComPtr<NativeMethodContext> nativeMethodContext, JNIEnv * initEnv, jobject inStream) :
		CPPToJavaSequentialInStream(nativeMethodContext, initEnv, inStream)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaInStream")

		_seekMethodID = GetMethodId(initEnv, "seek", "(JI)J");
		classname = "CPPToJavaInStream";
		_nextInStream = NULL;
		_previousInStream = NULL;
	}

	~CPPToJavaInStream() {
		RemoveItself();
	}

	void AddInStream(CPPToJavaInStream * inStream) {
		if (this->_nextInStream) {
			this->_nextInStream->_previousInStream = inStream;
		}
		inStream->_nextInStream = this->_nextInStream;
		inStream->_previousInStream = this;
		this->_nextInStream = inStream;
	}

	void RemoveItself() {
		if (this->_nextInStream) {
			this->_nextInStream->_previousInStream = this->_previousInStream;
		}
		if (this->_previousInStream) {
			this->_previousInStream->_nextInStream = this->_nextInStream;
		}
	}

    virtual void ClearNativeMethodContext()
    {
	    TRACE_OBJECT_CALL("ClearNativeMethodContext");
	    CPPToJavaAbstract::ClearNativeMethodContext();
	    if (_nextInStream) {
	    	_nextInStream->ClearNativeMethodContext();
	    }
    }

    virtual void SetNativMethodContext(CMyComPtr<NativeMethodContext> nativeMethodContext)
    {
	    TRACE_OBJECT_CALL("SetNativMethodContext");
	    CPPToJavaAbstract::SetNativMethodContext(nativeMethodContext);
	    if (_nextInStream) {
	    	_nextInStream->SetNativMethodContext(nativeMethodContext);
	    }

    }


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

	STDMETHOD(QueryInterface)(REFGUID iid, void ** outObject)
	{
		if (iid == IID_IInStream)
	    {
	        *outObject = (void *)(IInStream *)this;
	        AddRef();
	        return S_OK;
	    }
		return CPPToJavaSequentialInStream::QueryInterface(iid, outObject);
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
