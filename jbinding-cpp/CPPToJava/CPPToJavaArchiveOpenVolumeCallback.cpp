#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaInStream.h"
#include "UnicodeHelper.h"

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetProperty(PROPID propID, PROPVARIANT *value) {
    TRACE_OBJECT_CALL("GetProperty");

    TRACE("GetProperty(" << propID << ')')

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (value) {
        value->vt = VT_NULL;
    }

    jobject propIDObject = jni::PropID::getPropIDByIndex(jniEnvInstance, (jint) propID);
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    jobject result = _iArchiveOpenVolumeCallback->getProperty(jniEnvInstance, _javaImplementation,
            propIDObject);
#ifdef __ANDROID_API__
    jniEnvInstance->DeleteLocalRef(propIDObject);
#endif
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    ObjectToPropVariant(jniEnvInstance, result, value);
#ifdef __ANDROID_API__
    jniEnvInstance->DeleteLocalRef(result);
#endif

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetStream(const wchar_t *name,
                                                           IInStream **inStream) {
    TRACE_OBJECT_CALL("GetStream");

    JNIEnvInstance jniEnvInstance(_jbindingSession);

    if (inStream) {
        *inStream = NULL;
    }

    jstring nameString = ToJChar(name).toNewString(jniEnvInstance);

    jobject inStreamImpl = _iArchiveOpenVolumeCallback->getStream(jniEnvInstance,
            _javaImplementation, nameString);
    if (jniEnvInstance.exceptionCheck()) {
        jniEnvInstance->DeleteLocalRef(nameString);
        return S_FALSE;
    }
    jniEnvInstance->DeleteLocalRef(nameString);

    if (inStream) {
        if (inStreamImpl) {
            CPPToJavaInStream * newInStream = new CPPToJavaInStream(_jbindingSession, jniEnvInstance,
                    inStreamImpl);
#ifdef __ANDROID_API__
            jniEnvInstance->DeleteLocalRef(inStreamImpl);
#endif

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

