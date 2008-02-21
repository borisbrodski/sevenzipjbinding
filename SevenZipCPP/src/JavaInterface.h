#ifndef __JAVA_INTERFACE_H__INCLUDED__

class JavaInterface
{
protected:
	JNIEnv * env;
	jobject javaImplementation;
	jclass javaImplementationClass;
	
	JavaInterface(JNIEnv * env, jobject javaImplementation)
	{
		this->env = env;
		this->javaImplementation = javaImplementation;
		
		Init();
	}
	
private:
	void Init()
	{
		javaImplementationClass = env->GetObjectClass(javaImplementation);
		FATALIF(javaImplementationClass == NULL, "Can't determine class for object");
	}
};


#define __JAVA_INTERFACE_H__INCLUDED__
#endif // __JAVA_INTERFACE_H__INCLUDED__

