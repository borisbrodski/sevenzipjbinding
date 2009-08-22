#ifndef CPPTOJAVAABSTRACT_H_
#define CPPTOJAVAABSTRACT_H_

#include <stack>
#include "Debug.h"
#include "JNICallState.h"


class CPPToJavaAbstract : public Object
{
protected:
    CMyComPtr<NativeMethodContext> _nativeMethodContext;
    std::stack<NativeMethodContext *> _nativeMethodContextStack;

	jobject _javaImplementation;
	jclass _javaImplementationClass;
    const char * classname;

public:
    virtual void ClearNativeMethodContext()
    {
	    TRACE_OBJECT_CALL("ClearNativeMethodContext");
		_nativeMethodContextStack.pop();
	    _nativeMethodContext = _nativeMethodContextStack.top();
    }

    virtual void SetNativMethodContext(CMyComPtr<NativeMethodContext> nativeMethodContext)
    {
	    TRACE_OBJECT_CALL("SetNativMethodContext");
	    _nativeMethodContextStack.push(nativeMethodContext);
	    _nativeMethodContext = nativeMethodContext;
    }

protected:
	JNIEnv * BeginCPPToJavaCall()
	{
	    TRACE_OBJECT_CALL("BeginCPPToJavaCall");
	    return _nativeMethodContext->BeginCPPToJava();
	}

	void EndCPPToJavaCall()
	{
	    TRACE_OBJECT_CALL("EndCPPToJavaCall");
		_nativeMethodContext->EndCPPToJava();
	}

	CPPToJavaAbstract(CMyComPtr<NativeMethodContext> nativeMethodContext, JNIEnv * initEnv, jobject javaImplementation)
	{
	    TRACE_OBJECT_CREATION("CPPToJavaAbstract");

		_nativeMethodContextStack.push(NULL);
		_nativeMethodContextStack.push(nativeMethodContext);
		_nativeMethodContext = nativeMethodContext;
		_javaImplementation = initEnv->NewGlobalRef(javaImplementation);

		_javaImplementationClass = initEnv->GetObjectClass(javaImplementation);
		FATALIF(_javaImplementationClass == NULL, "Can't determine class for object");
		_javaImplementationClass = (jclass)initEnv->NewGlobalRef(_javaImplementationClass);
	}

	virtual ~CPPToJavaAbstract()
	{
        TRACE_OBJECT_CALL("~CPPToJavaAbstract");

        JNIInstance jniInstance(_nativeMethodContext);

        jniInstance.GetEnv()->DeleteGlobalRef(_javaImplementation);
        jniInstance.GetEnv()->DeleteGlobalRef(_javaImplementationClass);
	}

	/**
	 * Get jni method id of method 'methodName' in class '_javaImplementationClass'
	 * with signature 'methodSignature'.
	 *
	 * If such method don't exists, the fatal error will occurs.
	 *
	 * Return: jni methodID
	 */
	jmethodID GetMethodId(JNIEnv * env, const char * methodName, const char * methodSignature)
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
	jmethodID GetMethodId(JNIEnv * env, jclass javaClass, const char * methodName, const char * methodSignature)
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
	jmethodID GetStaticMethodId(JNIEnv * env, jclass javaClass, const char * methodName, const char * methodSignature)
	{
	    TRACE_OBJECT_CALL("GetStaticMethodId")

		char classname[1024];

		jmethodID methodID = env->GetStaticMethodID(javaClass, methodName, methodSignature);
		FATALIF3(methodID == NULL, "Static method %s.%s with signature '%s' was not found!",
				GetJavaClassName(env, javaClass, classname, sizeof(classname)),
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
	jclass GetClass(JNIEnv * env, const char * className)
	{
        TRACE_OBJECT_CALL("GetClass")

		jclass javaClass = env->FindClass(className);
		FATALIF1(javaClass == NULL, "Can't file java class '%s'", className);
		return (jclass)env->NewGlobalRef(javaClass);
	}
};


#endif /*CPPTOJAVAABSTRACT_H_*/

