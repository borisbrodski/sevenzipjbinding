#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_

#include "StdAfx.h"

#include "Common/IntToString.h"

#include "7zip/UI/Common/ArchiveExtractCallback.h"
#include "7zip/UI/Common/PropIDUtils.h"
#include "7zip/Common/FileStreams.h"
#include "Windows/PropVariant.h"
#include "7zip/Archive/IArchive.h"

#include <jni.h>

#include "Debug.h"

#define P7ZIP_VERSION_MAJOR 4
#define P7ZIP_VERSION_MINOR 65

#define SEVENZIPJBINDING_VERSION_MAJOR 0
#define SEVENZIPJBINDING_VERSION_MINOR 5

#define SEVENZIPJBINDING_LIBRARY_NAME_FILENAME "./7z.dll"


#define SEVEN_ZIP_EXCEPTION "net/sf/sevenzipjbinding/SevenZipException"
#define SEVEN_ZIP_EXCEPTION_T JAVA_MAKE_SIGNATURE_TYPE(SEVEN_ZIP_EXCEPTION)

// TODO Remove
// #define IN_ARCHIVE_IMPL "net/sf/sevenzipjbinding/impl/InArchiveImpl"
//#define IN_ARCHIVE_IMPL_T JAVA_MAKE_SIGNATURE_TYPE(IN_ARCHIVE_IMPL)
//#define IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInstance"
//#define IN_STREAM_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInStreamInstance"


// TODO Switch to use JavaStaticInfo for this
#define PROPERTYINFO_CLASS "net/sf/sevenzipjbinding/PropertyInfo"
#define PROPERTYINFO_CLASS_T JAVA_MAKE_SIGNATURE_TYPE(PROPERTYINFO_CLASS)

#define PROPID_CLASS "net/sf/sevenzipjbinding/PropID"
#define PROPID_CLASS_T JAVA_MAKE_SIGNATURE_TYPE(PROPID_CLASS)

#define SEQUENTIALOUTSTREAM_CLASS		"net/sf/sevenzipjbinding/ISequentialOutStream"
#define SEQUENTIALOUTSTREAM_CLASS_T		JAVA_MAKE_SIGNATURE_TYPE(SEQUENTIALOUTSTREAM_CLASS)

#define SEQUENTIALINSTREAM_CLASS		"net/sf/sevenzipjbinding/ISequentialInStream"
#define SEQUENTIALINSTREAM_CLASS_T		JAVA_MAKE_SIGNATURE_TYPE(SEQUENTIALINSTREAM_CLASS)

#define INSTREAM_CLASS		            "net/sf/sevenzipjbinding/IInStream"
#define INSTREAM_CLASS_T		        JAVA_MAKE_SIGNATURE_TYPE(INSTREAM_CLASS)

#define CRYPTOGETTEXTPASSWORD_CLASS	    "net/sf/sevenzipjbinding/ICryptoGetTextPassword"
#define CRYPTOGETTEXTPASSWORD_CLASS_T   JAVA_MAKE_SIGNATURE_TYPE(CRYPTOGETTEXTPASSWORD_CLASS)

#define ARCHIVEOPENVOLUMECALLBACK_CLASS	    "net/sf/sevenzipjbinding/IArchiveOpenVolumeCallback"
#define ARCHIVEOPENVOLUMECALLBACK_CLASS_T   JAVA_MAKE_SIGNATURE_TYPE(ARCHIVEOPENVOLUMECALLBACK_CLASS)

#define EXTRACTASKMODE_CLASS			"net/sf/sevenzipjbinding/ExtractAskMode"
#define EXTRACTASKMODE_CLASS_T			JAVA_MAKE_SIGNATURE_TYPE(EXTRACTASKMODE_CLASS)

#define EXTRACTOPERATIONRESULT_CLASS	"net/sf/sevenzipjbinding/ExtractOperationResult"
#define EXTRACTOPERATIONRESULT_CLASS_T	JAVA_MAKE_SIGNATURE_TYPE(EXTRACTOPERATIONRESULT_CLASS)

#define FATAL fatal
#define FATALIF(cond, fmt) { if (cond) fatal(fmt); }
#define FATALIF1(cond, fmt, p1) { if (cond) fatal(fmt, p1); }
#define FATALIF2(cond, fmt, p1, p2) { if (cond) fatal(fmt, p1, p2); }
#define FATALIF3(cond, fmt, p1, p2, p3) { if (cond) fatal(fmt, p1, p2, p3); }
#define FATALIF4(cond, fmt, p1, p2, p3, p4) { if (cond) fatal(fmt, p1, p2, p3, p4); }


#define TRY try {
#define CATCH_SEVEN_ZIP_EXCEPTION(nativeMethodContext, returnvalue)             \
    } catch(SevenZipException & sevenZipException)                              \
    {TRACE("Exception catched: " << &sevenZipException);                        \
    (nativeMethodContext).ThrowSevenZipException(&sevenZipException);}          \
    return returnvalue;

#define CATCH_SEVEN_ZIP_EXCEPTION_WITHOUT_RETURN(nativeMethodContext)    		\
    } catch(SevenZipException & sevenZipException)                              \
    {TRACE1("Exception catched: " << &sevenZipException);                       \
    (nativeMethodContext).ThrowSevenZipException(&sevenZipException);}


#define MIN(a,b) ((a) > (b) ? (b) : (a))
#define MAX(a,b) ((a) < (b) ? (b) : (a))

//typedef UINT32 (WINAPI * CreateObjectFunc)(const GUID *clsID,
//		const GUID *interfaceID, void **outObject);


typedef HRESULT (*CreateObjectFunc)(const GUID *clsid, const GUID *iid, void **outObject);

/**
 * Fatal error
 */
void fatal(const char * fmt, ...);

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
        if (--__m_RefCount != 0)
        {
#ifdef TRACE_OBJECTS_ON
            fatal("StackAllocatedObject : ref < 0 (this: 0x%08X)", (Object*)this);
#endif // TRACE_OBJECTS_ON
        }
        return __m_RefCount;
    }
};



#include "JavaStaticInfo.h" // TODO Move from here
#include "JavaStatInfos/JavaStandardLibrary.h" // TODO Move from here

#endif /*SEVENZIPJBINDING_H_*/
