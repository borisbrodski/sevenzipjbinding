// TODO Rename to JniTools.h

/*
 * JavaStaticInfo.h
 *
 * Created on: Jan 14, 2010
 * Author: Boris Brodski
 */

/*
 * JniTools: Generic library to easy access java methods from C++ through JNI interface.
 *
 * Usage:
 * ------
 *
 * JniTools-types:
 * - Int
 * - Long
 * - Void
 * - Object
 * - String
 * - Class
 *
 * Technical info
 * --------------
 *
 * 1 Name convention
 *
 * JT_* - public macros
 * _JT_* - intern macros
 *
 * _JT_CTYPE - C++ type of the java type. Example: jstring, jlong
 * _JT_JSIG -  Signature of java types (string const). Example "J", "Ljava/lang/String;"
 *
 */

#ifndef JAVASTATICINFO_H_
#define JAVASTATICINFO_H_

#include <map>
#include <stdarg.h>
#include "JObjectList.h"

#ifdef __ANDROID_API__
#include "JBindingTools.h"
#endif

// TODO Remove from here
#define JBINDING_JNIEXPORT extern "C" JNIEXPORT

// -----------------------------
// -- General purpose defines --
// -----------------------------

// Expand "a"
#define _JT_EXPAND(a)                           a

// Expand "f" and "a". Expand f(a)
#define _JT_APPLY1(f,a)                         f(a)

// Expand "a" and convert into string constant
#define _JT_TO_STRING(a)                        #a

// Expand "a" and "b" and concatenate it together
#define _JT_CONCAT2(a, b)                       _JT__CONCAT2(a, b)
#define _JT__CONCAT2(a, b)                      a##b

// Expand "a", "b" and "C" and concatenate it together
#define _JT_CONCAT3(a, b, c)                    _JT__CONCAT3(a, b, c)
#define _JT__CONCAT3(a, b, c)                   a##b##c

// --------------------------------------
// -- Internal structure configuration --
// --------------------------------------

// Name of the class to represent a method (extends JMethod)
#define _JT_METHOD_CLASS(name)                  C_##name

// Name of the instance variable of the method class
#define _JT_METHOD_OBJECT(name)                 _##name

// ------------------------------
// -- Type dependent constants --
// ------------------------------

#define _JT_CTYPE_Boolean                       jboolean
#define _JT_CTYPE_Int                           jint
#define _JT_CTYPE_Long                          jlong
#define _JT_CTYPE_Object                        jobject
#define _JT_CTYPE_Class                         jclass
#define _JT_CTYPE_String                        jstring
#define _JT_CTYPE_Void                          void
#define _JT_CTYPE_ByteArray                     jbyteArray

#define _JT_JSIG_Boolean                        "Z"
#define _JT_JSIG_Int                            "I"
#define _JT_JSIG_Long                           "J"
#define _JT_JSIG_Object                         "Ljava/lang/Object;"
#define _JT_JSIG_Class                          "Ljava/lang/Class;"
#define _JT_JSIG_String                         "Ljava/lang/String;"
#define _JT_JSIG_Void                           "V"
#define _JT_JSIG_ByteArray                      "[B"

#define _JT_CALL_AND_ASSIGN_TO_RESULT(type, e)  _JT_APPLY1(_JT_CALL_AND_ASSIGN_TO_RESULT_##type,e)

#define _JT_CALL_AND_ASSIGN_TO_RESULT_Object(e) jobject __result = static_cast<jobject>(e);
#define _JT_CALL_AND_ASSIGN_TO_RESULT_String(e) jstring __result = static_cast<jstring>(e);
#define _JT_CALL_AND_ASSIGN_TO_RESULT_Long(e)   jlong __result = static_cast<jlong>(e);
#define _JT_CALL_AND_ASSIGN_TO_RESULT_Int(e)    jint __result = static_cast<jint>(e);
#define _JT_CALL_AND_ASSIGN_TO_RESULT_Boolean(e)                                                \
                                                jboolean __result = static_cast<jboolean>(e);
#define _JT_CALL_AND_ASSIGN_TO_RESULT_Class(e)  jclass __result = static_cast<jclass>(e);
#define _JT_CALL_AND_ASSIGN_TO_RESULT_Void(e)   char const * __result = "<void>"; e;
#define _JT_CALL_AND_ASSIGN_TO_RESULT_ByteArray(e)                                              \
                                                jbyteArray __result = static_cast<jobject>(e);

#define _JT_RETURN_RESULT_String                return __result;
#define _JT_RETURN_RESULT_Long                  return __result;
#define _JT_RETURN_RESULT_Int                   return __result;
#define _JT_RETURN_RESULT_Boolean               return __result;
#define _JT_RETURN_RESULT_Class                 return __result;
#define _JT_RETURN_RESULT_Object                return __result;
#define _JT_RETURN_RESULT_ByteArray             return __result;
#define _JT_RETURN_RESULT_Void

