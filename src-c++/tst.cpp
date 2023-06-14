#include <stdlib.h> // NULL
#include <stdbool.h> // false
#include <windows.h> // AttachConsole, CTRL_C_EVENT, etc.
#include <stdio.h> // printf

using namespace std;

void testSend(unsigned long pid, int signal)
{
//	int p = pid;
//	AttachConsole(p);
	GenerateConsoleCtrlEvent(CTRL_C_EVENT, 0); // generate Control+C event
}
