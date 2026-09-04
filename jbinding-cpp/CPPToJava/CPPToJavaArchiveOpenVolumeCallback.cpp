#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "CPPToJavaArchiveOpenVolumeCallback.h"
#include "CPPToJavaInStream.h"
#include "UnicodeHelper.h"

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetProperty(PROPID propID, PROPVARIANT *value) noexcept {
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
    if (jniEnvInstance.exceptionCheck()) {
        return S_FALSE;
    }

    ObjectToPropVariant(jniEnvInstance, result, value);

    return S_OK;
}

STDMETHODIMP CPPToJavaArchiveOpenVolumeCallback::GetStream(const wchar_t *name,
                                                           IInStream **inStream) noexcept {
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

            CMyComPtr<IInStream> inStreamComPtr = newInStream;
            *inStream = inStreamComPtr.Detach();
        } else {
            // Volume not found - report error so SevenZipException is thrown to Java
            jniEnvInstance.reportError(E_FAIL,
                    "IArchiveOpenVolumeCallback.GetStream() returns stream=null for volume '%S'. "
                    "The requested volume doesn't exist.", name);
            return E_FAIL;
        }
    }

    return S_OK;
}

