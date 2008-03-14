#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_

#define DLLFILENAME "7z.dll"


#define SEVEN_ZIP_EXCEPTION "net/sf/sevenzip/SevenZipException"


#define IN_ARCHIVE_IMPL "net/sf/sevenzip/impl/InArchiveImpl"
#define IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInstance"

#define PROPERTYINFO_CLASS "net/sf/sevenzip/PropertyInfo"
#define PROPID_CLASS "net/sf/sevenzip/PropID"

#define SEQUENTIALOUTSTREAM_CLASS		"net/sf/sevenzip/ISequentialOutStream"
#define EXTRACTASKMODE_CLASS			"net/sf/sevenzip/ExtractAskMode"
#define EXTRACTOPERATIONRESULT_CLASS	"net/sf/sevenzip/ExtractOperationResult"

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
 * Load 7-Zip DLL.
 * 
 * Return: NULL - ok, else error message
 */
char * load7ZipLibrary(CreateObjectFunc * createObjectFunc);


#endif /*SEVENZIPJBINDING_H_*/
