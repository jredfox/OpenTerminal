package jredfox.selfcmd;

import java.io.IOException;

public class TestMain {
	
	public static void main(String[] args) throws IOException
	{
		args = SelfCommandPrompt.runWithCMD("test_app", "Test App", args);
		System.out.println("done");
//		long ms = System.currentTimeMillis();
//		while(System.currentTimeMillis() - ms < 1000)
//		{
//			;
//		}
//		
//		if(args.length == 0)
//		{
//			System.out.println("rebooting");
//			SelfCommandPrompt.reboot(new String[]{""});
//		}
	}

}
