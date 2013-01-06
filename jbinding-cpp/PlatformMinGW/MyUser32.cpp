#include <ctype.h>
#include <stdlib.h>
#include <string.h>

typedef char * LPSTR;
#define WINAPI __stdcall
typedef unsigned short WORD;
typedef unsigned long DWORD;
typedef const char *LPCSTR;


extern "C" {

LPSTR WINAPI CharPrevExA(
  WORD CodePage,
  LPCSTR lpStart,
  LPCSTR lpCurrentChar,
  DWORD dwFlags
) {
	if (lpStart == lpCurrentChar) {
		return (LPSTR)lpStart;
	}

	return (LPSTR)(lpCurrentChar - 1);
}


LPSTR WINAPI CharNextA( LPCSTR ptr ) {
  if (!*ptr)
	return (LPSTR)ptr;
// #ifdef HAVE_MBRTOWC
//  if (global_use_utf16_conversion)
//  {
//    wchar_t wc;
//    size_t len  = mbrtowc(&wc,ptr,MB_LEN_MAX,0);  // mbrtowc stales on some configurations.
//    if (len >= 1) return (LPSTR)(ptr + len);
//    printf("INTERNAL ERROR - CharNextA\n");
//    exit(EXIT_FAILURE);
//  } else {
//    return (LPSTR)(ptr + 1);
//  }
//#else
  return (LPSTR)(ptr + 1); // p7zip search only for ASCII characters like '/' so no need to worry about current locale
//#endif

/* My Implementation using mblen
  if (!*ptr)
    return (LPSTR)ptr;
  mblen(NULL, 0); // reset mblen
  int len = mblen(ptr, strlen(ptr));
  return (LPSTR)(ptr + len);
 */
}

LPSTR WINAPI CharPrevA( LPCSTR start, LPCSTR ptr ) { // OK for MBS
  while (*start && (start < ptr)) {
    LPCSTR next = CharNextA( start );
    if (next >= ptr)
      break;
    start = next;
  }
  return (LPSTR)start;
}

wchar_t WINAPI CharLowerW(wchar_t c)
{
   return towlower(c);
}

wchar_t WINAPI CharUpperW(wchar_t c)
{
   return towupper(c);
}

char WINAPI CharUpperA(char c)
{
	return toupper(c);
}


char WINAPI CharLowerA(char c)
{
	return tolower(c);
}


}
