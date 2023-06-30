/**
 * The Interface Between C++ and platform specific code
 */
#ifdef WIN32
#include "pidia-win.h"
namespace pidh = pidiaW;
#endif

unsigned long getPID()
{
	return pidh::getPID();
}

unsigned long getPPID()
{
	return pidh::getPPID();
}

unsigned long getPPID(unsigned long pid)
{
	return pidh::getPPID(pid);
}

bool isProcessAlive(unsigned long pid, string org_time)
{
	return pidh::isProcessAlive(pid, org_time);
}

string getProcessStartTime(unsigned long pid)
{
	return pidh::getProcessStartTime(pid);
}

string getProcessStartTime()
{
	return pidh::getProcessStartTime();
}

long unsigned getPID(string path)
{
	return pidh::getPID(path);
}

void sendWinUISignal(unsigned long pid, int signal)
{
	pidh::sendWinUISignal(pid, signal);
}

bool sendWinCLISignal(unsigned long pid, int signal)
{
	return pidh::sendWinCLISignal(pid, signal);
}

void sendSignal(unsigned long pid, int signal)
{
	pidh::sendSignal(pid, signal);
}

void closeProcess(unsigned long pid)
{
	pidh::closeProcess(pid);
}

void terminateProcess(unsigned long pid)
{
	pidh::terminateProcess(pid);
}

void killProcess(unsigned long pid)
{
	pidh::killProcess(pid);
}

string getProcessName(unsigned long pid)
{
	return pidh::getProcessName(pid);
}

bool endsWith (std::string const &fullString, std::string const &ending)
{
	return pidh::endsWith(fullString, ending);
}

/**
 * portable accross all PC's to get APPDATA
 */
string getAppData()
{
	return pidh::getAppData();
}

int runProcess(string exe, string args)
{
	return pidh::runProcess(exe, args);
}

/**
 * sets the console buffer size(x=amount of characters/bytes per line, y=amount of lines)
 */
void setConsoleBuffer(int Width, int Height)
{
	pidh::setConsoleBuffer(Width, Height);
}
/**
 * sets the opacity 0-255 where 255 is solid and 0 is invisible
 */
void setConsoleOpacity(int opacity)
{
	pidh::setConsoleOpacity(opacity);
}

void activateWindow(unsigned long pid)
{
	pidh::activateWindow(pid);
}
/**
 * gets children from the selected process but not all children
 */
void getChildren(unsigned long pid, vector<unsigned long> &vec)
{
	pidh::getChildren(pid, vec);
}
/**
 * gets all children iteratively as a flat vector of PIDs
 */
void getAllChildren(unsigned long pid, vector<unsigned long> &children)
{
	getChildren(pid, children);
	unsigned int size = 0;
	unsigned int index = 0;
	do
	{
		size = children.size();
		for(unsigned int i=index; i < size ; i++)
		{
			unsigned long c = children[i];
			getChildren(c, children);
			index++;
		}
	}
	while(children.size() > size);
}

string toString(bool b)
{
	return b ? "true" : "false";
}
