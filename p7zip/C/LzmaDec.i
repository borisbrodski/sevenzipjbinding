# 1 "LzmaDec.c"
# 1 "<built-in>"
# 1 "<command-line>"
# 1 "/usr/include/stdc-predef.h" 1 3 4
# 1 "<command-line>" 2
# 1 "LzmaDec.c"



# 1 "Precomp.h" 1






# 1 "Compiler.h" 1
# 8 "Precomp.h" 2
# 5 "LzmaDec.c" 2

# 1 "LzmaDec.h" 1






# 1 "7zTypes.h" 1
# 11 "7zTypes.h"
# 1 "/usr/lib/gcc/x86_64-linux-gnu/4.9/include/stddef.h" 1 3 4
# 147 "/usr/lib/gcc/x86_64-linux-gnu/4.9/include/stddef.h" 3 4
typedef long int ptrdiff_t;
# 212 "/usr/lib/gcc/x86_64-linux-gnu/4.9/include/stddef.h" 3 4
typedef long unsigned int size_t;
# 324 "/usr/lib/gcc/x86_64-linux-gnu/4.9/include/stddef.h" 3 4
typedef int wchar_t;
# 12 "7zTypes.h" 2
# 23 "7zTypes.h"

# 43 "7zTypes.h"
typedef int SRes;





typedef int WRes;






typedef unsigned char Byte;
typedef short Int16;
typedef unsigned short UInt16;





typedef int Int32;
typedef unsigned int UInt32;
# 83 "7zTypes.h"
typedef long long int Int64;
typedef unsigned long long int UInt64;
# 93 "7zTypes.h"
typedef size_t SizeT;


typedef int Bool;
# 129 "7zTypes.h"
typedef struct
{
  Byte (*Read)(void *p);
} IByteIn;

typedef struct
{
  void (*Write)(void *p, Byte b);
} IByteOut;

typedef struct
{
  SRes (*Read)(void *p, void *buf, size_t *size);


} ISeqInStream;


SRes SeqInStream_Read(ISeqInStream *stream, void *buf, size_t size);
SRes SeqInStream_Read2(ISeqInStream *stream, void *buf, size_t size, SRes errorType);
SRes SeqInStream_ReadByte(ISeqInStream *stream, Byte *buf);

typedef struct
{
  size_t (*Write)(void *p, const void *buf, size_t size);


} ISeqOutStream;

typedef enum
{
  SZ_SEEK_SET = 0,
  SZ_SEEK_CUR = 1,
  SZ_SEEK_END = 2
} ESzSeek;

typedef struct
{
  SRes (*Read)(void *p, void *buf, size_t *size);
  SRes (*Seek)(void *p, Int64 *pos, ESzSeek origin);
} ISeekInStream;

typedef struct
{
  SRes (*Look)(void *p, const void **buf, size_t *size);



  SRes (*Skip)(void *p, size_t offset);


  SRes (*Read)(void *p, void *buf, size_t *size);

  SRes (*Seek)(void *p, Int64 *pos, ESzSeek origin);
} ILookInStream;

SRes LookInStream_LookRead(ILookInStream *stream, void *buf, size_t *size);
SRes LookInStream_SeekTo(ILookInStream *stream, UInt64 offset);


SRes LookInStream_Read2(ILookInStream *stream, void *buf, size_t size, SRes errorType);
SRes LookInStream_Read(ILookInStream *stream, void *buf, size_t size);



typedef struct
{
  ILookInStream s;
  ISeekInStream *realStream;
  size_t pos;
  size_t size;
  Byte buf[(1 << 14)];
} CLookToRead;

void LookToRead_CreateVTable(CLookToRead *p, int lookahead);
void LookToRead_Init(CLookToRead *p);

typedef struct
{
  ISeqInStream s;
  ILookInStream *realStream;
} CSecToLook;

void SecToLook_CreateVTable(CSecToLook *p);

typedef struct
{
  ISeqInStream s;
  ILookInStream *realStream;
} CSecToRead;

void SecToRead_CreateVTable(CSecToRead *p);

typedef struct
{
  SRes (*Progress)(void *p, UInt64 inSize, UInt64 outSize);


} ICompressProgress;

typedef struct
{
  void *(*Alloc)(void *p, size_t size);
  void (*Free)(void *p, void *address);
} ISzAlloc;
# 254 "7zTypes.h"

# 8 "LzmaDec.h" 2


# 26 "LzmaDec.h"
typedef struct _CLzmaProps
{
  unsigned lc, lp, pb;
  UInt32 dicSize;
} CLzmaProps;







SRes LzmaProps_Decode(CLzmaProps *p, const Byte *data, unsigned size);
# 48 "LzmaDec.h"
typedef struct
{
  CLzmaProps prop;
  UInt16 *probs;
  Byte *dic;
  const Byte *buf;
  UInt32 range, code;
  SizeT dicPos;
  SizeT dicBufSize;
  UInt32 processedPos;
  UInt32 checkDicSize;
  unsigned state;
  UInt32 reps[4];
  unsigned remainLen;
  int needFlush;
  int needInitState;
  UInt32 numProbs;
  unsigned tempBufSize;
  Byte tempBuf[20];
} CLzmaDec;



void LzmaDec_Init(CLzmaDec *p);





typedef enum
{
  LZMA_FINISH_ANY,
  LZMA_FINISH_END
} ELzmaFinishMode;
# 98 "LzmaDec.h"
typedef enum
{
  LZMA_STATUS_NOT_SPECIFIED,
  LZMA_STATUS_FINISHED_WITH_MARK,
  LZMA_STATUS_NOT_FINISHED,
  LZMA_STATUS_NEEDS_MORE_INPUT,
  LZMA_STATUS_MAYBE_FINISHED_WITHOUT_MARK
} ELzmaStatus;
# 132 "LzmaDec.h"
SRes LzmaDec_AllocateProbs(CLzmaDec *p, const Byte *props, unsigned propsSize, ISzAlloc *alloc);
void LzmaDec_FreeProbs(CLzmaDec *p, ISzAlloc *alloc);

SRes LzmaDec_Allocate(CLzmaDec *state, const Byte *prop, unsigned propsSize, ISzAlloc *alloc);
void LzmaDec_Free(CLzmaDec *state, ISzAlloc *alloc);
# 179 "LzmaDec.h"
SRes LzmaDec_DecodeToDic(CLzmaDec *p, SizeT dicLimit,
    const Byte *src, SizeT *srcLen, ELzmaFinishMode finishMode, ELzmaStatus *status);
# 196 "LzmaDec.h"
SRes LzmaDec_DecodeToBuf(CLzmaDec *p, Byte *dest, SizeT *destLen,
    const Byte *src, SizeT *srcLen, ELzmaFinishMode finishMode, ELzmaStatus *status);
# 221 "LzmaDec.h"
SRes LzmaDecode(Byte *dest, SizeT *destLen, const Byte *src, SizeT *srcLen,
    const Byte *propData, unsigned propSize, ELzmaFinishMode finishMode,
    ELzmaStatus *status, ISzAlloc *alloc);


# 7 "LzmaDec.c" 2

# 1 "/usr/include/string.h" 1 3 4
# 25 "/usr/include/string.h" 3 4
# 1 "/usr/include/features.h" 1 3 4
# 364 "/usr/include/features.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/sys/cdefs.h" 1 3 4
# 402 "/usr/include/x86_64-linux-gnu/sys/cdefs.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/wordsize.h" 1 3 4
# 403 "/usr/include/x86_64-linux-gnu/sys/cdefs.h" 2 3 4
# 365 "/usr/include/features.h" 2 3 4
# 388 "/usr/include/features.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/gnu/stubs.h" 1 3 4
# 10 "/usr/include/x86_64-linux-gnu/gnu/stubs.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/gnu/stubs-64.h" 1 3 4
# 11 "/usr/include/x86_64-linux-gnu/gnu/stubs.h" 2 3 4
# 389 "/usr/include/features.h" 2 3 4
# 26 "/usr/include/string.h" 2 3 4






