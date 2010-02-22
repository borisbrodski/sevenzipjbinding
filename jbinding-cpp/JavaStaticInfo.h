// TODO Rename to JniTools.h

/*
 * JavaStaticInfo.h
 *
 *  Created on: Jan 14, 2010
 *      Author: Boris Brodski
 */

/*
 * JniTools: Generic library to easy access java methods from C++ through JNI interface.
 *
 * Usage:
 * ------
 *
 *
 * Technical info
 * --------------
 *
 * 1 Name convention
 *
 * CTYPE - C++ type of the java type. Example: jstring, jlong
 * JSIG -  Signature of java types (string const). Example "J", "Ljava/lang/String;"
 *
 * All other defines use prefix "JT_" and "_JT_" to avoid possible conflicts with other libraries.
 */

#ifndef JAVASTATICINFO_H_
#define JAVASTATICINFO_H_

#include <map>
#include <stdarg.h>
#include "JNITools.h"
#include "JObjectList.h"

// TODO Remove from here
#define JBINDING_JNIEXPORT extern "C" JNIEXPORT

// -----------------------------
// -- General purpose defines --
// -----------------------------

// Expand "a"
#define JT_EXPAND(a)                            a

// Expand "f" and "a". Expand f(a)
#define JT_APPLY1(f,a)                          f(a)

// Expand "a" and convert into string constant
#define JT_TO_STRING(a)                         #a

// Expand "a" and "b" and concatenate it together
#define JT_CONCAT2(a, b)                        _JT_CONCAT2(a, b)
#define _JT_CONCAT2(a, b)                       a##b

// Expand "a", "b" and "C" and concatenate it together
#define JT_CONCAT3(a, b, c)                     _JT_CONCAT3(a, b, c)
#define _JT_CONCAT3(a, b, c)                    a##b##c

// --------------------------------------
// -- Internal structure configuration --
// --------------------------------------

// Name of the class to represent a method (extends JMethod)
#define JT_METHOD_CLASS(name)                   C_##name

// Name of the instance variable of the method class
#define JT_METHOD_OBJECT(name)                  _##name

#define CTYPE_String                            jstring
#define CTYPE_Int                               jint
#define CTYPE_Long                              jlong
#define CTYPE_Class                             jclass
#define CTYPE_Object                            jobject
#define CTYPE_Void                              void

#define JSIG_Void                               "V"
#define JSIG_Long                               "J"
#define JSIG_Int                                "I"
#define JSIG_String                             "Ljava/lang/String;"
#define JSIG_Class                              "Ljava/lang/Class;"
#define JSIG_Object                             ""

#define JT_CALL_AND_ASSIGN_TO_RESULT(type, e)   JT_APPLY1(JT_CALL_AND_ASSIGN_TO_RESULT_##type,e)

#define JT_CALL_AND_ASSIGN_TO_RESULT_Object(e)  jobject result = static_cast<jobject>(e);
#define JT_CALL_AND_ASSIGN_TO_RESULT_String(e)  jstring result = static_cast<jstring>(e);
#define JT_CALL_AND_ASSIGN_TO_RESULT_Long(e)    jlong result = static_cast<jlong>(e);
#define JT_CALL_AND_ASSIGN_TO_RESULT_Int(e)     jint result = static_cast<jint>(e);
#define JT_CALL_AND_ASSIGN_TO_RESULT_Class(e)   jclass result = static_cast<jclass>(e);
#define JT_CALL_AND_ASSIGN_TO_RESULT_Void(e)    char const * result = "<void>"; e;

#define JT_RETURN_RESULT_String                 return result;
#define JT_RETURN_RESULT_Long                   return result;
#define JT_RETURN_RESULT_Int                    return result;
#define JT_RETURN_RESULT_Class                  return result;
#define JT_RETURN_RESULT_Object                 return result;
#define JT_RETURN_RESULT_Void

