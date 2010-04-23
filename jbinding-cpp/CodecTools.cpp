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

/**
 * Return index of the archive type. Save to UString converted archive type name into 'formatNameString'.
 * @param env instance of JNIEnv
 * @param formatName archive type format name
 * @param formatNameString instance of UString to put archive type name to
 * @return index of the archive type or -1 if archive type not found.
 */
int CodecTools::getIndexByName(JNIEnv * env, jstring formatName, UString & formatNameString) {
	const jchar * formatNameJChars = env->GetStringChars(formatName, NULL);
	formatNameString = UnicodeHelper(formatNameJChars);
	env->ReleaseStringChars(formatName, formatNameJChars);

	TRACE("Format: " << formatNameString)
	return CodecTools::codecs.FindFormatForArchiveType(formatNameString);
}
