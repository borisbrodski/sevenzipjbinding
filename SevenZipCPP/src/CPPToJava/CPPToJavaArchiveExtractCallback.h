#ifndef CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_
#define CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_

#include "CPPToJavaProgress.h"
#include "CPPToJavaCryptoGetTextPassword.h"

class CPPToJavaArchiveExtractCallback : public virtual IArchiveExtractCallback,
    public virtual ICryptoGetTextPassword,
	public virtual CPPToJavaProgress
{
private:
    ICryptoGetTextPassword * _cryptoGetTextPasswordImpl;
    
	jmethodID _getStreamMethodID;
	jmethodID _prepareOperationMethodID;
	jmethodID _setOperationResultMethodID;

	jclass _extractOperationResultClass;
	jmethodID _extractOperationResultGetOperationResultMethodID;
	
	jclass _extractAskModeClass;
	jmethodID _extractAskModeGetExtractAskModeByIndexMethodID;
	
	void Init(JNIEnv * initEnv);
	
public:
	CPPToJavaArchiveExtractCallback(CMyComPtr<JNICallState> jniCallState, JNIEnv * initEnv,
			jobject archiveExtractCallbackImpl) :
				CPPToJavaProgress(jniCallState, initEnv, archiveExtractCallbackImpl)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaArchiveExtractCallback")
	    
		Init(initEnv);
		classname = "CPPToJavaArchiveExtractCallback";
	}

	~CPPToJavaArchiveExtractCallback()
	{
	    JNIEnv * env = BeginCPPToJavaCall();
	    
		env->DeleteGlobalRef(_extractOperationResultClass);
		env->DeleteGlobalRef(_extractAskModeClass);
		if (_cryptoGetTextPasswordImpl)
		{
		    _cryptoGetTextPasswordImpl->Release();
		}
		
		EndCPPToJavaCall();
	}
	
	STDMETHOD(QueryInterface)(REFGUID refguid, void ** p)
	{
	    TRACE_OBJECT_CALL("QueryInterface")
	    
	    if (refguid == IID_ICryptoGetTextPassword && _cryptoGetTextPasswordImpl)
	    {
	        *p = (void *)(ICryptoGetTextPassword *)_cryptoGetTextPasswordImpl;
	        _cryptoGetTextPasswordImpl->AddRef();
	        return S_OK;
	    }

		return CPPToJavaProgress::QueryInterface(refguid, p);
	}

	STDMETHOD_(ULONG, AddRef)()
	{
	    TRACE_OBJECT_CALL("AddRef")
		return CPPToJavaProgress::AddRef();
	}

	STDMETHOD_(ULONG, Release)()
	{
	    TRACE_OBJECT_CALL("Release")
		return CPPToJavaProgress::Release();
	}
	
	STDMETHOD(SetTotal)(UInt64 total)
	{
		return CPPToJavaProgress::SetTotal(total);
	}
	
	STDMETHOD(SetCompleted)(const UInt64 *completeValue)
    {
        return CPPToJavaProgress::SetCompleted(completeValue);
    }
	
	STDMETHOD(CryptoGetTextPassword)(BSTR *password)
	{
	    TRACE_OBJECT_CALL("CryptoGetTextPassword")
	    
	    if (_cryptoGetTextPasswordImpl)
	    {
	        return _cryptoGetTextPasswordImpl->CryptoGetTextPassword(password);
	    }
	    
		return E_NOINTERFACE;
	}
	
	
	STDMETHOD(GetStream)(UInt32 index, ISequentialOutStream **outStream,
			Int32 askExtractMode);
	
	/*
	 * FROM 7-ZIP:
	 * 
	 * GetStream OUT: S_OK - OK, S_FALSE - skeep this file
	 * 
	 */
	STDMETHOD(PrepareOperation)(Int32 askExtractMode);
	STDMETHOD(SetOperationResult)(Int32 resultEOperationResult);
};

#endif /*CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_*/
