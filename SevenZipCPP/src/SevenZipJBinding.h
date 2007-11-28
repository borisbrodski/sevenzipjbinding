#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_


#include "Common/StringConvert.h"
#include "7zip/UI/Common/ArchiveExtractCallback.h"

#include "7zip/Common/FileStreams.h"
#include "Windows/PropVariant.h"
#include "Windows/PropVariantConversions.h"

#include "JavaSequentialOutStream.h"
#include "JavaArchiveExtractCallback.h"
#include "7zip/Archive/IArchive.h"


#define DLLFILENAME "7z.dll"


typedef UINT32 (WINAPI * CreateObjectFunc)(const GUID *clsID,
		const GUID *interfaceID, void **outObject);

extern CreateObjectFunc createObjectFunc;


#endif /*SEVENZIPJBINDING_H_*/
