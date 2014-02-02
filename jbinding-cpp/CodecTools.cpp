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

#include "JavaStatInfos/JavaPackageSevenZip.h"


CodecTools codecTools;

void CodecTools::init() {
	HRESULT result = codecs.Load();
	if (result != S_OK)
		fatal("codecs->Load() return error: 0x%08X", result);

#ifdef TRACE_ON
	for (int i = 0; i < CodecTools::codecs.Formats.Size(); i++) {
        const wchar_t * name = (const wchar_t*)CodecTools::codecs.Formats[i].Name;
		TRACE("Available codec: '" << name << "'")
	}
#endif // TRACE_ON

	for (int i = 0; i < CodecTools::codecs.Formats.Size(); i++) {
        const wchar_t * name = (const wchar_t*)CodecTools::codecs.Formats[i].Name;
        if (wcscmp(name, L"Cab") == 0) {
            cabIndex = i;
        }
	}
}

/**
 * Return index of the archive type. Save to UString converted archive type name into 'formatNameString'.
 * @param env instance of JNIEnv
 * @param formatName archive type format name
 */
static int getIndexByName(JNIEnv * env, jstring formatName) {
	const jchar * formatNameJChars = env->GetStringChars(formatName, NULL);
	UString formatNameString;
	formatNameString = UnicodeHelper(formatNameJChars);
	env->ReleaseStringChars(formatName, formatNameJChars);

	TRACE("Format: " << formatNameString)
	return codecTools.codecs.FindFormatForArchiveType(formatNameString);
}

int CodecTools::getArchiveFormatIndex(JNIEnv * env, jobject archiveFormat) {
	int index = jni::ArchiveFormat::codecIndex_Get(env, archiveFormat);
	if (index == -2) {
		jstring formatName = jni::ArchiveFormat::methodName_Get(env, archiveFormat);
		index = getIndexByName(env, formatName);
		jni::ArchiveFormat::codecIndex_Set(env, archiveFormat, index);
	}
	return index;
}
