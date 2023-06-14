package jredfox.common.pida;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import jmln.PID;

public class ShutdownHooks extends SecurityManager {
	
	public static List<ShutdownThread> shutdowns = new ArrayList<>(1);
	public static int EXIT_CODE = 0;
	public static enum SIGNAL
	{
		//start windows signals
		CTRL_C_EVENT(),
		CTRL_BREAK_EVENT(),
		CTRL_CLOSE_EVENT(),
		CTRL_LOGOFF_EVENT(),
		CTRL_SHUTDOWN_EVENT()
		//start POSIX shutdown codes //TODO:
	}
	
	static
	{
		System.setSecurityManager(new ShutdownHooks());
		PID.l();//loads PIDIA lib and adds the shutdown hook natively
	}
	
	public static void addShutDownHook(ShutdownThread st)
	{
		shutdowns.add(st);
		Runtime.getRuntime().addShutdownHook(st);
	}
	
	public static void shutdownWindows(int signal_code)
	{
		SIGNAL signal = SIGNAL.values()[signal_code];//windows signals unlike POSIX will always be the same codes across CPUS
		//shutdown normally
		if(signal == SIGNAL.CTRL_C_EVENT || signal == SIGNAL.CTRL_CLOSE_EVENT)
		{
			shutdown(signal);
		}
		//dump core and shutdown
		else if(signal == SIGNAL.CTRL_BREAK_EVENT)
		{
			coreDump(signal);
			shutdown(signal);
		}
		//Quickly terminate the process
		else
		{
			terminate(signal);
		}
	}
	
	private static void shutdown(SIGNAL exitCode)
	{
		for(ShutdownThread t : shutdowns)
			t.shutdown(0);
	}
	
	private static void terminate(SIGNAL signal) 
	{
		for(ShutdownThread t : shutdowns)
			t.terminate(-1);
	}

	private static void coreDump(SIGNAL exitCode) 
	{
		for(ShutdownThread t : shutdowns)
			t.dumpcore(0);
	}

	@Override
	public void checkExit(int status) 
	{
		EXIT_CODE = status;
	}
	
	@Override
	public void checkPermission(Permission perm) {}
	@Override
	public void checkPermission(Permission perm, Object context) {}

}
