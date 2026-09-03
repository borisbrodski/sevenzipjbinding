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

	/**
	 * Index of the BZip2 archive format.
	 */
	int bzip2Index;

	/**
	 * Index of the Xz archive format.
	 */
	int xzIndex;

public:
	CCodecs codecs;

	void init();

	/*
	 * Retrieve 7-zip CCoders-index of the archive format.
	 */
	int getArchiveFormatIndex(JNIEnv * env, jobject archiveFormat);
	void getArchiveFormatName(JNIEnv * env, jobject archiveFormat, UString & formatNameString);

	bool isCabArchive(int index) {
		return cabIndex == index;
	}

	bool isGZipArchive(int index) {
		return gzipIndex == index;
	}
	bool isBZip2Archive(int index) {
		return bzip2Index == index;
	}
	bool isXzArchive(int index) {
		return xzIndex == index;
	}
};

extern CodecTools codecTools;

#endif // __CODECTOOLS_H__INCLUDED__
