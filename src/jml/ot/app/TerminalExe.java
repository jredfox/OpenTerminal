package jml.ot.app;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jml.ot.TerminalApp;
import jredfox.common.io.IOUtils;

public abstract class TerminalExe {
	
	public TerminalApp app;
	public File shell;
	
	public TerminalExe(TerminalApp app, File shell) throws IOException
	{
		this.app = app;
		this.shell = shell;
		this.genStart();//enforce the need for pr-runtime setup
	}
	
	/**
	 * your boot shell script
	 */
	public abstract void createShell() throws IOException;
	/**
	 * your starting scripts such as the start command for powershell or macOs
	 */
	public abstract void genStart() throws IOException;//ensures preset scripts like powershell's start or mac's start script and more are done before execution
	
	public void run() throws IOException
	{
		this.createShell();
	}
	
	public void makeShell(List<String> li) throws IOException 
	{
		this.makeShell(li, this.shell);
	}
	
	public void makeShell(List<String> li, File sh) throws IOException 
	{
		IOUtils.saveFileLines(li, sh, true);
		IOUtils.makeExe(sh);
	}
	
	public void printPB(ProcessBuilder pb)
	{
		for (String s : pb.command())
			System.out.print(s + " ");
		System.out.println("\b");
	}
}
