package jml.ot.app.host;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OpenTerminal;
import jml.ot.TerminalApp;
import jml.ot.app.TerminalExe;

public class WTHost extends ConsoleHost {

	public WTHost(TerminalApp app) 
	{
		super(app);
	}

	@Override
	public void run() throws IOException
	{
		TerminalExe term = this.app.getTerminal(OpenTerminal.terminal);
		List<String> cmd = new ArrayList<>();
		cmd.add("wt");
		cmd.add("new-tab");
		cmd.add("-f");
		cmd.add("--title");
		cmd.add("\"" + this.app.getTitle() + "\"");
		cmd.add("-d");
		cmd.add("\"" + new File("").getAbsolutePath() + "\"");
		cmd.add("-p");
		cmd.add("\"Command Prompt\"");
		cmd.addAll(term.getBootCmd());//TODO: get proper profiling from windows terminal dynamically instead of having a huge switchlist
		term.run(new ProcessBuilder(cmd));
	}

}
