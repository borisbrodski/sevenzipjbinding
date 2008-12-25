#ifndef CPPTOJAVAABSTRACT_H_
#define CPPTOJAVAABSTRACT_H_

#include "vm.h"


class CPPToJavaAbstract : public Object
{
protected:
    CMyComPtr<VM> _vm;
    
	jobject _javaImplementation;
	jclass _javaImplementationClass;
    char * classname;

	JNIEnv * BeginCPPToJavaCall()
	{
	    TRACE3("====> BEGIN (%s) this=0x%08X, vm=0x%08X", classname, (size_t)this, (size_t)(void *)_vm)
	    return _vm->BeginCPPToJava();
	}
	
	void EndCPPToJavaCall()
	{
        TRACE3("<==== END   (%s) this=0x%08X, vm=0x%08X", classname, (size_t)this, (size_t)(void *)_vm)
        _vm->EndCPPToJava();
	}
	
	CPPToJavaAbstract(CMyComPtr<VM> vm, JNIEnv * initEnv, jobject javaImplementation)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaAbstract")
	    
		_vm = vm;
		_javaImplementation = initEnv->NewGlobalRef(javaImplementation);
		
		_javaImplementationClass = initEnv->GetObjectClass(javaImplementation);
		FATALIF(_javaImplementationClass == NULL, "Can't determine class for object");
		_javaImplementationClass = (jclass)initEnv->NewGlobalRef(_javaImplementationClass);
	}
	
	virtual ~CPPToJavaAbstract()
	{
        JNIEnv * env = BeginCPPToJavaCall();
        
        env->DeleteGlobalRef(_javaImplementation);
		env->DeleteGlobalRef(_javaImplementationClass);

        EndCPPToJavaCall();
        
	    //TRACE_OBJECT_DESTRUCTION
	}
	
	/**
	 * Get jni method id of method 'methodName' in class '_javaImplementationClass'
	 * with signature 'methodSignature'.
	 * 
	 * If such method don't exists, the fatal error will occurs.
	 * 
	 * Return: jni methodID
	 */
	jmethodID GetMethodId(JNIEnv * env, char * methodName, char * methodSignature)
	{
		return GetMethodId(env, _javaImplementationClass, methodName, methodSignature);
	}
	
	/**
	 * Get jni method id of method 'methodName' in class 'javaClass'
	 * with signature 'methodSignature'.
	 * 
	 * If such method don't exists, the fatal error will occurs.
	 * 
	 * Return: jni methodID
	 */
	jmethodID GetMethodId(JNIEnv * env, jclass javaClass, char * methodName, char * methodSignature)
	{
	    TRACE_OBJECT_CALL("GetMethodId")
	    
		char classname[1024];
		
		jmethodID methodID = env->GetMethodID(javaClass, methodName, methodSignature);
		FATALIF3(methodID == NULL, "Method %s.%s with signature '%s' was not found!",
				GetJavaClassName(env, _javaImplementationClass, classname, sizeof(classname)),
				methodName, methodSignature);
		
		return methodID;
	}
	
	/**
	 * Get jni static method id of method 'methodName' in class 'javaClass'
	 * with signature 'methodSignature'.
	 * 
	 * If such static method don't exists, the fatal error will occurs.
	 * 
	 * Return: jni methodID
	 */
	jmethodID GetStaticMethodId(JNIEnv * env, jclass javaClass, char * methodName, char * methodSignature)
	{
	    TRACE_OBJECT_CALL("GetStaticMethodId")
	    
		char classname[1024];
		
		jmethodID methodID = env->GetStaticMethodID(javaClass, methodName, methodSignature);
		FATALIF3(methodID == NULL, "Static method %s.%s with signature '%s' was not found!",
				GetJavaClassName(env, _javaImplementationClass, classname, sizeof(classname)),
				methodName, methodSignature);
		
		return methodID;
	}
	
	/**
	 * Find java class with name 'className'.
	 * If class don't exists, the fatal error will occurs.
	 * 
	 * WARNING: returned global reference must be deleted
	 * with '_env->DeleteGlobalRef(javaClass)'.
	 * 
	 * Return: _global_ reference of the class
	 */
	jclass GetClass(JNIEnv * env, char * className)
	{
        TRACE_OBJECT_CALL("GetClass")
	    
		jclass javaClass = env->FindClass(className);
		FATALIF1(javaClass == NULL, "Can't file java class '%s'", className);
		return (jclass)env->NewGlobalRef(javaClass);
	}
};


#endif /*CPPTOJAVAABSTRACT_H_*/

