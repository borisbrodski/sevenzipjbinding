#ifndef UNICODEHELPER_
#define UNICODEHELPER_

#include "SevenZipJBinding.h"

#define BUFFER_ON_STACK_SIZE 1024

template <typename T>
class WithStackBuffer {
private:
    unsigned char _bufferOnStack[BUFFER_ON_STACK_SIZE];
    T* ptr = NULL;

protected:
    ~WithStackBuffer() {
        if (ptr && (void *)ptr != (void *)&_bufferOnStack) {
            free(ptr);
#ifdef _DEBUG
            ptr = NULL;
#endif
        }
    }
    T* buffer_alloc(size_t bytes) {
        if (bytes <= BUFFER_ON_STACK_SIZE) {
            ptr = (T*)(void*)&_bufferOnStack;
        } else {
            ptr = (T*)malloc(bytes);

        }
        return ptr;
    }

    T* buffer() {
        return ptr;
    }
};

class ToJChar : WithStackBuffer<jchar> {
private:

    const wchar_t * _unicodeString;
    size_t _unicodeLength;

public:
    ToJChar(UString & ustring) :
        _unicodeString(ustring),
        _unicodeLength(ustring.Len())
    {
        TRACE("Converting UString=>jchar: \"" << ustring << "\"")
    }

    ToJChar(BSTR bstr) :
        _unicodeString(bstr),
        _unicodeLength(SysStringLen(bstr))
    {
        TRACE("Converting BSTR=>jchar: \"" << bstr <<"\"")
    }

    ToJChar(const wchar_t * str) :
        _unicodeString(str),
        _unicodeLength(wcslen(str))
    {
        TRACE("Converting BSTR=>jchar: \"" << str <<"\"")
    }

    ToJChar(CMyComBSTR & myComBSTR) :
        _unicodeString(myComBSTR),
        _unicodeLength(SysStringLen(*&myComBSTR))
    {
        TRACE("Converting MyComBSTR=>jchar: \"" << myComBSTR <<"\"")
    }

    jstring toNewString(JNIEnv * env) {
        return env->NewString((const jchar *)*this, _unicodeLength);
    }

    ~ToJChar() {
#ifdef _DEBUG
        _unicodeString = NULL;
        _unicodeLength = -1;
#endif
    }
    operator const jchar *() {
        if (buffer()) {
            return buffer();
        }

        buffer_alloc((_unicodeLength + 1) * sizeof(jchar));
        for (size_t i = 0; i < _unicodeLength; i++) {
            buffer()[i] = (jchar) _unicodeString[i];
        }
        buffer()[_unicodeLength] = 0;

        return buffer();
    }
};

class FromJChar : WithStackBuffer<wchar_t> {
private:
    size_t _jcharLength;
    const jchar * _jchars;
    JNIEnv * _env;
    jstring _string;
public:
    FromJChar(JNIEnv * env, jstring string) : _env(env), _string(string) {
        _jcharLength = env->GetStringLength(string);
        _jchars = env->GetStringChars(string, NULL);
        // TODO Check for NULL (OutOfMemory?)
    }
    ~FromJChar() {
        _env->ReleaseStringChars(_string, _jchars);
    }
    operator wchar_t *() {
        if (buffer()) {
            return buffer();
        }

        buffer_alloc((_jcharLength + 1) * sizeof(wchar_t));
        for (size_t i = 0; i < _jcharLength; i++) {
            buffer()[i] = (wchar_t)_jchars[i];
        }
        buffer()[_jcharLength] = 0;

        TRACE("Converting jchar=>wchar_t done: \"" << buffer() << "\"");
        return buffer();
    }

};


#endif // UNICODEHELPER_
