#include "SevenZipJBinding.h"
#include "JavaStaticInfo.h"
#include "JavaStatInfos/JavaStandardLibrary.h"

#ifdef ANDROID_NDK
static jclass g_SevenZipClass;
static jmethodID g_FindClassMethodID;

void InitFindClass(jclass sevenZipClass, jmethodID findClassMethodID) {
    g_SevenZipClass = sevenZipClass;
    g_FindClassMethodID = findClassMethodID;
}
#endif

jclass FindClass(JNIEnv *env, const char *className) {
#ifdef ANDROID_NDK
    return static_cast<jclass>(env->CallStaticObjectMethod(g_SevenZipClass, g_FindClassMethodID, env->NewStringUTF(className)));
#else
    return env->FindClass(className);
#endif
}

namespace jni {

void JMethod::initMethodIDIfNecessary(JNIEnv * env, jclass jclazz) {
	if (isInitialized) {
		return;
	}

	_initCriticalSection.Enter();

	if (isInitialized) {
		return;
	}

	initMethodID(env, jclazz);
	isInitialized = true;

	_initCriticalSection.Leave();

}

void JMethod::initMethodID(JNIEnv * env, jclass jclazz) {
	TRACE("Getting method id for " << *this);
	if (_isStatic) {
		_jmethodID = env->GetStaticMethodID(jclazz, _name, _signature);
	} else {
		_jmethodID = env->GetMethodID(jclazz, _name, _signature);
	}

	if (env->ExceptionOccurred()) {
		jthrowable exception = env->ExceptionOccurred();
		env->ExceptionClear();
		if (jni::NoSuchMethodError::_isInstance(env, exception)) {
			return;
		}
		if (jni::OutOfMemoryError::_isInstance(env, exception)) {
			FATAL("Out of memory during method lookup: '%s', '%s'", _name, _signature); // TODO Change fatal => exception (+test)
		}
		if (jni::ExceptionInInitializerError::_isInstance(env, exception)) {
			FATAL("Exception in initializer during method lookup: '%s', '%s'", _name, _signature); // TODO Change fatal => exception (+test)
		}
		FATAL("Unknown exception: '%s', '%s'", _name, _signature);
	}
}

}
