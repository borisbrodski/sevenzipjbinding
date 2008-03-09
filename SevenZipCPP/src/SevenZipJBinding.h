#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_



#define DLLFILENAME "7z.dll"

#define IN_ARCHIVE_IMPL "net/sf/sevenzip/impl/InArchiveImpl"
#define IN_ARCHIVE_IMPL_OBJ_ATTRIBUTE "sevenZipArchiveInstance"

#define PROPERTYINFO_CLASS "net/sf/sevenzip/PropertyInfo"
#define PROPID_CLASS "net/sf/sevenzip/PropID"


typedef UINT32 (WINAPI * CreateObjectFunc)(const GUID *clsID,
		const GUID *interfaceID, void **outObject);

extern CreateObjectFunc createObjectFunc;



#endif /*SEVENZIPJBINDING_H_*/
