#include <iostream>
#include <windows.h>
#include <wincon.h>
#include <dwmapi.h>
#include <io.h>
#include <fcntl.h>
#include "pidia.h"
#include "pidia-win.h"

using namespace std;

int main()
{
	vector<unsigned long> childs;
//	pidiaW::getChildren(pidiaW::getPID(GetConsoleWindow()), childs);
	getAllChildren(15940, childs);
	for(unsigned long c : childs)
	{
		cout << c << " " << getProcessName(c) << endl;
	}
}

void testIsAlive()
{
	unsigned long pid = 0;
	while(true)
	{
		cout << "Enter PID:";
		cin >> pid;
		if(pid == 0)
			break;
//		terminateProcess(pid);
		closeProcess(pid);
//		cout << "PID " << pid << " isAlive:" << toString(isProcessAlive(pid, getProcessStartTime(pid))) + "\n";
	}
}

