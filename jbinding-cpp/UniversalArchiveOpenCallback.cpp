#include "SevenZipJBinding.h"

#include "JNITools.h"
#include "UniversalArchiveOpenCallback.h"

void UniversalArchiveOpencallback::Init(JBindingSession & jbindingSession, JNIEnv * initEnv,
		jobject archiveOpenCallbackImpl)
{
    TRACE_OBJECT_CALL("Init")

    _jbindingSession = &jbindingSession;
    _initEnv = initEnv;

    CMyComPtr<IArchiveOpenCallback> archiveOpenCallbackComPtr = new CPPToJavaArchiveOpenCallback(jbindingSession, initEnv, archiveOpenCallbackImpl);
    _archiveOpenCallback = archiveOpenCallbackComPtr.Detach();

    _archiveOpenVolumeCallback = NULL;
    _cryptoGetTextPassword = NULL;

    _simulateArchiveOpenVolumeCallback = false;

    if (jni::ICryptoGetTextPassword::_isInstance(initEnv, archiveOpenCallbackImpl))
    {
    	TRACE("implements ICryptoGetTextPassword")
        CMyComPtr<ICryptoGetTextPassword> cryptoGetTextPasswordComPtr =
            new CPPToJavaCryptoGetTextPassword(jbindingSession, initEnv, archiveOpenCallbackImpl);
        _cryptoGetTextPassword = cryptoGetTextPasswordComPtr.Detach();
    }

    if (jni::IArchiveOpenVolumeCallback::_isInstance(initEnv, archiveOpenCallbackImpl))
    {
    	TRACE("implements IArchiveOpenVolumeCallback")
        CMyComPtr<IArchiveOpenVolumeCallback> archiveOpenVolumeCallbackComPtr =
            new CPPToJavaArchiveOpenVolumeCallback(jbindingSession, initEnv, archiveOpenCallbackImpl);
        _archiveOpenVolumeCallback = archiveOpenVolumeCallbackComPtr.Detach();
    }
}
