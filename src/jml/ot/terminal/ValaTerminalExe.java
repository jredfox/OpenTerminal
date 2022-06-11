package jml.ot.terminal;

import jml.ot.TerminalApp;

public class ValaTerminalExe extends LinuxCmdTerminalExe{

	public ValaTerminalExe(TerminalApp app) {
		super(app);
	}
	
	/**
	 * make the fontsize 14 pixels
	 */
	@Override
	public void run(ProcessBuilder pb)
	{
		pb.command().add(1, "14");
		pb.command().add(1, "-fs");
		super.run(pb);
	}

}
