//============================================================================
// Name        : pidia.cpp
// Author      : jredfox
// Version     : beta 1.0.0-b2
// Copyright   : Your copyright notice
// Description : PID is alive WINDOWS branch
//============================================================================
#define _WIN32_WINNT 0x0501
#include <windows.h>
#include <psapi.h>
#include <process.h>
#include <signal.h>
#include <winbase.h>
#include <windef.h>
#include <winnt.h>
#include <cstdlib>
#include <iostream>
#include <map>
#include <string>
#include <tlhelp32.h>
#include <winnt.h>
#include "jmln_PID.h"

using namespace std;

string toString(bool b);
void testIsAlive();
unsigned long getPID();
unsigned long getPPID();
unsigned long getPPID(unsigned long pid);
unsigned long getPID(string path);
void killProcess(unsigned long pid);
string getProcessStartTime(unsigned long pid);
string getProcessName(unsigned long pid);
bool isProcessAlive(unsigned long pid, string org_time);

int main()
{
	Java_jmln_PID_l(NULL, NULL);
	while(true);
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
		cout << "PID " << pid << " isAlive:" << toString(isProcessAlive(pid, getProcessStartTime(pid))) + "\n";
	}
}

/**
 * returns the current process's id
 */
unsigned long getPID()
{
	return getpid();
}

unsigned long getPPID(unsigned long pid)
{
	unsigned long ppid = -1;
    PROCESSENTRY32 entry;
    entry.dwSize = sizeof(PROCESSENTRY32);
	HANDLE snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    if (Process32First(snapshot, &entry))
    {
        while (Process32Next(snapshot, &entry))
        {
            if (entry.th32ProcessID == pid)
            {
                ppid = entry.th32ParentProcessID;
                break;
            }
        }
    }
    CloseHandle(snapshot);
	return ppid;
}

unsigned long getPPID()
{
	return getPPID(getPID());
}

/**
 * a reliable way to determine if PID is alive by securing it with the creation time.
 * cache a version of #getProcessStartTime(PID) when it returns a non-empty string for the second parameter
 */
bool isProcessAlive(unsigned long pid, string org_time)
{
	string new_time = getProcessStartTime(pid);
	return !new_time.empty() && org_time == new_time;
}

/**
 * returns process's creation time or "" if the process isn't open
 */
string getProcessStartTime(unsigned long pid)
{
	FILETIME startTime, ftExit, ftKernel, ftUser; // this variables for get process start time and etc.
	HANDLE hProc = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, pid);//Open process for all access
	if(hProc == 0)
		return "";
	if(GetProcessTimes(hProc, &startTime, &ftExit, &ftKernel, &ftUser) == 0)
		return "";
	CloseHandle(hProc);
	return to_string(startTime.dwLowDateTime) + "-" + to_string(startTime.dwHighDateTime);
}

string getProcessStartTime()
{
	return getProcessStartTime(getPID());
}

bool endsWith (std::string const &fullString, std::string const &ending)
{
    if (fullString.length() >= ending.length())
    {
        return (0 == fullString.compare (fullString.length() - ending.length(), ending.length(), ending));
    }
    return false;
}

/**
 * returns the first instance of the pid found from the given PATH
 */
long unsigned getPID(string path)
{
	unsigned long pid = 0;
    PROCESSENTRY32 entry;
    entry.dwSize = sizeof(PROCESSENTRY32);
	HANDLE snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    if (Process32First(snapshot, &entry))
    {
        while (Process32Next(snapshot, &entry))
        {
            if(endsWith(path, entry.szExeFile) && getProcessName(entry.th32ProcessID) == path)
            {
            	pid = entry.th32ProcessID;
            }
        }
    }
    CloseHandle(snapshot);
    return pid;
}

void sendSignal(unsigned long pid, int signal)
{
	GenerateConsoleCtrlEvent(CTRL_C_EVENT , 0);
}

/**
 * equal to System#exit(0) hopefully
 */
void stopProcess(unsigned long pid)
{
	sendSignal(pid, SIGINT);
}

/**
 * SIGTERM(SIGBREAK/CONTROL_CLOSE_EVENT on windows)
 */
void terminateProcess(unsigned long pid)
{
	sendSignal(pid, SIGBREAK);
}

/**
 * kills the process without being handled by System#exit or SIGNALING. this may cause file corruption
 */
void killProcess(unsigned long pid)
{
	const auto explorer = OpenProcess(PROCESS_TERMINATE, false, pid);
	TerminateProcess(explorer, 1);
	CloseHandle(explorer);
}

/**
 * returns the full executable path of the running process
 */
string getProcessName(unsigned long pid)
{
	string name = "";
	HANDLE phandle = OpenProcess( PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, pid);
	TCHAR filename[MAX_PATH];
	GetModuleFileNameEx(phandle, NULL, filename, MAX_PATH);
    CloseHandle(phandle);
    return string(filename);
}

string toString(bool b)
{
	return b ? "true" : "false";
}
