#ifndef __CODECTOOLS_H__INCLUDED__
#define __CODECTOOLS_H__INCLUDED__

class CodecTools {
	/**
	 * Index of the CAB archive format.
	 */
	int cabIndex;

	/**
	 * Index of the GZIP archive format.
	 */
	int gzipIndex;

public:
	CCodecs codecs;

	void init();

	/*
	 * Retrieve 7-zip CCoders-index of the archive format.
	 */
	int getArchiveFormatIndex(JNIEnv * env, jobject archiveFormat);

	bool isCabArchive(int index) {
		return cabIndex == index;
	}

	bool isGZipArchive(int index) {
		return gzipIndex == index;
	}
};

extern CodecTools codecTools;

#endif // __CODECTOOLS_H__INCLUDED__
