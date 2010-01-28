/*
 * JavaStaticInfo.h
 *
 *  Created on: Jan 14, 2010
 *      Author: Boris Brodski
 */
#ifndef JAVASTATICINFO_H_
#define JAVASTATICINFO_H_

#include <map>
#include <stdarg.h>
#include "JNITools.h"
#include "JObjectList.h"

#ifdef JSF_DEFINE_VARIABLES
#   define JSI_VARIABLE(type, name, init)	type name init;
#else
#   define JSI_VARIABLE(type, name, init)	extern type name;
#endif

#ifdef JSF_DEFINE_VARIABLES
#   define JAVA_FINAL_CLASS(package, name) namespace name {JavaFinalClass _class(package "/" #name);} namespace name
#   define JAVA_INTERFACE(name) namespace name {JavaInterface _interface;} namespace name
#else
#   define JAVA_FINAL_CLASS(package, name) namespace name {extern JavaFinalClass _class;} namespace name
#   define JAVA_INTERFACE(name) namespace name {extern JavaInterface _interface;} namespace name
#endif

//#define JAVA_CLASS(package, name) JAVA_INTERFACE(package, name)

#define JAVA_FINAL_CLASS_METHOD(rettype, name, signature) JSI_VARIABLE(JavaFinalClass##rettype##Method, name, (_class, #name, signature))
#define JAVA_INTERFACE_METHOD(name, signature) JSI_VARIABLE(JavaInterfaceMethod, name, (_interface, #name, signature))

#define JAVA_FIELD(type, name, signature) JSI_VARIABLE(Java##type##Field, name, (_class, #name, signature))

#define JAVA_MODIFIER_FINAL            0x00000010

class JavaFinalClass {
    char const * _canonicalName;
    jclass _jclass;

    void ensureJClassLoaded(JNIEnv * env) {
        if (!_jclass) {
            std::cout << "Initializing class " << _canonicalName << std::endl;
            std::cout.flush();
            jclass jclassLocalRef = env->FindClass(_canonicalName);
            FATALIF1(!jclassLocalRef, "Final class '%s' wasn't found", _canonicalName);
            _jclass = static_cast<jclass> (env->NewGlobalRef(jclassLocalRef));
            FATALIF1(!_jclass, "Error creating global reference for class '%s'", _canonicalName);
#ifdef USE_MY_ASSERTS
            // Ensure the passed class is final
            jclass jclassClass = env->GetObjectClass(_jclass);
            jmethodID getModifiersMethodId = env->GetMethodID(jclassClass, "getModifiers", "()I");
            jint modifiers = env->CallNonvirtualIntMethod(_jclass, jclassClass, getModifiersMethodId);
            MY_ASSERT(modifiers & JAVA_MODIFIER_FINAL)
#endif
        }
    }

public:
    JavaFinalClass(char const * canonicalName) :
        _canonicalName(canonicalName) {
    }

    jobject newInstance(JNIEnv * env);

    jclass getJClass(JNIEnv * env) {
        ensureJClassLoaded(env);
        return _jclass;
    }
    char const * getCanonicalName() {
        return _canonicalName;
    }
};

class JavaField {
    char const * _name;
    char const * _signature;
    JavaFinalClass & _javaFinalClass;
    jfieldID _jfieldID;

    void ensureJFieldIDLoaded(JNIEnv * env) {
        if (!_jfieldID) {
            std::cout << "Initializing field " << _name << std::endl;
            std::cout.flush();

            _jfieldID = env->GetFieldID(_javaFinalClass.getJClass(env), _name, _signature);
            FATALIF3(!_jfieldID, "Field '%s' with a signature '%s' of class %s was not found", _name, _signature, _javaFinalClass.getCanonicalName());
        }
    }
public:
    JavaField(JavaFinalClass & javaFinalClass, char const * name, char const * signature) :
        _javaFinalClass(javaFinalClass), _name(name), _signature(signature) {
    }

    jfieldID getJFieldID(JNIEnv * env) {
        ensureJFieldIDLoaded(env);
        return _jfieldID;
    }
};

class JavaLongField : public JavaField {
public:
    JavaLongField(JavaFinalClass & javaFinalClass, char const * name, char const * signature) :
        JavaField(javaFinalClass, name, signature) {
    }

    jlong getLong(JNIEnv * env, jobject object) {
        return env->GetLongField(object, getJFieldID(env));
    }
    void setLong(JNIEnv * env, jobject object, jlong value) {
        env->SetLongField(object, getJFieldID(env), value);
    }
};

