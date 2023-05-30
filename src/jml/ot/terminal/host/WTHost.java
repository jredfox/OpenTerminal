package jml.ot.terminal.host;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.terminal.TerminalExe;

/**
 * Windows Terminal Console host handler
 * NOTE: doesn't support multiple panes per tab as WT is the only Console that supports it and I think it's a good idea of having one instance per tab.
 * Doesn't always create a new window if WT is configured it will just open a new tab instead of a new instance. this is so we don't override the users preferences
 */
public class WTHost extends ConsoleHost {

	public WTHost(TerminalApp app) 
	{
		super(app);
	}

	@Override
	public void run()
	{
		TerminalExe term = this.app.getTerminalExe();
		List<String> cmd = new ArrayList<>();
		boolean isCmd = this.app.terminal.equals("cmd");
		boolean supportedProfile = isCmd || this.app.terminal.equals("powershell");
		Profile p = this.app.getProfile();
		cmd.add("wt");
		if(p != null)
		{
			if(p.wtFullScreen)
				cmd.add("-F");
			if(p.wtMaximized)
				cmd.add("-M");
		}
		cmd.add("new-tab");
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
		cmd.add("--focus");
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
