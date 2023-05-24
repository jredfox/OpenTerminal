package jml.ot;

import jml.ipc.pipes.PipeManager;
import jml.ot.terminal.host.ConsoleHost;

public class OpenTerminal {
	
	public static void open(TerminalApp app)
	{
		//if open terminal has launched and failed printline and exit the application as it failed
		if(System.console() == null && OTConstants.LAUNCHED)
		{
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			System.out.println("Console is non-null while forcing a new window isn't enabled!");
			return;
		}
		else if(System.getProperty("ot.bg") != null || System.getProperty("ot.background") != null)
			return;
		
		app.load();
		startPipes();
		ConsoleHost console = app.getConsoleHost();
		if(console != null)
			console.run();
		else
			app.getTerminalExe().run();
	}

	public static PipeManager manager;
	public static void startPipes()
	{
		manager = new PipeManager();//setup IPC pipes
		manager.loadPipes();
		manager.start();
	}

}