class JavaFinalClassMethod {
    char const * _name;
    char const * _signature;
    jmethodID _jmethodID;
    JavaFinalClass & _javaFinalClass;

    void ensureJMethodIDLoaded(JNIEnv * env) {
        if (!_jmethodID) {
            std::cout << "Initializing method " << _name << std::endl;
            std::cout.flush();
            _jmethodID = env->GetMethodID(_javaFinalClass.getJClass(env), _name, _signature);
            FATALIF3(!_jmethodID, "Method '%s' with a signature '%s' of class %s was not found", _name, _signature, _javaFinalClass.getCanonicalName());
        }
    }
public:
    JavaFinalClassMethod(JavaFinalClass & javaFinalClass, char const * name, char const * signature) :
        _javaFinalClass(javaFinalClass), _name(name), _signature(signature) {
    }

    jmethodID getJMethodID(JNIEnv * env) {
        ensureJMethodIDLoaded(env);
        return _jmethodID;
    }

    JavaFinalClass & getJavaFinalClass() {
        return _javaFinalClass;
    }
};

class JavaFinalClassVoidMethod : public JavaFinalClassMethod {
public:
    JavaFinalClassVoidMethod(JavaFinalClass & javaFinalClass, char const * name,
                             char const * signature) :
        JavaFinalClassMethod(javaFinalClass, name, signature) {
    }

    void operator()(JNIEnv * env, jobject object, ...) {
        va_list args;
        va_start(args, object);
        env->CallNonvirtualVoidMethodV(object, getJavaFinalClass().getJClass(env),
                getJMethodID(env), args);
        va_end(args);
    }
};

JAVA_FINAL_CLASS("net/sf", interface2)
{
/*    */JAVA_FIELD(Long, field1, "I")
/*    */JAVA_FINAL_CLASS_METHOD(Void, method4, "(t4)")
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
            jclass clazz = env->FindClass(_fullname);
            FATALIF1(!clazz, "Error finding class '%s'", _fullname)
            _jclass = static_cast<jclass>(env->NewGlobalRef(clazz));
            MY_ASSERT(_jclass)
        }
        std::cout << "getClass() for " << _fullname << std::endl; // TODO
        return _jclass;
    }

    static jobject newInstance(JNIEnv * env) {
        jclass clazz = _instance.getJClass(env);
        jmethodID defaultConstructor = _instance._defaultConstructor.getMethodID(env, clazz);
        FATALIF1(defaultConstructor == NULL, "Class '%s' has no default constructor", _instance._fullname);
        return env->NewObject(clazz, defaultConstructor);
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
#ifdef TRACE_ON
            std::cout << "Getting method id for " << *this << std::endl;
#endif
            if (_isStatic) {
                _jmethodID = env->GetStaticMethodID(jclazz, _name, _signature);
            } else {
                _jmethodID = env->GetMethodID(jclazz, _name, _signature);
            }
            FATALIF3(!_jmethodID, "Method not found: %s() signature %s%s", _name, _signature, _isStatic ? " (static)" : "");
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
#ifdef TRACE_ON
            std::cout << "Getting field id for " << *this << std::endl;
#endif
            if (_isStatic) {
                _jfieldID = env->GetStaticFieldID(jclazz, _name, _signature);
            } else {
                _jfieldID = env->GetFieldID(jclazz, _name, _signature);
            }
            FATALIF3(!_jfieldID, "Field not found: %s signature %s%s", _name, _signature, _isStatic ? " (static)" : "");
        }
        return _jfieldID;
    }
};

#ifdef TRACE_ON
inline std::ostream & operator<<(std::ostream & stream, JField & field) {
    stream << field._name << " (" << field._signature << ")";
}
#endif

#define JAVA_TYPE_String                    jstring
#define JAVA_TYPE_Int                       jint
#define JAVA_TYPE_Long                      jlong
#define JAVA_TYPE_Class                     jclass
#define JAVA_TYPE_Object                    jobject
#define JAVA_TYPE_Void                      void

#define FIELD_SIGNATURE_Long                "J"
#define FIELD_SIGNATURE_String              "Ljava/lang/String;"
#define FIELD_SIGNATURE_Class               "Ljava/lang/Class;"