#define _JT_ENV_VIRTUAL_CALL_Int                CallIntMethod
#define _JT_ENV_VIRTUAL_CALL_Long               CallLongMethod
#define _JT_ENV_VIRTUAL_CALL_Boolean            CallBooleanMethod
#define _JT_ENV_VIRTUAL_CALL_Object             CallObjectMethod
#define _JT_ENV_VIRTUAL_CALL_String             CallObjectMethod
#define _JT_ENV_VIRTUAL_CALL_Void               CallVoidMethod
#define _JT_ENV_VIRTUAL_CALL_ByteArray          CallObjectMethod

#define _JT_ENV_NON_VIRTUAL_CALL_Int            CallNonvirtualIntMethod
#define _JT_ENV_NON_VIRTUAL_CALL_Long           CallNonvirtualLongMethod
#define _JT_ENV_NON_VIRTUAL_CALL_Boolean        CallNonvirtualBooleanMethod
#define _JT_ENV_NON_VIRTUAL_CALL_Object         CallNonvirtualObjectMethod
#define _JT_ENV_NON_VIRTUAL_CALL_String         CallNonvirtualObjectMethod
#define _JT_ENV_NON_VIRTUAL_CALL_Void           CallNonvirtualVoidMethod
#define _JT_ENV_NON_VIRTUAL_CALL_ByteArray      CallNonvirtualObjectMethod

#define _JT_ENV_STATIC_CALL_Int                 CallStaticIntMethod
#define _JT_ENV_STATIC_CALL_Long                CallStaticLongMethod
#define _JT_ENV_STATIC_CALL_Boolean             CallStaticBooleanMethod
#define _JT_ENV_STATIC_CALL_Object              CallStaticObjectMethod
#define _JT_ENV_STATIC_CALL_String              CallStaticObjectMethod
#define _JT_ENV_STATIC_CALL_Void                CallStaticVoidMethod
#define _JT_ENV_STATIC_CALL_ByteArray           CallStaticObjectMethod

#define _JT_ENV_GET_Int                         GetIntField
#define _JT_ENV_GET_Long                        GetLongField
#define _JT_ENV_GET_Boolean                     GetBooleanField
#define _JT_ENV_GET_String                      GetObjectField
#define _JT_ENV_GET_Class                       GetObjectField
#define _JT_ENV_GET_Object                      GetObjectField
#define _JT_ENV_GET_ByteArray                   GetObjectField

#define _JT_ENV_STATIC_GET_Int                  GetStaticIntField
#define _JT_ENV_STATIC_GET_Long                 GetStaticLongField
#define _JT_ENV_STATIC_GET_Boolean              GetStaticBooleanField
#define _JT_ENV_STATIC_GET_String               GetStaticObjectField
#define _JT_ENV_STATIC_GET_Object               GetStaticObjectField
#define _JT_ENV_STATIC_GET_ByteArray            GetStaticObjectField

#define _JT_ENV_SET_Int                         SetIntField
#define _JT_ENV_SET_Long                        SetLongField
#define _JT_ENV_SET_Boolean                     SetBooleanField
#define _JT_ENV_SET_String                      SetObjectField
#define _JT_ENV_SET_Object                      SetObjectField
#define _JT_ENV_SET_Class                       SetObjectField
#define _JT_ENV_SET_ByteArray                   SetObjectField

#define _JT_ENV_STATIC_SET_Int                  SetStaticIntField
#define _JT_ENV_STATIC_SET_Long                 SetStaticLongField
#define _JT_ENV_STATIC_SET_Boolean              SetStaticBooleanField
#define _JT_ENV_STATIC_SET_Object               SetStaticObjectField
#define _JT_ENV_STATIC_SET_String               SetStaticObjectField
#define _JT_ENV_STATIC_SET_ByteArray            SetStaticObjectField

// -----------------------------
// -- Parameter specification --
// -----------------------------

// Allow code assistance for the parameter specification define
#define JT_PARAM(type, sig, name, param_spec)   JT_PARAM(type, sig, name, param_spec)

