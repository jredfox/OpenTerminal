package jml.ot;

import java.io.IOException;
import java.util.Scanner;

public class OTMain {
	
	/**
	 * call this main method directly using java -jar OpenTerminal.jar
	 * @param appId
	 * @param appName
	 * @param appVersion
	 * @param boolean forceNewWindow
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if(System.console() == null)
		{
			TerminalApp app = args.length != 0 ? new TerminalApp(args[0], args[1], args[2], Boolean.parseBoolean(args[3])) : new TerminalApp("ot", "Open Terminal", OTConstants.OTVERSION);
			OpenTerminal.open(app);
		}
		else
		{
			System.out.println("booted:" + OTConstants.userDir);
//			while(true)
//			{
//				
//			}
//			System.out.println("Press \"ENTER\" to continue");
			new Scanner(System.in).nextLine();
		}
	}

}
