#ifndef CPPTOJAVAARCHIVEUPDATECALLBACK_H_
#define CPPTOJAVAARCHIVEUPDATECALLBACK_H_

#include "CPPToJavaAbstract.h"
#include "CPPToJavaProgress.h"

class CPPToJavaArchiveUpdateCallback : public virtual IArchiveUpdateCallback,
        public CPPToJavaProgress {

private:
    jni::IArchiveUpdateCallback _iArchiveUpdateCallback;
    bool _isInArchiveAttached;

public:
    CPPToJavaArchiveUpdateCallback(JBindingSession & jbindingSession, JNIEnv * initEnv,
                                   jobject archiveUpdateCallback, bool isInArchiveAttached) :
        CPPToJavaProgress(jbindingSession, initEnv, archiveUpdateCallback),
                _iArchiveUpdateCallback(jni::IArchiveUpdateCallback::_getInstanceFromObject(
                        initEnv, archiveUpdateCallback)) {
        TRACE_OBJECT_CREATION("CPPToJavaArchiveOpenCallback")

        _isInArchiveAttached = isInArchiveAttached;
    }

    STDMETHOD(GetUpdateItemInfo)(UInt32 index, Int32 *newData, /*1 - new data, 0 - old data */
    Int32 *newProperties, /* 1 - new properties, 0 - old properties */
    UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
    );

    STDMETHOD(GetProperty)(UInt32 index, PROPID propID, PROPVARIANT *value);
    STDMETHOD(GetStream)(UInt32 index, ISequentialInStream **inStream);
    STDMETHOD(SetOperationResult)(Int32 operationResult);

    STDMETHOD(SetTotal)(UInt64 total) {
        TRACE_OBJECT_CALL("SetTotal");
        return CPPToJavaProgress::SetTotal(total);
    }

    STDMETHOD(SetCompleted)(const UInt64 *completeValue) {
        TRACE_OBJECT_CALL("SetCompleted");
        return CPPToJavaProgress::SetCompleted(completeValue);
    }

    STDMETHOD(QueryInterface)(REFGUID refguid, void ** p) {
        TRACE_OBJECT_CALL("QueryInterface");

        return CPPToJavaProgress::QueryInterface(refguid, p);
    }

    STDMETHOD_(ULONG, AddRef)() {
        TRACE_OBJECT_CALL("AddRef");
        return CPPToJavaProgress::AddRef();
    }

    STDMETHOD_(ULONG, Release)() {
        TRACE_OBJECT_CALL("Release");
        return CPPToJavaProgress::Release();
    }

};

#endif /*CPPTOJAVAARCHIVEUPDATECALLBACK_H_*/
