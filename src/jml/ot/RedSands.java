package jml.ot;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RedSands {
	
	public static void main(String[] args) throws IOException, AWTException, InterruptedException
	{
//		OSUtil.findExe("gnome-terminal");
		long ms = System.currentTimeMillis();
		System.out.println(TerminalUtil.getTerminal() + " in:" + (System.currentTimeMillis() - ms) + "ms");
//		for(String s : OSUtil.linux_terminals)
//			if(!OSUtil.isTerminalValid(s))
//				System.out.println("missing:" + s);
//		System.out.println("starting redsands... " + System.getProperty("java.version"));
//		while(true)
//		{
//			try 
//			{
//				Thread.sleep(1000);
//			} 
//			catch (InterruptedException e) 
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	public static String findExe(String name)
	{
	    for (String dirname : System.getenv("PATH").split(File.pathSeparator)) 
	    {
	        File file = new File(dirname, name);
	        if (file.isFile() && file.canExecute())
	        {
	            return file.getAbsolutePath();
	        }
	    }
	    return null;
	}
	

}
