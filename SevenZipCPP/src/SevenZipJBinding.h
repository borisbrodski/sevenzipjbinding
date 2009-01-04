#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_

#define DLLFILENAME "7z.dll"


#define SEVEN_ZIP_EXCEPTION "net/sf/sevenzip/SevenZipException"
#define SEVEN_ZIP_EXCEPTION_T JAVA_MAKE_SIGNATURE_TYPE(SEVEN_ZIP_EXCEPTION)


#define IN_ARCHIVE_IMPL "net/sf/sevenzip/impl/InArchiveImpl"
#define IN_ARCHIVE_IMPL_T JAVA_MAKE_SIGNATURE_TYPE(IN_ARCHIVE_IMPL)
#define IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInstance"
#define IN_STREAM_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInStreamInstance"

#define PROPERTYINFO_CLASS "net/sf/sevenzip/PropertyInfo"
#define PROPERTYINFO_CLASS_T JAVA_MAKE_SIGNATURE_TYPE(PROPERTYINFO_CLASS)

#define PROPID_CLASS "net/sf/sevenzip/PropID"
#define PROPID_CLASS_T JAVA_MAKE_SIGNATURE_TYPE(PROPID_CLASS)

#define SEQUENTIALOUTSTREAM_CLASS		"net/sf/sevenzip/ISequentialOutStream"
#define SEQUENTIALOUTSTREAM_CLASS_T		JAVA_MAKE_SIGNATURE_TYPE(SEQUENTIALOUTSTREAM_CLASS)

#define INSTREAM_CLASS		            "net/sf/sevenzip/IInStream"
#define INSTREAM_CLASS_T		        JAVA_MAKE_SIGNATURE_TYPE(INSTREAM_CLASS)

#define CRYPTOGETTEXTPASSWORD_CLASS	    "net/sf/sevenzip/ICryptoGetTextPassword"
#define CRYPTOGETTEXTPASSWORD_CLASS_T   JAVA_MAKE_SIGNATURE_TYPE(CRYPTOGETTEXTPASSWORD_CLASS)

#define ARCHIVEOPENVOLUMECALLBACK_CLASS	    "net/sf/sevenzip/IArchiveOpenVolumeCallback"
#define ARCHIVEOPENVOLUMECALLBACK_CLASS_T   JAVA_MAKE_SIGNATURE_TYPE(ARCHIVEOPENVOLUMECALLBACK_CLASS)

#define EXTRACTASKMODE_CLASS			"net/sf/sevenzip/ExtractAskMode"
#define EXTRACTASKMODE_CLASS_T			JAVA_MAKE_SIGNATURE_TYPE(EXTRACTASKMODE_CLASS)

#define EXTRACTOPERATIONRESULT_CLASS	"net/sf/sevenzip/ExtractOperationResult"
#define EXTRACTOPERATIONRESULT_CLASS_T	JAVA_MAKE_SIGNATURE_TYPE(EXTRACTOPERATIONRESULT_CLASS)

#ifdef _DEBUG
//#define TRACE_ON
#define TRACE_OBJECTS_ON
#define TRACE_OBJECT_CALLS
#endif

#ifdef TRACE_ON
    #define TRACE(msg) _TRACE("TRACE: " msg "\n")
    #define TRACE1(msg, p1) _TRACE1("TRACE: " msg "\n", p1)
    #define TRACE2(msg, p1, p2) _TRACE2("TRACE: " msg "\n", p1, p2)
    #define TRACE3(msg, p1, p2, p3) _TRACE3("TRACE: " msg "\n", p1, p2, p3)
    #define _TRACE(msg) {printf(msg); fflush(stdout);}
    #define _TRACE1(msg, p1) {printf(msg, p1); fflush(stdout);}
    #define _TRACE2(msg, p1, p2) {printf(msg, p1, p2); fflush(stdout);}
    #define _TRACE3(msg, p1, p2, p3) {printf(msg, p1, p2, p3); fflush(stdout);}
    #define _TRACE4(msg, p1, p2, p3, p4) {printf(msg, p1, p2, p3, p4); fflush(stdout);}
#else
    #define TRACE(msg) {}
    #define TRACE1(msg, p1) {}
    #define TRACE2(msg, p1, p2) {}
    #define TRACE3(msg, p1, p2, p3) {}
    #define _TRACE(msg) {}
    #define _TRACE1(msg, p1) {}
    #define _TRACE2(msg, p1, p2) {}
    #define _TRACE3(msg, p1, p2, p3) {}
    #define _TRACE4(msg, p1, p2, p3, p4) {}
#endif

#ifdef TRACE_OBJECTS_ON
    void TraceObjectCreation(char * classname, void * thiz);
    void TraceObjectDestruction(void * thiz);
    void TraceObjectCall(void * thiz, char * methodname);
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
/*
    #define TRACE_OBJECT_ENSURE_DESTRUCTION_WITH_STACK_CMYCOMPTR(object)    \
                    {                                                       \
                        void * __p = &(*object);                            \
                        object.Release();                                   \
                        TraceObjectEnsureDestruction((Object*)__p);         \
                    }
*/
#else
    #define TRACE_OBJECT_CREATION(classname) {}
    #define TRACE_OBJECT_DESTRUCTION {}
    #define TRACE_OBJECT_CALL(methodname) {}
    #define TRACE_CLASS_CHECK_UNKNOWN_IMPL_DESTRUCTION(classname) {}
//    #define TRACE_OBJECT_ENSURE_DESTRUCTION_WITH_STACK_CMYCOMPTR(object) {}
#endif

typedef UINT32 (WINAPI * CreateObjectFunc)(const GUID *clsID,
		const GUID *interfaceID, void **outObject);


/**
 * Fatal error
 */
void fatal(char * fmt, ...);

extern CreateObjectFunc createObjectFunc;

class Object {
public:
#ifdef TRACE_OBJECTS_ON
    virtual ~Object() {
        TRACE_OBJECT_DESTRUCTION
    }
#endif // TRACE_OBJECTS_ON
};

class StackAllocatedObject : public Object
{
public:
    ULONG __m_RefCount;
    StackAllocatedObject(): __m_RefCount(0) {}
    STDMETHOD_(ULONG, AddRef)()
    {
        return ++__m_RefCount;
    }
    STDMETHOD_(ULONG, Release)()
    {
        if (--__m_RefCount < 0)
        {
#ifdef TRACE_OBJECTS_ON
            fatal("StackAllocatedObject : ref < 0 (this: 0x%08X)", (Object*)this);
#endif // TRACE_OBJECTS_ON
        }
        return __m_RefCount;
    }
};

//TODO Remove comments
/*
 * Return error message from error code
 */
//char * getSevenZipErrorMessage(HRESULT hresult);

/**
 * Throw SevenZipException with error message.
 */
//void ThrowSevenZipException(JNIEnv * env, char * fmt, ...);

/**
 * Throw SevenZipException with error message.
 */
//void ThrowSevenZipException(JNIEnv * env, HRESULT hresult, char * fmt, ...);

/**
 * Save last occurred exception '_env->ExceptionOccurred()'
 * in global variable. Next call to ThrowSevenZipException(...) will set
 * 'lastOccurredException' as cause.
 * 
 * If _env->ExceptionOccurred() returns NULL,
 * last occurred exception will be set to NULL. 
 */
//void SaveLastOccurredException(JNIEnv * env);

/**
 * Load 7-Zip DLL.
 * 
 * Return: NULL - ok, else error message
 */
char * load7ZipLibrary(CreateObjectFunc * createObjectFunc);


#endif /*SEVENZIPJBINDING_H_*/