# 1 "/usr/lib/gcc/x86_64-linux-gnu/4.9/include/stddef.h" 1 3 4
# 33 "/usr/include/string.h" 2 3 4
# 44 "/usr/include/string.h" 3 4


extern void *memcpy (void *__restrict __dest, const void *__restrict __src,
       size_t __n) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));


extern void *memmove (void *__dest, const void *__src, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));






extern void *memccpy (void *__restrict __dest, const void *__restrict __src,
        int __c, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));





extern void *memset (void *__s, int __c, size_t __n) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1)));


extern int memcmp (const void *__s1, const void *__s2, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));
# 96 "/usr/include/string.h" 3 4
extern void *memchr (const void *__s, int __c, size_t __n)
      __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));


# 127 "/usr/include/string.h" 3 4


extern char *strcpy (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));

extern char *strncpy (char *__restrict __dest,
        const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));


extern char *strcat (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));

extern char *strncat (char *__restrict __dest, const char *__restrict __src,
        size_t __n) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));


extern int strcmp (const char *__s1, const char *__s2)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

extern int strncmp (const char *__s1, const char *__s2, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));


extern int strcoll (const char *__s1, const char *__s2)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));

extern size_t strxfrm (char *__restrict __dest,
         const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (2)));






# 1 "/usr/include/xlocale.h" 1 3 4
# 27 "/usr/include/xlocale.h" 3 4
typedef struct __locale_struct
{

  struct __locale_data *__locales[13];


  const unsigned short int *__ctype_b;
  const int *__ctype_tolower;
  const int *__ctype_toupper;


  const char *__names[13];
} *__locale_t;


typedef __locale_t locale_t;
# 164 "/usr/include/string.h" 2 3 4


extern int strcoll_l (const char *__s1, const char *__s2, __locale_t __l)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2, 3)));

extern size_t strxfrm_l (char *__dest, const char *__src, size_t __n,
    __locale_t __l) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (2, 4)));




extern char *strdup (const char *__s)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__malloc__)) __attribute__ ((__nonnull__ (1)));






extern char *strndup (const char *__string, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__malloc__)) __attribute__ ((__nonnull__ (1)));
# 210 "/usr/include/string.h" 3 4

# 235 "/usr/include/string.h" 3 4
extern char *strchr (const char *__s, int __c)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));
# 262 "/usr/include/string.h" 3 4
extern char *strrchr (const char *__s, int __c)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));


# 281 "/usr/include/string.h" 3 4



extern size_t strcspn (const char *__s, const char *__reject)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));


extern size_t strspn (const char *__s, const char *__accept)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));
# 314 "/usr/include/string.h" 3 4
extern char *strpbrk (const char *__s, const char *__accept)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));
# 341 "/usr/include/string.h" 3 4
extern char *strstr (const char *__haystack, const char *__needle)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));




extern char *strtok (char *__restrict __s, const char *__restrict __delim)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (2)));




extern char *__strtok_r (char *__restrict __s,
    const char *__restrict __delim,
    char **__restrict __save_ptr)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (2, 3)));

extern char *strtok_r (char *__restrict __s, const char *__restrict __delim,
         char **__restrict __save_ptr)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (2, 3)));
# 396 "/usr/include/string.h" 3 4


extern size_t strlen (const char *__s)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));





extern size_t strnlen (const char *__string, size_t __maxlen)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));





extern char *strerror (int __errnum) __attribute__ ((__nothrow__ , __leaf__));

# 426 "/usr/include/string.h" 3 4
extern int strerror_r (int __errnum, char *__buf, size_t __buflen) __asm__ ("" "__xpg_strerror_r") __attribute__ ((__nothrow__ , __leaf__))

                        __attribute__ ((__nonnull__ (2)));
# 444 "/usr/include/string.h" 3 4
extern char *strerror_l (int __errnum, __locale_t __l) __attribute__ ((__nothrow__ , __leaf__));





extern void __bzero (void *__s, size_t __n) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1)));



extern void bcopy (const void *__src, void *__dest, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));


extern void bzero (void *__s, size_t __n) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1)));


extern int bcmp (const void *__s1, const void *__s2, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));
# 488 "/usr/include/string.h" 3 4
extern char *index (const char *__s, int __c)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));
# 516 "/usr/include/string.h" 3 4
extern char *rindex (const char *__s, int __c)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1)));




extern int ffs (int __i) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__const__));
# 533 "/usr/include/string.h" 3 4
extern int strcasecmp (const char *__s1, const char *__s2)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));


extern int strncasecmp (const char *__s1, const char *__s2, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1, 2)));
# 556 "/usr/include/string.h" 3 4
extern char *strsep (char **__restrict __stringp,
       const char *__restrict __delim)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));




extern char *strsignal (int __sig) __attribute__ ((__nothrow__ , __leaf__));


extern char *__stpcpy (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));
extern char *stpcpy (char *__restrict __dest, const char *__restrict __src)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));



extern char *__stpncpy (char *__restrict __dest,
   const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));
extern char *stpncpy (char *__restrict __dest,
        const char *__restrict __src, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__nonnull__ (1, 2)));
# 631 "/usr/include/string.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/string.h" 1 3 4
# 632 "/usr/include/string.h" 2 3 4


# 1 "/usr/include/x86_64-linux-gnu/bits/string2.h" 1 3 4
# 51 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
# 1 "/usr/include/endian.h" 1 3 4
# 36 "/usr/include/endian.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/endian.h" 1 3 4
# 37 "/usr/include/endian.h" 2 3 4
# 60 "/usr/include/endian.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/byteswap.h" 1 3 4
# 27 "/usr/include/x86_64-linux-gnu/bits/byteswap.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/types.h" 1 3 4
# 27 "/usr/include/x86_64-linux-gnu/bits/types.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/wordsize.h" 1 3 4
# 28 "/usr/include/x86_64-linux-gnu/bits/types.h" 2 3 4


typedef unsigned char __u_char;
typedef unsigned short int __u_short;
typedef unsigned int __u_int;
typedef unsigned long int __u_long;


typedef signed char __int8_t;
typedef unsigned char __uint8_t;
typedef signed short int __int16_t;
typedef unsigned short int __uint16_t;
typedef signed int __int32_t;
typedef unsigned int __uint32_t;

typedef signed long int __int64_t;
typedef unsigned long int __uint64_t;







typedef long int __quad_t;
typedef unsigned long int __u_quad_t;
# 121 "/usr/include/x86_64-linux-gnu/bits/types.h" 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/typesizes.h" 1 3 4
# 122 "/usr/include/x86_64-linux-gnu/bits/types.h" 2 3 4


typedef unsigned long int __dev_t;
typedef unsigned int __uid_t;
typedef unsigned int __gid_t;
typedef unsigned long int __ino_t;
typedef unsigned long int __ino64_t;
typedef unsigned int __mode_t;
typedef unsigned long int __nlink_t;
typedef long int __off_t;
typedef long int __off64_t;
typedef int __pid_t;
typedef struct { int __val[2]; } __fsid_t;
typedef long int __clock_t;
typedef unsigned long int __rlim_t;
typedef unsigned long int __rlim64_t;
typedef unsigned int __id_t;
typedef long int __time_t;
typedef unsigned int __useconds_t;
typedef long int __suseconds_t;

typedef int __daddr_t;
typedef int __key_t;


typedef int __clockid_t;


typedef void * __timer_t;


typedef long int __blksize_t;




typedef long int __blkcnt_t;
typedef long int __blkcnt64_t;


typedef unsigned long int __fsblkcnt_t;
typedef unsigned long int __fsblkcnt64_t;


