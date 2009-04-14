#include <jni.h>
#include <stdio.h>

extern "C" void Java_test_Main_nativeTest(JNIEnv * env)
{
    printf("Hello from C++\n");
}