package jml.ot.terminal;

import jml.ot.TerminalApp;

public class GuakeTerminalExe extends LinuxCmdTerminalExe {

	public GuakeTerminalExe(TerminalApp app) 
	{
		super(app);
	}
	
	@Override
	public void run(ProcessBuilder pb)
	{
		pb.command().add(1, "--show");
		super.run(pb);
	}

}
