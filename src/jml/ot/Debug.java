package jml.ot;

import java.io.IOException;

public class Debug {

	public static void main(String[] args) throws InterruptedException, IOException
	{
		ProcessBuilder pb = new ProcessBuilder(new String[] {"osascript", "/Users/jredfox/Desktop/profileAndImport.scpt", "Red Sands", ""});
		pb.inheritIO().start().waitFor();
	}
	
}