#define JT_ENV_VIRTUAL_CALL_Int                 CallIntMethodV
#define JT_ENV_VIRTUAL_CALL_Long                CallLongMethodV
#define JT_ENV_VIRTUAL_CALL_Object              CallObjectMethodV
#define JT_ENV_VIRTUAL_CALL_String              CallObjectMethodV
#define JT_ENV_VIRTUAL_CALL_Void                CallVoidMethodV

#define JT_ENV_NON_VIRTUAL_CALL_Int             CallNonvirtualIntMethodV
#define JT_ENV_NON_VIRTUAL_CALL_Long            CallNonvirtualLongMethodV
#define JT_ENV_NON_VIRTUAL_CALL_Object          CallNonvirtualObjectMethodV
#define JT_ENV_NON_VIRTUAL_CALL_String          CallNonvirtualObjectMethodV
#define JT_ENV_NON_VIRTUAL_CALL_Void            CallNonvirtualVoidMethodV

#define JT_ENV_STATIC_CALL_Int                  CallStaticIntMethodV
#define JT_ENV_STATIC_CALL_Long                 CallStaticLongMethodV
#define JT_ENV_STATIC_CALL_Object               CallStaticObjectMethodV
#define JT_ENV_STATIC_CALL_String               CallStaticObjectMethodV
#define JT_ENV_STATIC_CALL_Void                 CallStaticVoidMethodV

#define JT_ENV_GET_Long                         GetLongField
#define JT_ENV_GET_String                       GetObjectField
#define JT_ENV_GET_Class                        GetObjectField
#define JT_ENV_GET_Object                       GetObjectField

#define JT_ENV_STATIC_GET_Long                  GetStaticLongField
#define JT_ENV_STATIC_GET_String                GetStaticObjectField
#define JT_ENV_STATIC_GET_Object                GetStaticObjectField

#define JT_ENV_SET_Long                         SetLongField
#define JT_ENV_SET_String                       SetObjectField
#define JT_ENV_SET_Object                       SetObjectField
#define JT_ENV_SET_Class                        SetObjectField

#define JT_ENV_STATIC_SET_Long                  SetStaticLongField
#define JT_ENV_STATIC_SET_Object                SetStaticObjectField
#define JT_ENV_STATIC_SET_String                SetStaticObjectField

