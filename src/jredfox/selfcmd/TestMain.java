package jredfox.selfcmd;

import java.io.File;
import java.io.IOException;

import jredfox.common.log.printer.LogPrinter;
import jredfox.common.log.printer.Printer;

public class TestMain {
	
	public static void main(String[] args) throws IOException
	{
		args = SelfCommandPrompt.runWithCMD("test_app", "Test App", args);
		Printer l = new LogPrinter(new File("log- " + System.currentTimeMillis() + ".txt"), System.out, System.err, false, true);
		l.load();
		long ms = System.currentTimeMillis();
		while(System.currentTimeMillis() - ms < 1000)
		{
			;
		}
		
		if(args.length == 0)
		{
			System.out.println("rebooting");
			SelfCommandPrompt.reboot(new String[]{""});
		}
		System.out.println("finished");
	}

}
