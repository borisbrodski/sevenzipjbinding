#include <map>
#include "SevenZipJBinding.h"
#include "JNICallState.h" // TODO Fix this. We need this only for PlatformGetThreadId()

#ifdef TRACE_ON
int trace_printf(const char * fmt, ...) {
	va_list args;
	va_start(args, fmt);
#ifdef TRACE_THREADS_ON
	DWORD threadId = PlatformGetCurrentThreadId();
	char filename[1024];
	snprintf(filename, sizeof(filename), TRACE_THREAD_LOG_FILENAME, threadId);
	filename[sizeof(filename) - 1] = '\0';
	FILE * f = fopen(filename, "at");
	vfprintf(f, fmt, args);
	fclose(f);
#else // TRACE_THREADS_ON
	vprintf(fmt, args);
#endif // TRACE_THREADS_ON
	va_end(args);
}
#endif // TRACE_ON
#ifdef TRACE_OBJECTS_ON

#ifdef COMPRESS_MT
#include "Windows/Synchronization.h"
#endif

#ifdef COMPRESS_MT
	#define ENTER_CRITICAL_SECTION   {g_criticalSection.Enter();}
	#define LEAVE_CRITICAL_SECTION   {g_criticalSection.Leave();}
#else
	#define ENTER_CRITICAL_SECTION   {}
	#define LEAVE_CRITICAL_SECTION   {}
#endif

#ifdef COMPRESS_MT
    NWindows::NSynchronization::CCriticalSection g_criticalSection;
#endif

using namespace std;

struct ClassInfo
{
    const char * _classname;
    void * _thiz;
};

map<void *, ClassInfo *> g_classes_map;

void TracePrintObjects()
{
	ENTER_CRITICAL_SECTION

	map<void *, ClassInfo *>::const_iterator i = g_classes_map.begin();
    _TRACE("Objects alive:\n")

    int count = 1;
    for (; i != g_classes_map.end(); i++)
    {
        _TRACE3("> %3i %s (this: 0x%08X)\n", count++, (*i).second->_classname, (size_t)(*i).second->_thiz)
    }

    LEAVE_CRITICAL_SECTION
}

void TracePrintObjectsUsingPrintf()
{
	ENTER_CRITICAL_SECTION

	map<void *, ClassInfo *>::const_iterator i = g_classes_map.begin();
    printf("Objects alive:\n");
	fflush(stdout);

    int count = 1;
    for (; i != g_classes_map.end(); i++)
    {
        printf("> %3i %s (this: 0x%08X)\n", count++, (*i).second->_classname, (size_t)(*i).second->_thiz);
		fflush(stdout);
    }

    LEAVE_CRITICAL_SECTION
}
void TraceObjectCreation(const char * classname, void * thiz)
{
	ENTER_CRITICAL_SECTION
	int found = g_classes_map.find(thiz) == g_classes_map.end();
	LEAVE_CRITICAL_SECTION

	if (found)
    {
        ClassInfo * classInfo = new ClassInfo();
        classInfo->_classname = classname;
        classInfo->_thiz = thiz;

        ENTER_CRITICAL_SECTION
        g_classes_map[thiz] = classInfo;
		_TRACE3("++++++++ %s (this: 0x%08X) [classes alive: %i]\n", classInfo->_classname, (size_t)classInfo->_thiz, g_classes_map.size())
		LEAVE_CRITICAL_SECTION

    }
    else
    {
        ENTER_CRITICAL_SECTION
        g_classes_map[thiz]->_classname = classname;
		LEAVE_CRITICAL_SECTION

		_TRACE2("KNOWN AS %s (this: 0x%08X)\n", classname, (size_t)thiz)
    }

}
void TraceObjectDestruction(void * thiz)
{
	ENTER_CRITICAL_SECTION
	int found = g_classes_map.find(thiz) == g_classes_map.end();
    LEAVE_CRITICAL_SECTION

    if (found)
    {
        fatal("TraceObjectDestruction(): destructor called for unknown this=0x%08X", (size_t)thiz);
    }

#ifdef TRACE_ON
	ENTER_CRITICAL_SECTION
    ClassInfo * classInfo = g_classes_map[thiz];
    LEAVE_CRITICAL_SECTION
#endif

	ENTER_CRITICAL_SECTION
    g_classes_map.erase(thiz);
    _TRACE3("~~~ %s (this: 0x%08X) [classes alive: %i]\n", classInfo->_classname, (size_t)classInfo->_thiz, g_classes_map.size())
    LEAVE_CRITICAL_SECTION

    TracePrintObjects();
}

void TraceObjectCall(void * thiz, const char * methodname)
{
	ENTER_CRITICAL_SECTION
	int found = g_classes_map.find(thiz) == g_classes_map.end();
    LEAVE_CRITICAL_SECTION

    if (found)
    {
        fatal("Object call for dead object. Method name: %s, this: 0x%08X", methodname, (size_t)thiz);
    }

#ifdef TRACE_ON
	ENTER_CRITICAL_SECTION
    ClassInfo * classInfo = g_classes_map[thiz];
    _TRACE4("-> %s::%s%s (this: 0x%08X)\n",classInfo->_classname, methodname,
            (strchr(methodname, '(') == NULL ? "(...)" : ""), (size_t)thiz);
    LEAVE_CRITICAL_SECTION
#endif

}

void TraceObjectEnsureDestruction(void * thiz)
{
	ENTER_CRITICAL_SECTION
	int found = g_classes_map.find(thiz) != g_classes_map.end();
    LEAVE_CRITICAL_SECTION

    if (found)
    {
    	ENTER_CRITICAL_SECTION
        ClassInfo * classInfo = g_classes_map[thiz];
		LEAVE_CRITICAL_SECTION
        fatal("Objcet %s (this: 0x%08X) wasn't destroyed as expected\n",classInfo->_classname, (size_t)thiz);
    }

}

extern "C" JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_junit_tools_SevenZipDebug_nativeGetObjectCount(JNIEnv * env, jobject thiz)
{
	ENTER_CRITICAL_SECTION
	int size = g_classes_map.size();
    LEAVE_CRITICAL_SECTION

    return (jint)size;
}

extern "C" JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_junit_tools_SevenZipDebug_nativePrintObjects(JNIEnv * env, jobject thiz)
{
	TracePrintObjectsUsingPrintf();
}

#endif // TRACE_OBJECTS_ON
