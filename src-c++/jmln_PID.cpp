#include <iostream>
#include <jni.h>
#include <windows.h>
#include <stdio.h>
#include <process.h>
#include "jmln_PID.h"

BOOL WINAPI controlHandler(DWORD sig)
{
	//normal close
	if(sig == CTRL_C_EVENT)
	{

	}
	//dump core and close
	else if(sig == CTRL_BREAK_EVENT)
	{

	}
	//terminate process CONTROL_CLOSE_EVENT(SIGBREAK) or another signal
	else
	{

	}
	return TRUE;
}

JNIEXPORT void JNICALL Java_jmln_PID_l (JNIEnv* env, jclass thisObject)
{
    std::cout << "Hello from C++ " << getpid() << std::endl;
    if(!SetConsoleCtrlHandler(&controlHandler, TRUE))
    	std::cerr << "Unable to set the Consoler's Handler";
}
