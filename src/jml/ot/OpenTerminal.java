package jml.ot;

import java.io.PrintStream;

import jml.ot.terminal.host.ConsoleHost;

public class OpenTerminal {
	
	public static void open(TerminalApp app)
	{
		 long ms = System.currentTimeMillis();
		 PrintStream boot = app.createBootLogger();
		 if(app.canLogBoot && boot == null)
		 {
			 System.err.println("Boot Logger is NULL while being enabled TerminalApp:" + app.id);
			 return;
		 }
		//log JRE & JRE VENDOR & OS
		 app.logBoot("JAVA:\t" + System.getProperty("java.version") + "\tJAVA VENDOR:" + System.getProperty("java.vendor") + "\tJAVAHOME:" + System.getProperty("java.home")
		 + "\n" + TerminalUtil.osName + "\tVERSION:" + System.getProperty("os.version") + "\tCPU-ISA(OS-ARCH):" + System.getProperty("os.arch") + "\n");
		
		//if open terminal has launched and failed printline and exit the application as it failed
		if(System.console() == null && OTConstants.LAUNCHED)
		{
			app.logBoot("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			boot.close();
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			app.logBoot("Console is non-null while forcing a new window isn't enabled!");
			System.err.println("Console is non-null while forcing a new window isn't enabled!");
			boot.close();
			return;
		}
		else if(System.getProperty("ot.bg") != null || System.getProperty("ot.background") != null)
		{
			app.logBoot("Running in the background...");//don't printstream only log it
			boot.close();
			return;
		}
		
		try
		{
			app.load();
			app.logBoot("TerminalApp Session Started On:\t" + app.session);
			ConsoleHost console = app.getConsoleHost();
			if(console != null)
				console.run();
			else
				app.getTerminalExe().run();
			app.logBoot("boot took:" + (System.currentTimeMillis()-ms));
			app.loadColors();//sync colors here
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

}
