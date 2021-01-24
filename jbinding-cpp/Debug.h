#ifndef DEBUG_H_
#define DEBUG_H_

// Use CMakeLists.txt to activate debug mode: uncomment "SET(CMAKE_BUILD_TYPE Debug)"
// #define _DEBUG
//#define TRACE_ON
//#define TRACE_OBJECTS_ON

//#define TRACE_ON
//#define TRACE_THREADS_ON
//#define TRACE_THREAD_LOG_FILENAME "sevenzipjbinding-thread-%i.log"

#if defined(NDEBUG) && defined(_DEBUG)
#   undef _DEBUG
#endif

#ifdef _DEBUG
// This cause probably a crash
//#   define _GLIBCXX_DEBUG


// Define JNI_TOOLS_DEBUG_CALL_AND_EXCEPTION_CLEAR_BEHAVIOR
// --------------------------------------------------------
//
// With this define it's possible to check that after each method call
// an exception check occurs. It destructs env after each method call
// (using expectExceptionCheck()). The env should be repaired before
// consequent use by calling prepareExceptionCheck().
//
// #define JNI_TOOLS_DEBUG_CALL_AND_EXCEPTION_CLEAR_BEHAVIOR
// WARNING: Not reentrant!!

// Define NATIVE_JUNIT_TEST_SUPPORT
// --------------------------------------------------------
//
// This define enables JniTools&JBinding test code allowing corresponding JUnit test to work
#   define NATIVE_JUNIT_TEST_SUPPORT
#endif

#ifdef _DEBUG
#   define USE_MY_ASSERTS
#endif

#if defined(_DEBUG) && !defined(TRACE_OBJECTS_ON)
#   define TRACE_OBJECTS_ON
#endif

#ifdef USE_MY_ASSERTS
#   define MY_ASSERT(a) {if (!(a)) fatal("ASSERT: " __FILE__ ":%i : %s\n", (int)__LINE__, #a );}
#else
#   define MY_ASSERT(a) {}
#endif

#if defined(TRACE_OBJECTS_ON) || defined(TRACE_ON)
#   include <iostream>
#   include <iomanip>
#endif

#ifdef TRACE_ON
#   define TRACE(msg) _TRACE("TRACE: " << msg << std::endl)
#   define _TRACE(msg) {std::cout << msg;}
/**/int trace_printf(const char * fmt, ...);
#else
#   define TRACE(msg) {}
#   define _TRACE(msg) {}
#endif

#if defined(TRACE_ON) || defined(NATIVE_JUNIT_TEST_SUPPORT)
/**
 * Fatal error
 */
void fatal(const char * fmt, ...); // TODO Remove this!

struct JOut {
    JNIEnv * _env;
    std::ostream & _stream;
    bool _noQuotes;
    JOut(JNIEnv * env, std::ostream & stream) :
        _env(env), _stream(stream), _noQuotes(false) {
    }
};

struct jstringNoQuotes {
};