typedef unsigned long int __fsfilcnt_t;
typedef unsigned long int __fsfilcnt64_t;


typedef long int __fsword_t;

typedef long int __ssize_t;


typedef long int __syscall_slong_t;

typedef unsigned long int __syscall_ulong_t;



typedef __off64_t __loff_t;
typedef __quad_t *__qaddr_t;
typedef char *__caddr_t;


typedef long int __intptr_t;


typedef unsigned int __socklen_t;
# 28 "/usr/include/x86_64-linux-gnu/bits/byteswap.h" 2 3 4
# 1 "/usr/include/x86_64-linux-gnu/bits/wordsize.h" 1 3 4
# 29 "/usr/include/x86_64-linux-gnu/bits/byteswap.h" 2 3 4






# 1 "/usr/include/x86_64-linux-gnu/bits/byteswap-16.h" 1 3 4
# 36 "/usr/include/x86_64-linux-gnu/bits/byteswap.h" 2 3 4
# 44 "/usr/include/x86_64-linux-gnu/bits/byteswap.h" 3 4
static __inline unsigned int
__bswap_32 (unsigned int __bsx)
{
  return __builtin_bswap32 (__bsx);
}
# 108 "/usr/include/x86_64-linux-gnu/bits/byteswap.h" 3 4
static __inline __uint64_t
__bswap_64 (__uint64_t __bsx)
{
  return __builtin_bswap64 (__bsx);
}
# 61 "/usr/include/endian.h" 2 3 4
# 52 "/usr/include/x86_64-linux-gnu/bits/string2.h" 2 3 4
# 393 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern void *__rawmemchr (const void *__s, int __c);
# 945 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern __inline size_t __strcspn_c1 (const char *__s, int __reject);
extern __inline size_t
__strcspn_c1 (const char *__s, int __reject)
{
  size_t __result = 0;
  while (__s[__result] != '\0' && __s[__result] != __reject)
    ++__result;
  return __result;
}

extern __inline size_t __strcspn_c2 (const char *__s, int __reject1,
         int __reject2);
extern __inline size_t
__strcspn_c2 (const char *__s, int __reject1, int __reject2)
{
  size_t __result = 0;
  while (__s[__result] != '\0' && __s[__result] != __reject1
  && __s[__result] != __reject2)
    ++__result;
  return __result;
}

extern __inline size_t __strcspn_c3 (const char *__s, int __reject1,
         int __reject2, int __reject3);
extern __inline size_t
__strcspn_c3 (const char *__s, int __reject1, int __reject2,
       int __reject3)
{
  size_t __result = 0;
  while (__s[__result] != '\0' && __s[__result] != __reject1
  && __s[__result] != __reject2 && __s[__result] != __reject3)
    ++__result;
  return __result;
}
# 1021 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern __inline size_t __strspn_c1 (const char *__s, int __accept);
extern __inline size_t
__strspn_c1 (const char *__s, int __accept)
{
  size_t __result = 0;

  while (__s[__result] == __accept)
    ++__result;
  return __result;
}

extern __inline size_t __strspn_c2 (const char *__s, int __accept1,
        int __accept2);
extern __inline size_t
__strspn_c2 (const char *__s, int __accept1, int __accept2)
{
  size_t __result = 0;

  while (__s[__result] == __accept1 || __s[__result] == __accept2)
    ++__result;
  return __result;
}

extern __inline size_t __strspn_c3 (const char *__s, int __accept1,
        int __accept2, int __accept3);
extern __inline size_t
__strspn_c3 (const char *__s, int __accept1, int __accept2, int __accept3)
{
  size_t __result = 0;

  while (__s[__result] == __accept1 || __s[__result] == __accept2
  || __s[__result] == __accept3)
    ++__result;
  return __result;
}
# 1097 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern __inline char *__strpbrk_c2 (const char *__s, int __accept1,
        int __accept2);
extern __inline char *
__strpbrk_c2 (const char *__s, int __accept1, int __accept2)
{

  while (*__s != '\0' && *__s != __accept1 && *__s != __accept2)
    ++__s;
  return *__s == '\0' ? ((void *)0) : (char *) (size_t) __s;
}

extern __inline char *__strpbrk_c3 (const char *__s, int __accept1,
        int __accept2, int __accept3);
extern __inline char *
__strpbrk_c3 (const char *__s, int __accept1, int __accept2, int __accept3)
{

  while (*__s != '\0' && *__s != __accept1 && *__s != __accept2
  && *__s != __accept3)
    ++__s;
  return *__s == '\0' ? ((void *)0) : (char *) (size_t) __s;
}
# 1147 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern __inline char *__strtok_r_1c (char *__s, char __sep, char **__nextp);
extern __inline char *
__strtok_r_1c (char *__s, char __sep, char **__nextp)
{
  char *__result;
  if (__s == ((void *)0))
    __s = *__nextp;
  while (*__s == __sep)
    ++__s;
  __result = ((void *)0);
  if (*__s != '\0')
    {
      __result = __s++;
      while (*__s != '\0')
 if (*__s++ == __sep)
   {
     __s[-1] = '\0';
     break;
   }
    }
  *__nextp = __s;
  return __result;
}
# 1179 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern char *__strsep_g (char **__stringp, const char *__delim);
# 1197 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern __inline char *__strsep_1c (char **__s, char __reject);
extern __inline char *
__strsep_1c (char **__s, char __reject)
{
  char *__retval = *__s;
  if (__retval != ((void *)0) && (*__s = (__extension__ (__builtin_constant_p (__reject) && !__builtin_constant_p (__retval) && (__reject) == '\0' ? (char *) __rawmemchr (__retval, __reject) : __builtin_strchr (__retval, __reject)))) != ((void *)0))
    *(*__s)++ = '\0';
  return __retval;
}

extern __inline char *__strsep_2c (char **__s, char __reject1, char __reject2);
extern __inline char *
__strsep_2c (char **__s, char __reject1, char __reject2)
{
  char *__retval = *__s;
  if (__retval != ((void *)0))
    {
      char *__cp = __retval;
      while (1)
 {
   if (*__cp == '\0')
     {
       __cp = ((void *)0);
   break;
     }
   if (*__cp == __reject1 || *__cp == __reject2)
     {
       *__cp++ = '\0';
       break;
     }
   ++__cp;
 }
      *__s = __cp;
    }
  return __retval;
}

extern __inline char *__strsep_3c (char **__s, char __reject1, char __reject2,
       char __reject3);
extern __inline char *
__strsep_3c (char **__s, char __reject1, char __reject2, char __reject3)
{
  char *__retval = *__s;
  if (__retval != ((void *)0))
    {
      char *__cp = __retval;
      while (1)
 {
   if (*__cp == '\0')
     {
       __cp = ((void *)0);
   break;
     }
   if (*__cp == __reject1 || *__cp == __reject2 || *__cp == __reject3)
     {
       *__cp++ = '\0';
       break;
     }
   ++__cp;
 }
      *__s = __cp;
    }
  return __retval;
}
# 1273 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
# 1 "/usr/include/stdlib.h" 1 3 4
# 32 "/usr/include/stdlib.h" 3 4
# 1 "/usr/lib/gcc/x86_64-linux-gnu/4.9/include/stddef.h" 1 3 4
# 33 "/usr/include/stdlib.h" 2 3 4


# 464 "/usr/include/stdlib.h" 3 4


extern void *malloc (size_t __size) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__malloc__)) __attribute__ ((__warn_unused_result__));

extern void *calloc (size_t __nmemb, size_t __size)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__malloc__)) __attribute__ ((__warn_unused_result__));

# 967 "/usr/include/stdlib.h" 3 4

# 1274 "/usr/include/x86_64-linux-gnu/bits/string2.h" 2 3 4




