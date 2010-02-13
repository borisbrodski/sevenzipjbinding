/*
 * JBindingTools.h
 *
 *  Created on: Jan 29, 2010
 *      Author: boris
 */

#ifndef JBINDINGTOOLS_H_
#define JBINDINGTOOLS_H_

#include "Debug.h"
#include "BaseSystem.h"
#include "JObjectList.h"

/*
 TODO
 BEGIN_JCLASS("net/sf/sevenzipjbinding", SevenZipException)
 JCLASS_VIRTUAL_METHOD(Object, initCause, "(Ljava/lang/Throwable;)Ljava/lang/Throwable;")
 END_JCLASS
 */

/*
 *    +--------------------------------------------------------------
 *    |
 *    |  +-----------------+
 *    |  | JBindingSession |
 *    |  +-----------------+
 *    |
 *    |
 *             /-+-----------------\
 * -JNICall1-> | JavaNativeContext |
 *             \-+-----------------/
 *               |
 *               +-----------------------------------------------> 7-Zip
 *               |                                                   |
 *               |                                                   |
 *               +<--------------------------Callback (Thread 1)-----+
 *               +---------------------------return thread 1-------->+
 *               |                                                   |
 *               |                                                   |
 *               |<--------------------------Callback (Thread 2)-----+
 *               |                                                   |
 * <-JNICallback2+                                                   |
 *               |       /-------------------\                       |
 * --JNICall3----------> | JavaNativeContext |                       |
 *               |       \-+-----------------/                       |
 *               |         |                                         |
 * <--return3--------------+                                         |
 *               |                                                   |
 * ---return2--->+                                                   |
 *               +----------------------------return thread 2------->+
 *               |                                                   |
 *               +----------------------------return-----------------/
 * <--return1----+
 */

class JNINativeCallContext;
class JBindingSession;
class JNIEnvInstance;

template<typename T>
struct CMyComPtrWrapper {
    CMyComPtr<T> _ptr;
    CMyComPtrWrapper() {
    }
    CMyComPtrWrapper(T * ptr) :
        _ptr(ptr) {
    }

    T* operator->() const {
        return (T*) _ptr;
    }
};

class ThreadContext {
public:
    JNIEnv * _env;
    int _attachedThreadCount;
    std::list<JNINativeCallContext*> _javaNativeContext;

    ThreadContext() :
        _attachedThreadCount(-1), _env(NULL) {
    }
};

/*
 * Represents a single session of 7-Zip-JBinding.
 * For example OpenArchive->QueryArchiveItems->ExtractItems->CloseArchive.
 */
class JBindingSession {
    friend class JNINativeCallContext;
    typedef std::map<ThreadId, ThreadContext> ThreadContextMap;

    std::list<CMyComPtrWrapper<IUnknown> > _objectList;
    std::list<JNINativeCallContext> _jniNativeCallContextList;
    ThreadContextMap _threadContextMap;
    PlatformCriticalSection _threadContextMapCriticalSection;
    static JavaVM * _vm;

#ifdef USE_MY_ASSERTS
public:
    static int _attachedThreadCount;
private:
#endif

    void registerNativeContext(JNIEnv * initEnv, JNINativeCallContext * jniNativeCallContext) {
        ThreadId threadId = PlatformGetCurrentThreadId();
        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[threadId];
        _threadContextMapCriticalSection.Leave();
        threadContext._env = initEnv;
        TRACE("JNINativeCallContext=" << jniNativeCallContext)
        threadContext._javaNativeContext.push_front(jniNativeCallContext);
    }

    void unregisterNativeContext(JNINativeCallContext & javaNativeContext) {
        ThreadId threadId = PlatformGetCurrentThreadId();

        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[threadId];
        MY_ASSERT(*(threadContext._javaNativeContext.begin()) == &javaNativeContext);

        threadContext._javaNativeContext.pop_front();
        if (threadContext._javaNativeContext.size() == 0) {
            _threadContextMap.erase(threadId);
        }
        _threadContextMapCriticalSection.Leave();
    }

