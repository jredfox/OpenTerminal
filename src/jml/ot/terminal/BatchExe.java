package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.TerminalUtil;

public class BatchExe extends TerminalExe {

	public File wtShell = new File(OTConstants.boot, "boot-wt.bat");
	public BatchExe(TerminalApp app) 
	{
		super(app, new File(OTConstants.boot, "boot.bat"));
	}

	@Override
	public void run()
	{
		String q = TerminalUtil.getQuote();
		ProcessBuilder pb = null;
		Profile profile = this.app.getProfile();
		if(profile != null || this.app.pause)
		{
			String pmsg = profile != null ? profile.getPauseMsg() : "";
			String colors = this.app.getBootTrueColor(profile);
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				TerminalUtil.getExeAndClose(),
				"start",
				"\"\"",//app name doesn't work with the batch boot shell for some reason
				"call",
				q + this.shell.getPath() + q,//path to the boot shell
				q + (colors.isEmpty() ? " " : colors) + q,//leave the space here it's to prevent command call bug
				q + this.app.getTitle() + q,
				q + (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replaceAll("\"", ",") + q,
				q + this.app.pause + q,
				q + pmsg + q
			});
		}
		else
		{
			//more optimized version and contains the java icon on boot like it should always have but the downside is there is no real pause
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				TerminalUtil.getExeAndClose(),
				"start " + q + this.app.getTitle() + q + " " + OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args,
			});
		}
		this.run(pb);
	}
	
	@Override
	public List<String> getBootCmd()
	{
		String q = TerminalUtil.getQuote();
		Profile profile = this.app.getProfile();
		String colors = this.app.getBootTrueColor(profile).replace(";", "$");//the character "$" is actually illegal in batch file params " " but somehow it's fine with WT
		String pmsg = profile != null ? profile.getPauseMsg().replace(";", "$") : "";
		List<String> cmd = new ArrayList<>();
		cmd.add("cmd");
		cmd.add(TerminalUtil.getExeAndClose());
		cmd.add("call");
		cmd.add(q + this.wtShell.getPath() + q);//path to the safe boot shell for Windows BUGGY terminal
		cmd.add(q + (colors.isEmpty() ? " " : colors) + q);
		cmd.add(q + this.app.getTitle() + q);
		cmd.add(q + (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replaceAll("\"", ",").replace(";", "$") + q);
		cmd.add(q + this.app.pause + q);
		cmd.add(q + pmsg + q);
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
                    + "IF NOT \"%c%\" == \" \" (\n"
                    + "  set c=%c:$=;%\n"
                    + "  echo=%c%\n"
                    + "  cls ::hotfix for Windows Terminal\n"
                    + ")\n"
                    + "title %~2%\n"
                    + "set boot=%~3%\n"
                    + "set boot=%boot:,=^\"% ::RE-MAP the boot command to double quotes\n"
                    + "set boot=%boot:$=;\"% ::Work around for WT\n"
                    + "set boot=%boot:@=$%\n"
                    + "call %boot%\n"
                    + "set pmsg=%~5%\n"
                    + "IF \"%pmsg%\" == \"\" set pmsg=Press ENTER to continue...\n"
                    + "set pmsg=%pmsg%%c%\n"
                    + "IF \"%~4%\" == \"true\" (\n"
                    + "set /p DUMMY=%pmsg%\n"
                    + ")\n"
                    + "exit 0 ::Work around from a command prompt bug");
            this.makeShell(li);
        }
        if (!this.wtShell.exists()) 
        {
            List<String> li = new ArrayList<>();
            li.add("@Echo off\n"
                    + "set c=%~1%\n"
                    + "set c=%c:$=;%\n"
                    + "set clear=[H[2J[3J\n"
                    + "<nul set /p hj=%c%%clear%\n"
                    + "title %~2%\n"
                    + "set boot=%~3%\n"
                    + "set boot=%boot:,=^\"% ::RE-MAP the boot command to double quotes\n"
                    + "set boot=%boot:$=;\"% ::Work around for WT\n"
                    + "set boot=%boot:@=$%\n"
                    + "call %boot%\n"
                    + "set pmsg=%~5%\n"
                    + "IF \"%pmsg%\" == \"\" set pmsg=Press ENTER to continue...\n"
                    + "set pmsg=%pmsg:$=;%\n"
                    + "set pmsg=%pmsg%%c%\n"
                    + "IF \"%~4%\" == \"true\" (\n"
                    + "set /p DUMMY=%pmsg%\n"
                    + ")\n"
                    + "exit 0 ::Work around from a command prompt bug");
            this.makeShell(li, this.wtShell);
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
		this.wtShell.delete();
	}

	@Override
	public String getJVMFlags() 
	{
		return "-Dot.w=true " + super.getJVMFlags();
	}

}
