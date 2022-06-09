package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.TerminalUtil;
import jredfox.common.os.OSUtil;

public class BatchExe extends TerminalExe {

	public BatchExe(TerminalApp app) 
	{
		super(app, new File(OTConstants.boot, "boot.bat"));
	}

	@Override
	public void run()
	{
		String q = OSUtil.getQuote();
		ProcessBuilder pb = null;
		Profile profile = this.app.getProfile();
		String colors = this.getColors(profile);
		if(profile != null)
		{
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				TerminalUtil.getExeAndClose(),
				"start",
				"\"\"",//app name doesn't work with the batch boot shell for some reason
				"call",
				q + this.shell.getPath() + q,//path to the boot shell
				profile != null && profile.bg != null ? q + colors + q : q + "" + q,//color
				q + this.app.getTitle() + q,
				q + (OTConstants.java_home + " -Dot.ansi.colors=" + q + colors + q + " " + OTConstants.args).replaceAll("\"", ",") + q,
				q + this.app.pause + q
			});
		}
		else
		{
			//more optimized version and contains the java icon on boot like it should always have but the downside is there is no real pause
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				OSUtil.getExeAndClose(),
				"start " + q + this.app.getTitle() + q + " " + OTConstants.java_home + (this.app.pause ? " -Dot.p " : " ") + OTConstants.args
			});
		}
		this.run(pb);
	}
	
	@Override
	public List<String> getBootCmd()
	{
		String q = OSUtil.getQuote();
		Profile profile = this.app.getProfile();
		String colors = this.getColors(profile).replace(";", "$");
		List<String> cmd = new ArrayList<>();
		cmd.add("cmd");
		cmd.add(OSUtil.getExeAndClose());
		cmd.add("call");
		cmd.add(q + this.shell.getPath() + q);//path to the boot shell
		cmd.add(profile != null && profile.bg != null ? q + colors + q : "");
		cmd.add(q + this.app.getTitle() + q);
		cmd.add(q + (OTConstants.java_home + " -Dot.ansi.colors=" + q + colors + q + " " + OTConstants.args).replaceAll("\"", ",") + q);
		cmd.add(q + this.app.pause + q);
		return cmd;
	}

    @Override
    public void createShell() throws IOException 
    {
        if (!this.shell.exists()) 
        {
            List<String> li = new ArrayList<>();
            li.add("@Echo off\n"
                    + "set c=%~1%\n"
                    + "IF NOT \"%c%\" == \"\" (\n"
                    + "  echo=%c:$=;%\n"
                    + ")\n"
                    + "cls ::hotfix for Windows Terminal\n"
                    + "title %~2%\n"
                    + "set boot=%~3\n"
                    + "set boot=%boot:,=^\"% ::RE-MAP the boot command to double quotes\n"
                    + "call %boot%\n"
                    + "IF \"%~4%\" == \"true\" (\n"
                    + "set /p DUMMY=Press ENTER to continue...\n"
                    + ")\n"
                    + "exit 0 ::Work around from a command prompt bug");
            this.makeShell(li);
        }
    }

	@Override
	public void genStart() throws IOException 
	{
		// nothing to do here the start command works just find using java
	}

	@Override
	public void cleanup() 
	{
		this.shell.delete();
	}

}