extern char *__strdup (const char *__string) __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__malloc__));
# 1297 "/usr/include/x86_64-linux-gnu/bits/string2.h" 3 4
extern char *__strndup (const char *__string, size_t __n)
     __attribute__ ((__nothrow__ , __leaf__)) __attribute__ ((__malloc__));
# 635 "/usr/include/string.h" 2 3 4




# 1 "/usr/include/x86_64-linux-gnu/bits/string3.h" 1 3 4
# 23 "/usr/include/x86_64-linux-gnu/bits/string3.h" 3 4
extern void __warn_memset_zero_len (void) __attribute__((__warning__ ("memset used with constant zero length parameter; this could be due to transposed parameters")))
                                                                                                   ;
# 49 "/usr/include/x86_64-linux-gnu/bits/string3.h" 3 4
extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) void *
__attribute__ ((__nothrow__ , __leaf__)) memcpy (void *__restrict __dest, const void *__restrict __src, size_t __len)

{
  return __builtin___memcpy_chk (__dest, __src, __len, __builtin_object_size (__dest, 0));
}

extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) void *
__attribute__ ((__nothrow__ , __leaf__)) memmove (void *__dest, const void *__src, size_t __len)
{
  return __builtin___memmove_chk (__dest, __src, __len, __builtin_object_size (__dest, 0));
}
# 77 "/usr/include/x86_64-linux-gnu/bits/string3.h" 3 4
extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) void *
__attribute__ ((__nothrow__ , __leaf__)) memset (void *__dest, int __ch, size_t __len)
{



  if (__builtin_constant_p (__len) && __len == 0
      && (!__builtin_constant_p (__ch) || __ch != 0))
    {
      __warn_memset_zero_len ();
      return __dest;
    }

  return __builtin___memset_chk (__dest, __ch, __len, __builtin_object_size (__dest, 0));
}


extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) void
__attribute__ ((__nothrow__ , __leaf__)) bcopy (const void *__src, void *__dest, size_t __len)
{
  (void) __builtin___memmove_chk (__dest, __src, __len, __builtin_object_size (__dest, 0));
}

extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) void
__attribute__ ((__nothrow__ , __leaf__)) bzero (void *__dest, size_t __len)
{
  (void) __builtin___memset_chk (__dest, '\0', __len, __builtin_object_size (__dest, 0));
}


extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) char *
__attribute__ ((__nothrow__ , __leaf__)) strcpy (char *__restrict __dest, const char *__restrict __src)
{
  return __builtin___strcpy_chk (__dest, __src, __builtin_object_size (__dest, 2 > 1));
}
# 122 "/usr/include/x86_64-linux-gnu/bits/string3.h" 3 4
extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) char *
__attribute__ ((__nothrow__ , __leaf__)) strncpy (char *__restrict __dest, const char *__restrict __src, size_t __len)

{
  return __builtin___strncpy_chk (__dest, __src, __len, __builtin_object_size (__dest, 2 > 1));
}


extern char *__stpncpy_chk (char *__dest, const char *__src, size_t __n,
       size_t __destlen) __attribute__ ((__nothrow__ , __leaf__));
extern char *__stpncpy_alias (char *__dest, const char *__src, size_t __n) __asm__ ("" "stpncpy") __attribute__ ((__nothrow__ , __leaf__))
                                 ;

extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) char *
__attribute__ ((__nothrow__ , __leaf__)) stpncpy (char *__dest, const char *__src, size_t __n)
{
  if (__builtin_object_size (__dest, 2 > 1) != (size_t) -1
      && (!__builtin_constant_p (__n) || __n <= __builtin_object_size (__dest, 2 > 1)))
    return __stpncpy_chk (__dest, __src, __n, __builtin_object_size (__dest, 2 > 1));
  return __stpncpy_alias (__dest, __src, __n);
}


extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) char *
__attribute__ ((__nothrow__ , __leaf__)) strcat (char *__restrict __dest, const char *__restrict __src)
{
  return __builtin___strcat_chk (__dest, __src, __builtin_object_size (__dest, 2 > 1));
}


extern __inline __attribute__ ((__always_inline__)) __attribute__ ((__artificial__)) char *
__attribute__ ((__nothrow__ , __leaf__)) strncat (char *__restrict __dest, const char *__restrict __src, size_t __len)

{
  return __builtin___strncat_chk (__dest, __src, __len, __builtin_object_size (__dest, 2 > 1));
}
# 640 "/usr/include/string.h" 2 3 4




# 9 "LzmaDec.c" 2
# 140 "LzmaDec.c"
static SizeT do_copy1(Byte * const dic,SizeT dicPos,SizeT pos,unsigned curLen)
{
    Byte *dest = dic + dicPos;
    const ptrdiff_t src = (ptrdiff_t)pos - (ptrdiff_t)dicPos;
    const Byte *lim = dest + curLen;
    dicPos += curLen;
    do
      *(dest) = (Byte)*(dest + src);
    while (++dest != lim);

    return dicPos;
}


static SizeT do_copy2(Byte * const dic,SizeT dicPos,SizeT pos,SizeT dicBufSize,unsigned curLen)
{
    do
    {
    dic[dicPos++] = dic[pos];
    if (++pos == dicBufSize)
      pos = 0;
    }
    while (--curLen != 0);

    return dicPos;
}

static unsigned do_lit1(UInt16 **p_prob , const Byte *buf, unsigned symbol, UInt32 * p_bound, UInt32 * p_range, UInt32 * p_code)
{
    UInt16 *prob = *p_prob;
    unsigned ttt;
    UInt32 range = *p_range;
    UInt32 code = *p_code;
    UInt32 bound = *p_bound;


    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
    ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }

    *p_range = range;
    *p_code = code;
    *p_prob = prob;
    *p_bound =bound;

    return symbol;
}


