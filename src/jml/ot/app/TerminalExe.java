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
	}
	/**
	 * your boot shell script
	 */
	public abstract void createShell() throws IOException;
	/**
	 * your starting scripts such as the start command for powershell or macOs
	 */
	public abstract void genStart() throws IOException;//ensures preset scripts like powershell's start or mac's start script and more are done before execution
	/**
	 * execute the command in a new terminal window
	 */
	public abstract void run() throws IOException;
	/**
	 * get's the boot command used by a console host instead of forcing a new window through other means
	 */
	public abstract List<String> getBootCmd() throws IOException;
	
	/**
	 * run the process builder with genStart and the boot shell checks
	 */
	public void run(ProcessBuilder pb) throws IOException
	{
		this.genStart();
		this.createShell();
//		this.printPB(pb);
		pb.start();
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
