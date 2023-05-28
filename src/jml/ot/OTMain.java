package jml.ot;

import jml.ot.colors.AnsiColors;

public class OTMain {
	
	/**
	 * call this main method directly using "java -jar OpenTerminal.jar" or "path/To/JavaRuntime/bin/java -jar OpenTerminal.jar" for the same JRE enforced on use every time
	 * @param appId
	 * @param appName
	 * @param appVersion
	 * @param forceNewWindow(boolean)
	 * @param pause(boolean)
	 * @param terminal
	 * @param consoleHost default is null
	 * @param Profile use {@link TerminalApp#getProfile#toString()} or null
	 * @param PID of the host
	 * @NOTE: USE java -cp INSTEAD OF java -jar for custom TerminalApp classes as you will get a NoClassFoundException otherwise
	 * @author jredfox
	 */
	public static void main(String[] args)
	{
		AnsiColors.enableCmdColors();//ensure ANSI colors are enabled by loading the class
		if(!OTConstants.LAUNCHED)
		{
			TerminalApp app = args.length != 0 ? new TerminalApp(args[0], args[1], args[2], Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4])) : new TerminalApp("ot", "Open Terminal", OTConstants.OTVERSION);
			OpenTerminal.open(app);
			app.manager.isRunning = false;
		}
		else
		{
			TerminalApp app = new TerminalApp("dummy", "Dummy", "1.0.0");//TODO: improve with override update
			app.loadSession();
			app.startPipeManager();
//			boolean hostIsAlive = true;
//			while(hostIsAlive)
//			{
////				//TODO:PID keep alive check here
//			}
//			JREUtil.sleep(3000);
			System.out.println(System.getProperty("ot.app"));
			
			//ensure final printlines happen before shutting down the client
			app.manager.isRunning = false;//TODO: shutdown the thread
			long time = System.currentTimeMillis();
			while(app.manager.isTicking)
			{
				if((System.currentTimeMillis() - time) > 10000)
					break;//ensure it breaks
			}
			//pause the app
			app.pause();
		}
	}

}
