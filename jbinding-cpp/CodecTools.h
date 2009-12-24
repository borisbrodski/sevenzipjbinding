#ifndef __CODECTOOLS_H__INCLUDED__
#define __CODECTOOLS_H__INCLUDED__

class CodecTools {
public:
	static CCodecs codecs;

	static void init();

	static int getIndexByName(JNIEnv * env, jstring format, UString & formatNameString);
};


#endif // __CODECTOOLS_H__INCLUDED__
