#include <windows.h>

#include <jni.h>
#include "jnitools.h"


/**
 * Fatal error
 */
void fatal(char * fmt, ...)
{
	va_list args;
	va_start(args, fmt);
	fputs("FATAL ERROR: ", stdout);
	vprintf(fmt, args);
	va_end(args);
	
	fputc('\n', stdout);
	fflush(stdout);

	exit(-1);
}

/**
 * Put name of the java class 'clazz'into the buffer 'buffer'
 * Return: buffer
 */
char * GetJavaClassName(JNIEnv * env, jclass clazz, char * buffer, int size)
{
	jclass reflectionClass = env->GetObjectClass(clazz);
	jmethodID id = env->GetMethodID(reflectionClass, "getName", "()Ljava/lang/String;");
	FATALIF(id == NULL, "Method Class.getName() can't be found");
	
	jstring string = (jstring)env->CallNonvirtualObjectMethod(clazz, reflectionClass, id);
	FATALIF(string == NULL, "CallNonvirtualObjectMethod() returns NULL");
		
	const char * cstr = env->GetStringUTFChars(string, NULL);
	strncpy(buffer, cstr, size);
	env->ReleaseStringUTFChars(string, cstr);
	
	return buffer;
}

/**
 * Create instance of class 'clazz' using default constructor. 
 */
jobject GetSimpleInstance(JNIEnv * env, jclass clazz)
{
	jmethodID defaultConstructor = env->GetMethodID(clazz, "<init>", "()V");
	
	char classname[256];
	FATALIF1(defaultConstructor == NULL, "Class '%s' has no default constructor", 
			GetJavaClassName(env, clazz, classname, sizeof(classname)));

	return env->NewObject(clazz, defaultConstructor);
}

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, char * fmt, ...)
{
	jclass exceptionClass = env->FindClass(SEVEN_ZIP_EXCEPTION);
	FATALIF(exceptionClass == NULL, "SevenZipException class '" SEVEN_ZIP_EXCEPTION "' can't be found");

	char buffer[64 * 1024];
	va_list args;
	va_start(args, fmt);
	_vsnprintf(buffer, sizeof(buffer), fmt, args);
	va_end(args);
	
	buffer[sizeof(buffer) - 1] = '\0';
	
	env->ThrowNew(exceptionClass, buffer);
}

