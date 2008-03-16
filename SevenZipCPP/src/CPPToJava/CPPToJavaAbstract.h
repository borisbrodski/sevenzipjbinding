#ifndef CPPTOJAVAABSTRACT_H_
#define CPPTOJAVAABSTRACT_H_

class CPPToJavaAbstract
{
protected:
	JNIEnv * _env;
	jobject _javaImplementation;
	jclass _javaImplementationClass;
	
	CPPToJavaAbstract(JNIEnv * env, jobject javaImplementation)
	{
		_env = env;
		_javaImplementation = env->NewGlobalRef(javaImplementation);
		
		_javaImplementationClass = _env->GetObjectClass(javaImplementation);
		FATALIF(_javaImplementationClass == NULL, "Can't determine class for object");
		_javaImplementationClass = (jclass)_env->NewGlobalRef(_javaImplementationClass);
	}
	
	virtual ~CPPToJavaAbstract()
	{
		_env->DeleteGlobalRef(_javaImplementation);
		_env->DeleteGlobalRef(_javaImplementationClass);
	}
	
	/**
	 * Get jni method id of method 'methodName' in class '_javaImplementationClass'
	 * with signature 'methodSignature'.
	 * 
	 * If such method don't exists, the fatal error will occurs.
	 * 
	 * Return: jni methodID
	 */
	jmethodID GetMethodId(char * methodName, char * methodSignature)
	{
		return GetMethodId(_javaImplementationClass, methodName, methodSignature);
	}
	
	/**
	 * Get jni method id of method 'methodName' in class 'javaClass'
	 * with signature 'methodSignature'.
	 * 
	 * If such method don't exists, the fatal error will occurs.
	 * 
	 * Return: jni methodID
	 */
	jmethodID GetMethodId(jclass javaClass, char * methodName, char * methodSignature)
	{
		char classname[1024];
		
		jmethodID methodID = _env->GetMethodID(javaClass, methodName, methodSignature);
		FATALIF3(methodID == NULL, "Method %s.%s with signature '%s' was not found!",
				GetJavaClassName(_env, _javaImplementationClass, classname, sizeof(classname)),
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
	jmethodID GetStaticMethodId(jclass javaClass, char * methodName, char * methodSignature)
	{
		char classname[1024];
		
		jmethodID methodID = _env->GetStaticMethodID(javaClass, methodName, methodSignature);
		FATALIF3(methodID == NULL, "Static method %s.%s with signature '%s' was not found!",
				GetJavaClassName(_env, _javaImplementationClass, classname, sizeof(classname)),
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
	jclass GetClass(char * className)
	{
		jclass javaClass = _env->FindClass(className);
		FATALIF1(javaClass == NULL, "Can't file java class '%s'", className);
		return (jclass)_env->NewGlobalRef(javaClass);
	}
};


#endif /*CPPTOJAVAABSTRACT_H_*/