static int LzmaDec_DecodeReal(CLzmaDec *p, SizeT limit, const Byte *bufLimit)
{
  UInt16 *probs = p->probs;

  unsigned state = p->state;
  UInt32 rep0 = p->reps[0], rep1 = p->reps[1], rep2 = p->reps[2], rep3 = p->reps[3];
  const unsigned pbMask = ((unsigned)1 << (p->prop.pb)) - 1;
  const unsigned lpMask = ((unsigned)1 << (p->prop.lp)) - 1;
  const unsigned lc = p->prop.lc;

  Byte *dic = p->dic;
  SizeT dicBufSize = p->dicBufSize;
  SizeT dicPos = p->dicPos;

  UInt32 processedPos = p->processedPos;
  UInt32 checkDicSize = p->checkDicSize;
  unsigned len = 0;

  const Byte *buf = p->buf;
  UInt32 range = p->range;
  UInt32 code = p->code;

  do
  {
    UInt16 *prob;
    UInt32 bound;
    unsigned ttt;
    unsigned posState = processedPos & pbMask;

    prob = probs + 0 + (state << 4) + posState;
    ttt = *(prob); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
    {
      unsigned symbol;
      range = bound; *(prob) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
      prob = probs + (((((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4)) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8))) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8)));
      if (processedPos != 0 || checkDicSize != 0)
        prob += ((UInt32)0x300 * (((processedPos & lpMask) << lc) +
            (dic[(dicPos == 0 ? dicBufSize : dicPos) - 1] >> (8 - lc))));
      processedPos++;

      if (state < 7)
      {
        state -= (state < 4) ? state : 3;
        symbol = 1;





        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }
        ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + symbol) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound; *(prob + symbol) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; ;; }




      }
      else
      {
        unsigned matchByte = dic[dicPos - rep0 + (dicPos < rep0 ? dicBufSize : 0)];
        unsigned offs = 0x100;
        state -= (state < 10) ? 3 : 6;
        symbol = 1;
# 271 "LzmaDec.c"
        {
          unsigned bit;
          UInt16 *probLit;
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
          matchByte <<= 1; bit = (matchByte & offs); probLit = prob + offs + bit + symbol; ttt = *(probLit); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(probLit) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound; *(probLit) = (UInt16)(ttt - (ttt >> 5));; symbol = (symbol + symbol) + 1; offs &= bit; }
        }

      }

      dic[dicPos++] = (Byte)symbol;
      continue;
    }

    {
      range -= bound; code -= bound; *(prob) = (UInt16)(ttt - (ttt >> 5));;
      prob = probs + (0 + (12 << 4)) + state;
      ttt = *(prob); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
      {
        range = bound; *(prob) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
        state += 12;
        prob = probs + (((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4));
      }
      else
      {
        range -= bound; code -= bound; *(prob) = (UInt16)(ttt - (ttt >> 5));;
        if (checkDicSize == 0 && processedPos == 0)
          return 1;
        prob = probs + ((0 + (12 << 4)) + 12) + state;
        ttt = *(prob); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
        {
          range = bound; *(prob) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
          prob = probs + (((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (state << 4) + posState;
          ttt = *(prob); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
          {
            range = bound; *(prob) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
            dic[dicPos] = dic[dicPos - rep0 + (dicPos < rep0 ? dicBufSize : 0)];
            dicPos++;
            processedPos++;
            state = state < 7 ? 9 : 11;
            continue;
          }
          range -= bound; code -= bound; *(prob) = (UInt16)(ttt - (ttt >> 5));;
        }
        else
        {
          UInt32 distance;
          range -= bound; code -= bound; *(prob) = (UInt16)(ttt - (ttt >> 5));;
          prob = probs + (((0 + (12 << 4)) + 12) + 12) + state;
          ttt = *(prob); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
          {
            range = bound; *(prob) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
            distance = rep1;
          }
          else
          {
            range -= bound; code -= bound; *(prob) = (UInt16)(ttt - (ttt >> 5));;
            prob = probs + ((((0 + (12 << 4)) + 12) + 12) + 12) + state;
            ttt = *(prob); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
            {
              range = bound; *(prob) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
              distance = rep2;
            }
            else
            {
              range -= bound; code -= bound; *(prob) = (UInt16)(ttt - (ttt >> 5));;
              distance = rep3;
              rep3 = rep2;
            }
            rep2 = rep1;
          }
          rep1 = rep0;
          rep0 = distance;
        }
        state = state < 7 ? 8 : 11;
        prob = probs + ((((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4)) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8)));
      }
# 388 "LzmaDec.c"
      {
        UInt16 *probLen = prob + 0;
        ttt = *(probLen); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
        {
          range = bound; *(probLen) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
          probLen = prob + ((0 + 1) + 1) + (posState << 3);
          len = 1;
          { ttt = *((probLen + len)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((probLen + len)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; len = (len + len); ;; } else { range -= bound; code -= bound; *((probLen + len)) = (UInt16)(ttt - (ttt >> 5));; len = (len + len) + 1; ;; }; };
          { ttt = *((probLen + len)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((probLen + len)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; len = (len + len); ;; } else { range -= bound; code -= bound; *((probLen + len)) = (UInt16)(ttt - (ttt >> 5));; len = (len + len) + 1; ;; }; };
          { ttt = *((probLen + len)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((probLen + len)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; len = (len + len); ;; } else { range -= bound; code -= bound; *((probLen + len)) = (UInt16)(ttt - (ttt >> 5));; len = (len + len) + 1; ;; }; };
          len -= 8;
        }
        else
        {
          range -= bound; code -= bound; *(probLen) = (UInt16)(ttt - (ttt >> 5));;
          probLen = prob + (0 + 1);
          ttt = *(probLen); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
          {
            range = bound; *(probLen) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));;
            probLen = prob + (((0 + 1) + 1) + ((1 << 4) << 3)) + (posState << 3);
            len = 1;
            { ttt = *((probLen + len)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((probLen + len)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; len = (len + len); ;; } else { range -= bound; code -= bound; *((probLen + len)) = (UInt16)(ttt - (ttt >> 5));; len = (len + len) + 1; ;; }; };
            { ttt = *((probLen + len)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((probLen + len)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; len = (len + len); ;; } else { range -= bound; code -= bound; *((probLen + len)) = (UInt16)(ttt - (ttt >> 5));; len = (len + len) + 1; ;; }; };
            { ttt = *((probLen + len)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((probLen + len)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; len = (len + len); ;; } else { range -= bound; code -= bound; *((probLen + len)) = (UInt16)(ttt - (ttt >> 5));; len = (len + len) + 1; ;; }; };
          }
          else
          {
            range -= bound; code -= bound; *(probLen) = (UInt16)(ttt - (ttt >> 5));;
            probLen = prob + ((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3));
            { len = 1; do { { ttt = *((probLen + len)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((probLen + len)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; len = (len + len); ;; } else { range -= bound; code -= bound; *((probLen + len)) = (UInt16)(ttt - (ttt >> 5));; len = (len + len) + 1; ;; }; }; } while (len < (1 << 8)); len -= (1 << 8); };
            len += (1 << 3) + (1 << 3);
          }
        }
      }


      if (state >= 12)
      {
        UInt32 distance;
        prob = probs + ((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) +
            ((len < 4 ? len : 4 - 1) << 6);
        { distance = 1; { ttt = *((prob + distance)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((prob + distance)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; distance = (distance + distance); ;; } else { range -= bound; code -= bound; *((prob + distance)) = (UInt16)(ttt - (ttt >> 5));; distance = (distance + distance) + 1; ;; }; }; { ttt = *((prob + distance)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((prob + distance)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; distance = (distance + distance); ;; } else { range -= bound; code -= bound; *((prob + distance)) = (UInt16)(ttt - (ttt >> 5));; distance = (distance + distance) + 1; ;; }; }; { ttt = *((prob + distance)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((prob + distance)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; distance = (distance + distance); ;; } else { range -= bound; code -= bound; *((prob + distance)) = (UInt16)(ttt - (ttt >> 5));; distance = (distance + distance) + 1; ;; }; }; { ttt = *((prob + distance)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((prob + distance)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; distance = (distance + distance); ;; } else { range -= bound; code -= bound; *((prob + distance)) = (UInt16)(ttt - (ttt >> 5));; distance = (distance + distance) + 1; ;; }; }; { ttt = *((prob + distance)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((prob + distance)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; distance = (distance + distance); ;; } else { range -= bound; code -= bound; *((prob + distance)) = (UInt16)(ttt - (ttt >> 5));; distance = (distance + distance) + 1; ;; }; }; { ttt = *((prob + distance)); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *((prob + distance)) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; distance = (distance + distance); ;; } else { range -= bound; code -= bound; *((prob + distance)) = (UInt16)(ttt - (ttt >> 5));; distance = (distance + distance) + 1; ;; }; }; distance -= 0x40; };
        if (distance >= 4)
        {
          unsigned posSlot = (unsigned)distance;
          unsigned numDirectBits = (unsigned)(((distance >> 1) - 1));
          distance = (2 | (distance & 1));
          if (posSlot < 14)
          {
            distance <<= numDirectBits;
            prob = probs + (((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + distance - posSlot - 1;
            {
              UInt32 mask = 1;
              unsigned i = 1;
              do
              {
                ttt = *(prob + i); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + i) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; i = (i + i); ;; } else { range -= bound; code -= bound; *(prob + i) = (UInt16)(ttt - (ttt >> 5));; i = (i + i) + 1; distance |= mask; };
                mask <<= 1;
              }
              while (--numDirectBits != 0);
            }
          }
          else
          {
            numDirectBits -= 4;
            do
            {
              if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }
              range >>= 1;

              {
                UInt32 t;
                code -= range;
                t = (0 - ((UInt32)code >> 31));
                distance = (distance << 1) + (t + 1);
                code += range & t;
              }
# 473 "LzmaDec.c"
            }
            while (--numDirectBits != 0);
            prob = probs + ((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14);
            distance <<= 4;
            {
              unsigned i = 1;
              ttt = *(prob + i); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + i) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; i = (i + i); ;; } else { range -= bound; code -= bound; *(prob + i) = (UInt16)(ttt - (ttt >> 5));; i = (i + i) + 1; distance |= 1; };
              ttt = *(prob + i); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + i) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; i = (i + i); ;; } else { range -= bound; code -= bound; *(prob + i) = (UInt16)(ttt - (ttt >> 5));; i = (i + i) + 1; distance |= 2; };
              ttt = *(prob + i); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + i) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; i = (i + i); ;; } else { range -= bound; code -= bound; *(prob + i) = (UInt16)(ttt - (ttt >> 5));; i = (i + i) + 1; distance |= 4; };
              ttt = *(prob + i); if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound; *(prob + i) = (UInt16)(ttt + (((1 << 11) - ttt) >> 5));; i = (i + i); ;; } else { range -= bound; code -= bound; *(prob + i) = (UInt16)(ttt - (ttt >> 5));; i = (i + i) + 1; distance |= 8; };
            }
            if (distance == (UInt32)0xFFFFFFFF)
            {
              len += (2 + (1 << 3) + (1 << 3) + (1 << 8));
              state -= 12;
              break;
            }
          }
        }

        rep3 = rep2;
        rep2 = rep1;
        rep1 = rep0;
        rep0 = distance + 1;
        if (checkDicSize == 0)
        {
          if (distance >= processedPos)
          {
            p->dicPos = dicPos;
            return 1;
          }
        }
        else if (distance >= checkDicSize)
        {
          p->dicPos = dicPos;
          return 1;
        }
        state = (state < 12 + 7) ? 7 : 7 + 3;
      }

      len += 2;

      {
        SizeT rem;

        if ((rem = limit - dicPos) == 0)
        {
          p->dicPos = dicPos;
          return 1;
        }

        unsigned curLen = ((rem < len) ? (unsigned)rem : len);
        SizeT pos = dicPos - rep0 + (dicPos < rep0 ? dicBufSize : 0);

        processedPos += curLen;

        len -= curLen;
        if (curLen <= dicBufSize - pos)
        {
# 542 "LzmaDec.c"
          dicPos = do_copy1(dic,dicPos,pos,curLen);
        }
        else
        {
# 555 "LzmaDec.c"
          dicPos = do_copy2(dic,dicPos,pos,dicBufSize,curLen);
        }
      }
    }
  }
  while (dicPos < limit && buf < bufLimit);

  if (range < ((UInt32)1 << 24)) { range <<= 8; code = (code << 8) | (*buf++); };

  p->buf = buf;
  p->range = range;
  p->code = code;
  p->remainLen = len;
  p->dicPos = dicPos;
  p->processedPos = processedPos;
  p->reps[0] = rep0;
  p->reps[1] = rep1;
  p->reps[2] = rep2;
  p->reps[3] = rep3;
  p->state = state;

  return 0;
}

static void LzmaDec_WriteRem(CLzmaDec *p, SizeT limit)
{
  if (p->remainLen != 0 && p->remainLen < (2 + (1 << 3) + (1 << 3) + (1 << 8)))
  {
    Byte *dic = p->dic;
    SizeT dicPos = p->dicPos;
    SizeT dicBufSize = p->dicBufSize;
    unsigned len = p->remainLen;
    SizeT rep0 = p->reps[0];
    SizeT rem = limit - dicPos;
    if (rem < len)
      len = (unsigned)(rem);

    if (p->checkDicSize == 0 && p->prop.dicSize - p->processedPos <= len)
      p->checkDicSize = p->prop.dicSize;

    p->processedPos += len;
    p->remainLen -= len;
    while (len != 0)
    {
      len--;
      dic[dicPos] = dic[dicPos - rep0 + (dicPos < rep0 ? dicBufSize : 0)];
      dicPos++;
    }
    p->dicPos = dicPos;
  }
}

static int LzmaDec_DecodeReal2(CLzmaDec *p, SizeT limit, const Byte *bufLimit)
{
  do
  {
    SizeT limit2 = limit;
    if (p->checkDicSize == 0)
    {
      UInt32 rem = p->prop.dicSize - p->processedPos;
      if (limit - p->dicPos > rem)
        limit2 = p->dicPos + rem;
    }

    { int __result__ = (LzmaDec_DecodeReal(p, limit2, bufLimit)); if (__result__ != 0) return __result__; };

    if (p->checkDicSize == 0 && p->processedPos >= p->prop.dicSize)
      p->checkDicSize = p->prop.dicSize;

    LzmaDec_WriteRem(p, limit);
  }
  while (p->dicPos < limit && p->buf < bufLimit && p->remainLen < (2 + (1 << 3) + (1 << 3) + (1 << 8)));

  if (p->remainLen > (2 + (1 << 3) + (1 << 3) + (1 << 8)))
    p->remainLen = (2 + (1 << 3) + (1 << 3) + (1 << 8));

  return 0;
}

typedef enum
{
  DUMMY_ERROR,
  DUMMY_LIT,
  DUMMY_MATCH,
  DUMMY_REP
} ELzmaDummy;

static ELzmaDummy LzmaDec_TryDummy(const CLzmaDec *p, const Byte *buf, SizeT inSize)
{
  UInt32 range = p->range;
  UInt32 code = p->code;
  const Byte *bufLimit = buf + inSize;
  const UInt16 *probs = p->probs;
  unsigned state = p->state;
  ELzmaDummy res;

  {
    const UInt16 *prob;
    UInt32 bound;
    unsigned ttt;
    unsigned posState = (p->processedPos) & ((1 << p->prop.pb) - 1);

    prob = probs + 0 + (state << 4) + posState;
    ttt = *(prob); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
    {
      range = bound;



      prob = probs + (((((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4)) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8))) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8)));
      if (p->checkDicSize != 0 || p->processedPos != 0)
        prob += ((UInt32)0x300 *
            ((((p->processedPos) & ((1 << (p->prop.lp)) - 1)) << p->prop.lc) +
            (p->dic[(p->dicPos == 0 ? p->dicBufSize : p->dicPos) - 1] >> (8 - p->prop.lc))));

      if (state < 7)
      {
        unsigned symbol = 1;
        do { ttt = *(prob + symbol); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound;; symbol = (symbol + symbol); ;; } else { range -= bound; code -= bound;; symbol = (symbol + symbol) + 1; ;; } } while (symbol < 0x100);
      }
      else
      {
        unsigned matchByte = p->dic[p->dicPos - p->reps[0] +
            (p->dicPos < p->reps[0] ? p->dicBufSize : 0)];
        unsigned offs = 0x100;
        unsigned symbol = 1;
        do
        {
          unsigned bit;
          const UInt16 *probLit;
          matchByte <<= 1;
          bit = (matchByte & offs);
          probLit = prob + offs + bit + symbol;
          ttt = *(probLit); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound;; symbol = (symbol + symbol); offs &= ~bit; } else { range -= bound; code -= bound;; symbol = (symbol + symbol) + 1; offs &= bit; }
        }
        while (symbol < 0x100);
      }
      res = DUMMY_LIT;
    }
    else
    {
      unsigned len;
      range -= bound; code -= bound;;

      prob = probs + (0 + (12 << 4)) + state;
      ttt = *(prob); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
      {
        range = bound;;
        state = 0;
        prob = probs + (((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4));
        res = DUMMY_MATCH;
      }
      else
      {
        range -= bound; code -= bound;;
        res = DUMMY_REP;
        prob = probs + ((0 + (12 << 4)) + 12) + state;
        ttt = *(prob); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
        {
          range = bound;;
          prob = probs + (((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (state << 4) + posState;
          ttt = *(prob); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
          {
            range = bound;;
            if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); };
            return DUMMY_REP;
          }
          else
          {
            range -= bound; code -= bound;;
          }
        }
        else
        {
          range -= bound; code -= bound;;
          prob = probs + (((0 + (12 << 4)) + 12) + 12) + state;
          ttt = *(prob); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
          {
            range = bound;;
          }
          else
          {
            range -= bound; code -= bound;;
            prob = probs + ((((0 + (12 << 4)) + 12) + 12) + 12) + state;
            ttt = *(prob); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
            {
              range = bound;;
            }
            else
            {
              range -= bound; code -= bound;;
            }
          }
        }
        state = 12;
        prob = probs + ((((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4)) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8)));
      }
      {
        unsigned limit, offset;
        const UInt16 *probLen = prob + 0;
        ttt = *(probLen); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
        {
          range = bound;;
          probLen = prob + ((0 + 1) + 1) + (posState << 3);
          offset = 0;
          limit = 1 << 3;
        }
        else
        {
          range -= bound; code -= bound;;
          probLen = prob + (0 + 1);
          ttt = *(probLen); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound)
          {
            range = bound;;
            probLen = prob + (((0 + 1) + 1) + ((1 << 4) << 3)) + (posState << 3);
            offset = (1 << 3);
            limit = 1 << 3;
          }
          else
          {
            range -= bound; code -= bound;;
            probLen = prob + ((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3));
            offset = (1 << 3) + (1 << 3);
            limit = 1 << 8;
          }
        }
        { len = 1; do { ttt = *(probLen + len); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound;; len = (len + len); ;; } else { range -= bound; code -= bound;; len = (len + len) + 1; ;; } } while (len < limit); len -= limit; };
        len += offset;
      }

      if (state < 4)
      {
        unsigned posSlot;
        prob = probs + ((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) +
            ((len < 4 ? len : 4 - 1) <<
            6);
        { posSlot = 1; do { ttt = *(prob + posSlot); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound;; posSlot = (posSlot + posSlot); ;; } else { range -= bound; code -= bound;; posSlot = (posSlot + posSlot) + 1; ;; } } while (posSlot < 1 << 6); posSlot -= 1 << 6; };
        if (posSlot >= 4)
        {
          unsigned numDirectBits = ((posSlot >> 1) - 1);



          if (posSlot < 14)
          {
            prob = probs + (((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + ((2 | (posSlot & 1)) << numDirectBits) - posSlot - 1;
          }
          else
          {
            numDirectBits -= 4;
            do
            {
              if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }
              range >>= 1;
              code -= range & (((code - range) >> 31) - 1);

            }
            while (--numDirectBits != 0);
            prob = probs + ((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14);
            numDirectBits = 4;
          }
          {
            unsigned i = 1;
            do
            {
              ttt = *(prob + i); if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); }; bound = (range >> 11) * ttt; if (code < bound) { range = bound;; i = (i + i); ;; } else { range -= bound; code -= bound;; i = (i + i) + 1; ;; };
            }
            while (--numDirectBits != 0);
          }
        }
      }
    }
  }
  if (range < ((UInt32)1 << 24)) { if (buf >= bufLimit) return DUMMY_ERROR; range <<= 8; code = (code << 8) | (*buf++); };
  return res;
}


