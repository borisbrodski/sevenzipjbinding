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
#include <iostream> // TODO Remove me
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
            ASSERT(modifiers & JAVA_MODIFIER_FINAL)
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











class JClassObjectInfo {
    friend std::ostream & operator<<(std::ostream &, JClassObjectInfo &);
public:
    virtual jclass getJClass() =0;
    virtual char const * getName() =0;
    virtual char const * getPackage() =0;
};

inline std::ostream & operator<<(std::ostream & stream, JClassObjectInfo & info) {
    stream << info.getPackage() << '/' << info.getName();
}


template<typename T>
class JInterface : public JClassObjectInfo {
    static JObjectMap<jclass, T> jinterfaceMap;
    char const * _package;
    char const * _name;
    jclass _jclass;
protected:
    JInterface(char const * package, char const * name) :
            _package(package), _name(name) {
    }
public:
    jclass getJClass() {
        return _jclass;
    }
    char const * getName() {
        return _name;
    }
    char const * getPackage() {
        return _package;
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
        objectClass = (jclass)env->NewGlobalRef(objectClass);
        T & newInstance = jinterfaceMap.add(objectClass);
        newInstance._jclass = objectClass;
        return newInstance;
    }
};

template<typename T>
JObjectMap<jclass, T> JInterface<T>::jinterfaceMap;

class JInterfaceMethod {
    friend std::ostream & operator<<(std::ostream &, JInterfaceMethod &);
    char const * _name;
    char const * _signature;
    JClassObjectInfo * _jclassObjectInfo;
    jmethodID _jmethodID;
protected:
    JInterfaceMethod(char const * name, char const * signature) :
        _name(name), _signature(signature), _jmethodID(NULL), _jclassObjectInfo(NULL) {
    }
    jmethodID getMethodID(JNIEnv * env) {
        if (_jmethodID) {
            return _jmethodID;
        }
        _jmethodID = env->GetMethodID(_jclassObjectInfo->getJClass(), _name, _signature);
        FATALIF4(!_jmethodID, "Method not found: %s/%s.%s() signature %s", _jclassObjectInfo->getPackage(), _jclassObjectInfo->getName(), _name, _signature);
    }
public:
    void setJClassObjectInfo(JClassObjectInfo * jclassObjectInfo) {
        _jclassObjectInfo = jclassObjectInfo;
    }
    JClassObjectInfo * getJClassObjectInfo() {
        return _jclassObjectInfo;
    }
    char const * getName() {
        return _name; // TODO Remove this
    }
};

inline std::ostream & operator<<(std::ostream & stream, JInterfaceMethod & method) {
    stream << *method.getJClassObjectInfo() << '.' << method._name << method._signature;
}

class JInterfaceLongMethod : public JInterfaceMethod {
protected:
    JInterfaceLongMethod(char const * name, char const * signature) :
        JInterfaceMethod(name, signature) {
    }

public:
    jlong operator()(JNIEnv * env, ...) {
        std::cout << *this << std::endl;
        return 0;
    }
};

class JInterfaceStringMethod : public JInterfaceMethod {
protected:
    JInterfaceStringMethod(char const * name, char const * signature) :
        JInterfaceMethod(name, signature) {
    }

public:
    jstring operator()(JNIEnv * env, jobject object, ...) {
        va_list args;
        va_start(args, object);
        jstring result = static_cast<jstring>(env->CallObjectMethodV(object, getMethodID(env), args));
        va_end(args);

        std::cout << *this << std::endl;

        return result;
    }
};

class JInterfaceIntMethod : public JInterfaceMethod {
protected:
    JInterfaceIntMethod(char const * name, char const * signature) :
        JInterfaceMethod(name, signature) {
    }

public:
    jint operator()(JNIEnv * env, jobject object, ...) {
        va_list args;
        va_start(args, object);
        jint result = env->CallIntMethodV(object, getMethodID(env), args);
        va_end(args);

        std::cout << *this << std::endl;

        return result;
    }
};



#ifdef JSF_DEFINE_VARIABLES
#   define DEFINE_JINTERFACEMAP(name) JObjectMap<jclass, name> JInterface<name>::jinterfaceMap;
#else
#   define DEFINE_JINTERFACEMAP(name)
#endif

#define BEGIN_JINTERFACE(package, name)                                                 \
    class name : public JInterface<name> {                                              \
        friend class JObjectMap<jclass, name>;                                          \
        name() : JInterface<name>(package, #name) {}                                    \
    public:


#define END_JINTERFACE                      };

#define JINTERFACE_METHOD(ret_type, name, signature)                            \
    private: class C_##name; friend class C_##name;                             \
    class C_##name : public JInterface##ret_type##Method {                      \
    public:                                                                     \
        C_##name() : JInterface##ret_type##Method(#name, signature) {           \
        }                                                                       \
    };                                                                          \
    C_##name _##name;                                                           \
    public:                                                                     \
        C_##name & name() {                                                     \
            _##name.setJClassObjectInfo(this);                                  \
            return _##name;                                                     \
        };

BEGIN_JINTERFACE("net/sf", JObject)
    JINTERFACE_METHOD(String, toString, "()Ljava/lang/String;")
    JINTERFACE_METHOD(Int, hashCode, "()I")
END_JINTERFACE

#ifdef JSF_DEFINE_VARIABLES
#endif

#endif /* JAVASTATICINFO_H_ */
