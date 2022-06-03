package jml.ot;

import java.io.IOException;
import java.util.Scanner;

public class OTMain {
	
	/**
	 * call this main method directly using "java -jar OpenTerminal.jar" or "path/To/JavaRuntime/bin/java -jar OpenTerminal.jar" for the same JRE enforced on use everytime
	 * @param appId
	 * @param appName
	 * @param appVersion
	 * @param forceNewWindow(boolean)
	 * @param pause(boolean)
	 * @param terminal
	 * @param consoleHost default is null
	 * @param Profile use {@link TerminalApp#getProfile#toString()} or null
	 * @param PID of the host
	 * @category NOTE: this doesn't support custom terminal app classes nor can it due to the fact it's an external jar with no deps. use {@link OpenTerminal#open(TerminalApp)} for custom terminal apps
	 * @author jredfox
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if(System.console() == null)
		{
			TerminalApp app = args.length != 0 ? new TerminalApp(args[0], args[1], args[2], Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4])) : new TerminalApp("ot", "Open Terminal", OTConstants.OTVERSION);
			OpenTerminal.open(app);
		}
		else
		{
//			new ProcessBuilder("cmd", "/c", "color 2f").inheritIO().start().waitFor();
//			new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor();
			System.out.println("booted:" + OTConstants.userDir);
//			new Scanner(System.in).nextLine();
		}
	}

}
