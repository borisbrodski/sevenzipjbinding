#ifndef JNICALLSTATE_H_
#define JNICALLSTATE_H_

#include <map>
#include "jnitools.h"
#include "SevenZipBindingException.h"

using namespace std;

class JNICallState;

class ThreadInfo
{
    friend class JNICallState;
private:
    int _callCounter;
    JNIEnv * _env;
    
    ThreadInfo(JNIEnv * env)
    {
        _callCounter = 1;
        _env = env;
    }
};

class JNICallState : public CMyUnknownImp, Object
{
private:
    JavaVM * _vm;
    DWORD _initThreadId;
    JNIEnv * _initEnv;
    map<DWORD, ThreadInfo *> _threadInfoMap;
public:
    MY_UNKNOWN_IMP
    
    JNICallState(JNIEnv * initEnv)
	{
        TRACE_OBJECT_CREATION("JNICallState")
        
	    _initEnv = initEnv;
	    _initThreadId = GetCurrentThreadId();
	    TRACE2("_initThreadId = %lu, this=0x%08X", _initThreadId, (size_t)this)
	    _vm = NULL;
	    if (initEnv->GetJavaVM(&_vm))
	    {
	        TRACE("Can't get JavaVM from env")
	        throw SevenZipBindingException("Can't get JavaVM from env");
	    }
	}
	
	virtual ~JNICallState()
	{
	    // TRACE_OBJECT_DESTRUCTION
	}
	
	JNIEnv * BeginCPPToJava();
	
	void EndCPPToJava();};

#endif /*JNICALLSTATE_H_*/