//#define CALL_AND_ASSIGN_TO_RESULT(type, e)  JAVA_TYPE_##type result = 0;
#define CALL_AND_ASSIGN_TO_RESULT(type, e)  CALL_AND_ASSIGN_TO_RESULT_I(CALL_AND_ASSIGN_TO_RESULT_##type,e)
#define CALL_AND_ASSIGN_TO_RESULT_I(m, e)   m(e)

#define CALL_AND_ASSIGN_TO_RESULT_String(e) jstring result = static_cast<jstring>(e);
#define CALL_AND_ASSIGN_TO_RESULT_Long(e)   jlong result = static_cast<jlong>(e);
#define CALL_AND_ASSIGN_TO_RESULT_Int(e)    jint result = static_cast<jint>(e);
#define CALL_AND_ASSIGN_TO_RESULT_Class(e)  jclass result = static_cast<jclass>(e);
#define CALL_AND_ASSIGN_TO_RESULT_Void(e)   char const * result = "<void>"; e;

#define RETURN_RESULT_String                return result;
#define RETURN_RESULT_Long                  return result;
#define RETURN_RESULT_Int                   return result;
#define RETURN_RESULT_Class                 return result;
#define RETURN_RESULT_Object                return result;
#define RETURN_RESULT_Void

#define JNI_ENV_VIRTUAL_CALL_Int            CallIntMethodV
#define JNI_ENV_VIRTUAL_CALL_Long           CallLongMethodV
#define JNI_ENV_VIRTUAL_CALL_String         CallObjectMethodV
#define JNI_ENV_VIRTUAL_CALL_Void           CallVoidMethodV

#define JNI_ENV_NON_VIRTUAL_CALL_Int        CallNonvirtualIntMethodV
#define JNI_ENV_NON_VIRTUAL_CALL_Long       CallNonvirtualLongMethodV
#define JNI_ENV_NON_VIRTUAL_CALL_String     CallNonvirtualObjectMethodV
#define JNI_ENV_NON_VIRTUAL_CALL_Void       CallNonvirtualVoidMethodV

#define JNI_ENV_STATIC_CALL_Int             CallStaticIntMethodV
#define JNI_ENV_STATIC_CALL_Long            CallStaticLongMethodV
#define JNI_ENV_STATIC_CALL_String          CallStaticObjectMethodV
#define JNI_ENV_STATIC_CALL_Void            CallStaticVoidMethodV

#define JNI_ENV_GET_Long                    GetLongField
#define JNI_ENV_GET_String                  GetObjectField
#define JNI_ENV_GET_Class                   GetObjectField
#define JNI_ENV_GET_Object                  GetObjectField

#define JNI_ENV_STATIC_GET_Long             GetStaticLongField
#define JNI_ENV_STATIC_GET_String           GetStaticObjectField
#define JNI_ENV_STATIC_GET_Object           GetStaticObjectField

#define JNI_ENV_SET_Long                    SetLongField
#define JNI_ENV_SET_String                  SetObjectField
#define JNI_ENV_SET_Object                   SetObjectField
#define JNI_ENV_SET_Class                   SetObjectField

#define JNI_ENV_STATIC_SET_Long             SetStaticLongField
#define JNI_ENV_STATIC_SET_Object           SetStaticObjectField
#define JNI_ENV_STATIC_SET_String           SetStaticObjectField

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

#define END_JCLASS                      };