void LzmaDec_InitDicAndState(CLzmaDec *p, Bool initDic, Bool initState)
{
  p->needFlush = 1;
  p->remainLen = 0;
  p->tempBufSize = 0;

  if (initDic)
  {
    p->processedPos = 0;
    p->checkDicSize = 0;
    p->needInitState = 1;
  }
  if (initState)
    p->needInitState = 1;
}

void LzmaDec_Init(CLzmaDec *p)
{
  p->dicPos = 0;
  LzmaDec_InitDicAndState(p, 1, 1);
}

static void LzmaDec_InitStateReal(CLzmaDec *p)
{
  SizeT numProbs = ((((((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4)) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8))) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8))) + ((UInt32)0x300 << ((&p->prop)->lc + (&p->prop)->lp)));
  SizeT i;
  UInt16 *probs = p->probs;
  for (i = 0; i < numProbs; i++)
    probs[i] = (1 << 11) >> 1;
  p->reps[0] = p->reps[1] = p->reps[2] = p->reps[3] = 1;
  p->state = 0;
  p->needInitState = 0;
}

SRes LzmaDec_DecodeToDic(CLzmaDec *p, SizeT dicLimit, const Byte *src, SizeT *srcLen,
    ELzmaFinishMode finishMode, ELzmaStatus *status)
{
  SizeT inSize = *srcLen;
  (*srcLen) = 0;
  LzmaDec_WriteRem(p, dicLimit);

  *status = LZMA_STATUS_NOT_SPECIFIED;

  while (p->remainLen != (2 + (1 << 3) + (1 << 3) + (1 << 8)))
  {
      int checkEndMarkNow;

      if (p->needFlush)
      {
        for (; inSize > 0 && p->tempBufSize < 5; (*srcLen)++, inSize--)
          p->tempBuf[p->tempBufSize++] = *src++;
        if (p->tempBufSize < 5)
        {
          *status = LZMA_STATUS_NEEDS_MORE_INPUT;
          return 0;
        }
        if (p->tempBuf[0] != 0)
          return 1;
        p->code =
              ((UInt32)p->tempBuf[1] << 24)
            | ((UInt32)p->tempBuf[2] << 16)
            | ((UInt32)p->tempBuf[3] << 8)
            | ((UInt32)p->tempBuf[4]);
        p->range = 0xFFFFFFFF;
        p->needFlush = 0;
        p->tempBufSize = 0;
      }

      checkEndMarkNow = 0;
      if (p->dicPos >= dicLimit)
      {
        if (p->remainLen == 0 && p->code == 0)
        {
          *status = LZMA_STATUS_MAYBE_FINISHED_WITHOUT_MARK;
          return 0;
        }
        if (finishMode == LZMA_FINISH_ANY)
        {
          *status = LZMA_STATUS_NOT_FINISHED;
          return 0;
        }
        if (p->remainLen != 0)
        {
          *status = LZMA_STATUS_NOT_FINISHED;
          return 1;
        }
        checkEndMarkNow = 1;
      }

      if (p->needInitState)
        LzmaDec_InitStateReal(p);

      if (p->tempBufSize == 0)
      {
        SizeT processed;
        const Byte *bufLimit;
        if (inSize < 20 || checkEndMarkNow)
        {
          int dummyRes = LzmaDec_TryDummy(p, src, inSize);
          if (dummyRes == DUMMY_ERROR)
          {
            memcpy(p->tempBuf, src, inSize);
            p->tempBufSize = (unsigned)inSize;
            (*srcLen) += inSize;
            *status = LZMA_STATUS_NEEDS_MORE_INPUT;
            return 0;
          }
          if (checkEndMarkNow && dummyRes != DUMMY_MATCH)
          {
            *status = LZMA_STATUS_NOT_FINISHED;
            return 1;
          }
          bufLimit = src;
        }
        else
          bufLimit = src + inSize - 20;
        p->buf = src;
        if (LzmaDec_DecodeReal2(p, dicLimit, bufLimit) != 0)
          return 1;
        processed = (SizeT)(p->buf - src);
        (*srcLen) += processed;
        src += processed;
        inSize -= processed;
      }
      else
      {
        unsigned rem = p->tempBufSize, lookAhead = 0;
        while (rem < 20 && lookAhead < inSize)
          p->tempBuf[rem++] = src[lookAhead++];
        p->tempBufSize = rem;
        if (rem < 20 || checkEndMarkNow)
        {
          int dummyRes = LzmaDec_TryDummy(p, p->tempBuf, rem);
          if (dummyRes == DUMMY_ERROR)
          {
            (*srcLen) += lookAhead;
            *status = LZMA_STATUS_NEEDS_MORE_INPUT;
            return 0;
          }
          if (checkEndMarkNow && dummyRes != DUMMY_MATCH)
          {
            *status = LZMA_STATUS_NOT_FINISHED;
            return 1;
          }
        }
        p->buf = p->tempBuf;
        if (LzmaDec_DecodeReal2(p, dicLimit, p->buf) != 0)
          return 1;

        {
          unsigned kkk = (unsigned)(p->buf - p->tempBuf);
          if (rem < kkk)
            return 11;
          rem -= kkk;
          if (lookAhead < rem)
            return 11;
          lookAhead -= rem;
        }
        (*srcLen) += lookAhead;
        src += lookAhead;
        inSize -= lookAhead;
        p->tempBufSize = 0;
      }
  }
  if (p->code == 0)
    *status = LZMA_STATUS_FINISHED_WITH_MARK;
  return (p->code == 0) ? 0 : 1;
}

