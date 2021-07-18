package jredfox.selfcmd;

import java.util.Scanner;

public class ConsoleLauncher {
	
	public static final String version = "3.0.0-alpha.1.0.0";
	public static ConsoleApp app = null;
	
	/**
	 * run your program with command prompt
	 */
	@SuppressWarnings("resource")
	public static void run(String appId, String appName, String appVersion, String[] args, boolean compiled, boolean hang)
	{
		app = new ConsoleApp(appId, appName, appVersion, args);
		app.process = boot(appName, args);
		
		int code = 0;
		while(app.process != null)
		{
			if(!app.process.isAlive())
			{
				code = app.process.exitValue();
				app.process = code == 22 ? boot(appId, parseBootArgs(appId)) : null;
			}
		}
		
		//exit the program
		if(hang)
		{
			Scanner scanner = new Scanner(System.in).useDelimiter("\n");//Warning says scanner is never closed but, useDelimiter returns itself
			System.out.println("Press ENTER to continue:");
			scanner.next();
		}
		System.exit(code);
	}
	
	public static void reboot()
	{
		System.exit(22);
	}

	private static Process boot(String appName, String[] command) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String[] parseBootArgs(String appId) {
		// TODO Auto-generated method stub
		return null;
	}

}