#define BEGIN_JINTERFACE(name)                                                  \
    class name : public JInterface<name> {                                      \
        friend class JObjectMap<jclass, name>;                                  \
        name() : JInterface<name>(#name) {}                                     \
    public:

#define END_JINTERFACE                      };

#define JCLASS_FINAL_METHOD(ret_type, name, signature)                          \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JMethod {                                           \
    public:                                                                     \
        C_##name() : JMethod(#name, signature) {                                \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        static JAVA_TYPE_##ret_type name(JNIEnv * env, jobject object, ...) {   \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_CALLING(&_instance, name, signature)                      \
            va_list args;                                                       \
            va_start(args, object);                                             \
            jclass clazz = _instance.getJClass(env);                            \
            CALL_AND_ASSIGN_TO_RESULT(ret_type,                                 \
                env->JNI_ENV_NON_VIRTUAL_CALL_##ret_type(object, clazz,         \
                    _instance._##name.getMethodID(env, clazz), args))           \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(&_instance, name, signature)                       \
            RETURN_RESULT_##ret_type                                            \
        }

#define JCLASS_VIRTUAL_METHOD(ret_type, name, signature)                        \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JMethod {                                           \
    public:                                                                     \
        C_##name() : JMethod(#name, signature) {                                \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        static JAVA_TYPE_##ret_type name(JNIEnv * env, jobject object, ...) {   \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_CALLING(&_instance, name, signature)                      \
            va_list args;                                                       \
            va_start(args, object);                                             \
            jclass clazz = _instance.getJClass(env);                            \
            CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
                env->JNI_ENV_VIRTUAL_CALL_##ret_type(object,                    \
                    _instance._##name.getMethodID(env, clazz), args))           \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(&_instance, name, signature)                       \
            RETURN_RESULT_##ret_type                                            \
        }

#define JCLASS_STATIC_METHOD(ret_type, name, signature)                         \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JMethod {                                           \
    public:                                                                     \
        C_##name() : JMethod(#name, signature, true) {                          \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        static JAVA_TYPE_##ret_type name(JNIEnv * env, ...) {                   \
            TRACE_JNI_CALLING(&_instance, name, signature)                      \
            va_list args;                                                       \
            va_start(args, env);                                                \
            jclass clazz = _instance.getJClass(env);                            \
            CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
                env->JNI_ENV_STATIC_CALL_##ret_type(clazz,                      \
                    _instance._##name.getMethodID(env, clazz), args))           \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(&_instance, name, signature)                       \
            RETURN_RESULT_##ret_type                                            \
        }

#define JCLASS_FIELD(ret_type, name)                                            \
    _JCLASS_FIELD(ret_type, name, FIELD_SIGNATURE_##ret_type)

#define JCLASS_FIELD_OBJECT(name, signature)                                    \
    _JCLASS_FIELD(Object, name, signature)

#define _JCLASS_FIELD(ret_type, name, signature)                                \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JField {                                            \
    public:                                                                     \
        C_##name() : JField(#name, signature) {                                 \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        static JAVA_TYPE_##ret_type name##_Get(JNIEnv * env, jobject object) {  \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_GETTING(&_instance, name, signature)                      \
            JAVA_TYPE_##ret_type result = static_cast<JAVA_TYPE_##ret_type>(    \
                env->JNI_ENV_GET_##ret_type(object,                             \
                        _instance._##name.getFieldID(env,                       \
                                    _instance.getJClass(env))));                \
            TRACE_JNI_GOT(&_instance, name, signature)                          \
            RETURN_RESULT_##ret_type                                            \
        }                                                                       \
        static void name##_Set(JNIEnv * env, jobject object,                    \
                         JAVA_TYPE_##ret_type value) {                          \
            CHECK_OBJECT_CLASS(env, &_instance, object)                         \
            TRACE_JNI_SETTING(&_instance, name, signature)                      \
            env->JNI_ENV_SET_##ret_type(object,                                 \
                _instance._##name.getFieldID(env, _instance.getJClass(env)),    \
                value);                                                         \
            TRACE_JNI_SET(&_instance, name, signature)                          \
        }

#define JCLASS_STATIC_FIELD(ret_type, name)                                     \
    _JCLASS_STATIC_FIELD(ret_type, name, FIELD_SIGNATURE_##ret_type)

#define JCLASS_STATIC_FIELD_OBJECT(name, signature)                             \
    _JCLASS_STATIC_FIELD(Object, name, signature)

#define _JCLASS_STATIC_FIELD(ret_type, name, signature)                         \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JField {                                            \
    public:                                                                     \
        C_##name() : JField(#name, signature, true) {                           \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        static JAVA_TYPE_##ret_type name##_Get(JNIEnv * env) {                  \
            TRACE_JNI_GETTING(&_instance, name, signature)                      \
            jclass clazz = _instance.getJClass(env);                            \
            JAVA_TYPE_##ret_type result = static_cast<JAVA_TYPE_##ret_type>(    \
                env->JNI_ENV_STATIC_GET_##ret_type(clazz,                       \
                        _instance._##name.getFieldID(env, clazz)));             \
            TRACE_JNI_GOT(&_instance, name, signature)                          \
            RETURN_RESULT_##ret_type                                            \
        }                                                                       \
        static void name##_Set(JNIEnv * env, JAVA_TYPE_##ret_type value) {      \
            TRACE_JNI_SETTING(&_instance, name, signature)                      \
            jclass clazz = _instance.getJClass(env);                            \
            env->JNI_ENV_STATIC_SET_##ret_type(clazz,                           \
                _instance._##name.getFieldID(env, clazz), value);               \
            TRACE_JNI_SET(&_instance, name, signature)                          \
        }

