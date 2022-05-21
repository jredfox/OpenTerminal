package jml.ot.app;

import java.io.File;
import java.io.IOException;

import jml.ot.TerminalApp;

public abstract class TerminalExe {
	
	public TerminalApp app;
	public File shell;
	
	public TerminalExe(TerminalApp app) throws IOException
	{
		this.app = app;
		this.genPresets();//enforce the need for pr-runtime setup
	}
	
	public abstract void createShell() throws IOException;//create your java app's shell
	public abstract void genPresets() throws IOException;//ensures preset scripts like powershell's start or mac's start script and more are done before execution
	
	public void run() throws IOException
	{
		this.createShell();
	}
}
