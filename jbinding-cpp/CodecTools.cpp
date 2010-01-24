/*
 * CodecTools.cpp
 *
 *  Created on: Dec 23, 2009
 *      Author: boris
 */

#include "SevenZipJBinding.h"

#include "CodecTools.h"
#include "SevenZipException.h"
#include "UnicodeHelper.h"


CCodecs CodecTools::codecs;

void CodecTools::init() {
	HRESULT result = codecs.Load();
	if (result != S_OK)
		fatal("codecs->Load() return error: 0x%08X", result);

}

int CodecTools::getIndexByName(JNIEnv * env, jstring formatName, UString & formatNameString) {
	int index;
	const jchar * formatNameJChars = env->GetStringChars(formatName, NULL);
	formatNameString = UnicodeHelper(formatNameJChars);
	env->ReleaseStringChars(formatName, formatNameJChars);

	TRACE("Format: " << formatNameString)
	index = CodecTools::codecs.FindFormatForArchiveType(formatNameString);
	if (index == -1) {
		throw SevenZipException("Not registered archive format: '%S'", (const wchar_t*)formatNameString);
	}

	return index;
}
