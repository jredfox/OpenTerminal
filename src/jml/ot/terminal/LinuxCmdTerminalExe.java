package jml.ot.terminal;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;

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
		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replace("$", "\\$");
		Profile p = this.app.getProfile();
		String hd = p == null ? "" : p.getPauseMsg();
		String lowRes = p == null ? "" : p.getPauseLowResMsg();
		String trueColor = this.app.getBootTrueColor(p);
		String platteColor = this.app.getBootPaletteColor(p);
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			this.app.terminal,
			this.app.getLinuxExe(),
			"bash " + q + this.shell.getPath() + q + " " +
			q + trueColor + q + " " + q + platteColor + q + " " + q + this.app.getTitle() + q + " " + q + OTConstants.userDir.getPath() 
			+ q + " " + q + command + q + " " + q + this.app.pause + q + " " + q + hd + q + " " + q + lowRes + q
		});
		this.run(pb);
	}

}
