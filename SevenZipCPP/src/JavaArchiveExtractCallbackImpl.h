#ifndef JAVAARCHIVEEXTRACTCALLBACKIMPL_H_
#define JAVAARCHIVEEXTRACTCALLBACKIMPL_H_

#include "JavaInterface.h"
#include "JavaProgressImpl.h"

class JavaArchiveExtractCallbackImpl : public virtual IArchiveExtractCallback,
	public virtual JavaProgressImpl
{
private:
	void Init();
	jmethodID GetStreamMethodID;
	jmethodID PrepareOperationMethodID;
	jmethodID SetOperationResultMethodID;

	jclass ExtractOperationResultClass;
	jmethodID ExtractOperationResultGetOperationResultMethodID;
	
	jclass ExtractAskModeClass;
	jmethodID ExtractAskModeGetExtractAskModeByIndexMethodID;
public:
	JavaArchiveExtractCallbackImpl(JNIEnv * env,
			jobject archiveExtractCallbackImpl) :
		JavaProgressImpl(env, archiveExtractCallbackImpl)
	{
		Init();
	}

	STDMETHOD(QueryInterface)(REFGUID refguid, void ** p)
	{
		return JavaProgressImpl::QueryInterface(refguid, p);
	}

	STDMETHOD_(ULONG, AddRef)()
	{
		return JavaProgressImpl::AddRef();
	}

	STDMETHOD_(ULONG, Release)()
	{
		return JavaProgressImpl::Release();
	}
	
	STDMETHOD(SetTotal)(UInt64 total)
	{
		return JavaProgressImpl::SetTotal(total);
	}
	
	STDMETHOD(SetCompleted)(const UInt64 *completeValue)
	{
		return JavaProgressImpl::SetCompleted(completeValue);
	}
	
	STDMETHOD(GetStream)(UInt32 index, ISequentialOutStream **outStream,
			Int32 askExtractMode);
	
	// GetStream OUT: S_OK - OK, S_FALSE - skeep this file
	STDMETHOD(PrepareOperation)(Int32 askExtractMode);
	STDMETHOD(SetOperationResult)(Int32 resultEOperationResult);
};

#endif /*JAVAARCHIVEEXTRACTCALLBACKIMPL_H_*/
