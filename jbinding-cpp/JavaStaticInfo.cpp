#include "SevenZipJBinding.h"
#include "JavaStaticInfo.h"
#include "JavaStatInfos/JavaStandardLibrary.h"
#include "JBindingTools.h"

#include <cstring>

namespace jni {

static jobject g_classLoader = NULL;
static jmethodID g_loadClassMethod = NULL;
static jclass g_threadClass = NULL;
static jmethodID g_currentThreadMethod = NULL;
static jmethodID g_getContextClassLoaderMethod = NULL;
static jmethodID g_setContextClassLoaderMethod = NULL;
static bool g_classInitializationDone = false;

void initializeClassLoader(JNIEnv * env, jclass sevenZipClass) {
    if (g_threadClass) {
        return;
    }
    jclass threadClass = env->FindClass("java/lang/Thread");
    FATALIF(!threadClass, "Can't find java.lang.Thread");
    g_currentThreadMethod = env->GetStaticMethodID(threadClass, "currentThread", "()Ljava/lang/Thread;");
    FATALIF(!g_currentThreadMethod, "Can't find Thread.currentThread()");
    g_getContextClassLoaderMethod = env->GetMethodID(threadClass, "getContextClassLoader",
            "()Ljava/lang/ClassLoader;");
    FATALIF(!g_getContextClassLoaderMethod, "Can't find Thread.getContextClassLoader()");
    g_setContextClassLoaderMethod = env->GetMethodID(threadClass, "setContextClassLoader",
            "(Ljava/lang/ClassLoader;)V");
    FATALIF(!g_setContextClassLoaderMethod, "Can't find Thread.setContextClassLoader()");
    g_threadClass = (jclass) env->NewGlobalRef(threadClass);
    env->DeleteLocalRef(threadClass);

    jclass classClass = env->GetObjectClass(sevenZipClass);
    FATALIF(!classClass, "Can't get java.lang.Class");
    jmethodID getClassLoader = env->GetMethodID(classClass, "getClassLoader", "()Ljava/lang/ClassLoader;");
    FATALIF(!getClassLoader, "Can't find Class.getClassLoader()");
    jobject classLoader = env->CallObjectMethod(sevenZipClass, getClassLoader);
    FATALIF(env->ExceptionCheck(), "Class.getClassLoader() failed");
    env->DeleteLocalRef(classClass);
    if (!classLoader) {
        // Bootstrap class loader: FindClass() will always find our classes
        TRACE("Binding loaded by the bootstrap class loader")
        return;
    }
    jclass classLoaderClass = env->GetObjectClass(classLoader);
    FATALIF(!classLoaderClass, "Can't get the class of the class loader");
    g_loadClassMethod = env->GetMethodID(classLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    FATALIF(!g_loadClassMethod, "Can't find ClassLoader.loadClass()");
    env->DeleteLocalRef(classLoaderClass);
    g_classLoader = env->NewGlobalRef(classLoader);
    env->DeleteLocalRef(classLoader);
    TRACE("Binding class loader remembered")
}

void classInitializationDone() {
    g_classInitializationDone = true;
}

#ifdef USE_MY_ASSERTS
/**
 * The native JUnit glue (NATIVE_JUNIT_TEST_SUPPORT, test/CTests) resolves its own test classes lazily on Java threads.
 * They aren't part of the binding and the test jar isn't necessarily present at initialization time.
 */
static bool isNativeTestClass(const char * name) {
    static const char prefix[] = "net/sf/sevenzipjbinding/junit/";
    return strncmp(name, prefix, sizeof(prefix) - 1) == 0;
}
#endif

jclass findClass(JNIEnv * env, const char * name) {
    // Every class is resolved by initializeJavaClasses() at initialization time. A call after that means the eager
    // list is incomplete: Release builds get by with the fallback below, Debug builds fail right here.
    MY_ASSERT(!g_classInitializationDone || isNativeTestClass(name));

    jclass clazz = env->FindClass(name);
    if (clazz) {
        return clazz;
    }
    env->ExceptionClear();
    if (!g_classLoader) {
        return NULL;
    }

    TRACE("FindClass() failed for " << name << ", asking the binding's class loader")
    size_t length = strlen(name);
    char * binaryName = (char *) malloc(length + 1);
    FATALIF(!binaryName, "Out of memory");
    for (size_t i = 0; i <= length; i++) {
        binaryName[i] = name[i] == '/' ? '.' : name[i];
    }
    jstring binaryNameString = env->NewStringUTF(binaryName);
    free(binaryName);
    if (!binaryNameString) {
        env->ExceptionClear();
        return NULL;
    }
    clazz = (jclass) env->CallObjectMethod(g_classLoader, g_loadClassMethod, binaryNameString);
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        clazz = NULL;
    }
    env->DeleteLocalRef(binaryNameString);
    return clazz;
}

jobject newContextClassLoaderGlobalRef(JNIEnv * env) {
    jobject thread = env->CallStaticObjectMethod(g_threadClass, g_currentThreadMethod);
    FATALIF(env->ExceptionCheck() || !thread, "Thread.currentThread() failed");
    jobject classLoader = env->CallObjectMethod(thread, g_getContextClassLoaderMethod);
    env->DeleteLocalRef(thread);
    if (env->ExceptionCheck()) {
        // A security manager may deny access to the loader: then 7-Zip's threads keep the JVM default
        env->ExceptionClear();
        return NULL;
    }
    if (!classLoader) {
        return NULL;
    }
    jobject result = env->NewGlobalRef(classLoader);
    env->DeleteLocalRef(classLoader);
    return result;
}

void setContextClassLoader(JNIEnv * env, jobject classLoader) {
    jobject thread = env->CallStaticObjectMethod(g_threadClass, g_currentThreadMethod);
    FATALIF(env->ExceptionCheck() || !thread, "Thread.currentThread() failed");
    env->CallVoidMethod(thread, g_setContextClassLoaderMethod, classLoader);
    if (env->ExceptionCheck()) {
        // A security manager may deny it: then the thread keeps the JVM default
        env->ExceptionClear();
    }
    env->DeleteLocalRef(thread);
}

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
