#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaInStream.h"
#include "UnicodeHelper.h"

void CPPToJavaArchiveOpenVolumeCallback::Init(JNIEnv * initEnv) {
	TRACE_OBJECT_CALL("Init")

	// public Object getProperty(PropID propID);
	_getPropertyMethodID = GetMethodId(initEnv, "getProperty",
			"(" PROPID_CLASS_T ")" JAVA_OBJECT_T);

	// public IInStream getStream(String filename);
	_getStreamMethodID = GetMethodId(initEnv, "getStream",
			"(" JAVA_STRING_T ")" INSTREAM_CLASS_T);

	_propIDClass = GetClass(initEnv, PROPID_CLASS);

	_propIDGetPropIDByIndexMethodID = GetStaticMethodId(initEnv, _propIDClass,
			"getPropIDByIndex", "(I)" PROPID_CLASS_T);
}

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetProperty(PROPID propID,
		PROPVARIANT *value) {
	TRACE_OBJECT_CALL("GetProperty");

	TRACE1("GetProperty(%i)", propID)

	JNIInstance jniInstance(_nativeMethodContext);
	JNIEnv * env = jniInstance.GetEnv();

	jniInstance.PrepareCall();
	jobject propIDObject = env->CallStaticObjectMethod(_propIDClass,
			_propIDGetPropIDByIndexMethodID, (jint) propID);
	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	jniInstance.PrepareCall();
	jobject result = env->CallObjectMethod(_javaImplementation,
			_getPropertyMethodID, propIDObject);
	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	ObjectToPropVariant(&jniInstance, result, value);

	return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetStream(const wchar_t *name,
		IInStream **inStream) {
	TRACE_OBJECT_CALL("GetStream");

	JNIInstance jniInstance(_nativeMethodContext);
	JNIEnv * env = jniInstance.GetEnv();

	jstring nameString = env->NewString(UnicodeHelper(name), (jsize) wcslen(
			name));

	jniInstance.PrepareCall();
	jobject inStreamImpl = env->CallObjectMethod(_javaImplementation,
			_getStreamMethodID, nameString);
	if (jniInstance.IsExceptionOccurs()) {
		return S_FALSE;
	}

	if (inStream) {
		if (inStreamImpl) {
			CPPToJavaInStream * newInStream = new CPPToJavaInStream(
					_nativeMethodContext, env, inStreamImpl);
			lastVolume->AddInStream(newInStream);
			lastVolume = newInStream;

			CMyComPtr<IInStream> inStreamComPtr = newInStream;
			*inStream = inStreamComPtr.Detach();
		} else {
//			jniInstance.ThrowSevenZipException(
//					"IArchiveOpenVolumeCallback.GetStream() returns stream=null. "
//						"Use non-zero return value if requested volume doesn't exists");
			return S_FALSE;
		}
	}

	return S_OK;
}

