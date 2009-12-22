#ifndef CPPTOJAVAARCHIVEUPDATECALLBACK_H_
#define CPPTOJAVAARCHIVEUPDATECALLBACK_H_

#include "CPPToJavaAbstract.h"
#include "CPPToJavaProgress.h"

class CPPToJavaArchiveUpdateCallback :
					public virtual IArchiveUpdateCallback,
					public virtual CPPToJavaProgress
{

private:
    static jmethodID _isNewDataID;
    static jmethodID _isNewPropertiesID;
    static jmethodID _getOldArchiveItemIndexID;
    static jmethodID _getPropertyID;
    static jmethodID _getStreamID;
    static jmethodID _setOperationResultID;

    void Init(JNIEnv * initEnv);

public:
    CPPToJavaArchiveUpdateCallback(CMyComPtr<NativeMethodContext> nativeMethodContext, JNIEnv * initEnv, jobject archiveUpdateCallback) :
		CPPToJavaProgress(nativeMethodContext, initEnv, archiveUpdateCallback)
    {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenCallback")

		Init(initEnv);
        classname = "CPPToJavaArchiveOpenCallback";
    }


    STDMETHOD(GetUpdateItemInfo)(UInt32 index,
        Int32 *newData, /*1 - new data, 0 - old data */
        Int32 *newProperties, /* 1 - new properties, 0 - old properties */
        UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
        );

    STDMETHOD(GetProperty)(UInt32 index, PROPID propID, PROPVARIANT *value);
    STDMETHOD(GetStream)(UInt32 index, ISequentialInStream **inStream);
    STDMETHOD(SetOperationResult)(Int32 operationResult);

	STDMETHOD(SetTotal)(UInt64 total)
	{
	    TRACE_OBJECT_CALL("SetTotal");
		return CPPToJavaProgress::SetTotal(total);
	}

	STDMETHOD(SetCompleted)(const UInt64 *completeValue)
    {
	    TRACE_OBJECT_CALL("SetCompleted");
        return CPPToJavaProgress::SetCompleted(completeValue);
    }

};

#endif /*CPPTOJAVAARCHIVEUPDATECALLBACK_H_*/
