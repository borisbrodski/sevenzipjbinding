#ifndef __CODECTOOLS_H__INCLUDED__
#define __CODECTOOLS_H__INCLUDED__

class CodecTools {
	/**
	 * Index of the CAB archive format.
	 */
	int cabIndex;

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
};

extern CodecTools codecTools;

#endif // __CODECTOOLS_H__INCLUDED__
