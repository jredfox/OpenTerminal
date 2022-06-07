package jml.ot.terminal;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;

/**
 * for the terminals that can't handle pre-parsed arguments
 */
public class LinuxCmdTerminalExe extends LinuxBashExe {

	public LinuxCmdTerminalExe(TerminalApp app) 
	{
		super(app);
	}
	
	@Override
	public void run() 
	{
		String q = "'";
		String command = (OTConstants.java_home + " " + OTConstants.args);
		ProcessBuilder pb = new ProcessBuilder(new String[] 
		{
			this.app.terminal,
			this.app.getLinuxExe(),
			"bash " + q + this.shell.getPath() + q + " " + q + this.app.getTitle() + q + " " + q + OTConstants.userDir.getPath() 
			+ q + " " + q + command + q + " " + q + this.app.pause + q
		});
		this.run(pb);
	}

}
