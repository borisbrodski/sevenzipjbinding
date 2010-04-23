#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveUpdateCallback.h"
#include "CPPToJavaSequentialInStream.h"

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetUpdateItemInfo(UInt32 index, Int32 *newData, /*1 - new data, 0 - old data */
Int32 *newProperties, /* 1 - new properties, 0 - old properties */
UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
) {
    TRACE_OBJECT_CALL("GetUpdateItemInfo");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (newData) {
        if (_isInArchiveAttached) {
            jboolean isNewData = _iArchiveUpdateCallback.isNewData(jniEnvInstance,
                    _javaImplementation, (jint) index);
            if (jniEnvInstance.exceptionCheck()) {
                return S_FALSE;
            }
            *newData = isNewData ? 1 : 0; // TODO Check, if this really helps
        } else {
            *newData = 1;
        }
    }

    if (newProperties) {
        if (_isInArchiveAttached) {
            jboolean isNewProperties = _iArchiveUpdateCallback.isNewProperties(jniEnvInstance,
                    _javaImplementation, (jint) index);
            if (jniEnvInstance.exceptionCheck()) {
                return S_FALSE;
            }
            *newProperties = isNewProperties ? 1 : 0; // TODO Check, if this really helps
        } else {
            *newProperties = 1;
        }
    }

    if (indexInArchive) {
        if (_isInArchiveAttached) {
            *indexInArchive = (UInt32) _iArchiveUpdateCallback.getOldArchiveItemIndex(
                    jniEnvInstance, _javaImplementation, (jint) index);
            if (jniEnvInstance.exceptionCheck()) {
                return S_FALSE;
            }
        } else {
            *indexInArchive = (UInt32) -1;
        }
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetProperty(UInt32 index, PROPID propID,
                                                         PROPVARIANT *value) {
    TRACE_OBJECT_CALL("GetProperty");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!value) {
        return S_OK;
    }

    value->vt = VT_NULL;

    jobject propIDObject = jni::PropID::getPropIDByIndex(jniEnvInstance, (jint) propID);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    // public Object getProperty(int index, PropID propID);
    jobject result = _iArchiveUpdateCallback.getProperty(jniEnvInstance, _javaImplementation,
            (jint) index, propIDObject);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    ObjectToPropVariant(jniEnvInstance, result, value);

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetStream(UInt32 index, ISequentialInStream **inStream) {
    TRACE_OBJECT_CALL("GetStream");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (!inStream) {
        return S_OK;
    }

    jobject inStreamImpl = _iArchiveUpdateCallback.getStream(jniEnvInstance, _javaImplementation,
            (jint) index);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    if (inStreamImpl) {
        CPPToJavaSequentialInStream * newInStream = new CPPToJavaSequentialInStream(
                _jbindingSession, jniEnvInstance, inStreamImpl);

        CMyComPtr<ISequentialInStream> inStreamComPtr = newInStream;
        *inStream = inStreamComPtr.Detach();
    } else {
        return S_FALSE;
    }

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::SetOperationResult(Int32 operationResult) {
    TRACE_OBJECT_CALL("SetOperationResult");
    JNIEnvInstance jniEnvInstance(_jbindingSession);

    jboolean operationResultBoolean = (operationResult == NArchive::NUpdate::NOperationResult::kOK);

    _iArchiveUpdateCallback.setOperationResult(jniEnvInstance, _javaImplementation,
            operationResultBoolean);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    return S_OK;
}