#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME(param_spec)                                            \
    JT_PARAM_SPEC_TO_CONSTRUCTORNAME1_##param_spec
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME1_JPARAM(type, sig, name, param_spec)                   \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME2_##param_spec))
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME2_JPARAM(type, sig,name, param_spec)                    \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME3_##param_spec))
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME3_JPARAM(type, sig,name, param_spec)                    \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME4_##param_spec))
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME4_JPARAM(type, sig,name, param_spec)                    \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME5_##param_spec))
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME5_JPARAM(type, sig,name, param_spec)                    \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME6_##param_spec))
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME6_JPARAM(type, sig,name, param_spec)                    \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME7_##param_spec))
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME7_JPARAM(type, sig,name, param_spec)                    \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME8_##param_spec))
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME8_JPARAM(type, sig,name, param_spec)                    \
    JT_CONCAT3(_,name,JT_EXPAND(JT_PARAM_SPEC_TO_CONSTRUCTORNAME9_##param_spec))

#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME1__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME2__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME3__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME4__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME5__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME6__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME7__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME8__     _constr
#define JT_PARAM_SPEC_TO_CONSTRUCTORNAME9__     _constr

#define JT_PARAM_SPEC_TO_SIG(param_spec) "(" JT_PARAM_SPEC_TO_SIG1_##param_spec
#define JT_PARAM_SPEC_TO_SIG1_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG2_##param_spec)
#define JT_PARAM_SPEC_TO_SIG2_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG3_##param_spec)
#define JT_PARAM_SPEC_TO_SIG3_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG4_##param_spec)
#define JT_PARAM_SPEC_TO_SIG4_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG5_##param_spec)
#define JT_PARAM_SPEC_TO_SIG5_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG6_##param_spec)
#define JT_PARAM_SPEC_TO_SIG6_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG7_##param_spec)
#define JT_PARAM_SPEC_TO_SIG7_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG8_##param_spec)
#define JT_PARAM_SPEC_TO_SIG8_JPARAM(type, sig, name, param_spec)                               \
    JSIG_##type JT_EXPAND(JT_PARAM_SPEC_TO_SIG9_##param_spec)

#define JT_PARAM_SPEC_TO_SIG1__                 ")"
#define JT_PARAM_SPEC_TO_SIG2__                 ")"
#define JT_PARAM_SPEC_TO_SIG3__                 ")"
#define JT_PARAM_SPEC_TO_SIG4__                 ")"
#define JT_PARAM_SPEC_TO_SIG5__                 ")"
#define JT_PARAM_SPEC_TO_SIG6__                 ")"
#define JT_PARAM_SPEC_TO_SIG7__                 ")"
#define JT_PARAM_SPEC_TO_SIG8__                 ")"
#define JT_PARAM_SPEC_TO_SIG9__                 ")"

#define JT_PARAM_SPEC_TO_PARAM_LIST(param_spec) JT_PARAM_SPEC_TO_PARAM_LIST1_##param_spec
#define JT_PARAM_SPEC_TO_PARAM_LIST1_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST2_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_LIST2_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST3_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_LIST3_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST4_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_LIST4_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST5_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_LIST5_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST6_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_LIST6_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST7_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_LIST7_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST8_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_LIST8_JPARAM(type, sig, name, param_spec)                        \
    , name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_LIST9_##param_spec)

#define JT_PARAM_SPEC_TO_PARAM_LIST1__
#define JT_PARAM_SPEC_TO_PARAM_LIST2__
#define JT_PARAM_SPEC_TO_PARAM_LIST3__
#define JT_PARAM_SPEC_TO_PARAM_LIST4__
#define JT_PARAM_SPEC_TO_PARAM_LIST5__
#define JT_PARAM_SPEC_TO_PARAM_LIST6__
#define JT_PARAM_SPEC_TO_PARAM_LIST7__
#define JT_PARAM_SPEC_TO_PARAM_LIST8__
#define JT_PARAM_SPEC_TO_PARAM_LIST9__

#define JT_PARAM_SPEC_TO_PARAM_DEF(param_spec) JT_PARAM_SPEC_TO_PARAM_DEF1_##param_spec
#define JT_PARAM_SPEC_TO_PARAM_DEF1_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type name) JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF2_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_DEF2_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type) name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF3_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_DEF3_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type) name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF4_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_DEF4_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type) name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF5_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_DEF5_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type) name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF6_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_DEF6_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type) name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF7_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_DEF7_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type) name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF8_##param_spec)
#define JT_PARAM_SPEC_TO_PARAM_DEF8_JPARAM(type, sig, name, param_spec)                         \
    , JT_EXPAND(CTYPE_##type) name JT_EXPAND(JT_PARAM_SPEC_TO_PARAM_DEF9_##param_spec)

#define JT_PARAM_SPEC_TO_PARAM_DEF1__
#define JT_PARAM_SPEC_TO_PARAM_DEF2__
#define JT_PARAM_SPEC_TO_PARAM_DEF3__
#define JT_PARAM_SPEC_TO_PARAM_DEF4__
#define JT_PARAM_SPEC_TO_PARAM_DEF5__
#define JT_PARAM_SPEC_TO_PARAM_DEF6__
#define JT_PARAM_SPEC_TO_PARAM_DEF7__
#define JT_PARAM_SPEC_TO_PARAM_DEF8__
#define JT_PARAM_SPEC_TO_PARAM_DEF9__

#ifdef USE_MY_ASSERTS
#   define CHECK_OBJECT_CLASS(env, this, object) {(this)->checkObjectClass(env, object);}
#else
#   define CHECK_OBJECT_CLASS(env, this, object) {}
#endif

#ifdef TRACE_ON
#   define TRACE_JNI_CALLING(this, name, signature) {std::cout << "Calling " << *(this) << '.' << #name << signature << std::endl;}
#   define TRACE_JNI_CALLED(this, name, signature) {std::cout << "Called " << *(this) << '.' << #name << " returned " << env << result << std::endl;}
#   define TRACE_JNI_GETTING(this, name, signature) {std::cout << "Getting " << *(this) << '.' << #name << '(' << signature << ')' << std::endl;}
#   define TRACE_JNI_GOT(this, name, signature) {std::cout << "Got " << *(this) << '.' << #name << "='" << env << result << "'" << std::endl;}
#   define TRACE_JNI_SETTING(this, name, signature) {std::cout << "Setting " << *(this) << '.' << #name << '(' << signature << ')' << '=' << env << value << std::endl;}
#   define TRACE_JNI_SET(this, name, signature) {std::cout << "Set " << *(this) << '.' << #name << std::endl;}
#else
#   define TRACE_JNI_CALLING(this, name, signature) {}
#   define TRACE_JNI_CALLED(this, name, signature) {}
#   define TRACE_JNI_GETTING(this, name, signature) {}
#   define TRACE_JNI_GOT(this, name, signature) {}
#   define TRACE_JNI_SETTING(this, name, signature) {}
#   define TRACE_JNI_SET(this, name, signature) {}
#endif

#define BEGIN_JCLASS(package, name)                                             \
    namespace jni {                                                             \
    class name : public JavaClass<name> {                                       \
        friend class JavaClass<name>;                                           \
        class C_DefaultConstructor; friend class C_DefaultConstructor;          \
        class C_DefaultConstructor : public JMethod {                           \
        public:                                                                 \
            C_DefaultConstructor() : JMethod("<init>", "()V") {}                \
        };                                                                      \
        C_DefaultConstructor _defaultConstructor;                               \
    public:                                                                     \
        name() : JavaClass<name>(package "/" #name) {}

#define END_JCLASS                      };};

#define BEGIN_JINTERFACE(name)                                                  \
    namespace jni {                                                             \
    class name : public JInterface<name> {                                      \
        friend class JObjectMap<jclass, name>;                                  \
        name() : JInterface<name>(#name) {}                                     \
    public:

#define END_JINTERFACE                      };};

#define JCLASS_FINAL_METHOD(ret_type, name, signature)                          \
    private: class JT_METHOD_CLASS(name); friend class JT_METHOD_CLASS(name);                             \
    class JT_METHOD_CLASS(name) : public JMethod {                                           \
    public:                                                                     \
        JT_METHOD_CLASS(name)() : JMethod(#name, signature JSIG_##ret_type) {            \
        }                                                                       \
    };                                                                          \
    JT_METHOD_CLASS(name) JT_METHOD_OBJECT(name);                                                           \
    public:                                                                     \
        static CTYPE_##ret_type name(JNIEnv * env, jobject object, ...) {   \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_CALLING(&_instance, name, signature JSIG_##ret_type)  \
            va_list args;                                                       \
            va_start(args, object);                                             \
            jclass clazz = _instance.getJClass(env);                            \
            JT_CALL_AND_ASSIGN_TO_RESULT(ret_type,                                 \
                env->JT_ENV_NON_VIRTUAL_CALL_##ret_type(object, clazz,         \
                    _instance.JT_METHOD_OBJECT(name).getMethodID(env, clazz), args))           \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(&_instance, name, signature JSIG_##ret_type)   \
            expectExceptionCheck(env);                                          \
            JT_RETURN_RESULT_##ret_type                                            \
        }

#define JCLASS_VIRTUAL_METHOD(ret_type, name, signature)                        \
    private: class JT_METHOD_CLASS(name); friend class JT_METHOD_CLASS(name);                             \
    class JT_METHOD_CLASS(name) : public JMethod {                                           \
    public:                                                                     \
        JT_METHOD_CLASS(name)() : JMethod(#name, signature JSIG_##ret_type) {            \
        }                                                                       \
    };                                                                          \
    JT_METHOD_CLASS(name) JT_METHOD_OBJECT(name);                                                           \
    public:                                                                     \
        static CTYPE_##ret_type name(JNIEnv * env, jobject object, ...) {   \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_CALLING(&_instance, name, signature JSIG_##ret_type)  \
            va_list args;                                                       \
            va_start(args, object);                                             \
            jclass clazz = _instance.getJClass(env);                            \
            JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
                env->JT_ENV_VIRTUAL_CALL_##ret_type(object,                    \
                    _instance.JT_METHOD_OBJECT(name).getMethodID(env, clazz), args))           \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(&_instance, name, signature JSIG_##ret_type)   \
            expectExceptionCheck(env);                                          \
            JT_RETURN_RESULT_##ret_type                                            \
        }

#define JCLASS_STATIC_METHOD(ret_type, name, signature)                         \
    private: class JT_METHOD_CLASS(name); friend class JT_METHOD_CLASS(name);                             \
    class JT_METHOD_CLASS(name) : public JMethod {                                           \
    public:                                                                     \
        JT_METHOD_CLASS(name)() : JMethod(#name, signature JSIG_##ret_type, true) {      \
        }                                                                       \
    };                                                                          \
    JT_METHOD_CLASS(name) JT_METHOD_OBJECT(name);                                                           \
    public:                                                                     \
        static CTYPE_##ret_type name(JNIEnv * env, ...) {                   \
            TRACE_JNI_CALLING(&_instance, name, signature JSIG_##ret_type)  \
            va_list args;                                                       \
            va_start(args, env);                                                \
            jclass clazz = _instance.getJClass(env);                            \
            JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
                env->JT_ENV_STATIC_CALL_##ret_type(clazz,                      \
                    _instance.JT_METHOD_OBJECT(name).getMethodID(env, clazz), args))           \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(&_instance, name, signature JSIG_##ret_type)   \
            expectExceptionCheck(env);                                          \
            JT_RETURN_RESULT_##ret_type                                            \
        }

#define JCLASS_FIELD(ret_type, name)                                            \
    _JCLASS_FIELD(ret_type, name, JSIG_##ret_type)

#define JCLASS_FIELD_OBJECT(name, signature)                                    \
    _JCLASS_FIELD(Object, name, signature)

#define _JCLASS_FIELD(ret_type, name, signature)                                \
    private: class JT_METHOD_CLASS(name); friend class JT_METHOD_CLASS(name);                             \
    class JT_METHOD_CLASS(name) : public JField {                                            \
    public:                                                                     \
        JT_METHOD_CLASS(name)() : JField(#name, signature) {                                 \
        }                                                                       \
    };                                                                          \
    JT_METHOD_CLASS(name) JT_METHOD_OBJECT(name);                                                           \
    public:                                                                     \
        static CTYPE_##ret_type name##_Get(JNIEnv * env, jobject object) {  \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_GETTING(&_instance, name, signature)                      \
            CTYPE_##ret_type result = static_cast<CTYPE_##ret_type>(    \
                env->JT_ENV_GET_##ret_type(object,                             \
                        _instance.JT_METHOD_OBJECT(name).getFieldID(env,                       \
                                    _instance.getJClass(env))));                \
            TRACE_JNI_GOT(&_instance, name, signature)                          \
            JT_RETURN_RESULT_##ret_type                                            \
        }                                                                       \
        static void name##_Set(JNIEnv * env, jobject object,                    \
                         CTYPE_##ret_type value) {                          \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_SETTING(&_instance, name, signature)                      \
            env->JT_ENV_SET_##ret_type(object,                                 \
                _instance.JT_METHOD_OBJECT(name).getFieldID(env, _instance.getJClass(env)),    \
                value);                                                         \
            TRACE_JNI_SET(&_instance, name, signature)                          \
        }

#define JCLASS_STATIC_FIELD(ret_type, name)                                     \
    _JCLASS_STATIC_FIELD(ret_type, name, JSIG_##ret_type)

#define JCLASS_STATIC_FIELD_OBJECT(name, signature)                             \
    _JCLASS_STATIC_FIELD(Object, name, signature)

#define _JCLASS_STATIC_FIELD(ret_type, name, signature)                         \
    private: class JT_METHOD_CLASS(name); friend class JT_METHOD_CLASS(name);                             \
    class JT_METHOD_CLASS(name) : public JField {                                            \
    public:                                                                     \
        JT_METHOD_CLASS(name)() : JField(#name, signature, true) {                           \
        }                                                                       \
    };                                                                          \
    JT_METHOD_CLASS(name) JT_METHOD_OBJECT(name);                                                           \
    public:                                                                     \
        static CTYPE_##ret_type name##_Get(JNIEnv * env) {                  \
            TRACE_JNI_GETTING(&_instance, name, signature)                      \
            jclass clazz = _instance.getJClass(env);                            \
            CTYPE_##ret_type result = static_cast<CTYPE_##ret_type>(    \
                env->JT_ENV_STATIC_GET_##ret_type(clazz,                       \
                        _instance.JT_METHOD_OBJECT(name).getFieldID(env, clazz)));             \
            TRACE_JNI_GOT(&_instance, name, signature)                          \
            JT_RETURN_RESULT_##ret_type                                            \
        }                                                                       \
        static void name##_Set(JNIEnv * env, CTYPE_##ret_type value) {      \
            TRACE_JNI_SETTING(&_instance, name, signature)                      \
            jclass clazz = _instance.getJClass(env);                            \
            env->JT_ENV_STATIC_SET_##ret_type(clazz,                           \
                _instance.JT_METHOD_OBJECT(name).getFieldID(env, clazz), value);               \
            TRACE_JNI_SET(&_instance, name, signature)                          \
        }

