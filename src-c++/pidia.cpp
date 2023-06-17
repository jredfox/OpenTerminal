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

string toString(bool b)
{
	return b ? "true" : "false";
}
