#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveUpdateCallback.h"


jmethodID CPPToJavaArchiveUpdateCallback::_isNewDataID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_isNewPropertiesID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_getOldArchiveItemIndexID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_getPropertyID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_getStreamID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_setOperationResultID = NULL;

void CPPToJavaArchiveUpdateCallback::Init(JNIEnv * initEnv) {
    if (!_isNewDataID) {
		// public boolean isNewData(int index);
    	_isNewDataID = GetMethodId(initEnv, "isNewData", "(I)Z");
    }

    if (!_isNewPropertiesID) {
		// public boolean isNewProperties(int index);
    	_isNewPropertiesID = GetMethodId(initEnv, "isNewProperties", "(I)Z");
    }

    if (!_getOldArchiveItemIndexID) {
		// public int getOldArchiveItemIndex(int index);
    	_getOldArchiveItemIndexID = GetMethodId(initEnv, "getOldArchiveItemIndex", "(I)I");
    }

    if (!_getPropertyID) {
    	// public Object getProperty(int index, PropID propID);
    	_getPropertyID = GetMethodId(initEnv, "getProperty", "(I" PROPID_CLASS_T ")" JAVA_OBJECT_T);
    }

    if (!_getStreamID) {
    	// public ISequentialInStream getStream(int index);
    	_getStreamID = GetMethodId(initEnv, "getStream", "(I)" SEQUENTIALINSTREAM_CLASS_T);
    }

    if (!_setOperationResultID) {
    	// public void setOperationResult(boolean operationResultOk);
    	_setOperationResultID = GetMethodId(initEnv, "setOperationResult", "(Z)V");
    }
}


STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetUpdateItemInfo(UInt32 index,
    Int32 *newData, /*1 - new data, 0 - old data */
    Int32 *newProperties, /* 1 - new properties, 0 - old properties */
    UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
    ) {

	return S_OK; // TODO
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetProperty(UInt32 index, PROPID propID, PROPVARIANT *value) {

	return S_OK; // TODO
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetStream(UInt32 index, ISequentialInStream **inStream) {

	return S_OK; // TODO
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::SetOperationResult(Int32 operationResult) {

	return S_OK; // TODO
}
