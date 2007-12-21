#ifndef SEVENZIPJBINDING_H_
#define SEVENZIPJBINDING_H_



#define DLLFILENAME "7z.dll"


typedef UINT32 (WINAPI * CreateObjectFunc)(const GUID *clsID,
		const GUID *interfaceID, void **outObject);

extern CreateObjectFunc createObjectFunc;


#endif /*SEVENZIPJBINDING_H_*/
