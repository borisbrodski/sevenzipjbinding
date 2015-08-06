/*
 * JavaStandardLibrary.h
 *
 *  Created on: Jan 29, 2010
 *      Author: boris
 */

#ifndef JAVASTANDARDLIBRARY_H_
#define JAVASTANDARDLIBRARY_H_

#define JAVA_MAKE_SIGNATURE_TYPE(classname) "L" classname ";"

#define JAVA_OBJECT "java/lang/Object"
#define JAVA_OBJECT_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_OBJECT)

#define JAVA_BYTE "java/lang/Byte"
#define JAVA_BYTE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_BYTE)

#define JAVA_CHARACTER "java/lang/Character"
#define JAVA_CHARACTER_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_CHARACTER)

#define JAVA_SHORT "java/lang/Short"
#define JAVA_SHORT_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_SHORT)

#define JAVA_NUMBER "java/lang/Number"
#define JAVA_NUMBER_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_NUMBER)

#define JAVA_INTEGER "java/lang/Integer"
#define JAVA_INTEGER_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_INTEGER)

#define JAVA_LONG "java/lang/Long"
#define JAVA_LONG_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_LONG)

#define JAVA_FLOAT "java/lang/Float"
#define JAVA_FLOAT_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_FLOAT)

#define JAVA_DOUBLE "java/lang/Double"
#define JAVA_DOUBLE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_DOUBLE)

#define JAVA_BOOLEAN "java/lang/Boolean"
#define JAVA_BOOLEAN_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_BOOLEAN)

#define JAVA_STRING "java/lang/String"
#define JAVA_STRING_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_STRING)

#define JAVA_THROWABLE "java/lang/Throwable"
#define JAVA_THROWABLE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_THROWABLE)

#define JAVA_DATE "java/util/Date"
#define JAVA_DATE_T JAVA_MAKE_SIGNATURE_TYPE(JAVA_DATE)

JT_BEGIN_CLASS("java/util", Date)
    JT_CLASS_VIRTUAL_METHOD(Long, getTime, _)
JT_END_CLASS

JT_BEGIN_CLASS("java/lang", Integer)
    JT_CLASS_VIRTUAL_METHOD(Int, intValue, _)
    JT_CLASS_STATIC_METHOD_OBJECT(JAVA_INTEGER_T, valueOf, JT_INT(value, _))
JT_END_CLASS

JT_BEGIN_CLASS("java/lang", Long)
    JT_CLASS_VIRTUAL_METHOD(Long, longValue, _)
    JT_CLASS_STATIC_METHOD_OBJECT(JAVA_LONG_T, valueOf, JT_LONG(value, _))
JT_END_CLASS

JT_BEGIN_CLASS("java/lang", Boolean)
    JT_CLASS_VIRTUAL_METHOD(Boolean, booleanValue, _)
JT_END_CLASS

JT_BEGIN_CLASS("java/lang", NoSuchMethodError)
JT_END_CLASS

JT_BEGIN_CLASS("java/lang", ExceptionInInitializerError)
JT_END_CLASS

JT_BEGIN_CLASS("java/lang", OutOfMemoryError)
JT_END_CLASS

#define FILETIME_TO_JAVATIME_SHIFT  (((LONGLONG) 0x19db1de) << 32 | 0xd53e8000)
#define FILETIME_TO_JAVATIME_FACTOR 10000


#endif /* JAVASTANDARDLIBRARY_H_ */
