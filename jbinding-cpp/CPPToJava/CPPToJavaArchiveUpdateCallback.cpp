#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveUpdateCallback.h"
#include "CPPToJavaSequentialInStream.h"


jmethodID CPPToJavaArchiveUpdateCallback::_isNewDataID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_isNewPropertiesID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_getOldArchiveItemIndexID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_getPropertyID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_getStreamID = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_setOperationResultID = NULL;

jclass CPPToJavaArchiveUpdateCallback::_propIDClass = NULL;
jmethodID CPPToJavaArchiveUpdateCallback::_propIDGetPropIDByIndexMethodID = NULL;

void CPPToJavaArchiveUpdateCallback::Init(JNIEnv * initEnv, bool isInArchiveAttached) {
	this->isInArchiveAttached = isInArchiveAttached;
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

    if (!_propIDClass) {
    	_propIDClass = GetClass(initEnv, PROPID_CLASS);
    }

	if (!_propIDGetPropIDByIndexMethodID) {
		_propIDGetPropIDByIndexMethodID = GetStaticMethodId(initEnv, _propIDClass,
				"getPropIDByIndex", "(I)" PROPID_CLASS_T);
	}
}


STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetUpdateItemInfo(UInt32 index,
		Int32 *newData, /*1 - new data, 0 - old data */
		Int32 *newProperties, /* 1 - new properties, 0 - old properties */
		UInt32 *indexInArchive /* -1 if there is no in archive, or if doesn't matter */
		) {
    TRACE_OBJECT_CALL("GetUpdateItemInfo");
    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    if (newData) {
    	if (isInArchiveAttached) {
			jniInstance.PrepareCall();
			jboolean isNewData = env->CallBooleanMethod(_javaImplementation, _isNewDataID, (jint)index);
			if (jniInstance.IsExceptionOccurs())
			{
				return S_FALSE;
			}
			*newData = isNewData ? 1 : 0; // TODO Check, if this really helps
    	} else {
    		*newData = 1;
    	}
    }

    if (newProperties) {
    	if (isInArchiveAttached) {
			jniInstance.PrepareCall();
			jboolean isNewProperties = env->CallBooleanMethod(_javaImplementation, _isNewPropertiesID, (jint)index);
			if (jniInstance.IsExceptionOccurs())
			{
				return S_FALSE;
			}
			*newProperties = isNewProperties ? 1 : 0; // TODO Check, if this really helps
    	} else {
    		*newProperties = 1;
    	}
    }

    if (indexInArchive) {
    	if (isInArchiveAttached) {
			jniInstance.PrepareCall();
			*indexInArchive = (UInt32)env->CallIntMethod(_javaImplementation, _getOldArchiveItemIndexID, (jint)index);
			if (jniInstance.IsExceptionOccurs())
			{
				return S_FALSE;
			}
    	} else {
    		*indexInArchive = (UInt32)-1;
    	}
    }

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetProperty(UInt32 index, PROPID propID, PROPVARIANT *value) {
    TRACE_OBJECT_CALL("GetProperty");
    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

	if (!value) {
		return S_OK;
	}

	value->vt = VT_NULL;

	jniInstance.PrepareCall();
	jobject propIDObject = env->CallStaticObjectMethod(_propIDClass,
			_propIDGetPropIDByIndexMethodID, (jint) propID);
	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	// public Object getProperty(int index, PropID propID);
	jniInstance.PrepareCall();
	jobject result = env->CallObjectMethod(_javaImplementation,
			_getPropertyID, (jint)index, propIDObject);
	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	ObjectToPropVariant(&jniInstance, result, value);

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::GetStream(UInt32 index, ISequentialInStream **inStream) {
    TRACE_OBJECT_CALL("GetStream");
    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    if (!inStream) {
    	return S_OK;
    }

	jniInstance.PrepareCall();
	jobject inStreamImpl = env->CallObjectMethod(_javaImplementation,
			_getStreamID, (jint)index);
	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	if (inStreamImpl) {
		CPPToJavaSequentialInStream * newInStream = new CPPToJavaSequentialInStream(
				_nativeMethodContext, env, inStreamImpl);

		CMyComPtr<ISequentialInStream> inStreamComPtr = newInStream;
		*inStream = inStreamComPtr.Detach();
	} else {
		return S_FALSE;
	}

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveUpdateCallback::SetOperationResult(Int32 operationResult) {
    TRACE_OBJECT_CALL("SetOperationResult");
    JNIInstance jniInstance(_nativeMethodContext);
    JNIEnv * env = jniInstance.GetEnv();

    jboolean operationResultBoolean = (operationResult == NArchive::NUpdate::NOperationResult::kOK);

	jniInstance.PrepareCall();
	env->CallVoidMethod(_javaImplementation, _setOperationResultID, operationResultBoolean);
	if (jniInstance.IsExceptionOccurs())
	{
		return S_FALSE;
	}

	return S_OK;
}
