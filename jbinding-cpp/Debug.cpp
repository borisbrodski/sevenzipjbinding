#include <map>

#include "SevenZipJBinding.h"

#ifdef TRACE_OBJECTS_ON

using namespace std;

struct ClassInfo
{
    const char * _classname;
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
void TraceObjectCreation(const char * classname, void * thiz)
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

void TraceObjectCall(void * thiz, const char * methodname)
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
