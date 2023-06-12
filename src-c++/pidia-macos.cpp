//============================================================================
// Name        : PID IS ALIVE
// Author      : jredfox
// Version     : beta 1.0.0
// Description : check if PID IS ALIVE macOs branch
//============================================================================

#include <signal.h>
#include <stddef.h>
#include <sys/_types/_timeval.h>
#include <sys/errno.h>
#include <sys/proc.h>
#include <sys/sysctl.h>
#include <cstring>
#include <iostream>
#include <map>
#include <string>

using namespace std;

void testIsAlive();
string toString(bool b);
bool isProcessAlive(unsigned long pid);
bool isPIDAlive(unsigned long pid);

int main()
{
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
		cout << "PID " + to_string(pid) + " isAlive:" + toString(isProcessAlive(pid)) + "\n";
	}
}

/**
 * map of unsigned long, creation time either in jiffies, ms, or in clock ticks or different on mac even. so we keep it as a string
 */
std::map<unsigned long, string> handles;
/**
 * returns true if the process is alive and attempts to suggest a handle to linux's os that we are reading the directory /proc/[PID] reserve this PID till program shutdown
 */
bool isProcessAlive(unsigned long pid)
{
    // Get process info from kernel
    struct kinfo_proc info;
    int mib[] = { CTL_KERN, KERN_PROC, KERN_PROC_PID, (int)pid };
    size_t len = sizeof info;
    memset(&info,0,len);
    int rc = sysctl(mib, (sizeof(mib)/sizeof(int)), &info, &len, NULL, 0);

    //exit program sysctl failed to verify PID
    if (rc != 0)
    {
    	handles.erase(pid);
        return false;
    }

    //extract start time and confirm PID start time equals org start time
    struct timeval tv = info.kp_proc.p_starttime;
    string time = to_string(tv.tv_usec) + "-" + to_string(tv.tv_sec);
    if(handles.find(pid) != handles.end())
    {
    	string org_time = handles[pid];
    	if(org_time != time)
    	{
    		cout << "PID Conflict PID:" << pid << " org_time:" + org_time << " new_time:" << time << endl;
    		handles.erase(pid);
    		return false;
    	}
    }
    else
    {
    	handles[pid] = time;
    }
    return true;
}

/**
 * the problematic version of isProcessAlive due to recycled PIDs and there is no easy solution around it
 */
bool isPIDAlive(unsigned long pid)
{
    int res = kill(pid, 0);
    return res == 0 || (res == -1 && errno != ESRCH);
}
