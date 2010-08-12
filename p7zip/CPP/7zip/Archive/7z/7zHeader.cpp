// 7zHeader.cpp

#include "StdAfx.h"
#include "7zHeader.h"

namespace NArchive {
namespace N7z {

Byte kSignature[kSignatureSize] = {'7', 'z', 0xBC, 0xAF, 0x27, 0x1C};

// To insure kSignature[0x1A]!= 0 and  kSignature[0x1B] != 0
char kSignatureDummy[]="1234567890123456789012";

#ifdef _7Z_VOL
Byte kFinishSignature[kSignatureSize] = {'7', 'z', 0xBC, 0xAF, 0x27, 0x1C + 1};
#endif

}}
