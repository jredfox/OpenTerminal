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

void printTest(int signal)
{
	cout << to_string(signal) << endl;
	string s = "singal-" + to_string(signal) + ".txt";
	std::ofstream outfile (filesystem::current_path().string() + "\\" + s);
	outfile << "singal-test";
	outfile.flush();
}

BOOL WINAPI controlHandler(DWORD sig)
{
	Beep(800, 500);
	printTest(sig);
	//normal close
	if(sig == CTRL_C_EVENT)
	{
		return TRUE;
	}
	//dump core and close
	else if(sig == CTRL_BREAK_EVENT)
	{
		return TRUE;
	}
	//terminate process CONTROL_CLOSE_EVENT(SIGBREAK) or another signal
	else
	{

	}
	return TRUE;
}

void handle(int signal)
{
	cout << "im handling here\n";
	printTest(signal);
}

JNIEXPORT void JNICALL Java_jmln_PID_l (JNIEnv* env, jclass thisObject)
{
    std::cout << "Hello from C++ " << getpid() << std::endl;
    SetConsoleCtrlHandler(NULL, FALSE);
    if(!SetConsoleCtrlHandler(&controlHandler, TRUE))
    {
    	std::cerr << "Unable to set the Consoler's Handler";
        //use windows signals on console failure
    	signal(SIGINT, handle);//^C CONTROL+C
    	signal(SIGBREAK, handle);//CONTROL+BREAK
    	signal(SIGTERM, handle);//SIGTERM in case older or newer versions of TaskManager send this instead of SIGBREAK for terminating the program
    }
}
