#include "StdAfx.h"

#include "JNICallState.h"


JNIEnv * JNICallState::BeginCPPToJava()
{
    TRACE_OBJECT_CALL("BeginCPPToJava")
    
    DWORD currentThreadId = GetCurrentThreadId();
    
    if (currentThreadId == _initThreadId)
    {
        return _initEnv;
    }
    
    if (_threadInfoMap.find(currentThreadId) == _threadInfoMap.end())
    {
        JNIEnv * env;
        TRACE2("JNIEnv* was requested from other thread. Current threadId=%lu, initThreadId=%lu", currentThreadId, _initThreadId)
        jint result = _vm->GetEnv((void**)&env, JNI_VERSION_1_6);
        if (result == JNI_OK) {
            TRACE("Current thread is already attached")
            return env;
        }
        TRACE("Attaching current thread to VM.")
        if ((result = _vm->AttachCurrentThread((void**)&env, NULL)) || env == NULL)
        {
            TRACE1("New thread couldn't be attached: %li", result)
            throw SevenZipBindingException("Can't attach current thread (id: %i) to the VM", currentThreadId);
        }
        TRACE1("Thread attached. New env=0x%08X", (size_t)env);
        
        ThreadInfo * threadInfo = new ThreadInfo(env);
        _threadInfoMap[currentThreadId] = threadInfo;
        
        return env;
    } else {
        ThreadInfo * threadInfo = _threadInfoMap[currentThreadId];
        threadInfo->_callCounter++;
        TRACE1("Begin => deattaching counter: %i", threadInfo->_callCounter)

        return threadInfo->_env;
    }
}

void JNICallState::EndCPPToJava()
{
    TRACE_OBJECT_CALL("EndCPPToJava")
    
    DWORD currentThreadId = GetCurrentThreadId();
    
    if (currentThreadId == _initThreadId)
    {
        return;
    }
#ifdef _DEBUG
    if (_threadInfoMap.find(currentThreadId) == _threadInfoMap.end())
    {
        TRACE1("EndCPPToJava(): unknown current thread (id: %i)", currentThreadId)
        throw SevenZipBindingException("EndCPPToJava(): unknown current thread (id: %i)", currentThreadId);
    }
#endif //_DEBUG
    
    ThreadInfo * threadInfo = _threadInfoMap[currentThreadId];
    if (--threadInfo->_callCounter <= 0)
    {
        TRACE("End => Deataching current thread from JavaVM")
        _vm->DetachCurrentThread();
        _threadInfoMap.erase(currentThreadId);
        delete threadInfo;
    }
    else
    {
        TRACE1("End => deattaching counter: %i", threadInfo->_callCounter)
    }
}