    /*
    JNIEnv * getJNIEnv(JNINativeCallContext & javaNativeContext) {
        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
        _threadContextMapCriticalSection.Leave();
        if (threadContext._env) {
            return threadContext._env;
        }

        TRACE("Attaching current thread to VM.")
#ifdef USE_MY_ASSERTS
        _attachedVmCount++;
#endif
        JNIEnv * env = NULL;
        jint result;
        if ((result = _vm->AttachCurrentThread((void**) &env, NULL)) || env == NULL) {
            TRACE("New thread couldn't be attached: " << result)
            // throw SevenZipException("Can't attach current thread (id: %i) to the VM", currentThreadId);
            // TODO Decide, what to do this it
            fatal("Can't attach current thread (id: %i) to the VM", PlatformGetCurrentThreadId());
        }
        TRACE("Thread attached. New env=" << (void *)env);
    }
    */

    void handleThrownException(jthrowable exceptionLocalRef);

public:
    JBindingSession(JNIEnv * initEnv) {
        if (!_vm && initEnv->GetJavaVM(&_vm)) {
            fatal("Can't get JavaVM from JNIEnv");
        }
        MY_ASSERT(_vm);
    }

    JNIEnv * beginCallback(JNINativeCallContext ** jniNativeCallContext) {
        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
        _threadContextMapCriticalSection.Leave();
        if (!threadContext._env) {
            // Attach new thread
            threadContext._attachedThreadCount = 0;

            TRACE("Attaching current thread to VM.")
#ifdef USE_MY_ASSERTS
            _attachedThreadCount++;
#endif
            jint result;
            if ((result = _vm->AttachCurrentThread((void**) &threadContext._env, NULL))
                    || threadContext._env == NULL) {
                TRACE("New thread couldn't be attached: " << result)
                // throw SevenZipException("Can't attach current thread (id: %i) to the VM", currentThreadId);
                // TODO Decide, what to do this it
                fatal("Can't attach current thread (id: %i) to the VM",
                        PlatformGetCurrentThreadId());
            }
            TRACE("Thread attached. New env=" << (void *)threadContext._env);
        }
        if (threadContext._javaNativeContext.size()) {
            *jniNativeCallContext = *threadContext._javaNativeContext.begin();
        }
        threadContext._attachedThreadCount++;
        return threadContext._env;
    }

    void endCallback() {
        ThreadId threadId = PlatformGetCurrentThreadId();

        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[threadId];
        if (!--threadContext._attachedThreadCount) {
#ifdef USE_MY_ASSERTS
            _attachedThreadCount--;
#endif
            _vm->DetachCurrentThread();
            _threadContextMap.erase(threadId);
        }
        _threadContextMapCriticalSection.Leave();
    }

    bool exceptionCheck(JNIEnv * env) {
        jni::prepareExceptionCheck(env);
        jthrowable exceptionLocalRef = env->ExceptionOccurred();
        if (exceptionLocalRef) {
            env->ExceptionClear();
            handleThrownException(exceptionLocalRef);
            env->DeleteLocalRef(exceptionLocalRef);
            return true;
        }
        return false;
    }

    void addObject(IUnknown * object) {
        _objectList.push_back(CMyComPtrWrapper<IUnknown> (object));
    }

    void closeSession(JNIEnv * initEnv) {
        _objectList.clear();
    }

    ~JBindingSession() {
        MY_ASSERT(_objectList.size() == 0);
        MY_ASSERT(_threadContextMap.size() == 0);
        // TODO Check _threadContextMap
    }
};

class JNINativeCallContext {
    friend class JBindingSession;
    friend class JNIEnvInstance;

    jthrowable _firstThrownException;
    jthrowable _lastThrownException;
    jthrowable _firstThrownExceptionInOtherThread;
    jthrowable _lastThrownExceptionInOtherThread;

    JBindingSession & _jbindingSession;
    JNIEnv * _jniCallOriginalEnv;

    JNINativeCallContext(JNINativeCallContext const &);
    void * operator new(size_t i);

    void exceptionThrown(JNIEnv * env, jthrowable throwableLocalRef) {
        jthrowable throwableGlobalRef = static_cast<jthrowable> (env->NewGlobalRef(
                throwableLocalRef));
        if (!_firstThrownException) {
            _firstThrownException = throwableGlobalRef;
        } else {
            if (_lastThrownException) {
                env->DeleteGlobalRef(_lastThrownException);
            }
            _lastThrownException = throwableGlobalRef;
        }
    }
    void exceptionThrownInOtherThread(JNIEnv * env, jthrowable throwableLocalRef) {
        jthrowable throwableGlobalRef = static_cast<jthrowable> (env->NewGlobalRef(
                throwableLocalRef));
        if (!_firstThrownExceptionInOtherThread) {
            _firstThrownExceptionInOtherThread = throwableGlobalRef;
        } else {
            if (_lastThrownExceptionInOtherThread) {
                env->DeleteGlobalRef(_lastThrownException);
            }
            _lastThrownExceptionInOtherThread = throwableGlobalRef;
        }
    }
public:

