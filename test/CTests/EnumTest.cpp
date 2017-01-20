/*
 * JBindingTest.cpp
 *
 *  Created on: Jan 31, 2010
 *      Author: boris
 */
#include <iostream>
#include <sstream>

#include <vector>

#include "SevenZipJBinding.h"
#include "Common/MyCom.h"
#include "Windows/Thread.h"

#include "JBindingTools.h"

#ifdef NATIVE_JUNIT_TEST_SUPPORT

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getPropertyIndexSymLink(JNIEnv * env,
                                                                                  jclass thiz) {
    return kpidSymLink;
}

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getPropertyIndexHardLink(JNIEnv * env,
                                                                                   jclass thiz) {
    return kpidHardLink;
}

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getPropertyIndexCopyLink(JNIEnv * env,
                                                                                   jclass thiz) {
    return kpidCopyLink;
}

#endif
