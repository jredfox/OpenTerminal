package jml.ot.terminal;

import java.io.IOException;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;

/**
 * since terminology doesn't support UI for bash files it needs custom support
 * @author jredfox
 */
public class TerminologyExe extends TerminalExe{

	public TerminologyExe(TerminalApp app) throws IOException 
	{
		super(app, OTConstants.nullFile);
	}

	@Override
	public void createShell() throws IOException {}

	@Override
	public void genStart() throws IOException {}

	@Override
	public void run() throws IOException 
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

	@Override
	public List<String> getBootCmd() throws IOException
	{
		return null;
	}

}
