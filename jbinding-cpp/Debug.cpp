#include <map>
#include "SevenZipJBinding.h"

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
	return 0;
}
#endif // TRACE_ON
#ifdef TRACE_OBJECTS_ON

#ifndef _7ZIP_ST
#include "Windows/Synchronization.h"
#endif

#ifndef _7ZIP_ST
	#define ENTER_CRITICAL_SECTION   {g_criticalSection.Enter();}
	#define LEAVE_CRITICAL_SECTION   {g_criticalSection.Leave();}
#else
	#define ENTER_CRITICAL_SECTION   {}
	#define LEAVE_CRITICAL_SECTION   {}
#endif

#ifndef _7ZIP_ST
    NWindows::NSynchronization::CCriticalSection g_criticalSection;
#endif

using namespace std;

struct ClassInfo
{
    const char * _classname;
    void * _thiz;
};

map<void *, ClassInfo *> g_classes_map;
long g_jbindingSessionCount;


void TraceJBindingSessionCreation()
{
	ENTER_CRITICAL_SECTION
	g_jbindingSessionCount++;
    LEAVE_CRITICAL_SECTION
}

void TraceJBindingSessionDestruction()
{
	ENTER_CRITICAL_SECTION
	g_jbindingSessionCount--;
    LEAVE_CRITICAL_SECTION
}

void TracePrintObjects()
{
	ENTER_CRITICAL_SECTION

	map<void *, ClassInfo *>::const_iterator i = g_classes_map.begin();
    _TRACE("Objects alive:" << std::endl)

    int count = 1;
    for (; i != g_classes_map.end(); i++) {
        _TRACE("> " << std::setw(3) << count++ << " " << (*i).second->_classname << " (this: " << (*i).second->_thiz << ")" << std::endl)
    }

    LEAVE_CRITICAL_SECTION
}

void TracePrintObjectsUsingPrintf()
{
	ENTER_CRITICAL_SECTION

	map<void *, ClassInfo *>::const_iterator i = g_classes_map.begin();
    std::cout << "Objects alive:" << std::endl;
	fflush(stdout);

    int count = 1;
    for (; i != g_classes_map.end(); i++)
    {
        std::cout << "> " << setw(3) << count++ << ' ' << (*i).second->_classname << " (this: " << (*i).second->_thiz << ")" << std::endl;
		std::cout.flush();
    }

    std::cout << "Count of JBindingSession objects: " << g_jbindingSessionCount << std::endl;
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
		_TRACE("++++++++ " << classInfo->_classname << " (this: " << classInfo->_thiz << ") [classes alive: " << g_classes_map.size() << "]" << std::endl)
		LEAVE_CRITICAL_SECTION

    }
    else
    {
        ENTER_CRITICAL_SECTION
        g_classes_map[thiz]->_classname = classname;
		LEAVE_CRITICAL_SECTION

		_TRACE("KNOWN AS " << classname << " (this: " << thiz << ")" << std::endl)
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
    _TRACE("~~~ " << classInfo->_classname << " (this: " << classInfo->_thiz << ") [classes alive: " << g_classes_map.size() << "]" << std::endl)
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
    _TRACE("-> " << classInfo->_classname << "::" << methodname << (strchr(methodname, '(') == NULL ? "(...)" : "") << " (this: " << thiz << ")" << std::endl)
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
        fatal("Object %s (this: 0x%08X) wasn't destroyed as expected\n",classInfo->_classname, (size_t)thiz);
    }

}

extern "C" JNIEXPORT jint JNICALL Java_net_sf_sevenzipjbinding_junit_tools_SevenZipDebug_nativeGetObjectCount(JNIEnv * env, jclass clazz)
{
	ENTER_CRITICAL_SECTION
	int classes_size = g_classes_map.size();
	int jbindindSession_count = g_jbindingSessionCount;
    LEAVE_CRITICAL_SECTION

    return (jint)classes_size + jbindindSession_count;
}

extern "C" JNIEXPORT void JNICALL Java_net_sf_sevenzipjbinding_junit_tools_SevenZipDebug_nativePrintObjects(JNIEnv * env, jclass clazz)
{
	TracePrintObjectsUsingPrintf();
}

#endif // TRACE_OBJECTS_ON
