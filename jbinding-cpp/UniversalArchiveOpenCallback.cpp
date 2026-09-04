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

    jclass cryptoGetTextPasswordClass = initEnv->FindClass(CRYPTOGETTEXTPASSWORD_CLASS);
    FATALIF(cryptoGetTextPasswordClass == NULL,
            "Can't find class " CRYPTOGETTEXTPASSWORD_CLASS);

    jclass archiveOpenVolumeCallbackClass = initEnv->FindClass(ARCHIVEOPENVOLUMECALLBACK_CLASS);
    FATALIF(archiveOpenVolumeCallbackClass == NULL,
            "Can't find class " ARCHIVEOPENVOLUMECALLBACK_CLASS);

    if (initEnv->IsInstanceOf(archiveOpenCallbackImpl, cryptoGetTextPasswordClass))
    {
    	TRACE("implements ICryptoGetTextPassword")
        CMyComPtr<ICryptoGetTextPassword> cryptoGetTextPasswordComPtr =
            new CPPToJavaCryptoGetTextPassword(jbindingSession, initEnv, archiveOpenCallbackImpl);
        _cryptoGetTextPassword = cryptoGetTextPasswordComPtr.Detach();
    }

    if (initEnv->IsInstanceOf(archiveOpenCallbackImpl, archiveOpenVolumeCallbackClass))
    {
    	TRACE("implements IArchiveOpenVolumeCallback")
        CMyComPtr<IArchiveOpenVolumeCallback> archiveOpenVolumeCallbackComPtr =
            new CPPToJavaArchiveOpenVolumeCallback(jbindingSession, initEnv, archiveOpenCallbackImpl);
        _archiveOpenVolumeCallback = archiveOpenVolumeCallbackComPtr.Detach();
    }
}
