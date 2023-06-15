#include <iostream>
#include <jni.h>
#include <windows.h>
#include <process.h>
#include "jmln_PID.h"

BOOL WINAPI CtrlHandler(DWORD sig)
{
	switch(sig)
	{
		case CTRL_CLOSE_EVENT:
		{
			return TRUE;
		}
		default:
			return FALSE;
	}
}

JNIEXPORT void JNICALL Java_jmln_PID_l (JNIEnv* env, jclass thisObject)
{
    std::cout << "Hello from C++ !!" << std::endl << getpid() << std::endl;
}
