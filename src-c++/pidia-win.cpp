//============================================================================
// Name        : pidia.cpp
// Author      : jredfox
// Version     : beta 1.0.0
// Copyright   : Your copyright notice
// Description : PID is alive WINDOWS branch
//============================================================================

#include <process.h>
#include <winbase.h>
#include <windef.h>
#include <winerror.h>
#include <winnt.h>
#include <iostream>
#include <map>
#include <string>
#include <signal.h>
#include <sys/types.h>

using namespace std;

bool isProcessAlive(DWORD pid);
string toString(bool b);
void testIsAlive();

map<DWORD, string> m;
int main()
{
	testIsAlive();
}

void testIsAlive()
{
	DWORD pid = 0;
	while(true)
	{
		cout << "Enter PID:";
		cin >> pid;
		if(pid == 0)
			break;
		cout << "PID " << pid << " isAlive:" << toString(isProcessAlive(pid)) + "\n";
	}
}

/**
 * returns the current process's id
 */
unsigned long getPID()
{
	return getpid();
}

unsigned long getProcessTime(unsigned long pid)
{
	return -1;//TODO:
}

unsigned long getProcessTime()
{
	return getProcessTime(getPID());
}

long unsigned getPID(string process_name)
{
	return -1;//TODO:
}

void sendSignal(unsigned long pid, int signal)
{
	//TODO:
}

/**
 * equal to System#exit(0) hopefully
 */
void stopProcess(unsigned long pid)
{
	sendSignal(pid, SIGINT);//TODO figure out if SIGSTOP should be here instead
}

/**
 * abnormal termination of a process. I believe there is a 2 second delay before the OS closes the process or sends a SIGKILL
 */
void terminateProcess(unsigned long pid)
{
	sendSignal(pid, SIGTERM);
}

/**
 * kills the process without being handled by System#exit. this may cause file corruption
 */
void killProcesss(unsigned long pid)
{
	sendSignal(pid, 9);//SIGKILL is 9 on all CPUS ARCS and isn't defined on windows
}

map<DWORD, HANDLE> handles;
bool isProcessAlive(DWORD pid)
{
	HANDLE process;
	if(handles.find(pid) == handles.end())
	{
		process = OpenProcess(SYNCHRONIZE, FALSE, pid);
		handles[pid] = process;
	}
	else
	{
		process = handles[pid];
	}
	DWORD ret = WaitForSingleObject(process, 0);
	bool isRunning = ret == WAIT_TIMEOUT;
	if(!isRunning)//close the cached handle to free the PID and erase from the cache
	{
		CloseHandle(process);
		handles.erase(pid);
	}
    return isRunning;
}

string toString(bool b)
{
	return b ? "true" : "false";
}
