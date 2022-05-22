package jml.ot;

import java.io.IOException;
import java.util.Scanner;

public class OTMain {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if(System.console() == null)
			OpenTerminal.open(new TerminalApp("ot", "Open Terminal", "1.0.0"));
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