#define JINTERFACE_METHOD(ret_type, name, signature)                            \
    private: class JT_METHOD_CLASS(name); friend class JT_METHOD_CLASS(name);                             \
    class JT_METHOD_CLASS(name) : public JMethod {                                           \
    public:                                                                     \
        JT_METHOD_CLASS(name)() : JMethod(#name, signature JSIG_##ret_type) {            \
        }                                                                       \
    };                                                                          \
    JT_METHOD_CLASS(name) JT_METHOD_OBJECT(name);                                                           \
    public:                                                                     \
        CTYPE_##ret_type name(JNIEnv * env, jobject object, ...) {          \
            CHECK_OBJECT_CLASS(env, this, object)                               \
            TRACE_JNI_CALLING(this, name, signature JSIG_##ret_type)        \
            va_list args;                                                       \
            va_start(args, object);                                             \
            JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
                        env->JT_ENV_VIRTUAL_CALL_##ret_type(object,            \
                            JT_METHOD_OBJECT(name).getMethodID(env, getJClass()), args));      \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(this, name, signature JSIG_##ret_type)         \
            expectExceptionCheck(env);                                          \
            JT_RETURN_RESULT_##ret_type                                            \
        }

#define JCLASS_CONSTRUCTOR(param_spec)                                                          \
        _JCLASS_CONSTRUCTOR_WITH_NAME(                                                          \
                JT_PARAM_SPEC_TO_CONSTRUCTORNAME(param_spec),                                   \
                JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                         \
                JT_PARAM_SPEC_TO_PARAM_LIST(param_spec),                                        \
                JT_PARAM_SPEC_TO_SIG(param_spec))

