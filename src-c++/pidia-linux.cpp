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

#include <iostream>
#include <iterator>
#include <sstream>
#include <fstream>
#include <vector>
#include <cstring>
#include <cerrno>
#include <ctime>
#include <cstdio>
#include <fcntl.h>
#include <sys/time.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdlib.h>
#include <string>
#include "/usr/include/x86_64-linux-gnu/sys/param.h"

using namespace std;

void testIsAlive();
string toString(bool b);
bool isProcessAlive(unsigned long pid);

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
		cout << "PID " << pid << " isAlive:" << toString(isProcessAlive(pid)) + "\n";
	}
}

/**
 * map of unsigned long, creation time either in jiffies, ms, or in clock ticks or different on mac even. so we keep it as a string
 */
std::map<unsigned long, string> handles = {};
/**
 * returns true if the process is alive and attempts to suggest a handle to linux's os that we are reading the directory /proc/[PID] reserve this PID till program shutdown
 */
bool isProcessAlive(unsigned long pid)
{
    ifstream procFile;
    string f = "/proc/"+ std::to_string(pid)+ "/stat";
    procFile.open(f.c_str());
    if(!procFile.fail())
    {
    	//get creation time of current pid's process
        char str[255];
        procFile.getline(str, 255);  // delim defaults to '\n'

        vector<string> tmp;
        istringstream iss(str);
        copy(istream_iterator<string>(iss),
             istream_iterator<string>(),
             back_inserter<vector<string> >(tmp));

        string creation_time = tmp.at(21);

    	//check if the process's creation time matches the cached creation time
    	if(handles.find(pid) != handles.end())
    	{
    		string org = handles[pid];
    		//if the pid's creation time is not the cached creation time we assume it's not the same process and the original has closed
    		//unlike java the ==,!= actually checks .equals() when comparing
    		if(creation_time != org)
    		{
    			std::cerr << "PID conflict:" + to_string(pid) + " orgCreationTime:" + org + " newCreationTime:" + creation_time;
    			handles.erase(pid);
    			procFile.close();
    			return false;
    		}
    	}
        handles[pid] = creation_time;
        procFile.close();
        return true;
    }
    handles.erase(pid);
    procFile.close();
    return false;
}
/**
 * the problematic version of isProcessAlive due to recycled PIDs and there is no easy solution around it
 */
bool isPIDAlive(unsigned long pid)
{
    int res = kill(pid, 0);
    return res == 0 || (res == -1 && errno != ESRCH);
}
