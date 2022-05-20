package jml.ot;

import java.io.IOException;

public class OTMain {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if(System.console() == null)
		{
			System.err.println("System Console is null rebooting! Use OpenTerminal#open instead of using java -jar!");
			OpenTerminal.open(new TerminalApp("ot", "Open Terminal", "1.0.0"));
		}
		else
		{
			System.out.println("booted");
			while(true)
			{

			}
		}
	}

}
