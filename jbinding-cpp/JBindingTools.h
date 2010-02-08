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

    std::list<CMyComPtrWrapper<IUnknown> > _objectList;
    std::list<JNINativeCallContext> _jniNativeCallContextList;
    std::map<ThreadId, ThreadContext> _threadContextMap;
    PlatformCriticalSection _threadContextMapCriticalSection;
    static JavaVM * _vm;

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

    JNIEnv * getJNIEnv(JNINativeCallContext & javaNativeContext) {
        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
        _threadContextMapCriticalSection.Leave();
        if (threadContext._env) {
            return threadContext._env;
        }

        TRACE("Attaching current thread to VM.")
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

public:
    JBindingSession(JNIEnv * initEnv) {
        if (!_vm && initEnv->GetJavaVM(&_vm)) {
            fatal("Can't get JavaVM from JNIEnv");
        }
        MY_ASSERT(_vm);
    }

    JNIEnv * beginCallback() {
        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
        _threadContextMapCriticalSection.Leave();
        if (!threadContext._env) {
            // Attach new thread
            threadContext._attachedThreadCount = 0;

            TRACE("Attaching current thread to VM.")
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
        threadContext._attachedThreadCount++;
        return threadContext._env;
    }

    void endCallback() {
        ThreadId threadId = PlatformGetCurrentThreadId();

        _threadContextMapCriticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[threadId];
        if (!--threadContext._attachedThreadCount) {
            _vm->DetachCurrentThread();
            _threadContextMap.erase(threadId);
        }
        _threadContextMapCriticalSection.Leave();
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

    void exceptionThrown(JNIEnv * env, jthrowable throwable);
};

class JNINativeCallContext {
    friend class JBindingSession;
    friend class JNIEnvInstance;

    jthrowable _firstThrownException;
    jthrowable _lastThrownException;
    JBindingSession & _jbindingSession;
    JNIEnv * _jniCallOriginalEnv;

    JNINativeCallContext(JNINativeCallContext const &);
    void * operator new(size_t i);
public:

    JNINativeCallContext(JBindingSession & _jbindingSession, JNIEnv * initEnv) :
        _jbindingSession(_jbindingSession), _jniCallOriginalEnv(initEnv), _firstThrownException(
                NULL), _lastThrownException(NULL) {
        _jbindingSession.registerNativeContext(initEnv, this);
    }
    ~JNINativeCallContext() {
        _jbindingSession.unregisterNativeContext(*this);
    }

    void exceptionThrown(JNIEnv * env, jthrowable throwableLocalRef) {
        jthrowable throwableGlobalRef = static_cast<jthrowable> (env->NewGlobalRef(
                throwableLocalRef));
        if (!_firstThrownException) {
            _firstThrownException = throwableGlobalRef;
        } else {
            if (_lastThrownException) {
                env->DeleteLocalRef(_lastThrownException);
            }
            _lastThrownException = throwableGlobalRef;
        }
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
    JBindingSession * _jbindingSession;
    JNINativeCallContext * _jniNativeCallContext;
    JNIEnv * _env;
    bool _isCallback;

    void * operator new(size_t i);

    void init() {
        _isCallback = !_env;
        if (_isCallback) {
            _env = _jbindingSession->beginCallback();
            MY_ASSERT(_env);
        }
    }
public:
    JNIEnvInstance(JNIEnv * env, JNINativeCallContext & jniNativeCallContext,
                   JBindingSession * jbindingSession) :
        _env(env), _jniNativeCallContext(&jniNativeCallContext), _jbindingSession(jbindingSession) {
        init();
    }
    JNIEnvInstance(JBindingSession * jbindingSession, JNIEnv * env = NULL) :
        _env(env), _jniNativeCallContext(NULL), _jbindingSession(jbindingSession) {
        init();
    }
    ~JNIEnvInstance() {
        if (_isCallback) {
            _jbindingSession->endCallback();
            MY_ASSERT(_env);
        }
    }

    bool exceptionCheck() {
        jni::prepareExceptionCheck(_env);
        if (_env->ExceptionCheck()) {
            // TODO Get and save exception
            _env->ExceptionClear();
            return true;
        }
        return false;
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

    AbstractJavaCallback(JBindingSession & jbindingSession, JNIEnv * initEnv, jobject implementation) :
        _jbindingSession(jbindingSession), _implementation(implementation), /**/
        _javaClass(T::getInstanceFromObject(initEnv, implementation)) {
        TRACE_OBJECT_CREATION("CPPToJavaAbstract");
    }

};
#endif /* JBINDINGTOOLS_H_ */
