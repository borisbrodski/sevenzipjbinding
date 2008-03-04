#ifndef __JNITOOLS_H__INCLUDED__


#define FATALIF(cond, fmt) { if (cond) fatal(fmt); }
#define FATALIF1(cond, fmt, p1) { if (cond) fatal(fmt, p1); }
#define FATALIF2(cond, fmt, p1, p2) { if (cond) fatal(fmt, p1, p2); }
#define FATALIF3(cond, fmt, p1, p2, p3) { if (cond) fatal(fmt, p1, p2, p3); }
#define FATALIF4(cond, fmt, p1, p2, p3, p4) { if (cond) fatal(fmt, p1, p2, p3, p4); }

#define SEVEN_ZIP_EXCEPTION "net/sf/sevenzip/SevenZipException"

/**
 * Fatal error
 */
void fatal(char * fmt, ...);


/**
 * Create instance of class 'clazz' using default constructor. 
 */
jobject GetSimpleInstance(JNIEnv * env, jclass clazz);

/**
 * Put name of the java class 'clazz'into the buffer 'buffer'
 * Return: buffer
 */
char * GetJavaClassName(JNIEnv * env, jclass clazz, char * buffer, int size);

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, char * fmt, ...);


#define __JNITOOLS_H__INCLUDED__
#endif // __JNITOOLS_H__INCLUDED__
