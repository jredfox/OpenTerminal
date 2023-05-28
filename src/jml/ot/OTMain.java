package jml.ot;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import jml.ot.colors.AnsiColors;
import jml.reflect.ObfString;
import jml.reflect.ReflectionHandler;
import jredfox.common.utils.JREUtil;

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
	public static void main(String[] args) throws IOException, InterruptedException
	{
		AnsiColors.enableCmdColors();//ensure ANSI colors are enabled by loading the class
		if(System.console() == null)
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
//			new ProcessBuilder("cmd", "/c", "color 2f").inheritIO().start().waitFor();
//			new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor();
//			System.out.println("booted:" + OTConstants.userDir);
//			Test.printTest(AnsiColors.INSTANCE);
			
			boolean hostIsAlive = true;
//			while(hostIsAlive)
//			{
////				//TODO:PID keep alive check here
//			}
//			Map<String,String> c = ReflectionHandler.get(ReflectionHandler.getField(ReflectionHandler.getClass("java.lang.ProcessEnvironment"), new ObfString("theCaseInsensitiveEnvironment", "theCaseInsensitiveEnvironment")), null);
//			for(String s : c.keySet())
//			{
//				System.out.println(s);
//			}
			System.out.println("A:" + System.getenv("$color") + " " + System.getenv("color") + " " + System.getenv("color"));
			JREUtil.sleep(3000);
			
			//ensure final printlines happen before shutting down the client
			app.manager.isRunning = false;//TODO: shutdown the thread
			long time = System.currentTimeMillis();
			while(app.manager.isTicking)
			{
				if((System.currentTimeMillis() - time) > 10000)
					break;//ensure it breaks
			}
			
			//java pause for non shell terminals. should be safe to do as OpenTerminal is a separate process
			if(System.getProperty("ot.p") != null)
			{
				System.out.print("Press ENTER to continue...");
				new Scanner(System.in).nextLine();
			}
		}
	}

}
