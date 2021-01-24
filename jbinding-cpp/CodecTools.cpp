/*
 * CodecTools.cpp
 *
 *  Created on: Dec 23, 2009
 *      Author: boris
 */

#include "SevenZipJBinding.h"

#include "CodecTools.h"
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

	gzipIndex = -1;
	bzip2Index = -1;
	for (int i = 0; i < CodecTools::codecs.Formats.Size(); i++) {
        const wchar_t * name = (const wchar_t*)CodecTools::codecs.Formats[i].Name;
        if (wcscmp(name, L"Cab") == 0) {
            cabIndex = i;
        } else if (wcscmp(name, L"gzip") == 0) {
            gzipIndex = i;
        } else if (wcscmp(name, L"bzip2") == 0) {
            bzip2Index = i;
        }
	}
}

/**
 * Return index of the archive type. Save to UString converted archive type name into 'formatNameString'.
 * @param env instance of JNIEnv
 * @param archiveFormat Java ArchiveFormat object
 */
static int getIndexByName(JNIEnv * env, UString & formatNameString) {
	TRACE("Format: " << formatNameString)
	return codecTools.codecs.FindFormatForArchiveType(formatNameString);
}

void CodecTools::getArchiveFormatName(JNIEnv * env, jobject archiveFormat, UString & formatNameString) {
    jstring formatName = jni::ArchiveFormat::methodName_Get(env, archiveFormat);
    formatNameString = FromJChar(env, formatName);
#ifdef __ANDROID_API__
	env->DeleteLocalRef(formatName);
#endif
}

int CodecTools::getArchiveFormatIndex(JNIEnv * env, jobject archiveFormat) {
	int index = jni::ArchiveFormat::codecIndex_Get(env, archiveFormat);
	if (index == -2) {
	    UString formatNameString;
	    getArchiveFormatName(env, archiveFormat, formatNameString);
		index = getIndexByName(env, formatNameString);
		jni::ArchiveFormat::codecIndex_Set(env, archiveFormat, index);
	}
	return index;
}
