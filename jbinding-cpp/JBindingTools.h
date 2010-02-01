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

struct ThreadContext {
    JNIEnv * _env;
    bool _attachedThread;
    std::list<JNINativeCallContext *> _javaNativeContext;

    ThreadContext() :
        _attachedThread(false), _env(NULL) {
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
        if (_threadContextMap.size() == 0) {
            // No need for synchronization in this case
            ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
            threadContext._env = initEnv;
            threadContext._javaNativeContext.push_back(jniNativeCallContext);
        } else {
            _threadContextMapCriticalSection.Enter();
            ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
            threadContext._env = initEnv;
            threadContext._javaNativeContext.push_back(jniNativeCallContext);
            _threadContextMapCriticalSection.Leave();
        }
    }

    void unregisterNativeContext(JNINativeCallContext & javaNativeContext) {

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

    void addObject(IUnknown * object) {
        _objectList.push_back(CMyComPtrWrapper<IUnknown> (object));
    }

    void closeSession(JNIEnv * initEnv) {
        _objectList.clear();
    }

    ~JBindingSession() {
        MY_ASSERT(_objectList.size() == 0);
        // TODO Check _threadContextMap
    }
};

class JNINativeCallContext {
    friend class JBindingSession;
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

};

class JNIEnvInstance {
    JNINativeCallContext & _jniNativeCallContext;
    JNIEnv * _env;

    void * operator new(size_t i);

public:
    JNIEnvInstance(JNINativeCallContext & jniNativeCallContext) :
        _jniNativeCallContext(jniNativeCallContext), _env(NULL) {

    }
    //    JNIEnvInstance(JNINativeCallContext & jniNativeCallContext, JNIEnv * env) :
    //            _jniNativeCallContext(jniNativeCallContext), _env(env) {
    //    }
    JNIEnv * getJNIEnv() {
        if (!_env) {
            // TODO uncomment
            //            _env = javaNativeContext.getJNIEnv();
            MY_ASSERT(_env);
        }
        return _env;
    }
};

#endif /* JBINDINGTOOLS_H_ */
