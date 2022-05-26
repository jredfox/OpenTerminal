package jml.ot.terminal.host;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.terminal.TerminalExe;

public class WTHost extends ConsoleHost {

	public WTHost(TerminalApp app) 
	{
		super(app);
	}

	@Override
	public void run() throws IOException
	{
		TerminalExe term = this.app.getTerminalExe();
		List<String> cmd = new ArrayList<>();
		boolean isCmd = this.app.terminal.equals("cmd");
		boolean supportedProfile = isCmd || this.app.terminal.equals("powershell");
		cmd.add("wt");
		cmd.add("new-tab");
		Profile p = this.app.getProfile();
		if(p != null)
		{
			if(p.wtScheme != null)
			{
				cmd.add("--colorScheme");
				cmd.add("\"" + this.app.getProfile().wtScheme + "\"");
			}
			if(p.wtTab != null)
			{
				cmd.add("--tabColor");
				cmd.add("#" + p.wtTab);
			}
		}
		cmd.add("-f");
		cmd.add("--title");
		cmd.add("\"" + this.app.getTitle() + "\"");
		cmd.add("-d");
		cmd.add("\"" + new File("").getAbsolutePath() + "\"");
		if(supportedProfile)
		{
			cmd.add("-p");
			cmd.add(isCmd ? "\"Command Prompt\"" : "\"Windows PowerShell\"");
		}
		cmd.addAll(term.getBootCmd());
		term.run(new ProcessBuilder(cmd));
	}

}
