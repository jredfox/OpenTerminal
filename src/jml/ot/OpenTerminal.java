package jml.ot;

import java.io.PrintStream;
import java.util.Scanner;

import jml.ipc.pipes.WrappedPrintStream;
import jml.ot.terminal.host.ConsoleHost;
import jml.ot.threads.OTDSPThread;
import jml.ot.threads.OTSPThread;
import jredfox.common.io.NullOutputStream;

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
		
		if(System.getProperty("ot.bg") != null || System.getProperty("ot.background") != null)
		{
			app.logBoot("Running in the background...");//don't printstream only log it
			app.load(false);
			app.pause = false;//disable pauses we are running in the background
			app.softPause = false;
			boot.close();
			return;
		}
		//if open terminal has launched and failed printline and exit the application as it failed
		else if(System.console() == null && OTConstants.LAUNCHED)
		{
			app.logBoot("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			boot.close();
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			app.logBoot("Console is already opened. Loading TerminalApp with IPC disabled");
			app.load(false);
			setPauseThread(app);
			boot.close();
			return;
		}
		
		try
		{
			//nullify output of forceCLI mode so the end user doesn't get confused on which window is which
			if(System.console() != null && !OTConstants.LAUNCHED && !hasNullified)
			{
				System.out.println("Launched:" + app.getTitle() + " Do Not close this window or it will close the app");
				PrintStream nullified = new PrintStream(new NullOutputStream());
				System.setOut(nullified);
				System.setErr(nullified);
				hasNullified = true;
			}
			
			app.load(true);
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
			System.err.print("OpenTerminal boot has failed! ");
			t.printStackTrace();
			if(app.canLogBoot)//ensure the user didn't disable this via TerminalApp#load()
			{
				app.logBoot("OpenTerminal boot has failed! ", false);
				t.printStackTrace(boot);
			}
			System.exit(-1);
		}
		finally
		{
			boot.close();
		}
	}
	
	/**
	 * open your TerminalApp and grab args before executing your main(String[] args)
	 * @return the new arguments
	 */
	public static String[] openWithArgs(TerminalApp app, String msg, String[] initArgs)
	{
		open(app);
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		boolean newArgs = initArgs.length == 0 || initArgs[0] == null || initArgs[0].isEmpty();
		if(newArgs)
			System.out.print(msg);
		return newArgs ? TerminalUtil.parseCommand(scanner.nextLine()) : initArgs;
	}

	protected static OTDSPThread dp;
	protected static OTSPThread sp;
	protected static final Thread main = Thread.currentThread();
	protected static boolean hasNullified;
	protected static void setPauseThread(TerminalApp app) 
	{
		//disabled shell pause
		if(dp == null)
		{
			dp = new OTDSPThread(app);
			Runtime.getRuntime().addShutdownHook(dp);
		}
		else
			dp.app = app;
		
		//soft pause
		if(sp == null)
		{
			sp = new OTSPThread(app, main);
			sp.start();
		}
		else
			sp.app = app;
	}

}
