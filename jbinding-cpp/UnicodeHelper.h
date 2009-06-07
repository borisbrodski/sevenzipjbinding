#ifndef UNICODEHELPER_
#define UNICODEHELPER_

#include "SevenZipJBinding.h"

class UnicodeHelper {
private:
	const wchar_t * _unicodeString;
	jchar * _jcharBuffer;
public:
	UnicodeHelper(const wchar_t * unicodeString) {
		_unicodeString = unicodeString;
		_jcharBuffer = NULL;
	}

	~UnicodeHelper() {
		TRACE("~UnicodeHelper()");
		if (_jcharBuffer) {
			TRACE("Freeing buffer");
#ifdef _DEBUG
			size_t len = wcslen(_unicodeString);
			memset(_jcharBuffer, 0, sizeof(jchar) * (len + 1));
#endif // _DEBUG
			delete[] _jcharBuffer;
		}
	}

	operator jchar *() {
		TRACE("Converting...")
		if (sizeof(wchar_t) == sizeof(jchar)) {
			return (jchar *) _unicodeString;
		}
		size_t len = wcslen(_unicodeString);
		_jcharBuffer = new jchar[len + 1];
		for (size_t i = 0; i < len; i++) {
			_jcharBuffer[i] = (jchar) _unicodeString[i];
		}

		return _jcharBuffer;

	}
};

#endif // UNICODEHELPER_
