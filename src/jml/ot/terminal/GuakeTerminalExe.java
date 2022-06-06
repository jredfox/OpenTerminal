package jml.ot.terminal;

import java.io.IOException;
import java.util.List;

import jml.ot.TerminalApp;

public class GuakeTerminalExe extends LinuxCmdTerminalExe {

	public GuakeTerminalExe(TerminalApp app) throws IOException 
	{
		super(app);
	}
	
	@Override
	public void run(ProcessBuilder pb) throws IOException
	{
		pb.command().add(1, "--show");
		super.run(pb);
	}

	@Override
	public List<String> getBootCmd() 
	{
		return null;//TODO: support non new window for linux terminals
	}

}