inline JOut operator<<(std::ostream & stream, JNIEnv * env) {
    return JOut(env, stream);
}
inline JOut operator<<(JOut jout, jstringNoQuotes) {
    jout._noQuotes = true;
    return jout;
}
inline std::ostream & operator<<(JOut jout, jstring str) {
    if (jout._env->ExceptionCheck()) {
        return jout._stream << "<exception>";
    }
    if (!str) {
        return jout._stream << "<null>";
    }
    char const * s = jout._env->GetStringUTFChars(str, NULL);
    if (jout._noQuotes) {
        jout._stream << s;
    } else {
        jout._stream << '"' << s << '"';
    }
    jout._env->ReleaseStringUTFChars(str, s);
    return jout._stream;
}
inline std::ostream & operator<<(JOut jout, jobject object) {
    if (jout._env->ExceptionCheck()) {
        return jout._stream << "<exception>";
    }
    if (!object) {
        return jout._stream << "<null>";
    }
    jclass objectClass = jout._env->GetObjectClass(object);
    MY_ASSERT(objectClass)
    jmethodID id = jout._env->GetMethodID(objectClass, "toString", "()Ljava/lang/String;");
#ifdef __ANDROID_API__
    jout._env->DeleteLocalRef(objectClass);
#endif
    jstring string = (jstring) jout._env->CallObjectMethod(object, id);
    MY_ASSERT(string)//FATALIF(string == NULL, "CallNonvirtualObjectMethod() returns NULL");

    std::ostream & stream = jout << string;
    jout._env->DeleteLocalRef(string);
    return stream;
}
inline std::ostream & operator<<(JOut jout, jclass clazz) {
    if (jout._env->ExceptionCheck()) {
        return jout._stream << "<exception>";
    }
    if (!clazz) {
        return jout._stream << "<null>";
    }
    jclass classClass = jout._env->GetObjectClass(clazz);
    MY_ASSERT(classClass)
    jmethodID id = jout._env->GetMethodID(classClass, "getName", "()Ljava/lang/String;");
    MY_ASSERT(id) //FATALIF(!id, "Method Class.getName() can't be found");

    jstring string = (jstring) jout._env->CallNonvirtualObjectMethod(clazz, classClass, id);
    MY_ASSERT(string)//FATALIF(string == NULL, "CallNonvirtualObjectMethod() returns NULL");
#ifdef __ANDROID_API__
    jout._env->DeleteLocalRef(classClass);
#endif

    std::ostream & stream = jout << string;
    jout._env->DeleteLocalRef(string);
    return stream;
}
inline std::ostream & operator<<(JOut jout, jint i) {
    if (jout._env->ExceptionCheck()) {
        return jout._stream << "<exception>";
    }
    jout._stream << i;
    return jout._stream;
}
inline std::ostream & operator<<(JOut jout, char const * str) {
    if (jout._env->ExceptionCheck()) {
        return jout._stream << "<exception>";
    }
    if (!str) {
        return jout._stream << "<null>";
    }
    jout._stream << '"' << str << '"';
    return jout._stream;
}
#endif

#ifdef TRACE_OBJECTS_ON
/**/void TraceObjectCreation(const char * classname, void * thiz);
/**/void TraceObjectDestruction(void * thiz);
/**/void TraceObjectCall(void * thiz, const char * methodname);
/**/void TraceObjectEnsureDestruction(void * object);
/**/void TraceJBindingSessionCreation();
/**/void TraceJBindingSessionDestruction();

#   define TRACE_OBJECT_CREATION(classname) {TraceObjectCreation(classname, (Object *)this);}
#   define TRACE_OBJECT_DESTRUCTION         {TraceObjectDestruction((Object *)this);}
#   define TRACE_OBJECT_CALL(methodname)    {TraceObjectCall((Object *)this, methodname);}
#   define TRACE_CLASS_CHECK_UNKNOWN_IMPL_DESTRUCTION(classname)                    \
        {                                                                           \
            if (__m_RefCount != 0)                                                  \
            {                                                                       \
                fatal("The referenced object %s (this: 0x%08X) was destructed",     \
                        classname, (Object *)this);                                 \
            }                                                                       \
        }
#   define TRACE_PRINT_OBJECTS {TracePrintObjectsUsingPrintf();}

/**/void TracePrintObjectsUsingPrintf();

/*
 #define TRACE_OBJECT_ENSURE_DESTRUCTION_WITH_STACK_CMYCOMPTR(object)       \
                    {                                                       \
                        void * __p = &(*object);                            \
                        object.Release();                                   \
                        TraceObjectEnsureDestruction((Object*)__p);         \
                    }
 */
#else
#   define TRACE_PRINT_OBJECTS {}
#   define TRACE_OBJECT_CREATION(classname) {}
#   define TRACE_OBJECT_DESTRUCTION {}
#   define TRACE_OBJECT_CALL(methodname) {}
#   define TRACE_CLASS_CHECK_UNKNOWN_IMPL_DESTRUCTION(classname) {}
//    #define TRACE_OBJECT_ENSURE_DESTRUCTION_WITH_STACK_CMYCOMPTR(object) {}
#endif

#endif /* DEBUG_H_ */
