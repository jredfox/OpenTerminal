package jml.ot;

import java.io.PrintStream;
import java.util.Scanner;

import jml.ot.terminal.host.ConsoleHost;
import jml.ot.threads.OTDSPThread;
import jml.ot.threads.OTSPThread;

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
		
		boolean forcedWindow = false;
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
		//nullify output of forceCLI mode so the end user doesn't get confused on which window is which
		else if(System.console() != null && !OTConstants.LAUNCHED)
		{
			setCtrStream();
			forcedWindow = app.replaceSYSO;//allow non main CLI windows to not cause blank output
		}
		
		try
		{
			//disable out during launch but let boot errors print to the first CLI
			if(forcedWindow)
			{
				System.out.println("Don't Close this window or it will shutdown the program! Launched:" + app.getTitle() + " id:" + app.id);
				ctrOut.setEnabled(false);
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
			if(forcedWindow)
			{
				ctrOut.setEnabled(false);
				ctrErr.setEnabled(false);
			}
		}
	}
	
	public static void setCtrStream() 
	{
		if(ctrOut == null)
		{
			ctrOut = new ControlStream(System.out);
			ctrErr = new ControlStream(System.err);
			System.setOut(ctrOut);
			System.setErr(ctrErr);
		}
		else
		{
			ctrOut.setEnabled(true);
			ctrErr.setEnabled(true);
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
	protected static ControlStream ctrOut;
	protected static ControlStream ctrErr;
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
