#ifndef CPPTOJAVASEQUENTIALOUTSTREAM_H_
#define CPPTOJAVASEQUENTIALOUTSTREAM_H_

#include "7zip/Archive/IArchive.h"
#include "Common/MyCom.h"
#include "CPPToJavaAbstract.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaSequentialOutStream : public CPPToJavaAbstract,
	public virtual ISequentialOutStream,
	public CMyUnknownImp
{
private:
    jni::ISequentialOutStream * _iSequentialOutStream;
public:
	MY_UNKNOWN_IMP1(ISequentialOutStream)

//    STDMETHOD_(ULONG, AddRef)()
//    {
//        TRACE2("CPPToJavaSequentialOutStream::AddRef(). Counter before: %i => %i", (int)__m_RefCount,(int)__m_RefCount + 1);
//        return ++__m_RefCount;
//    }
//    STDMETHOD_(ULONG, Release)()
//    {
//        TRACE2("CPPToJavaSequentialOutStream::Release(). Counter before: %i => %i", (int)__m_RefCount, (int)__m_RefCount - 1);
//        if (--__m_RefCount != 0)
//            return __m_RefCount; delete this; return 0;
//    }
//
//    STDMETHOD(QueryInterface)(REFGUID, void **)
//    {
//        return E_NOINTERFACE;
//    }


	CPPToJavaSequentialOutStream(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject javaSequentialOutStreamImpl) :
		CPPToJavaAbstract(jbindingSession, initEnv, javaSequentialOutStreamImpl),
		        _iSequentialOutStream(jni::ISequentialOutStream::_getInstanceFromObject(initEnv, javaSequentialOutStreamImpl))
	{
	    TRACE_OBJECT_CREATION("CPPToJavaSequentialOutStream")
	}

	STDMETHOD(Write)(const void *data, UInt32 size, UInt32 *processedSize);
};


#endif /*CPPTOJAVASEQUENTIALOUTSTREAM_H_*/
