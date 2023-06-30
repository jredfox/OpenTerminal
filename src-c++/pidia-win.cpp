//============================================================================
// Name        : pidia.cpp
// Author      : jredfox
// Version     : beta 1.1.0
// Description : PID is alive WINDOWS branch
//============================================================================
#ifndef _WIN32_WINNT
#define _WIN32_WINNT 0x0501
#endif
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
#include <winuser.h>
#include <vector>
#include <filesystem>
#include <stdlib.h>
#include <stdio.h>
#include <tchar.h>
using namespace std;

namespace pidiaW
{

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

	/**
	 * a reliable way to determine if PID is alive by securing it with the creation time.
	 * cache a version of #getProcessStartTime(PID) when it returns a non-empty string for the second parameter
	 */
	bool isProcessAlive(unsigned long pid, string org_time)
	{
		string new_time = getProcessStartTime(pid);
		return !new_time.empty() && org_time == new_time;
	}

	bool endsWith (string const &fullString, string const &ending)
	{
		if (fullString.length() >= ending.length())
		{
			return (0 == fullString.compare (fullString.length() - ending.length(), ending.length(), ending));
		}
		return false;
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

	/**
	 * returns the first instance of the pid found from the given PATH
	 */
	unsigned long getPID(string path)
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

	DWORD getPID(HWND h)
	{
		DWORD pid = 0;
		GetWindowThreadProcessId(h, &pid);
		return pid;
	}

	void GetAllWindowsFromProcessID(DWORD dwProcessID, vector <HWND> &vhWnds, bool bg)
	{
		// find all hWnds (vhWnds) associated with a process id (dwProcessID)
		HWND hCurWnd = nullptr;
		do
		{
			hCurWnd = FindWindowEx(nullptr, hCurWnd, nullptr, nullptr);
			DWORD checkProcessID = 0;
			GetWindowThreadProcessId(hCurWnd, &checkProcessID);
			if (checkProcessID == dwProcessID && (bg || IsWindowVisible(hCurWnd)))
			{
				vhWnds.push_back(hCurWnd);
			}
		}
		while (hCurWnd != nullptr);
	}

	/**
	 * send a signal to a windows ui app
	 */
	void sendWinUISignal(unsigned long pid, int signal)
	{
		BOOL force = signal != SIGINT;
		vector<HWND> windows;
		GetAllWindowsFromProcessID(pid, windows, false);

		//handle bg process's and susy bakas
		if(windows.empty())
		{
			GetAllWindowsFromProcessID(pid, windows, true);
			if(!windows.empty())
			{
				cerr << "All Windows Are Invisible! Possibly SUS or ERR PID:" + to_string(pid) + " EXE:" + getProcessName(pid) << endl;
				force = true;
			}
		}

		for(HWND win : windows)
		{
			if(!force)
			{
				PostMessage(win, WM_CLOSE, 0, 0);
			}
			else
			{
				PostMessage(win, WM_SYSCOMMAND, SC_CLOSE, 0);//Terminate what TaskManager uses
			}
		}
	}

	/**
	 * portable across all PC's to get APPDATA
	 */
	string getAppData()
	{
		#ifdef WIN32
			return filesystem::path(std::getenv("APPDATA")).generic_u8string();
		#elif __APPLE__
			return (filesystem::path(std::getenv("HOME")).generic_u8string()) + "\\Library\\Application Support";//OSX / macOS
		#else
			return filesystem::path(std::getenv("HOME")).generic_u8string(); //linux / unix
		#endif
	}

	/**
	 * run a process and wait for the exit code
	 */
	int runProcess(string exe, string args)
	{
		STARTUPINFO si;
		PROCESS_INFORMATION pi;

		ZeroMemory( &si, sizeof(si) );
		si.cb = sizeof(si);
		ZeroMemory( &pi, sizeof(pi) );
		string cmd = exe + " " + args;

		// Start the child process.
		if( !CreateProcess(NULL,   // No module name (use command line)
				(LPSTR)cmd.c_str(),    // Command line
				NULL,           // Process handle not inheritable
				NULL,           // Thread handle not inheritable
				FALSE,          // Set handle inheritance to FALSE
				0,              // No creation flags
				NULL,           // Use parent's environment block
				NULL,           // Use parent's starting directory
				&si,            // Pointer to STARTUPINFO structure
				&pi )           // Pointer to PROCESS_INFORMATION structure
		)
		{
			cerr << "Failed to create Process:" + cmd;
		}
		WaitForSingleObject(pi.hProcess, INFINITE);
		DWORD exitCode = -2;
		GetExitCodeProcess(pi.hProcess, &exitCode);

		// Close process and thread handles.
		CloseHandle(pi.hProcess);
		CloseHandle(pi.hThread);

		return exitCode;
	}

	bool sendWinCLISignal(unsigned long pid, int signal)
	{
		int exitCode = runProcess(getAppData() + "\\PIDIA\\natives\\WINSIG.exe", (to_string(pid) + " " + to_string(signal)));
		return exitCode == 0;
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
	* sends a signal to CLI apps first and if it fails it will send a signal to a UI app
	*/
	void sendSignal(unsigned long pid, int signal)
	{
		//SIGKILL is 9 accross all CPUS
		if(signal == 9)
		{
			killProcess(pid);
			return;
		}

		//we have to first see if it's a CLI App by using WINSIG
		if(!sendWinCLISignal(pid, signal))
		{
			sendWinUISignal(pid, signal);
		}
	}

	/**
	* equal to System#exit(0) hopefully
	*/
	void closeProcess(unsigned long pid)
	{
		sendSignal(pid, SIGINT);
	}

	/**
	* SIGTERM(SIGBREAK/CONTROL_CLOSE_EVENT on windows)
	*/
	void terminateProcess(unsigned long pid)
	{
		sendSignal(pid, SIGTERM);
	}

	/**
	 * defaults to width 120 with no max but increasing it past 500 will affect cli performance
	 * defaults to height is 9001 with height being capped at 9999 and sometimes can glitch it to 10000
	 */
	void setConsoleBuffer(int Width, int Height)
	{
	    _COORD coord;
	    coord.X = Width;
	    coord.Y = Height;

	    _SMALL_RECT Rect;
	    Rect.Top = 0;
	    Rect.Left = 0;
	    Rect.Bottom = Height - 1;
	    Rect.Right = Width - 1;

	    HANDLE Handle = GetStdHandle(STD_OUTPUT_HANDLE);      // Get Handle
	    SetConsoleWindowInfo(Handle, TRUE, &Rect);            // Set Window Size
	    SetConsoleScreenBufferSize(Handle, coord);            // Set Buffer Size
	}

	/**
	 * sets the transparency of the console window
	 */
	void setConsoleOpacity(int opacity)
	{
		SetLayeredWindowAttributes(GetConsoleWindow(), 0, opacity, LWA_ALPHA);
	}

	void activateWindow(HWND hwnd)
	{
		//if it's minimized restore it
		if (IsIconic(hwnd))
		{
			SendMessage(hwnd, WM_SYSCOMMAND, SC_RESTORE, 0);
		}
		//bring the window to the top and activate it
		SetForegroundWindow(hwnd);
		SetFocus(hwnd);
		SetActiveWindow(hwnd);
		SetWindowPos(hwnd, HWND_TOP, 0, 0, 0, 0, SWP_SHOWWINDOW | SWP_NOMOVE | SWP_NOSIZE);
		//redraw to prevent the window blank.
		RedrawWindow(hwnd, NULL, 0, RDW_FRAME | RDW_INVALIDATE | RDW_ALLCHILDREN);
	}

	void activateWindow(unsigned long pid)
	{
		vector<HWND> vec;
		GetAllWindowsFromProcessID(pid, vec, false);
		for(HWND h : vec)
		{
			activateWindow(h);
		}
	}

	void EnableUTF8()
	{
		SetConsoleOutputCP(65001);
	}

	void EnableConsoleColors()
	{
		HANDLE hStdin = GetStdHandle(STD_OUTPUT_HANDLE);
		DWORD mode = 0;
		GetConsoleMode(hStdin, &mode);
		mode |= ENABLE_PROCESSED_OUTPUT | ENABLE_VIRTUAL_TERMINAL_PROCESSING;
		SetConsoleMode(hStdin, mode);
	}

	/**
	 * enables colors unicode and if requested replaces the close button
	 */
	void fixConsole(bool replaceClose)
	{
		EnableConsoleColors();
		EnableUTF8();
	}

	bool getChildren(unsigned long pid, vector<unsigned long> &vec)
	{
		bool hasKids = false;
		PROCESSENTRY32 entry;
		entry.dwSize = sizeof(PROCESSENTRY32);
		HANDLE snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
		if (Process32First(snapshot, &entry))
    	{
			while (Process32Next(snapshot, &entry))
			{
				if (entry.th32ParentProcessID == pid)
            	{
					vec.push_back(entry.th32ProcessID);
					hasKids = true;
            	}
			}
    	}
		CloseHandle(snapshot);
		return hasKids;
	}

	string toString(bool b)
	{
		return b ? "true" : "false";
	}
}
