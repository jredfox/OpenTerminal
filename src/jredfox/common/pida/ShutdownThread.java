package jredfox.common.pida;

public abstract class ShutdownThread extends Thread {
	public volatile boolean hasFired;
	/**
	 * fired when the program is signaled to shutdown or {@link System#exit(int)} has been called. will only be called once even if {@link System#exit(int)} gets called during a shutdown signal
	 */
	public abstract void shutdown(int exitCode);
	/**
	 * fired when the program terminates there may be a timer before the program forcibly closes
	 */
	public abstract void terminate(int exitCode);
	/**
	 * fired when a terminate code wants a coredump/crash SIGNAL is sent like CONTROL+BREAK
	 */
	public abstract void dumpcore(int exitCode);
	
	@Override
	public void run()
	{
		if(!hasFired)
			this.shutdown(ShutdownHooks.EXIT_CODE);
		this.hasFired = true;
	}

}