SRes LzmaDec_DecodeToBuf(CLzmaDec *p, Byte *dest, SizeT *destLen, const Byte *src, SizeT *srcLen, ELzmaFinishMode finishMode, ELzmaStatus *status)
{
  SizeT outSize = *destLen;
  SizeT inSize = *srcLen;
  *srcLen = *destLen = 0;
  for (;;)
  {
    SizeT inSizeCur = inSize, outSizeCur, dicPos;
    ELzmaFinishMode curFinishMode;
    SRes res;
    if (p->dicPos == p->dicBufSize)
      p->dicPos = 0;
    dicPos = p->dicPos;
    if (outSize > p->dicBufSize - dicPos)
    {
      outSizeCur = p->dicBufSize;
      curFinishMode = LZMA_FINISH_ANY;
    }
    else
    {
      outSizeCur = dicPos + outSize;
      curFinishMode = finishMode;
    }

    res = LzmaDec_DecodeToDic(p, outSizeCur, src, &inSizeCur, curFinishMode, status);
    src += inSizeCur;
    inSize -= inSizeCur;
    *srcLen += inSizeCur;
    outSizeCur = p->dicPos - dicPos;
    memcpy(dest, p->dic + dicPos, outSizeCur);
    dest += outSizeCur;
    outSize -= outSizeCur;
    *destLen += outSizeCur;
    if (res != 0)
      return res;
    if (outSizeCur == 0 || outSize == 0)
      return 0;
  }
}

