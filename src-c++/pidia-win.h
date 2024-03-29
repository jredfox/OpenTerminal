#ifndef _JREDFOX_PIDIA_WH
#define _JREDFOX_PIDIA_WH
#include <iostream>
#include <vector>
#include <windows.h>
using namespace std;

namespace pidiaW
{
	/**
 	 * Your Process ID (PID)
 	 */
	unsigned long getPID();
	/**
	 * Parent Process ID
	 */
	unsigned long getPPID();
	/**
	 * Parent Process ID of x PID
	 */
	unsigned long getPPID(unsigned long pid);
	/**
	 * PIDIA is Process Alive?
	 */
	bool isProcessAlive(unsigned long pid, string org_time);
	/**
	 * Process Start time as string (different OS's will return different formats)
 	 * Cache this for your app then call #isProcessAlive(pid, process_time)
 	 */
	string getProcessStartTime(unsigned long pid);
	/**
	 * Get Your process's start time as string
	 */
	string getProcessStartTime();
	/**
	 * get the PID from the full Executable Path. Note: this won't always be your PID or consistently return the correct running instance of app if there are multiple running instances
	 * Use this for Singleton Apps or if you want to see if an executable is already running like chrome
	 */
	long unsigned getPID(string path);
	/**
	 * get PID from window
	 */
	DWORD getPID(HWND h);
	/**
	 * send a kill signal to a GUI app
	 * NOTE: recommend using sendSignal directly as it will handle if it needs to send CLI or GUI signal
	 */
	void sendWinUISignal(unsigned long pid, int signal);
	/**
	 * send a kill signal to a CLI app
	 * NOTE: recommend using sendSignal directly as it will handle if it needs to send CLI or GUI signal
	 */
	bool sendWinCLISignal(unsigned long pid, int signal);
	/**
	 * send a kill signal to any app
	 */
	void sendSignal(unsigned long pid, int signal);
	/**
	 * send a close process kill signal to the specified UID. Specific OS's may not support per PID and may close the entire process group
	 * AKA if your on windows only call one PID per process group. A process may be freed from the group on windows by calling FreeConsole
	 */
	void closeProcess(unsigned long pid);
	/**
	 * send a SIGTERM to a process. While the APP can handle and ignore this it's generally understood there may be a timer to shutdown before a SIGKILL occurs
	 * NOTE: on windows SIGTERM or CLOSE won't work unless the process has at least one window open regardless of whether or not it's visible
	 */
	void terminateProcess(unsigned long pid);
	/**
	 * forcibly terminate a process without any handling. May cause file corruption or errors of other process's expecting it to be alive
	 */
	void killProcess(unsigned long pid);
	/**
	 * get the full Executable name of a process
	 */
	string getProcessName(unsigned long pid);
	/**
	 * sets the console buffer size(x=amount of characters/bytes per line, y=amount of lines)
	 */
	void setConsoleBuffer(int Width, int Height);
	/**
	 * sets the opacity 0-255 where 255 is solid and 0 is invisible
	 */
	void setConsoleOpacity(int opacity);
	/**
	 * activates all windows from a specific process
	 */
	void activateWindow(unsigned long pid);
	/**
	 * fixes conHost on windows 10 or higher
	 * UTF-8, COLORS, replacing the close button
	 */
	void fixConsole(bool replaceClose);
	/**
	 * gets children of the process but not all children
	 */
	void getChildren(unsigned long pid, vector<unsigned long> &vec);

	//START INTERNAL UTILITY METHODS
	bool endsWith(std::string const &fullString, std::string const &ending);
	void GetAllWindowsFromProcessID(DWORD dwProcessID, std::vector<HWND> &vhWnds, bool bg);
	string getAppData();
	int runProcess(string exe, string args);
	string toString(bool b); //bool to string why wasn't this implemented correctly in std::to_string(bool b);????
}
#endif
