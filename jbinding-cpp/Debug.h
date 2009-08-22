#ifndef DEBUG_H_
#define DEBUG_H_

// Use CMakeLists.txt to activate debug mode: uncomment "SET(CMAKE_BUILD_TYPE Debug)"
//#define _DEBUG
//#define TRACE_ON
//#define TRACE_OBJECTS_ON

//#define TRACE_THREADS_ON
//#define TRACE_THREAD_LOG_FILENAME "sevenzipjbinding-thread-%i.log"

#if defined(NDEBUG) && defined(_DEBUG)
#undef _DEBUG
#endif

#ifdef _DEBUG
#define _GLIBCXX_DEBUG
#endif

#if defined(_DEBUG) && !defined(TRACE_OBJECTS_ON)
#define TRACE_OBJECTS_ON
#endif

#ifdef TRACE_ON
    #define TRACE(msg) _TRACE("TRACE: " msg "\n")
    #define TRACE1(msg, p1) _TRACE1("TRACE: " msg "\n", p1)
    #define TRACE2(msg, p1, p2) _TRACE2("TRACE: " msg "\n", p1, p2)
    #define TRACE3(msg, p1, p2, p3) _TRACE3("TRACE: " msg "\n", p1, p2, p3)
    #define TRACE4(msg, p1, p2, p3, p4) _TRACE4("TRACE: " msg "\n", p1, p2, p3, p4)
    #define _TRACE(msg) {trace_printf(msg);}
    #define _TRACE1(msg, p1) {trace_printf(msg, p1);}
    #define _TRACE2(msg, p1, p2) {trace_printf(msg, p1, p2);}
    #define _TRACE3(msg, p1, p2, p3) {trace_printf(msg, p1, p2, p3);}
    #define _TRACE4(msg, p1, p2, p3, p4) {trace_printf(msg, p1, p2, p3, p4);}

	int trace_printf (const char * fmt, ...);
#else
    #define TRACE(msg) {}
    #define TRACE1(msg, p1) {}
    #define TRACE2(msg, p1, p2) {}
    #define TRACE3(msg, p1, p2, p3) {}
    #define TRACE4(msg, p1, p2, p3, p4) {}
    #define _TRACE(msg) {}
    #define _TRACE1(msg, p1) {}
    #define _TRACE2(msg, p1, p2) {}
    #define _TRACE3(msg, p1, p2, p3) {}
    #define _TRACE4(msg, p1, p2, p3, p4) {}
#endif

#ifdef TRACE_OBJECTS_ON
    void TraceObjectCreation(const char * classname, void * thiz);
    void TraceObjectDestruction(void * thiz);
    void TraceObjectCall(void * thiz, const char * methodname);
    void TraceObjectEnsureDestruction(void * object);

    #define TRACE_OBJECT_CREATION(classname) {TraceObjectCreation(classname, (Object *)this);}
    #define TRACE_OBJECT_DESTRUCTION         {TraceObjectDestruction((Object *)this);}
    #define TRACE_OBJECT_CALL(methodname)    {TraceObjectCall((Object *)this, methodname);}
    #define TRACE_CLASS_CHECK_UNKNOWN_IMPL_DESTRUCTION(classname)                   \
        {                                                                           \
            if (__m_RefCount > 0)                                                   \
            {                                                                       \
                fatal("The referenced object %s (this: 0x%08X) was destructed",     \
                        classname, (Object *)this);                                 \
            }                                                                       \
        }
	#define TRACE_PRINT_OBJECTS {TracePrintObjectsUsingPrintf();}

    void TracePrintObjectsUsingPrintf();

    /*
    #define TRACE_OBJECT_ENSURE_DESTRUCTION_WITH_STACK_CMYCOMPTR(object)    \
                    {                                                       \
                        void * __p = &(*object);                            \
                        object.Release();                                   \
                        TraceObjectEnsureDestruction((Object*)__p);         \
                    }
*/
#else
	#define TRACE_PRINT_OBJECTS {}
    #define TRACE_OBJECT_CREATION(classname) {}
    #define TRACE_OBJECT_DESTRUCTION {}
    #define TRACE_OBJECT_CALL(methodname) {}
    #define TRACE_CLASS_CHECK_UNKNOWN_IMPL_DESTRUCTION(classname) {}
//    #define TRACE_OBJECT_ENSURE_DESTRUCTION_WITH_STACK_CMYCOMPTR(object) {}
#endif


#endif /* DEBUG_H_ */
