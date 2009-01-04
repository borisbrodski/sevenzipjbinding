//============================================================================
// Name        : SevenZipBinding.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include "StdAfx.h"

#include "jnitools.h"
#include "SevenZipJBinding.h"

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
 * Load 7-Zip DLL.
 * 
 * Return: NULL - ok, else error message
 */
char * load7ZipLibrary(CreateObjectFunc * createObjectFunc)
{
	HINSTANCE lib = LoadLibraryA(DLLFILENAME);

	if (NULL == lib)
		return "Error loading 7-Zip library: " DLLFILENAME;

	*createObjectFunc = (CreateObjectFunc)GetProcAddress(lib, "CreateObject");

	if (NULL == *createObjectFunc)
		return "Not a 7-Zip Library. Missing 'CreateObject' export name";

	return NULL;
}


#ifdef TRACE_OBJECTS_ON

#include <map>
using namespace std;
struct ClassInfo
{
    char * _classname;
    void * _thiz;
};
map<void *, ClassInfo *> _classes_map;

void TracePrintObjects()
{
    map<void *, ClassInfo *>::const_iterator i = _classes_map.begin();
    _TRACE("Objects alive:\n")
#ifdef TRACE_ON
    int count = 1;
    for (; i != _classes_map.end(); i++)
    {
        _TRACE3("> %3i %s (this: 0x%08X)\n", count++, (*i).second->_classname, (size_t)(*i).second->_thiz)
    }
#endif
}
void TraceObjectCreation(char * classname, void * thiz)
{
    if (_classes_map.find(thiz) == _classes_map.end())
    {
        ClassInfo * classInfo = new ClassInfo();
        classInfo->_classname = classname;
        classInfo->_thiz = thiz;
        _classes_map[thiz] = classInfo;
        _TRACE3("++++++++ %s (this: 0x%08X) [classes alive: %i]\n", classInfo->_classname, (size_t)classInfo->_thiz, _classes_map.size())
    }
    else
    {
        _classes_map[thiz]->_classname = classname;
        _TRACE2("KNOWN AS %s (this: 0x%08X)\n", classname, (size_t)thiz)
    }
}
void TraceObjectDestruction(void * thiz)
{
    if (_classes_map.find(thiz) == _classes_map.end())
    {
        fatal("TraceObjectDestruction(): destructor called for unknown this=0x%08X", (size_t)thiz);
    }
    
#ifdef TRACE_ON
    ClassInfo * classInfo = _classes_map[thiz];
#endif
    
    _classes_map.erase(thiz);
    _TRACE3("~~~ %s (this: 0x%08X) [classes alive: %i]\n", classInfo->_classname, (size_t)classInfo->_thiz, _classes_map.size())
    
    TracePrintObjects();
}

void TraceObjectCall(void * thiz, char * methodname)
{
    if (_classes_map.find(thiz) == _classes_map.end())
    {
        fatal("Object call for dead object. Method name: %s, this: 0x%08X", methodname, (size_t)thiz);
    }
    
#ifdef TRACE_ON
    ClassInfo * classInfo = _classes_map[thiz];
    _TRACE4("-> %s::%s%s (this: 0x%08X)\n",classInfo->_classname, methodname, 
            (strchr(methodname, '(') == NULL ? "(...)" : ""), (size_t)thiz);
#endif
}

void TraceObjectEnsureDestruction(void * thiz)
{
    if (_classes_map.find(thiz) != _classes_map.end())
    {
        ClassInfo * classInfo = _classes_map[thiz];
        fatal("Objcet %s (this: 0x%08X) wasn't destroyed as expected\n",classInfo->_classname, (size_t)thiz);
    }
}
#endif // TRACE_OBJECTS_ON

