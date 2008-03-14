#ifndef CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_
#define CPPTOJAVAARCHIVEEXTRACTCALLBACK_H_

#include "CPPToJavaProgress.h"

class CPPToJavaArchiveExtractCallback : public virtual IArchiveExtractCallback,
	public virtual CPPToJavaProgress
{
private:
	jmethodID _getStreamMethodID;
	jmethodID _prepareOperationMethodID;
	jmethodID _setOperationResultMethodID;

	jclass _extractOperationResultClass;
	jmethodID _extractOperationResultGetOperationResultMethodID;
	
	jclass _extractAskModeClass;
	jmethodID _extractAskModeGetExtractAskModeByIndexMethodID;
	
	void Init();
	
public:
	CPPToJavaArchiveExtractCallback(JNIEnv * env,
			jobject archiveExtractCallbackImpl) :
				CPPToJavaProgress(env, archiveExtractCallbackImpl)
	{
		Init();
	}

	~CPPToJavaArchiveExtractCallback()
	{
		_env->DeleteGlobalRef(_extractOperationResultClass);
		_env->DeleteGlobalRef(_extractAskModeClass);
	}
	
	STDMETHOD(QueryInterface)(REFGUID refguid, void ** p)
	{
		return CPPToJavaProgress::QueryInterface(refguid, p);
	}

	STDMETHOD_(ULONG, AddRef)()
	{
		return CPPToJavaProgress::AddRef();
	}

	STDMETHOD_(ULONG, Release)()
	{
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