    JNINativeCallContext(JBindingSession & _jbindingSession, JNIEnv * initEnv) :
        _jbindingSession(_jbindingSession), _jniCallOriginalEnv(initEnv),
        _firstThrownException(NULL), _lastThrownException(NULL),
        _firstThrownExceptionInOtherThread(NULL), _lastThrownExceptionInOtherThread(NULL) {
        _jbindingSession.registerNativeContext(initEnv, this);
    }
    ~JNINativeCallContext() {
        _jbindingSession.unregisterNativeContext(*this);
        if (_firstThrownException) {
            _jniCallOriginalEnv->DeleteGlobalRef(_firstThrownException);
        }
        if (_lastThrownException) {
            _jniCallOriginalEnv->DeleteGlobalRef(_lastThrownException);
        }
        if (_firstThrownExceptionInOtherThread) {
            _jniCallOriginalEnv->DeleteGlobalRef(_firstThrownExceptionInOtherThread);
        }
        if (_lastThrownExceptionInOtherThread) {
            _jniCallOriginalEnv->DeleteGlobalRef(_lastThrownExceptionInOtherThread);
        }

        // TODO Throw exception
    }

    bool exceptionCheck(JNIEnv * env) {
        jni::prepareExceptionCheck(env);

        jthrowable exceptionLocalRef = env->ExceptionOccurred();
        if (exceptionLocalRef) {
            env->ExceptionClear();
            exceptionThrown(env, exceptionLocalRef);
            env->DeleteLocalRef(exceptionLocalRef);
            return true;
        }
        return false;
    }

    void endJNICall(JNIEnv * initEnv) {
        if (_firstThrownException) {
            // Throw Exceptions
            // - throw SevenZipException directly
            // - throw all other exceptions wrapped in SevenZipException
            // - set lastThrownException
        }

        if (_firstThrownException) {
            initEnv->DeleteGlobalRef(_firstThrownException);
        }
        if (_lastThrownException) {
            initEnv->DeleteGlobalRef(_lastThrownException);
        }
    }
};

class JNIEnvInstance {
    JBindingSession & _jbindingSession;
    JNINativeCallContext * _jniNativeCallContext;
    JNIEnv * _env;
    bool _isCallback;

    void * operator new(size_t i);

    void initCallback() {
        MY_ASSERT(_isCallback);

        _env = _jbindingSession.beginCallback(&_jniNativeCallContext);
        MY_ASSERT(_env);
    }
public:
    JNIEnvInstance(JBindingSession & jbindingSession, JNINativeCallContext & jniNativeCallContext,
            JNIEnv * env) :
        _env(env), _jniNativeCallContext(&jniNativeCallContext), _jbindingSession(jbindingSession), _isCallback(false) {
        MY_ASSERT(env);
    }
    JNIEnvInstance(JBindingSession & jbindingSession) :
        _env(NULL), _jniNativeCallContext(NULL), _jbindingSession(jbindingSession), _isCallback(true) {
        initCallback();
    }
    ~JNIEnvInstance() {
        if (_isCallback) {
            _jbindingSession.endCallback();
            MY_ASSERT(_env);
        }
    }

    bool exceptionCheck() {
        if (_jniNativeCallContext) {
            return _jniNativeCallContext->exceptionCheck(_env);
        }

        return _jbindingSession.exceptionCheck(_env);
    }

    operator JNIEnv*() {
        MY_ASSERT(_env);
        return _env;
    }
    JNIEnv * operator->() {
        return (JNIEnv *) *this;
    }
};

template<class T>
class AbstractJavaCallback : public Object {
protected:
    const jobject _implementation;
    T & _javaClass;
    JBindingSession & _jbindingSession;

    AbstractJavaCallback(JBindingSession & jbindingSession, JNIEnv * initEnv,
                         jobject implementation) :
        _jbindingSession(jbindingSession), _implementation(implementation), /**/
        _javaClass(T::getInstanceFromObject(initEnv, implementation)) {
        TRACE_OBJECT_CREATION("CPPToJavaAbstract");
    }

};
#endif /* JBINDINGTOOLS_H_ */
