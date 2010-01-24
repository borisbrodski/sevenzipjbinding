#ifndef UNICODEHELPER_
#define UNICODEHELPER_

#include "SevenZipJBinding.h"

/**
 * Helper class to convert between wchar_t and jchar.
 * On some machines sizeof(wchar_t) != sizeof(jchar)
 *
 * TODO Write a C++ test for this class
 */
class UnicodeHelper {
private:
	const wchar_t * _unicodeString;
	wchar_t * _unicodeBuffer;

	const jchar * _jcharString;
	jchar * _jcharBuffer;

public:
	UnicodeHelper(const wchar_t * unicodeString) {
		_unicodeString = unicodeString;
		_jcharString = NULL;
		_jcharBuffer = NULL;
		_unicodeBuffer = NULL;
	}

	UnicodeHelper(const jchar * jcharString) {
		_jcharString = jcharString;
		_unicodeString = NULL;
		_jcharBuffer = NULL;
		_unicodeBuffer = NULL;
	}

	~UnicodeHelper() {
//		TRACE("~UnicodeHelper()");
		if (_jcharBuffer) {
//			TRACE("Freeing jchar string");
#ifndef _DEBUG
			size_t len = wcslen(_unicodeString);
			memset(_jcharBuffer, 0, sizeof(jchar) * (len + 1));
#endif // _DEBUG
			delete[] _jcharBuffer;
		}

		if (_unicodeBuffer) {
//			TRACE("Freeing unicode string");
#ifndef _DEBUG
			size_t len = jcharlen(_jcharString);
			memset(_unicodeBuffer, 0, sizeof(wchar_t) * (len + 1));
#endif // _DEBUG
			delete[] _unicodeBuffer;
		}

	}

	operator const jchar *() {
		if (_jcharString) {
			return _jcharString;
		}

		TRACE("Converting wchar_t=>jchar: \"" << _unicodeString <<"\"")
		if (sizeof(wchar_t) == sizeof(jchar)) {
			_jcharString = (const jchar *)( _unicodeString);
			return _jcharString;
		}
		size_t len = wcslen(_unicodeString);
		_jcharBuffer = new jchar[len + 1];
		for (size_t i = 0; i < len; i++) {
			_jcharBuffer[i] = (jchar) _unicodeString[i];
		}
		_jcharBuffer[len] = 0;

		return _jcharString = _jcharBuffer;
	}

	operator const wchar_t *() {
		if (_unicodeString) {
			return _unicodeString;
		}
//		TRACE("Converting jchar=>wchar_t ...")
		if (sizeof(wchar_t) == sizeof(jchar)) {
			_unicodeString = (wchar_t*) (_jcharString);
			return _unicodeString;
		}
		size_t len = jcharlen(_jcharString);
//		TRACE1("len: %i" , len)
		_unicodeBuffer = new wchar_t[len + 1];
		for (size_t i = 0; i < len; i++) {
			_unicodeBuffer[i] = (wchar_t) _jcharString[i];
		}
		_unicodeBuffer[len] = 0;

		TRACE("Converting jchar=>wchar_t done: \"" << _unicodeBuffer << "\"");
		return _unicodeString = _unicodeBuffer;
	}

private:
	static size_t jcharlen(const jchar * jcharString) {
		size_t len = 0;
		const jchar * ptr = jcharString;
		while (*ptr++) {
			len++;
		}
		return len;
	}

};

#endif // UNICODEHELPER_
