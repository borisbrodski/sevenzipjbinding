#ifndef CPPTOJAVAPROGRESS_H_
#define CPPTOJAVAPROGRESS_H_

#include "CPPToJavaAbstract.h"
#include "JavaStatInfos/JavaPackageSevenZip.h"

class CPPToJavaProgress : public CPPToJavaAbstract,
                          public virtual IProgress,
                          public CMyUnknownImp {
private:
  jni::IProgress *_iProgress;

public:
  CPPToJavaProgress(JBindingSession &jbindingSession, JNIEnv *initEnv,
                    jobject progress)
      : CPPToJavaAbstract(jbindingSession, initEnv, progress),
        _iProgress(jni::IProgress::_getInstanceFromObject(initEnv, progress)) {
    TRACE_OBJECT_CREATION("CPPToJavaProgress")
  }

  virtual ~CPPToJavaProgress() {}

  Z7_IFACE_COM7_IMP_NONFINAL(IProgress)

private:
  Z7_COM_UNKNOWN_IMP_1(IProgress)
};

#endif /* CPPTOJAVAPROGRESS_H_ */
