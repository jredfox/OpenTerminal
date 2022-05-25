package jml.ot.app.host;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		TerminalExe term = this.app.getTerminal();
		List<String> cmd = new ArrayList<>();
		boolean isCmd = this.app.terminal.equals("cmd");
		cmd.add("wt");
		cmd.add("new-tab");
		if(this.app.getProfile() != null && this.app.getProfile().wtScheme != null)
		{
			cmd.add("--colorScheme");
			cmd.add("\"" + this.app.getProfile().wtScheme + "\"");
		}
		cmd.add("-f");
		cmd.add("--title");
		cmd.add("\"" + this.app.getTitle() + "\"");
		cmd.add("-d");
		cmd.add("\"" + new File("").getAbsolutePath() + "\"");
		cmd.add("-p");
		cmd.add(isCmd ? "\"Command Prompt\"" : "\"Windows PowerShell\"");//TODO: get dynamic profiles for custom terminals and custom wt json profiles
		cmd.addAll(term.getBootCmd());
		term.run(new ProcessBuilder(cmd));
	}

}
