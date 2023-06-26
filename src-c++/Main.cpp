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
	pidiaW::fixConsole(true);
	cout << "Testing unicode -- English -- Ελληνικά -- Español. -- Chinese 你好吗" << std::endl;
	cout << "\033[31mHELLOW WORLD" << endl;
	cout << "\033[38;2;0;255;0mHELLOW WORLD green" << endl;
	printf("%s\n", "中文");
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

