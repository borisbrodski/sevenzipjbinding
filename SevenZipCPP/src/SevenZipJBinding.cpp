//============================================================================
// Name        : SevenZipBinding.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include "StdAfx.h"
#include <initguid.h>

#include "SevenZipJBinding.h"

using namespace std;


bool g_IsNT = false;


// {23170F69-40C1-278A-1000-000110070000}
DEFINE_GUID(CLSID_CFormat7z, 0x23170F69, 0x40C1, 0x278A, 0x10, 0x00, 0x00,
		0x01, 0x10, /*0x07*/0x01, 0x00, 0x00);
 
// {23170F69-40C1-278A-1000-000100030000}
//DEFINE_GUID(CLSID_CAgentArchiveHandler, 
//  0x23170F69, 0x40C1, 0x278A, 0x10, 0x00, 0x00, 0x01, 0x00, 0x03, 0x00, 0x00);


//DEFINE_GUID(IID_XXX, 0x23170F69, 0x40C1, 0x278A, 0x00, 0x00, 0x00, 0x09, 0x00, 0x02, 0x00, 0x00);


int main() {
	CMyComPtr<IInArchive> archive;
	/*
	 CMyComPtr<CAgent> agent;
	 
	 int retcode = createObjectFunc(&CLSID_CAgentArchiveHandler, &IID_XXX, (void **)&agent);
	 if (retcode != S_OK) {
	 char * name;
	 switch (retcode) {
	 case  CLASS_E_CLASSNOTAVAILABLE:
	 name = "CLASS_E_CLASSNOTAVAILABLE";
	 break;
	 case E_NOINTERFACE:
	 name = "E_NOINTERFACE";
	 break;
	 default:
	 name = "unknown";
	 }
	 printf("Can not get agent. Retcode: %08X (%s)\n", retcode, name);
	 printf("Last error: %08X", GetLastError());
	 return 1;
	 }
	 
	 */

	if (createObjectFunc(&CLSID_CFormat7z, &IID_IInArchive, (void **)&archive)
			!= S_OK) {
		printf("Can not get class object");
		return 1;
	}

	printf("Successfull!\n\n");

	CInFileStream *fileSpec = new CInFileStream;
	CMyComPtr<IInStream> file = fileSpec;

	if (!fileSpec->Open("test.zip")) {
		printf("Can not open archive file\n");
		return 1;
	}
	if (archive->Open(file, 0, 0) != S_OK) {
		printf("Problems...");
		return 0;
	}
	/*
	 UInt32 countOfProperties;
	 archive->GetNumberOfArchiveProperties(&countOfProperties);
	 for (UInt32 i = 0; i < countOfProperties; i++) {
	 BSTR name;
	 PROPID id;
	 VARTYPE type;
	 archive->GetArchivePropertyInfo(i, &name, &id, &type);
	 printf("%i name: '%S'\n", i, name);
	 }
	 */

	UInt32 numItems = 0;
	archive->GetNumberOfItems(&numItems);
	printf("Count of items: %i\n", numItems);
	for (UInt32 i = 0; i < numItems; i++) {
		NWindows::NCOM::CPropVariant propVariant;
		archive->GetProperty(i, kpidPath, &propVariant);
		UString s = ConvertPropVariantToString(propVariant);
		printf("%s\n", (LPCSTR)GetOemString(s));

		JavaArchiveExtractCallback aec(new JavaSequentialOutStream);
		archive->Extract(&i, 1, 0, &aec);
	}

	return 0;
}
