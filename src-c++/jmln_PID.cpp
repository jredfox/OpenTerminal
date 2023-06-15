#include <iostream>
#include <jni.h>
#include "jmln_PID.h"

JNIEXPORT void JNICALL Java_jmln_PID_l (JNIEnv* env, jclass thisObject)
{
    std::cout << "Hello from C++ !!" << std::endl;
}
