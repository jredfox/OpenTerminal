#include <iostream>
#include "pidia.h"

using namespace std;

int main()
{
	cout << getPID() << getProcessName(getPPID());
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