#define _JCLASS_CONSTRUCTOR_WITH_NAME(name, param_def, param_list, sig)                         \
    private: class JT_METHOD_CLASS(name); friend class JT_METHOD_CLASS(name);                   \
    class JT_METHOD_CLASS(name) : public JMethod {                                              \
    public:                                                                                     \
        JT_METHOD_CLASS(name)() : JMethod("<init>", sig "V") {}                                 \
    };                                                                                          \
    JT_METHOD_CLASS(name) JT_METHOD_OBJECT(name);                                               \
    public:                                                                                     \
        static jobject newInstance(JNIEnv * env param_def) {                                    \
            jclass clazz = _instance.getJClass(env);                                            \
            jobject result = env->NewObject(clazz, _instance.JT_METHOD_OBJECT(name)             \
                                    .getMethodID(env, clazz) param_list);                       \
            expectExceptionCheck(env);                                                          \
            return result;                                                                      \
        }

//            JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
//                env->JT_ENV_VIRTUAL_CALL_##ret_type(object,                    \
//                    _instance.JNI_INNER_CLASSINSTANCE(name).getMethodID(env, clazz), args))
//            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
//            TRACE_JNI_CALLING(&_instance, name, signature JSIG_##ret_type)  \
//            va_list args;                                                       \
//            va_start(args, object);
//            va_end(args);                                                       \
//            TRACE_JNI_CALLED(&_instance, name, signature JSIG_##ret_type)