// Get constructor name from the parameter specification
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME(param_spec)                                           \
    _JT_PARAM_SPEC_TO_CONSTRUCTORNAME1_##param_spec
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME1_JT_PARAM(type, sig, name, param_spec)                \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME2_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME2_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME3_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME3_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME4_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME4_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME5_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME5_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME6_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME6_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME7_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME7_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME8_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME8_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME9_##param_spec))
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME9_JT_PARAM(type, sig,name, param_spec)                 \
    _JT_CONCAT3(_,name,_JT_EXPAND(_JT_PARAM_SPEC_TO_CONSTRUCTORNAME10_##param_spec))

#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME1__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME2__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME3__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME4__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME5__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME6__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME7__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME8__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME9__    _constr
#define _JT_PARAM_SPEC_TO_CONSTRUCTORNAME10__    _constr

// Get signature from the parameter specification
#define _JT_PARAM_SPEC_TO_SIG(param_spec) "(" _JT_PARAM_SPEC_TO_SIG1_##param_spec
#define _JT_PARAM_SPEC_TO_SIG1_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG2_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG2_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG3_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG3_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG4_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG4_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG5_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG5_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG6_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG6_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG7_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG7_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG8_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG8_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG9_##param_spec)
#define _JT_PARAM_SPEC_TO_SIG9_JT_PARAM(type, sig, name, param_spec)                            \
    sig _JT_EXPAND(_JT_PARAM_SPEC_TO_SIG10_##param_spec)

#define _JT_PARAM_SPEC_TO_SIG1__                ")"
#define _JT_PARAM_SPEC_TO_SIG2__                ")"
#define _JT_PARAM_SPEC_TO_SIG3__                ")"
#define _JT_PARAM_SPEC_TO_SIG4__                ")"
#define _JT_PARAM_SPEC_TO_SIG5__                ")"
#define _JT_PARAM_SPEC_TO_SIG6__                ")"
#define _JT_PARAM_SPEC_TO_SIG7__                ")"
#define _JT_PARAM_SPEC_TO_SIG8__                ")"
#define _JT_PARAM_SPEC_TO_SIG9__                ")"
#define _JT_PARAM_SPEC_TO_SIG10__               ")"

// Get parameter definition list from the parameter specification
#define _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec) _JT_PARAM_SPEC_TO_PARAM_DEF1_##param_spec
#define _JT_PARAM_SPEC_TO_PARAM_DEF1_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type name) _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF2_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF2_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF3_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF3_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF4_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF4_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF5_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF5_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF6_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF6_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF7_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF7_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF8_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF8_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF9_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_DEF9_JT_PARAM(type, sig, name, param_spec)                      \
    , _JT_EXPAND(_JT_CTYPE_##type) name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_DEF10_##param_spec)

#define _JT_PARAM_SPEC_TO_PARAM_DEF1__
#define _JT_PARAM_SPEC_TO_PARAM_DEF2__
#define _JT_PARAM_SPEC_TO_PARAM_DEF3__
#define _JT_PARAM_SPEC_TO_PARAM_DEF4__
#define _JT_PARAM_SPEC_TO_PARAM_DEF5__
#define _JT_PARAM_SPEC_TO_PARAM_DEF6__
#define _JT_PARAM_SPEC_TO_PARAM_DEF7__
#define _JT_PARAM_SPEC_TO_PARAM_DEF8__
#define _JT_PARAM_SPEC_TO_PARAM_DEF9__
#define _JT_PARAM_SPEC_TO_PARAM_DEF10__

// Get parameter list (for function call) from the parameter specification
#define _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec) _JT_PARAM_SPEC_TO_PARAM_LIST1_##param_spec
#define _JT_PARAM_SPEC_TO_PARAM_LIST1_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST2_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST2_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST3_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST3_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST4_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST4_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST5_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST5_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST6_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST6_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST7_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST7_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST8_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST8_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST9_##param_spec)
#define _JT_PARAM_SPEC_TO_PARAM_LIST9_JT_PARAM(type, sig, name, param_spec)                     \
    , name _JT_EXPAND(_JT_PARAM_SPEC_TO_PARAM_LIST10_##param_spec)

#define _JT_PARAM_SPEC_TO_PARAM_LIST1__
#define _JT_PARAM_SPEC_TO_PARAM_LIST2__
#define _JT_PARAM_SPEC_TO_PARAM_LIST3__
#define _JT_PARAM_SPEC_TO_PARAM_LIST4__
#define _JT_PARAM_SPEC_TO_PARAM_LIST5__
#define _JT_PARAM_SPEC_TO_PARAM_LIST6__
#define _JT_PARAM_SPEC_TO_PARAM_LIST7__
#define _JT_PARAM_SPEC_TO_PARAM_LIST8__
#define _JT_PARAM_SPEC_TO_PARAM_LIST9__
#define _JT_PARAM_SPEC_TO_PARAM_LIST10__

// Define aliases for common parameter types
#define JT_INT(name, param_spec)                                                                \
            JT_PARAM(Int, "I", name, param_spec)

#define JT_LONG(name, param_spec)                                                               \
            JT_PARAM(Long, "J", name, param_spec)

#define JT_BOOLEAN(name, param_spec)                                                            \
            JT_PARAM(Boolean, "Z", name, param_spec)

#define JT_STRING(name, param_spec)                                                             \
            JT_PARAM(String, "Ljava/lang/String;", name, param_spec)

#define JT_THROWABLE(name, param_spec)                                                          \
            JT_PARAM(Object, "Ljava/lang/Throwable;", name, param_spec)

#define JT_BYTE_ARRAY(name, param_spec)                                                         \
            JT_PARAM(ByteArray, "[B", name, param_spec)

#define JT_LONG_OBJECT(name, param_spec)                                                        \
            JT_PARAM(Object, "Ljava/lang/Long;", name, param_spec)

// ------------------------------
// -- Assert and trace defines --
// ------------------------------

#ifdef USE_MY_ASSERTS
#   define CHECK_OBJECT_CLASS(env, this, obj)   {(this)->checkObjectClass(env, obj);}
#else
#   define CHECK_OBJECT_CLASS(env, this, obj)   {}
#endif

#ifdef TRACE_ON
#   define TRACE_JNI_CALLING(this, name, sig)   {std::cout << "Calling " << *(this) << '.' << #name << sig << std::endl;}
#   define TRACE_JNI_CALLED(this, name, sig)    {std::cout << "Called " << *(this) << '.' << #name << " returned " << __env << __result << std::endl;}
#   define TRACE_JNI_GETTING(this, name, sig)   {std::cout << "Getting " << *(this) << '.' << #name << '(' << sig << ')' << std::endl;}
#   define TRACE_JNI_GOT(this, name, sig)       {std::cout << "Got " << *(this) << '.' << #name << "='" << env << __result << "'" << std::endl;}
#   define TRACE_JNI_SETTING(this, name, sig)   {std::cout << "Setting " << *(this) << '.' << #name << '(' << sig << ')' << '=' << env << value << std::endl;}
#   define TRACE_JNI_SET(this, name, sig)       {std::cout << "Set " << *(this) << '.' << #name << std::endl;}
#else
#   define TRACE_JNI_CALLING(this, name, sig)   {}
#   define TRACE_JNI_CALLED(this, name, sig)    {}
#   define TRACE_JNI_GETTING(this, name, sig)   {}
#   define TRACE_JNI_GOT(this, name, sig)       {}
#   define TRACE_JNI_SETTING(this, name, sig)   {}
#   define TRACE_JNI_SET(this, name, sig)       {}
#endif
#define JT_BEGIN_CLASS(package, name)                                                           \
    namespace jni {                                                                             \
    class name : public JavaClass<name> {                                                       \
        friend class JavaClass<name>;                                                           \
        class C_DefaultConstructor; friend class C_DefaultConstructor;                          \
        class C_DefaultConstructor : public JMethod {                                           \
        public:                                                                                 \
            C_DefaultConstructor() : JMethod("<init>", "()V") {}                                \
        };                                                                                      \
        C_DefaultConstructor _defaultConstructor;                                               \
    public:                                                                                     \
        static const char * getName() {                                                         \
            static const char * _name = package "/" #name ;                                     \
            return _name;                                                                       \
        }                                                                                       \
        name() : JavaClass<name>() {}

#define JT_END_CLASS                      };};

#define JT_BEGIN_INTERFACE(package, name)                                                       \
    namespace jni {                                                                             \
    class name : public JInterface<name> {                                                      \
        friend class JObjectMap<name>;                                                          \
    public:                                                                                     \
        name() : JInterface<name>() {}                                                          \
        static const char * _getName() {                                                        \
            static const char * _name = package "/" #name;                                      \
            return _name;                                                                       \
        }

#define JT_END_INTERFACE                      };};


#define JT_CLASS_FINAL_METHOD(ret_type, name, param_spec)                                       \
            _JT_CLASS_FINAL_METHOD(ret_type, name,                                              \
                _JT_PARAM_SPEC_TO_SIG(param_spec) _JT_JSIG_##ret_type,                          \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define JT_CLASS_FINAL_METHOD_OBJECT(ret_sig, name, param_spec)                                 \
            _JT_CLASS_FINAL_METHOD(Object, name,                                                \
                _JT_PARAM_SPEC_TO_SIG(param_spec) ret_sig,                                      \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define _JT_CLASS_FINAL_METHOD(ret_type, name, sig, param_def, param_list)                      \
    private: class _JT_METHOD_CLASS(name); friend class _JT_METHOD_CLASS(name);                 \
    class _JT_METHOD_CLASS(name) : public JMethod {                                             \
    public:                                                                                     \
        _JT_METHOD_CLASS(name)() : JMethod(#name, sig) {}                                       \
    };                                                                                          \
    _JT_METHOD_CLASS(name) _JT_METHOD_OBJECT(name);                                             \
    public:                                                                                     \
        static _JT_CTYPE_##ret_type name(JNIEnv * __env, jobject __obj param_def) {             \
            CHECK_OBJECT_CLASS(__env, &_instance, __obj)                                        \
            TRACE_JNI_CALLING(&_instance, name, sig)                                            \
            jclass __clazz = _instance.getJClass(__env);                                        \
            _JT_CALL_AND_ASSIGN_TO_RESULT(ret_type,                                             \
                __env->_JT_ENV_NON_VIRTUAL_CALL_##ret_type(__obj, __clazz,                      \
                    _instance._JT_METHOD_OBJECT(name).getMethodID(__env, __clazz) param_list))  \
            TRACE_JNI_CALLED(&_instance, name, sig)                                             \
            expectExceptionCheck(__env);                                                        \
            _JT_RETURN_RESULT_##ret_type                                                        \
        }


#define JT_CLASS_VIRTUAL_METHOD(ret_type, name, param_spec)                                     \
            _JT_CLASS_VIRTUAL_METHOD(ret_type, name,                                            \
                _JT_PARAM_SPEC_TO_SIG(param_spec) _JT_JSIG_##ret_type,                          \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define JT_CLASS_VIRTUAL_METHOD_OBJECT(ret_sig, name, param_spec)                               \
            _JT_CLASS_VIRTUAL_METHOD(Object, name,                                              \
                _JT_PARAM_SPEC_TO_SIG(param_spec) ret_sig,                                      \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define _JT_CLASS_VIRTUAL_METHOD(ret_type, name, sig, param_def, param_list)                    \
    private: class _JT_METHOD_CLASS(name); friend class _JT_METHOD_CLASS(name);                 \
    class _JT_METHOD_CLASS(name) : public JMethod {                                             \
    public:                                                                                     \
        _JT_METHOD_CLASS(name)() : JMethod(#name, sig) {}                                       \
    };                                                                                          \
    _JT_METHOD_CLASS(name) _JT_METHOD_OBJECT(name);                                             \
    public:                                                                                     \
        static _JT_CTYPE_##ret_type name(JNIEnv * __env, jobject __obj param_def) {             \
            CHECK_OBJECT_CLASS(__env, &_instance, __obj)                                        \
            TRACE_JNI_CALLING(&_instance, name, sig)                                            \
            jclass __clazz = _instance.getJClass(__env);                                        \
            _JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                                           \
                __env->_JT_ENV_VIRTUAL_CALL_##ret_type(__obj,                                   \
                    _instance._JT_METHOD_OBJECT(name).getMethodID(__env, __clazz) param_list))  \
            TRACE_JNI_CALLED(&_instance, name, sig)                                             \
            expectExceptionCheck(__env);                                                        \
            _JT_RETURN_RESULT_##ret_type                                                        \
        }


#define JT_CLASS_STATIC_METHOD(ret_type, name, param_spec)                                      \
            _JT_CLASS_STATIC_METHOD(ret_type, name,                                             \
                _JT_PARAM_SPEC_TO_SIG(param_spec) _JT_JSIG_##ret_type,                          \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define JT_CLASS_STATIC_METHOD_OBJECT(ret_sig, name, param_spec)                                \
            _JT_CLASS_STATIC_METHOD(Object, name,                                               \
                _JT_PARAM_SPEC_TO_SIG(param_spec) ret_sig,                                      \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define _JT_CLASS_STATIC_METHOD(ret_type, name, sig, param_def, param_list)                     \
    private: class _JT_METHOD_CLASS(name); friend class _JT_METHOD_CLASS(name);                 \
    class _JT_METHOD_CLASS(name) : public JMethod {                                             \
    public:                                                                                     \
        _JT_METHOD_CLASS(name)() : JMethod(#name, sig, true) {}                                 \
    };                                                                                          \
    _JT_METHOD_CLASS(name) _JT_METHOD_OBJECT(name);                                             \
    public:                                                                                     \
        static _JT_CTYPE_##ret_type name(JNIEnv * __env param_def) {                            \
            TRACE_JNI_CALLING(&_instance, name, sig)                                            \
            jclass __clazz = _instance.getJClass(__env);                                        \
            _JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                                           \
                __env->_JT_ENV_STATIC_CALL_##ret_type(__clazz,                                  \
                    _instance._JT_METHOD_OBJECT(name).getMethodID(__env, __clazz) param_list))  \
            TRACE_JNI_CALLED(&_instance, name, sig)                                             \
            expectExceptionCheck(__env);                                                        \
            _JT_RETURN_RESULT_##ret_type                                                        \
        }

#define JT_FIELD(ret_type, name)                                                                \
    _JT_FIELD(ret_type, name, _JT_JSIG_##ret_type)

#define JT_FIELD_OBJECT(name, signature)                                                        \
    _JT_FIELD(Object, name, signature)

#define _JT_FIELD(ret_type, name, signature)                                                    \
    private: class _JT_METHOD_CLASS(name); friend class _JT_METHOD_CLASS(name);                 \
    class _JT_METHOD_CLASS(name) : public JField {                                              \
    public:                                                                                     \
        _JT_METHOD_CLASS(name)() : JField(#name, signature) {                                   \
        }                                                                                       \
    };                                                                                          \
    _JT_METHOD_CLASS(name) _JT_METHOD_OBJECT(name);                                             \
    public:                                                                                     \
        static _JT_CTYPE_##ret_type name##_Get(JNIEnv * env, jobject object) {                  \
            CHECK_OBJECT_CLASS(env, &_instance, object)                                         \
            TRACE_JNI_GETTING(&_instance, name, signature)                                      \
            _JT_CTYPE_##ret_type __result = static_cast<_JT_CTYPE_##ret_type>(                  \
                env->_JT_ENV_GET_##ret_type(object,                                             \
                        _instance._JT_METHOD_OBJECT(name).getFieldID(env,                       \
                                    _instance.getJClass(env))));                                \
            TRACE_JNI_GOT(&_instance, name, signature)                                          \
            _JT_RETURN_RESULT_##ret_type                                                        \
        }                                                                                       \
        static void name##_Set(JNIEnv * env, jobject object,                                    \
                         _JT_CTYPE_##ret_type value) {                                          \
            CHECK_OBJECT_CLASS(env, &_instance, object)                                         \
            TRACE_JNI_SETTING(&_instance, name, signature)                                      \
            env->_JT_ENV_SET_##ret_type(object,                                                 \
                _instance._JT_METHOD_OBJECT(name).getFieldID(env, _instance.getJClass(env)),    \
                value);                                                                         \
            TRACE_JNI_SET(&_instance, name, signature)                                          \
        }

#define JT_STATIC_FIELD(ret_type, name)                                                         \
    _JT_STATIC_FIELD(ret_type, name, _JT_JSIG_##ret_type)

#define JT_STATIC_FIELD_OBJECT(name, signature)                                                 \
    _JT_STATIC_FIELD(Object, name, signature)

#define _JT_STATIC_FIELD(ret_type, name, signature)                                             \
    private: class _JT_METHOD_CLASS(name); friend class _JT_METHOD_CLASS(name);                 \
    class _JT_METHOD_CLASS(name) : public JField {                                              \
    public:                                                                                     \
        _JT_METHOD_CLASS(name)() : JField(#name, signature, true) {                             \
        }                                                                                       \
    };                                                                                          \
    _JT_METHOD_CLASS(name) _JT_METHOD_OBJECT(name);                                             \
    public:                                                                                     \
        static _JT_CTYPE_##ret_type name##_Get(JNIEnv * env) {                                  \
            TRACE_JNI_GETTING(&_instance, name, signature)                                      \
            jclass clazz = _instance.getJClass(env);                                            \
            _JT_CTYPE_##ret_type __result = static_cast<_JT_CTYPE_##ret_type>(                  \
                env->_JT_ENV_STATIC_GET_##ret_type(clazz,                                       \
                        _instance._JT_METHOD_OBJECT(name).getFieldID(env, clazz)));             \
            TRACE_JNI_GOT(&_instance, name, signature)                                          \
            _JT_RETURN_RESULT_##ret_type                                                        \
        }                                                                                       \
        static void name##_Set(JNIEnv * env, _JT_CTYPE_##ret_type value) {                      \
            TRACE_JNI_SETTING(&_instance, name, signature)                                      \
            jclass clazz = _instance.getJClass(env);                                            \
            env->_JT_ENV_STATIC_SET_##ret_type(clazz,                                           \
                _instance._JT_METHOD_OBJECT(name).getFieldID(env, clazz), value);               \
            TRACE_JNI_SET(&_instance, name, signature)                                          \
        }

#define JT_INTERFACE_METHOD(ret_type, name, param_spec)                                         \
            _JT_INTERFACE_METHOD(ret_type, name,                                                \
                _JT_PARAM_SPEC_TO_SIG(param_spec) _JT_JSIG_##ret_type,                          \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define JT_INTERFACE_METHOD_OBJECT(ret_sig, name, param_spec)                                   \
            _JT_INTERFACE_METHOD(Object, name,                                                  \
                _JT_PARAM_SPEC_TO_SIG(param_spec) ret_sig,                                      \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec))

#define _JT_INTERFACE_METHOD(ret_type, name, sig, param_def, param_list)                        \
    private: class _JT_METHOD_CLASS(name); friend class _JT_METHOD_CLASS(name);                 \
    class _JT_METHOD_CLASS(name) : public JMethod {                                             \
    public:                                                                                     \
        _JT_METHOD_CLASS(name)() : JMethod(#name, sig) {}                                       \
    };                                                                                          \
    _JT_METHOD_CLASS(name) _JT_METHOD_OBJECT(name);                                             \
    public:                                                                                     \
        _JT_CTYPE_##ret_type name(JNIEnv * __env, jobject __object param_def) {                 \
            CHECK_OBJECT_CLASS(__env, this, __object)                                           \
            TRACE_JNI_CALLING(this, name, sig)                                                  \
            _JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                                           \
                    __env->_JT_ENV_VIRTUAL_CALL_##ret_type(__object,                            \
                        _JT_METHOD_OBJECT(name).getMethodID(__env, getJClass()) param_list))    \
            TRACE_JNI_CALLED(this, name, sig)                                                   \
            expectExceptionCheck(__env);                                                        \
            _JT_RETURN_RESULT_##ret_type                                                        \
        }                                                                                       \
        bool _##name##_exists(JNIEnv * __env) {                                                 \
            return _JT_METHOD_OBJECT(name).exists(__env, getJClass());                          \
        }


