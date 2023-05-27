package jml.ot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import jml.ipc.pipes.PipeManager;
import jml.ot.terminal.host.ConsoleHost;
import jredfox.common.utils.FileUtil;

public class OpenTerminal {
	
	private static PrintStream boot;
	public static void open(TerminalApp app)
	{
		 boot = app.logBoot ? createBootLog(app.id) : null;//the boot logger for sanity and error checking
		
		//if open terminal has launched and failed printline and exit the application as it failed
		if(System.console() == null && OTConstants.LAUNCHED)
		{
			boot.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			boot.close();
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			boot.println("Console is non-null while forcing a new window isn't enabled!");
			System.err.println("Console is non-null while forcing a new window isn't enabled!");
			boot.close();
			return;
		}
		else if(System.getProperty("ot.bg") != null || System.getProperty("ot.background") != null)
		{
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
			if(app.logBoot)//ensure the user didn't disable this via TerminalApp#load()
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
	
	public static void logBoot(String s)
	{
		if(boot != null)
			boot.println(s);
	}
	
	public static void logBoot(Throwable t)
	{
		if(boot != null)
			t.printStackTrace(boot);
	}

	public static PrintStream createBootLog(String id)
	{
		try
		{
			File flog = new File(OTConstants.home, "logs/" + id + "/boot-" + (OTConstants.LAUNCHED ? "client" : "host") + ".txt");
			FileUtil.create(flog);
			return new PrintStream(new FileOutputStream(flog), true);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

}
