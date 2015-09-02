#include "SevenZipJBinding.h"
#include "JavaStaticInfo.h"
#include "JavaStatInfos/JavaStandardLibrary.h"

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
