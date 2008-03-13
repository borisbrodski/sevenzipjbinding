#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_



#define DLLFILENAME "7z.dll"

#define IN_ARCHIVE_IMPL "net/sf/sevenzip/impl/InArchiveImpl"
#define IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInstance"

#define PROPERTYINFO_CLASS "net/sf/sevenzip/PropertyInfo"
#define PROPID_CLASS "net/sf/sevenzip/PropID"

#define SEQUENTIALOUTSTREAM_CLASS		"net/sf/sevenzip/SequentialOutStream"
#define EXTRACTASKMODE_CLASS			"net/sf/sevenzip/ExtractAskMode"
#define EXTRACTOPERATIONRESULT_CLASS	"net/sf/sevenzip/ExtractOperationResult"

typedef UINT32 (WINAPI * CreateObjectFunc)(const GUID *clsID,
		const GUID *interfaceID, void **outObject);

extern CreateObjectFunc createObjectFunc;

/*
 * Return error message from error code
 */
char * getSevenZipErrorMessage(HRESULT hresult);



#endif /*SEVENZIPJBINDING_H_*/
