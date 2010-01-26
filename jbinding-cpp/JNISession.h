/*
 * JNISession.h
 *
 *  Created on: Jan 16, 2010
 *      Author: boris
 */

#ifndef JNISESSION_H_
#define JNISESSION_H_

#include <list>
#include <map>
#include <new>

//#include "Common/MyWindows.h"

#include "Debug.h"
#include "BaseSystem.h"

template<typename T>
struct CMyComPtrWrapper {
    CMyComPtr<T> _ptr;
    CMyComPtrWrapper(CMyComPtr<T> & ptr) :
        _ptr(ptr) {
    }
};

/*
 * Represents a single session of 7-Zip-JBinding.
 */
class BindingSession {
    std::list<CMyComPtrWrapper<IUnknown> > _objectList;
public:
    BindingSession(JNIEnv * env) {

    }
    void addObject(CMyComPtr<IUnknown> & object) {
        _objectList.push_back(CMyComPtrWrapper<IUnknown> (object));
    }
};

struct ThreadContext {
    JNIEnv * _env;
    BindingSession * _bindingSession;
};

class BindingGlobals {
    static std::map<ThreadId, ThreadContext> _threadContextMap;
    static PlatformCriticalSection _criticalSection;
    BindingGlobals() {
    }
public:
    /**
     * Returns JNIEnv for the current thread. JNIEnv should already be created for this thread.
     * @return JNIEnv for the current thread
     */
    static JNIEnv * getJNIEnvForCurrentThread() {
        _criticalSection.Enter();
        MY_ASSERT(_threadContextMap.find(PlatformGetCurrentThreadId()) != _threadContextMap.end())
        ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
        _criticalSection.Leave();
        return threadContext._env;
    }

    static ThreadContext & getThreadContextForCurrentThread() {
        _criticalSection.Enter();
        ThreadContext & threadContext = _threadContextMap[PlatformGetCurrentThreadId()];
        _criticalSection.Leave();
        return threadContext;
    }
};

class JavaNativeContext {
private:
    JavaVM * _vm;
    std::list<jthrowable> _thrownExceptionList;
    BindingSession & _bindingSession;

    /**
     * Prevent dynamic instances from this class
     */
    void * operator new(size_t i) {
        throw std::bad_alloc();
    }

public:
    JavaNativeContext(BindingSession & bindingSession, JNIEnv * initEnv) :
        _bindingSession(bindingSession) {
        if (initEnv->GetJavaVM(&_vm)) {
            fatal("Can't get JavaVM from JNIEnv");
        }
    }

    JNIEnv * getJNIEnv() {
        ThreadContext & threadContext = BindingGlobals::getThreadContextForCurrentThread();
        if (threadContext._env) {
            return threadContext._env;
        }
    }
};

class JavaNativeCallbackContext {
    JavaNativeContext & _javaNativeContext;
    /**
     * Prevent dynamic instances from this class
     */
    void * operator new(size_t i) {
        throw std::bad_alloc();
    }

public:
    JavaNativeCallbackContext(JavaNativeContext & javaNativeContext) :
        _javaNativeContext(javaNativeContext) {

    }
};

#endif /* JNISESSION_H_ */