#define JT_CLASS_CONSTRUCTOR(param_spec)                                                        \
        _JT_CLASS_CONSTRUCTOR(                                                                  \
                _JT_PARAM_SPEC_TO_CONSTRUCTORNAME(param_spec),                                  \
                _JT_PARAM_SPEC_TO_PARAM_DEF(param_spec),                                        \
                _JT_PARAM_SPEC_TO_PARAM_LIST(param_spec),                                       \
                _JT_PARAM_SPEC_TO_SIG(param_spec))

#define _JT_CLASS_CONSTRUCTOR(name, param_def, param_list, sig)                                 \
    private: class _JT_METHOD_CLASS(name); friend class _JT_METHOD_CLASS(name);                 \
    class _JT_METHOD_CLASS(name) : public JMethod {                                             \
    public:                                                                                     \
        _JT_METHOD_CLASS(name)() : JMethod("<init>", sig "V") {}                                \
    };                                                                                          \
    _JT_METHOD_CLASS(name) _JT_METHOD_OBJECT(name);                                             \
    public:                                                                                     \
        static jobject newInstance(JNIEnv * env param_def) {                                    \
            jclass clazz = _instance.getJClass(env);                                            \
            jobject __result = env->NewObject(clazz, _instance._JT_METHOD_OBJECT(name)          \
                                    .getMethodID(env, clazz) param_list);                       \
            expectExceptionCheck(env);                                                          \
            return __result;                                                                    \
        }
