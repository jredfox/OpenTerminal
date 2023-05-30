package jml.ot.terminal;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;

/**
 * since terminology doesn't support UI for bash files it needs custom support
 * @author jredfox
 */
public class TerminologyExe extends LinuxBashExe {

	public TerminologyExe(TerminalApp app) 
	{
		super(app);
	}

	@Override
	public void run() 
	{
		String q = "'";
		String args = "-Dot.p " + OTConstants.args;//add the java pause option
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"terminology",
			"-T",
			q + this.app.getTitle() + q,
			"-2",
			"-d",
			OTConstants.userDir.getPath(),
			"-e",
			OTConstants.java_home,
			args
		});
		this.run(pb);
	}

}