#define JINTERFACE_METHOD(ret_type, name, signature)                            \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JMethod {                                           \
    public:                                                                     \
        C_##name() : JMethod(#name, signature) {                                \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        JAVA_TYPE_##ret_type name(JNIEnv * env, jobject object, ...) {          \
            CHECK_OBJECT_CLASS(env, this, object)                               \
            TRACE_JNI_CALLING(this, name, signature)                            \
            va_list args;                                                       \
            va_start(args, object);                                             \
            CALL_AND_ASSIGN_TO_RESULT_##ret_type(                               \
                        env->JNI_ENV_VIRTUAL_CALL_##ret_type(object,            \
                            _##name.getMethodID(env, getJClass()), args));      \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(this, name, signature)                             \
            RETURN_RESULT_##ret_type                                            \
        }

BEGIN_JCLASS("net/sf/sevenzipjbinding/junit/jnitools", JTestAbstractClass)
/*    */JCLASS_FINAL_METHOD(Long, privateLongMethod, "(I)J")
/*    */JCLASS_FINAL_METHOD(String, privateStringMethod, "(I)Ljava/lang/String;")
/*    */JCLASS_FINAL_METHOD(Void, privateVoidMethod, "(I)V")

/*    */JCLASS_FINAL_METHOD(Long, privateFinalLongMethod, "(I)J")
/*    */JCLASS_FINAL_METHOD(String, privateFinalStringMethod, "(I)Ljava/lang/String;")
/*    */JCLASS_FINAL_METHOD(Void, privateFinalVoidMethod, "(I)V")

/*    */JCLASS_STATIC_METHOD(Long, privateStaticLongMethod, "(I)J")
/*    */JCLASS_STATIC_METHOD(String, privateStaticStringMethod, "(I)Ljava/lang/String;")
/*    */JCLASS_STATIC_METHOD(Void, privateStaticVoidMethod, "(I)V")

/*    */JCLASS_VIRTUAL_METHOD(Long, protectedVirtualLongMethod, "(I)J")
/*    */JCLASS_VIRTUAL_METHOD(String, protectedVirtualStringMethod, "(I)Ljava/lang/String;")
/*    */JCLASS_VIRTUAL_METHOD(Void, protectedVirtualVoidMethod, "(I)V")

/*    */JCLASS_FIELD(Long, privateLongField)
/*    */JCLASS_FIELD(String, privateStringField)

/*    */JCLASS_STATIC_FIELD(Long, privateStaticLongField)
/*    */JCLASS_STATIC_FIELD(String, privateStaticStringField)
END_JCLASS
BEGIN_JCLASS("net/sf/sevenzipjbinding/junit/jnitools", JTestFinalClass)
/*    */JCLASS_VIRTUAL_METHOD(Long, protectedVirtualLongMethod, "(I)J")
/*    */JCLASS_VIRTUAL_METHOD(String, protectedVirtualStringMethod, "(I)Ljava/lang/String;")
/*    */JCLASS_VIRTUAL_METHOD(Void, protectedVirtualVoidMethod, "(I)V")

/*    */JCLASS_FIELD(Class, privateClassField)
/*    */JCLASS_FIELD(Long, id)

/*    */JCLASS_FIELD_OBJECT(privateJTestFinalClassField, "Lnet/sf/sevenzipjbinding/junit/jnitools/JTestFinalClass;")
/*    */JCLASS_FIELD_OBJECT(privateJTestAbstractClassField, "Lnet/sf/sevenzipjbinding/junit/jnitools/JTestAbstractClass;")
END_JCLASS

BEGIN_JINTERFACE(Interface1)
/*    */JINTERFACE_METHOD(Long, longMethod, "(I)J")
/*    */JINTERFACE_METHOD(String, stringMethod, "(I)Ljava/lang/String;")
/*    */JINTERFACE_METHOD(Void, voidMethod, "(I)V")
END_JINTERFACE

BEGIN_JINTERFACE(JObject)
/*    */JINTERFACE_METHOD(String, toString, "()Ljava/lang/String;")
/*    */JINTERFACE_METHOD(Int, hashCode, "()I")
END_JINTERFACE

#endif /* JAVASTATICINFO_H_ */
