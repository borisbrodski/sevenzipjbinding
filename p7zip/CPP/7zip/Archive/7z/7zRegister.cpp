// 7zRegister.cpp

#include "StdAfx.h"

#include "../../Common/RegisterArc.h"

#include "7zHandler.h"
static IInArchive *CreateArc() { return new NArchive::N7z::CHandler; }
#ifndef EXTRACT_ONLY
static IOutArchive *CreateArcOut() { return new NArchive::N7z::CHandler; }
#else
#define CreateArcOut 0
#endif

static CArcInfo g_ArcInfo =
 { L"7z", L"7z", 0, 7,
    // To insure kSignature[0x1A]!= 0 and  kSignature[0x1B] != 0
    {'7', 'z', 0xBC, 0xAF, 0x27, 0x1C, 1 , 1 , 1, 1 ,1 ,1 , 1, 1 ,1 ,1 ,1 ,1 , 1, 1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 , 1},
    6, false, CreateArc, CreateArcOut };

REGISTER_ARC(7z)
