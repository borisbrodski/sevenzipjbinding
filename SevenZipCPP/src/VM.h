#ifndef VM_H_
#define VM_H_

#include <map>
#include "jnitools.h"
#include "SevenZipBindingException.h"

using namespace std;

class VM;

class ThreadInfo
{
    friend class VM;
private:
    int _callCounter;
    JNIEnv * _env;
    
    ThreadInfo(JNIEnv * env)
    {
        _callCounter = 1;
        _env = env;
    }
};

class VM : public CMyUnknownImp, Object
{
private:
    JavaVM * _vm;
    DWORD _initThreadId;
    JNIEnv * _initEnv;
    map<DWORD, ThreadInfo *> _threadInfoMap;
public:
    MY_UNKNOWN_IMP
    
	VM(JNIEnv * initEnv)
	{
        TRACE_OBJECT_CREATION("VM")
        
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
	
	virtual ~VM()
	{
	    // TRACE_OBJECT_DESTRUCTION
	}
	
	JNIEnv * BeginCPPToJava();
	
	void EndCPPToJava();};

#endif /*VM_H_*/
