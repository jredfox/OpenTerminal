#include <iostream>
#include <jni.h>
#include <windows.h>
#include <stdio.h>
#include <process.h>
#include <fstream>
#include <signal.h>
#include <filesystem>
#include "jmln_PID.h"

using namespace std;
JavaVM* jvm;
JNIEnv* env;


void printTest(int signal)
{
	cout << to_string(signal) << endl;
	string s = "singal-" + to_string(signal) + ".txt";
	std::ofstream outfile (filesystem::current_path().string() + "\\" + s);
	outfile << "singal-test";
	outfile.flush();
}

void shutdownJVM()
{
	jint res =  jvm->AttachCurrentThread((void **)(&env), &env);
	jclass cls = env->FindClass("jredfox/common/pida/ShutdownHooks");
	jmethodID mid = env->GetStaticMethodID(cls, "shutdownWindows", "(I)V");
	env->CallStaticVoidMethod(cls, mid, sig);
	jvm->DetachCurrentThread();
}

BOOL WINAPI controlHandler(DWORD sig)
{
//	Beep(800, 500);
//	printTest(sig);
	shutdownJVM();
	return TRUE;
}

void handle(int signal)
{
//	printTest(signal);
	shutdownJVM();
}

JNIEXPORT void JNICALL Java_jmln_PID_l (JNIEnv* p_env, jclass thisObject)
{
	env = p_env;
	env->GetJavaVM(&jvm);
    if(!SetConsoleCtrlHandler(&controlHandler, TRUE))
    {
    	std::cerr << "Unable to set the Consoler's Handler";
        //use windows signals on console failure
    	signal(SIGINT, handle);//^C CONTROL+C
    	signal(SIGBREAK, handle);//CONTROL+BREAK
    	signal(SIGTERM, handle);//SIGTERM in case older or newer versions of TaskManager send this instead of SIGBREAK for terminating the program
    }
    std::cout << "Win ShutDown Hooks installed C++ " << getpid() << std::endl;
}