namespace jni {

inline void expectExceptionCheck(JNIEnv * env) {
#ifdef JNI_TOOLS_DEBUG_CALL_AND_EXCEPTION_CLEAR_BEHAVIOR
    char * p = (char*)env;
    for (int i = 0; i < sizeof(*env); i++) {
        p[i]++;
    }
#endif
}

inline void prepareExceptionCheck(JNIEnv * env) {
#ifdef JNI_TOOLS_DEBUG_CALL_AND_EXCEPTION_CLEAR_BEHAVIOR
    char * p = (char*)env;
    for (int i = 0; i < sizeof(*env); i++) {
        p[i]--;
    }
#endif
}

template<class T>
class JavaClass {
    char const * _fullname;
    jclass _jclass;
protected:
    static T & _instance;
    JavaClass(char const * fullname) :
        _fullname(fullname), _jclass(NULL) {
    }

#ifdef TRACE_ON
    template<typename T2>
    friend std::ostream & operator<<(std::ostream &, JavaClass<T2> &);
#endif
#ifdef USE_MY_ASSERTS
    void checkObjectClass(JNIEnv * env, jobject object) {
        MY_ASSERT(env->IsInstanceOf(object, getJClass(env)))
    }
#endif // USE_MY_ASSERTS
public:
    jclass getJClass(JNIEnv * env) {
        if (!_jclass) {
            // Lazy initialize jclass reference
            TRACE ("env->FindClass() for " << _fullname)
            jclass clazz = env->FindClass(_fullname);
            FATALIF1(!clazz, "Error finding class '%s'", _fullname)
            _jclass = static_cast<jclass> (env->NewGlobalRef(clazz));
            MY_ASSERT(_jclass);
        }
        return _jclass;
    }

