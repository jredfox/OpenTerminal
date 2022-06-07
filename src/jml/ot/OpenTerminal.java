package jml.ot;

import java.io.IOException;

import jml.ot.terminal.host.ConsoleHost;

public class OpenTerminal {
	
	public static void open(TerminalApp app) throws IOException
	{
		//if open terminal has launched and failed printline and exit the application as it failed
		if(System.console() == null && System.getProperty("ot.l") != null)
		{
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			System.out.println("console is nonnull while forcing a new window isn't enabled!");
			return;
		}
		else if(System.getProperty("ot.bg") != null)
			return;
		
		ConsoleHost console = app.getConsoleHost();
		if(console != null)
			console.run();
		else
			app.getTerminalExe().run();
	}

}