void LzmaDec_FreeProbs(CLzmaDec *p, ISzAlloc *alloc)
{
  alloc->Free(alloc, p->probs);
  p->probs = ((void *)0);
}

static void LzmaDec_FreeDict(CLzmaDec *p, ISzAlloc *alloc)
{
  alloc->Free(alloc, p->dic);
  p->dic = ((void *)0);
}

void LzmaDec_Free(CLzmaDec *p, ISzAlloc *alloc)
{
  LzmaDec_FreeProbs(p, alloc);
  LzmaDec_FreeDict(p, alloc);
}

SRes LzmaProps_Decode(CLzmaProps *p, const Byte *data, unsigned size)
{
  UInt32 dicSize;
  Byte d;

  if (size < 5)
    return 4;
  else
    dicSize = data[1] | ((UInt32)data[2] << 8) | ((UInt32)data[3] << 16) | ((UInt32)data[4] << 24);

  if (dicSize < (1 << 12))
    dicSize = (1 << 12);
  p->dicSize = dicSize;

  d = data[0];
  if (d >= (9 * 5 * 5))
    return 4;

  p->lc = d % 9;
  d /= 9;
  p->pb = d / 5;
  p->lp = d % 5;

  return 0;
}

static SRes LzmaDec_AllocateProbs2(CLzmaDec *p, const CLzmaProps *propNew, ISzAlloc *alloc)
{
  UInt32 numProbs = ((((((((((((0 + (12 << 4)) + 12) + 12) + 12) + 12) + (12 << 4)) + (4 << 6)) + (1 << (14 >> 1)) - 14) + (1 << 4)) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8))) + (((((0 + 1) + 1) + ((1 << 4) << 3)) + ((1 << 4) << 3)) + (1 << 8))) + ((UInt32)0x300 << ((propNew)->lc + (propNew)->lp)));
  if (!p->probs || numProbs != p->numProbs)
  {
    LzmaDec_FreeProbs(p, alloc);
    p->probs = (UInt16 *)alloc->Alloc(alloc, numProbs * sizeof(UInt16));
    p->numProbs = numProbs;
    if (!p->probs)
      return 2;
  }
  return 0;
}

SRes LzmaDec_AllocateProbs(CLzmaDec *p, const Byte *props, unsigned propsSize, ISzAlloc *alloc)
{
  CLzmaProps propNew;
  { int __result__ = (LzmaProps_Decode(&propNew, props, propsSize)); if (__result__ != 0) return __result__; };
  { int __result__ = (LzmaDec_AllocateProbs2(p, &propNew, alloc)); if (__result__ != 0) return __result__; };
  p->prop = propNew;
  return 0;
}

SRes LzmaDec_Allocate(CLzmaDec *p, const Byte *props, unsigned propsSize, ISzAlloc *alloc)
{
  CLzmaProps propNew;
  SizeT dicBufSize;
  { int __result__ = (LzmaProps_Decode(&propNew, props, propsSize)); if (__result__ != 0) return __result__; };
  { int __result__ = (LzmaDec_AllocateProbs2(p, &propNew, alloc)); if (__result__ != 0) return __result__; };

  {
    UInt32 dictSize = propNew.dicSize;
    SizeT mask = ((UInt32)1 << 12) - 1;
         if (dictSize >= ((UInt32)1 << 30)) mask = ((UInt32)1 << 22) - 1;
    else if (dictSize >= ((UInt32)1 << 22)) mask = ((UInt32)1 << 20) - 1;;
    dicBufSize = ((SizeT)dictSize + mask) & ~mask;
    if (dicBufSize < dictSize)
      dicBufSize = dictSize;
  }

  if (!p->dic || dicBufSize != p->dicBufSize)
  {
    LzmaDec_FreeDict(p, alloc);
    p->dic = (Byte *)alloc->Alloc(alloc, dicBufSize);
    if (!p->dic)
    {
      LzmaDec_FreeProbs(p, alloc);
      return 2;
    }
  }
  p->dicBufSize = dicBufSize;
  p->prop = propNew;
  return 0;
}

SRes LzmaDecode(Byte *dest, SizeT *destLen, const Byte *src, SizeT *srcLen,
    const Byte *propData, unsigned propSize, ELzmaFinishMode finishMode,
    ELzmaStatus *status, ISzAlloc *alloc)
{
  CLzmaDec p;
  SRes res;
  SizeT outSize = *destLen, inSize = *srcLen;
  *destLen = *srcLen = 0;
  *status = LZMA_STATUS_NOT_SPECIFIED;
  if (inSize < 5)
    return 6;
  { (&p)->dic = 0; (&p)->probs = 0; };
  { int __result__ = (LzmaDec_AllocateProbs(&p, propData, propSize, alloc)); if (__result__ != 0) return __result__; };
  p.dic = dest;
  p.dicBufSize = outSize;
  LzmaDec_Init(&p);
  *srcLen = inSize;
  res = LzmaDec_DecodeToDic(&p, outSize, src, srcLen, finishMode, status);
  *destLen = p.dicPos;
  if (res == 0 && *status == LZMA_STATUS_NEEDS_MORE_INPUT)
    res = 6;
  LzmaDec_FreeProbs(&p, alloc);
  return res;
}