// TODO Log constructor calls

//            _JT_CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
//                env->_JT_ENV_VIRTUAL_CALL_##ret_type(object,                    \
//                    _instance.JNI_INNER_CLASSINSTANCE(name).getMethodID(env, clazz), args))
//            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
//            TRACE_JNI_CALLING(&_instance, name, signature _JT_JSIG_##ret_type)  \
//            va_list args;                                                       \
//            va_start(args, object);
//            va_end(args);                                                       \
//            TRACE_JNI_CALLED(&_instance, name, signature _JT_JSIG_##ret_type)

namespace jni {

inline void expectExceptionCheck(JNIEnv * env) {
#ifdef JNI_TOOLS_DEBUG_CALL_AND_EXCEPTION_CLEAR_BEHAVIOR
    TRACE("Expect exception check")
    char * p = (char*)env;
    for (int i = 0; i < sizeof(*env); i++) {
        p[i]++;
    }
#endif
}

inline void prepareExceptionCheck(JNIEnv * env) {
#ifdef JNI_TOOLS_DEBUG_CALL_AND_EXCEPTION_CLEAR_BEHAVIOR
    TRACE("Prepare exception check")
    char * p = (char*)env;
    for (int i = 0; i < sizeof(*env); i++) {
        p[i]--;
    }
#endif
}

template<class T>
class JavaClass {
    jclass _jclass;
    PlatformCriticalSection _initCriticalSection;
protected:
    static T & _instance;
    JavaClass() :
        _jclass(NULL) {
    }

#ifdef TRACE_ON
    template<typename T2>
    friend std::ostream & operator<<(std::ostream &, JavaClass<T2> &);
#endif
#ifdef USE_MY_ASSERTS
    void checkObjectClass(JNIEnv * env, jobject object) {
        jclass expectedClass = getJClass(env);
#ifndef __ANDROID_API__
        if (!env->IsInstanceOf(object, expectedClass)) {
            jclass objectClass = env->GetObjectClass(object);

            jmethodID getCanonicalNameMethod = env->GetMethodID(objectClass, "getCanonicalName", "()Ljava/lang/String;");

            jstring objectClassNameString = (jstring)env->CallObjectMethod(objectClass, getCanonicalNameMethod);
            const char* objectClassName = env->GetStringUTFChars(objectClassNameString, NULL);

            jstring expectedClassNameString = (jstring)env->CallObjectMethod(expectedClass, getCanonicalNameMethod);
            const char* expectedClassName = env->GetStringUTFChars(expectedClassNameString, NULL);

            fatal("Passed object (instance of %s) doesn't match expected class %s (%s)\n",
                    objectClassName, expectedClassName, T::getName());
        }
#endif
    }
#endif // USE_MY_ASSERTS
private:
    void initIfNecessary(JNIEnv * env) {
        if (_jclass) {
            return;
        }
        _initCriticalSection.Enter();
        if (!_jclass) {
            init(env);
        }
        _initCriticalSection.Leave();
    }
    void init(JNIEnv * env) {
        TRACE ("env->FindClass() for " << T::getName())
        jclass clazz = env->FindClass(T::getName());
#ifdef __ANDROID_API__
        if (clazz == nullptr) {
            clazz = findClass(env, T::getName());
        }
#endif
        FATALIF1(!clazz, "Error finding class '%s'", T::getName())
        _jclass = static_cast<jclass> (env->NewGlobalRef(clazz));
        env->DeleteLocalRef(clazz);
        MY_ASSERT(_jclass);
    }
public:
    jclass getJClass(JNIEnv * env) {
        initIfNecessary(env);
        return _jclass;
    }

