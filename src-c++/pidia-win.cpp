//============================================================================
// Name        : pidia.cpp
// Author      : jredfox
// Version     : beta 1.0.0
// Copyright   : Your copyright notice
// Description : PID is alive WINDOWS branch
//============================================================================

#include <windows.h>
#include <iostream>
#include <vector>
#include <map>

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
