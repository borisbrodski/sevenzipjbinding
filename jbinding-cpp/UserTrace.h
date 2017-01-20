/*
 * UserTrace.h
 *
 *  Created on: Sep 17, 2015
 *      Author: Boris Brodski
 */

#ifndef USERTRACE_H_
#define USERTRACE_H_

#include "Common/IntToString.h"

inline AString UIntToString(UInt32 v)
{
  char sz[32];
  ConvertUInt64ToString(v, sz);
  return sz;
}

inline UString operator<<(UString msg, const wchar_t * str) {
    return msg + UString(str);
}

inline UString operator<<(UString msg, UInt32 number) {
    wchar_t sz[64];
    ConvertUInt32ToString(number, sz);
    return msg + UString(sz);
}

inline UString operator<<(UString msg, Int32 number) {
    wchar_t sz[64];
    ConvertInt64ToString(number, sz);
    return msg + UString(sz);
}

inline UString operator<<(UString msg, BSTR bstr) {
    UString ustring;
    ustring.SetFromBstr(bstr);
    return msg + ustring;
}

inline UString operator<<(UString msg, CMyComBSTR myComBSTR) {
    return msg << *(&myComBSTR);
}

bool isUserTraceEnabled(JNIEnvInstance & jniEnvInstance, jobject thiz);
void userTrace(JNIEnvInstance & jniEnvInstance, jobject thiz, UString msg);

#endif /* USERTRACE_H_ */
