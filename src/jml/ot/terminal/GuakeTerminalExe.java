package jml.ot.terminal;

import java.io.IOException;
import java.util.List;

import jml.ot.OSUtil;
import jml.ot.OTConstants;
import jml.ot.TerminalApp;

public class GuakeTerminalExe extends LinuxBashExe{

	public GuakeTerminalExe(TerminalApp app) throws IOException 
	{
		super(app);
	}

	@Override
	public void run() throws IOException 
	{
		String q = OSUtil.getQuote();
		String q2 = "'";
		String command = (OTConstants.java_home + " " + OTConstants.args);
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			this.app.terminal,
			"--show",
			OSUtil.getLinuxNewWin(this.app.terminal),
			"bash " + q + this.shell.getPath() + q + " " + q + this.app.getTitle() + q + " " + q + OTConstants.userDir.getPath() + q + " " + q + command + q + " " + q + String.valueOf(this.app.pause) + q
		});
		this.run(pb);
	}

	@Override
	public List<String> getBootCmd() throws IOException 
	{
		return null;//TODO: support non new window for linux terminals
	}

}
