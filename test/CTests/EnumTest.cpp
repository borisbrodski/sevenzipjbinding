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

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getPropertyIndexArcFileName(JNIEnv * env, jclass thiz) {
    return kpidArcFileName;
}

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getPropertyIndexUserId(JNIEnv * env, jclass thiz) {
    return kpidUserId;
}

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getPropertyIndexDevMinor(JNIEnv * env, jclass thiz) {
    return kpidDevMinor;
}

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getPropertyIndexNumDefined(JNIEnv * env, jclass thiz) {
    return kpid_NUM_DEFINED;
}

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getFileTimeTypeDos(JNIEnv * env, jclass thiz) {
    return NFileTimeType::kDOS;
}

JBINDING_JNIEXPORT jint JNICALL
Java_net_sf_sevenzipjbinding_junit_jbindingtools_EnumTest_getFileTimeType1ns(JNIEnv * env, jclass thiz) {
    return NFileTimeType::k1ns;
}

#endif
