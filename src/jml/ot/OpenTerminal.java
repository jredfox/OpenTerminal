package jml.ot;

import java.io.File;
import java.io.IOException;

import jml.ot.terminal.host.ConsoleHost;

public class OpenTerminal {
	
	public static final String console_host = "";//"wt";
	public static final String terminal = "tilda";
	
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

    /**
     * enforces it to run in the command prompt terminal as sometimes it doesn't work without it
     */
    public static Process runInTerminal(String terminal, String flag, String command, File dir) throws IOException
    {
        return run(new String[]{terminal, flag, command}, dir);
    }
	
    public static Process run(String[] cmdarray, File dir) throws IOException
    {
        return new ProcessBuilder(cmdarray).directory(dir).start();
    }

}
