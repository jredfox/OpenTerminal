package jml.ot;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException 
	{
		long ms = System.currentTimeMillis();
		OpenTerminal.open(new TerminalApp("test", "MF POWERSHELL", "1.0.0"));
		System.out.println("launch in:" + (System.currentTimeMillis() - ms) + "ms");
	}

}
