#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_

#define DLLFILENAME "7z.dll"


#define SEVEN_ZIP_EXCEPTION "net/sf/sevenzip/SevenZipException"
#define SEVEN_ZIP_EXCEPTION_T JAVA_MAKE_SIGNATURE_TYPE(SEVEN_ZIP_EXCEPTION)


#define IN_ARCHIVE_IMPL "net/sf/sevenzip/impl/InArchiveImpl"
#define IN_ARCHIVE_IMPL_T JAVA_MAKE_SIGNATURE_TYPE(IN_ARCHIVE_IMPL)
#define IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInstance"

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


typedef UINT32 (WINAPI * CreateObjectFunc)(const GUID *clsID,
		const GUID *interfaceID, void **outObject);


extern CreateObjectFunc createObjectFunc;

/*
 * Return error message from error code
 */
char * getSevenZipErrorMessage(HRESULT hresult);

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, char * fmt, ...);

/**
 * Throw SevenZipException with error message.
 */
void ThrowSevenZipException(JNIEnv * env, HRESULT hresult, char * fmt, ...);

/**
 * Save last occurred exception '_env->ExceptionOccurred()'
 * in global variable. Next call to ThrowSevenZipException(...) will set
 * 'lastOccurredException' as cause.
 * 
 * If _env->ExceptionOccurred() returns NULL,
 * last occurred exception will be set to NULL. 
 */
void SaveLastOccurredException(JNIEnv * env);

/**
 * Load 7-Zip DLL.
 * 
 * Return: NULL - ok, else error message
 */
char * load7ZipLibrary(CreateObjectFunc * createObjectFunc);


#endif /*SEVENZIPJBINDING_H_*/
