package jml.ot;

import java.io.PrintStream;

import jml.ipc.pipes.PipeManager;
import jml.ot.terminal.host.ConsoleHost;

public class OpenTerminal {
	
	public static void open(TerminalApp app)
	{
		 PrintStream boot = app.getBootLogger();//the boot logger for sanity and error checking
		 if(app.canLogBoot && boot == null)
		 {
			 System.err.println("Boot Logger is NULL while being enabled TerminalApp:" + app.id);
			 return;
		 }
		
		//if open terminal has launched and failed printline and exit the application as it failed
		if(System.console() == null && OTConstants.LAUNCHED)
		{
			if(app.canLogBoot)
				boot.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			boot.close();
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			if(app.canLogBoot)
				boot.println("Console is non-null while forcing a new window isn't enabled!");
			System.err.println("Console is non-null while forcing a new window isn't enabled!");
			boot.close();
			return;
		}
		else if(System.getProperty("ot.bg") != null || System.getProperty("ot.background") != null)
		{
			if(app.canLogBoot)
				boot.println("Running in the background...");//don't printstream only log it
			boot.close();
			return;
		}
		
		try
		{
			app.load();
			startPipes();
			ConsoleHost console = app.getConsoleHost();
			if(console != null)
				console.run();
			else
				app.getTerminalExe().run();
		}
		catch(Throwable t)
		{
			if(app.canLogBoot)//ensure the user didn't disable this via TerminalApp#load()
				t.printStackTrace(boot);
		}
		finally
		{
			boot.close();
		}
	}

	public static PipeManager manager;
	public static void startPipes()
	{
		manager = new PipeManager();//setup IPC pipes
		manager.loadPipes();
		manager.start();
	}

}