    static void _initialize(JNIEnv * env) {
        _instance.initIfNecessary(env);
    }

    // TODO Remove it
    static jobject _newInstance(JNIEnv * env) {
        jclass clazz = _instance.getJClass(env);
        jmethodID defaultConstructor = _instance._defaultConstructor.getMethodID(env, clazz);
        FATALIF1(defaultConstructor == NULL, "Class '%s' has no default constructor",
                T::getName());
        jobject newObject = env->NewObject(clazz, defaultConstructor);
        expectExceptionCheck(env);
        return newObject;
    }

    /*
     * true  - if "object instanceof Class" yields true.
     * false - any other object or null
     */
    static bool _isInstance(JNIEnv * env, jobject object) {
        if (object) {
            return env->IsInstanceOf(object, _instance.getJClass(env));
        }
        return false;
    }
    static bool _isAssingableFromInstanceOf(JNIEnv * env, jclass clazz) {
        return env->IsAssignableFrom(clazz, _instance.getJClass(env));
    }
};

template<typename T>
T & JavaClass<T>::_instance = *(new T());

#ifdef TRACE_ON
template<typename T>
inline std::ostream & operator<<(std::ostream & stream, JavaClass<T> & javaClass) {
    stream << T::getName();
    return stream;
}
#endif // TRACE_ON

template<typename T>
class JInterface {
    static JObjectMap<T*> _jinterfaceMap;
    static PlatformCriticalSection _criticalSection;
    static jclass _classObject;
    jclass _jclass;
protected:
    JInterface() :
        _jclass(NULL) {
    }
#ifdef USE_MY_ASSERTS
    void checkObjectClass(JNIEnv * env, jobject object) {
#ifndef __ANDROID_API__
        jclass clazz = env->GetObjectClass(object);
        FATALIF(!clazz, "JInterface::checkObject(): GetObjectClass() failed")
        MY_ASSERT(env->IsSameObject(_jclass, clazz))
#endif
    }
#endif // USE_MY_ASSERTS
public:
    jclass getJClass() {
        return _jclass;
    }
    static jclass _getClassObject(JNIEnv * env) {
        if (_classObject == NULL) {
            jclass objectClass = env->FindClass(T::_getName());
#ifdef __ANDROID_API__
            if (objectClass == nullptr) {
                objectClass = findClass(env, T::_getName());
            }
#endif
            FATALIF1(!objectClass, "Error finding class '%s'", T::_getName());
            _classObject = (jclass) env->NewGlobalRef(objectClass);
            env->DeleteLocalRef(objectClass);
        }
        return _classObject;
    }
    /*
     * true  - if "object instanceof Class" yields true.
     * false - any other object or null
     */
    static bool _isInstance(JNIEnv * env, jobject object) {
        if (object) {
            return env->IsInstanceOf(object, _getClassObject(env));
        }
        return false;
    }
    static T * _getInstanceFromObject(JNIEnv * env, jobject jobject) {
        FATALIF(!jobject, "_getInstanceFromObject(): 'jobject' can't be null")
        FATALIF(!env, "_getInstanceFromObject(): 'env' can't be null")
#ifdef __ANDROID_API__
        jclass jobjectClass = findClass(env, T::_getName());
#else
        jclass jobjectClass = env->GetObjectClass(jobject);
#endif
        FATALIF(!jobjectClass, "Error determining object class");
        T * instance = _getInstance(env, jobjectClass);
        env->DeleteLocalRef(jobjectClass);
        return instance;
    }
    static T * _getInstance(JNIEnv * env, jclass objectClass) {
        _criticalSection.Enter();
        T ** instance = _jinterfaceMap.get(env, objectClass);
        if (instance) {
            _criticalSection.Leave();
            return *instance;
        }

        objectClass = (jclass) env->NewGlobalRef(objectClass);
        T * newInstance = new T();
        newInstance->_jclass = objectClass;
        _jinterfaceMap.add(objectClass, newInstance);

        _criticalSection.Leave();

        return newInstance;
    }
};

template<typename T>
JObjectMap<T*> JInterface<T>::_jinterfaceMap;

template<typename T>
jclass JInterface<T>::_classObject = NULL;

template<typename T>
PlatformCriticalSection JInterface<T>::_criticalSection;



#ifdef TRACE_ON
template<typename T>
inline std::ostream & operator<<(std::ostream & stream, JInterface<T> & jinterface) {
    stream << T::_getName(); // TODO Also output the class of the implementation here
    return stream;
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
    bool  isInitialized;
    PlatformCriticalSection _initCriticalSection;
protected:
    JMethod(char const * name, char const * signature, bool isStatic = false) :
        _name(name), _signature(signature), _isStatic(isStatic), _jmethodID(NULL) {
        isInitialized = false;
    }
public:
    jmethodID getMethodID(JNIEnv * env, jclass jclazz) {
        initMethodID(env, jclazz);
        if (!_jmethodID) {
            char const * javaClassName = "(error getting ObjectClass)";
            env->ExceptionClear();
#ifndef __ANDROID_API__
            jclass classClass = env->GetObjectClass(jclazz);
            if (classClass) {
                javaClassName = "(error getting Class.getName() method)";
                jmethodID method_getName = env->GetMethodID(classClass, "getName", "()Ljava/lang/String;");
                if (method_getName) {
                    jstring name = (jstring)env->CallObjectMethod(jclazz, method_getName);
                    if (env->ExceptionCheck()) {
                        javaClassName = "(error calling Class.getName())";
                    } else {
                        javaClassName = env->GetStringUTFChars(name, NULL);
                    }
                }
            }
#endif
            FATALIF4(!_jmethodID, "Method not found: %s() signature '%s'%s, java-class: %s", _name, _signature,
                    _isStatic ? " (static)" : "", javaClassName);
        }
        return _jmethodID;
    }
    bool exists(JNIEnv * env, jclass jclazz) {
        initMethodID(env, jclazz);
        return _jmethodID != NULL;
    }
private:
    void initMethodIDIfNecessary(JNIEnv * env, jclass jclazz);
    void initMethodID(JNIEnv * env, jclass jclazz);
};

#ifdef TRACE_ON
inline std::ostream & operator<<(std::ostream & stream, JMethod & method) {
    stream << method._name << method._signature;
    return stream;
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
    return stream;
}
#endif

}

#endif /* JAVASTATICINFO_H_ */
