//============================================================================
// Name        : PID IS ALIVE
// Author      : jredfox
// Version     : beta 1.0.0
// Description : check if process is alive
//============================================================================

#include <iostream>
#include <vector>
#include <map>
#include <signal.h>
#include <dirent.h>
#include <errno.h>
#include <fstream>

using namespace std;

void testIsAlive();
string toString(bool b);
bool isPIDAlive(unsigned long pid);

int getProcIdByName(string procName)
{
    int pid = -1;

    // Open the /proc directory
    DIR *dp = opendir("/proc");
    if (dp != NULL)
    {
        // Enumerate all entries in directory until process found
        struct dirent *dirp;
        while (pid < 0 && (dirp = readdir(dp)))
        {
            // Skip non-numeric entries
            int id = atoi(dirp->d_name);
            if (id > 0)
            {
                // Read contents of virtual /proc/{pid}/cmdline file
                string cmdPath = string("/proc/") + dirp->d_name + "/cmdline";
                ifstream cmdFile(cmdPath.c_str());
                string cmdLine;
                getline(cmdFile, cmdLine);
                if (!cmdLine.empty())
                {
                    // Keep first cmdline item which contains the program path
                    size_t pos = cmdLine.find('\0');
                    if (pos != string::npos)
                        cmdLine = cmdLine.substr(0, pos);
                    // Keep program name only, removing the path
                    pos = cmdLine.rfind('/');
                    if (pos != string::npos)
                        cmdLine = cmdLine.substr(pos + 1);
                    // Compare against requested process name
                    if (procName == cmdLine)
                        pid = id;
                }
            }
        }
    }

    closedir(dp);

    return pid;
}

std::map<unsigned long, DIR*> handles = {};
/**
 * returns true if the process is alive and attempts to suggest a handle to linux's os that we are reading the directory /proc/[PID] reserve this PID till program shutdown
 */
bool isProcessAlive(unsigned long pid)
{
	 string path = "/proc/" + to_string(pid);
	 DIR *dp = opendir(path.c_str());//always create new handle to check process status
	 if (dp != NULL)
	 {
		 //if the current handle isn't the old one then close the old one
		 if(handles.find(pid) != handles.end() && handles[pid] != dp)
			 closedir(handles[pid]);
		 handles[pid] = dp;//cache initial handle to preserve PID
		 return true;
	 }
	 //attempt to cleanup when the process has closed
	 if(handles.find(pid) != handles.end())
	 {
		 closedir(handles[pid]);
		 closedir(dp);
		 handles.erase(pid);
	 }
	 return false;
}

int main()
{
	cout << toString(isProcessAlive(2084)) << " " + toString(isProcessAlive(2084)) << endl;
	testIsAlive();
}

string toString(bool b)
{
	return b ? "true" : "false";
}

void testIsAlive()
{
	unsigned long pid;
	while(true)
	{
		cout << "Enter PID:";
		cin >> pid;
		cout << "PID " << pid << " isAlive:" << toString(isProcessAlive(pid)) + "\n";
	}
}
/**
 * the problematic version of isProcessAlive due to recycled PIDs and there is no easy solution around it
 */
bool isPIDAlive(unsigned long pid)
{
    int res = kill(pid, 0);
    return res == 0 || (res == -1 && errno != ESRCH);
}
