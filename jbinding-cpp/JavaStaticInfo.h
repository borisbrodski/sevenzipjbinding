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

#ifdef TRACE_ON
#include <ostream>
struct JOut {
    JNIEnv * _env;
    std::ostream & _stream;
    JOut(JNIEnv * env, std::ostream & stream) : _env(env), _stream(stream) {}
};

inline JOut operator<< (std::ostream & stream, JNIEnv * env) {
    return JOut(env, stream);
}
inline std::ostream & operator<< (JOut jout, jstring str) {
    char const * s = jout._env->GetStringUTFChars(str, NULL);
    jout._stream << s;
    jout._env->ReleaseStringUTFChars(str, s);
    return jout._stream;
}
inline std::ostream & operator<< (JOut jout, jint i) {
    jout._stream << i;
    return jout._stream;
}
#endif


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

class JavaInterface {
public:
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

class JavaInterfaceMethod {
    char const * _name;
    char const * _signature;
    std::map<jclass, jmethodID> _jmethodIDMap;
    JavaInterface & _javaInterface;

public:
    JavaInterfaceMethod(JavaInterface & javaInterface, char const * name, char const * signature) :
        _javaInterface(javaInterface), _name(name), _signature(signature) {
    }
    jmethodID getJMethodID(JNIEnv * env, jclass implementingClass) {
        std::map<jclass, jmethodID>::iterator iterator = _jmethodIDMap.find(implementingClass);
        if (iterator != _jmethodIDMap.end()) {
            return iterator->second;
        }

        jmethodID jmethodID = env->GetMethodID(implementingClass, _name, _signature);
        FATALIF2(!jmethodID, "Can't find method '%s' signature '%s'", _name, _signature);
        _jmethodIDMap[implementingClass] = jmethodID;
        return jmethodID;
    }
};

class JavaEnum {
    char const * _name;
public:
    JavaEnum(char const * name) :
        _name(name) {

    }
};

JAVA_INTERFACE(interface1)
{
/*    */JAVA_INTERFACE_METHOD(method1, "(t1)")
/*    */JAVA_INTERFACE_METHOD(method2, "(t2)")
}

JAVA_FINAL_CLASS("net/sf", interface2)
{
/*    */JAVA_FIELD(Long, field1, "I")
/*    */JAVA_FINAL_CLASS_METHOD(Void, method4, "(t4)")
}

template<typename T>
class JInterface {
    static JObjectMap<jclass, T> jinterfaceMap;
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
        T * instance = jinterfaceMap.get(env, objectClass);
        if (instance) {
            return *instance;
        }
        objectClass = (jclass) env->NewGlobalRef(objectClass);
        T & newInstance = jinterfaceMap.add(objectClass);
        newInstance._jclass = objectClass;
        return newInstance;
    }
};

template<typename T>
JObjectMap<jclass, T> JInterface<T>::jinterfaceMap;

#ifdef TRACE_ON
template<typename T>
inline std::ostream & operator<<(std::ostream & stream, JInterface<T> & interface) {
    stream << interface.getName();
}
#endif // TRACE_ON
class JInterfaceMethod {
#ifdef TRACE_ON
    friend std::ostream & operator<<(std::ostream &, JInterfaceMethod &);
#endif
    char const * _name;
    char const * _signature;
    jmethodID _jmethodID;
protected:
    JInterfaceMethod(char const * name, char const * signature) :
        _name(name), _signature(signature), _jmethodID(NULL) {
    }
public:
    jmethodID getMethodID(JNIEnv * env, jclass jclazz) {
        if (_jmethodID) {
            return _jmethodID;
        }
#ifdef TRACE_ON
        std::cout << "Getting method id for " << *this << std::endl;
#endif
        _jmethodID = env->GetMethodID(jclazz, _name, _signature);
        FATALIF2(!_jmethodID, "Method not found: .%s() signature %s", _name, _signature);
    }
};

#ifdef TRACE_ON
inline std::ostream & operator<<(std::ostream & stream, JInterfaceMethod & method) {
    stream << method._name << method._signature;
}
#endif

#ifdef JSF_DEFINE_VARIABLES
#   define DEFINE_JINTERFACEMAP(name) JObjectMap<jclass, name> JInterface<name>::jinterfaceMap;
#else
#   define DEFINE_JINTERFACEMAP(name)
#endif

#define BEGIN_JINTERFACE(name)                                                          \
    class name : public JInterface<name> {                                              \
        friend class JObjectMap<jclass, name>;                                          \
        name() : JInterface<name>(#name) {}                                             \
    public:

#define END_JINTERFACE                      };

#define JAVA_TYPE_String jstring
#define JAVA_TYPE_Int jint
#define JAVA_TYPE_Long jlong

#define JNI_ENV_CALL_String CallObjectMethodV
#define JNI_ENV_CALL_Int CallIntMethodV
#define JNI_ENV_CALL_Long CallLongMethodV

#ifdef USE_MY_ASSERTS
#   define CHECK_OBJECT_CLASS(env, object) {checkObjectClass(env, object);}
#else
#   define CHECK_OBJECT_CLASS(env, object) {}
#endif

#ifdef TRACE_ON
#   define TRACE_JNI_CALLING(name, signature) {std::cout << "Calling " << *this << '.' << #name << signature << std::endl;}
#   define TRACE_JNI_CALLED(name, signature) {std::cout << "Called " << *this << '.' << #name << " returned '" << env << result << "'" << std::endl;}
#else
#   define TRACE_JNI_CALLING(name, signature) {}
#   define TRACE_JNI_CALLED(name, signature) {}
#endif

#define JINTERFACE_METHOD(ret_type, name, signature)                            \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JInterfaceMethod {                                  \
    public:                                                                     \
        C_##name() : JInterfaceMethod(#name, signature) {                       \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        JAVA_TYPE_##ret_type name(JNIEnv * env, jobject object, ...) {          \
            CHECK_OBJECT_CLASS(env, object)                                     \
            TRACE_JNI_CALLING(name, signature)                                  \
            va_list args;                                                       \
            va_start(args, object);                                             \
            JAVA_TYPE_##ret_type result = static_cast<JAVA_TYPE_##ret_type>(    \
                        env->JNI_ENV_CALL_##ret_type(object,                    \
                            _##name.getMethodID(env, getJClass()), args));      \
            va_end(args);                                                       \
            TRACE_JNI_CALLED(name, signature)                                   \
            return result;                                                      \
        }

BEGIN_JINTERFACE(JObject)
/*    */JINTERFACE_METHOD(String, toString, "()Ljava/lang/String;")
/*    */JINTERFACE_METHOD(Int, hashCode, "()I")
END_JINTERFACE

#ifdef JSF_DEFINE_VARIABLES
#endif

#endif /* JAVASTATICINFO_H_ */
