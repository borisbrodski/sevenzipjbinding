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
            // Volume not found. Handlers probe for the next volume (e.g. the split handler asks for
            // "x.7z.005" after the last one) and expect S_FALSE, as returned by 7-Zip's own clients.
            return S_FALSE;
        }
    }

    return S_OK;
}