    static jobject newInstance(JNIEnv * env) {
        jclass clazz = _instance.getJClass(env);
        jmethodID defaultConstructor = _instance._defaultConstructor.getMethodID(env, clazz);
        FATALIF1(defaultConstructor == NULL, "Class '%s' has no default constructor",
                _instance._fullname);
        jobject newObject = env->NewObject(clazz, defaultConstructor);
        expectExceptionCheck(env);
        return newObject;
    }

    static bool isInstance(JNIEnv * env, jobject object) {
        return env->IsInstanceOf(object, _instance.getJClass(env));
    }
    static bool isAssingableFromInstanceOf(JNIEnv * env, jclass clazz) {
        return env->IsAssignableFrom(clazz, _instance.getJClass(env));
    }
};

template<typename T>
T & JavaClass<T>::_instance = *(new T());

#ifdef TRACE_ON
template<typename T>
inline std::ostream & operator<<(std::ostream & stream, JavaClass<T> & javaClass) {
    stream << javaClass._fullname;
}
#endif // TRACE_ON
template<typename T>
class JInterface {
    static JObjectMap<jclass, T> _jinterfaceMap;
    char const * _name;
    jclass _jclass;
protected:
    JInterface(char const * name) :
        _name(name) {
    }
#ifdef USE_MY_ASSERTS
    void checkObjectClass(JNIEnv * env, jobject object) {
        jclass clazz = env->GetObjectClass(object);
        FATALIF(!clazz, "JInterface::checkObject(): GetObjectClass() failed")
        MY_ASSERT(env->IsSameObject(_jclass, clazz))
    }
#endif // USE_MY_ASSERTS
public:
    jclass getJClass() {
        return _jclass;
    }
    char const * getName() {
        return _name;
    }
    static T & getInstanceFromObject(JNIEnv * env, jobject jobject) {
        jclass jobjectClass = env->GetObjectClass(jobject);
        FATALIF(!jobjectClass, "Error determining object class");
        return getInstance(env, jobjectClass);
    }
    static T & getInstance(JNIEnv * env, jclass objectClass) {
        T * instance = _jinterfaceMap.get(env, objectClass);
        if (instance) {
            return *instance;
        }
        objectClass = (jclass) env->NewGlobalRef(objectClass);
        T & newInstance = _jinterfaceMap.add(objectClass);
        newInstance._jclass = objectClass;
        return newInstance;
    }
};

template<typename T>
JObjectMap<jclass, T> JInterface<T>::_jinterfaceMap;

#ifdef TRACE_ON
template<typename T>
inline std::ostream & operator<<(std::ostream & stream, JInterface<T> & interface) {
    stream << interface.getName();
}
#endif // TRACE_ON
class JMethod {
#ifdef TRACE_ON
    friend std::ostream & operator<<(std::ostream &, JMethod &);
#endif
    char const * _name;
    char const * _signature;
    bool _isStatic;
    jmethodID _jmethodID;
protected:
    JMethod(char const * name, char const * signature, bool isStatic = false) :
        _name(name), _signature(signature), _isStatic(isStatic), _jmethodID(NULL) {
    }
public:
    jmethodID getMethodID(JNIEnv * env, jclass jclazz) {
        if (!_jmethodID) {
            TRACE("Getting method id for " << *this);
            if (_isStatic) {
                _jmethodID = env->GetStaticMethodID(jclazz, _name, _signature);
            } else {
                _jmethodID = env->GetMethodID(jclazz, _name, _signature);
            }
            FATALIF3(!_jmethodID, "Method not found: %s() signature %s%s", _name, _signature,
                    _isStatic ? " (static)" : "");
        }
        return _jmethodID;
    }
};

#ifdef TRACE_ON
inline std::ostream & operator<<(std::ostream & stream, JMethod & method) {
    stream << method._name << method._signature;
}
#endif

class JField {
#ifdef TRACE_ON
    friend std::ostream & operator<<(std::ostream &, JField &);
#endif
    char const * _name;
    char const * _signature;
    bool _isStatic;
    jfieldID _jfieldID;

protected:
    JField(char const * name, char const * signature, bool isStatic = false) :
        _name(name), _signature(signature), _isStatic(isStatic), _jfieldID(NULL) {
    }
public:
    jfieldID getFieldID(JNIEnv * env, jclass jclazz) {
        if (!_jfieldID) {
            TRACE("Getting field id for " << *this);
            if (_isStatic) {
                _jfieldID = env->GetStaticFieldID(jclazz, _name, _signature);
            } else {
                _jfieldID = env->GetFieldID(jclazz, _name, _signature);
            }
            FATALIF3(!_jfieldID, "Field not found: %s signature %s%s", _name, _signature, _isStatic
                    ? " (static)" : "");
        }
        return _jfieldID;
    }
};

#ifdef TRACE_ON
inline std::ostream & operator<<(std::ostream & stream, JField & field) {
    stream << field._name << " (" << field._signature << ")";
}
#endif

}

#endif /* JAVASTATICINFO_H_ */
