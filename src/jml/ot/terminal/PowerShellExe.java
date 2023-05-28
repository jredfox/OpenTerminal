package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.TerminalUtil;

public class PowerShellExe extends TerminalExe {

	public static final File start_ps = new File(OTConstants.start, "powershell.ps1");
	
	public PowerShellExe(TerminalApp app)
	{
		super(app, new File(OTConstants.boot, "boot.ps1"));
	}

	@Override
	public void run() 
	{
		String q = TerminalUtil.getQuote();
		String colors = this.app.getBootTrueColor(this.app.getProfile()).replace(";", "$");
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"powershell",
			"-ExecutionPolicy",
			"Bypass",
			"-File",
			q + start_ps + q,
			"-boot",
			q + this.shell + q,
			"-colors",
			q + colors + q,
			"-title",
			q + this.app.getTitle() + q,
			"-java_home",
			OTConstants.java_home,
			"-java_args",
			q + this.getJVMFlags() + " " + OTConstants.args.replaceAll(q, "'").replace(";", "$") + q,
			"-pause",
			q + true + q,
		});
		this.run(pb);
	}
	
	@Override
	public List<String> getBootCmd() 
	{
		String q = TerminalUtil.getQuote();
		String q2 = "'";
		Profile p = this.app.getProfile();
		String colors = this.app.getBootTrueColor(p).replace(";", "$");
		List<String> cmd = new ArrayList<>();
		cmd.add("powershell");
		cmd.add("-ExecutionPolicy");
		cmd.add("Bypass");
		cmd.add("-File");
		cmd.add(q + this.shell + q);
		cmd.add("-colors");
		cmd.add(q + colors + q);
		cmd.add("-title");
		cmd.add(q + this.app.getTitle() + q);
		cmd.add("-java_home");
		cmd.add(OTConstants.java_home);
		cmd.add("-java_args");
		cmd.add(q + this.getJVMFlags() + " " + OTConstants.args.replaceAll(q, q2).replace(";", "$") + q);
		cmd.add("-pause");
		cmd.add(q + false + q);//do not use write-host clears the coloring and there is no way to work around it for true color or custom ansi formatting
		return cmd;
	}
	
    @Override
    public void createShell() throws IOException
    {
        if(!this.shell.exists())
        {
            List<String> li = new ArrayList<>();
            li.add("param(\n"
                    + "[Parameter(Mandatory = $true)] $colors,\n"
                    + "[Parameter(Mandatory = $true)] $title,\n"
                    + "[Parameter(Mandatory = $true)] $java_home,\n"
                    + "[Parameter(Mandatory = $true)] $java_args,\n"
                    + "[Parameter(Mandatory = $true)] $pause\n"
                    + ")\n"
                    + "$colors = $colors.Replace(\"$\", \";\")\n"
                    + "Write-Host -NoNewLine \"$colors\"\n"
                    + "cls\n"
                    + "$host.UI.RawUI.WindowTitle = \"$title\"\n"
                    + "$java_args = $java_args.Replace(\"'\", \"\"\"\").Replace(\"$\", \";\")\n"
                    + "Start-Process -Wait -NoNewWindow $java_home -ArgumentList $java_args\n"
                    + "if($pause -eq \"true\")\n"
                    + "{\n"
                    + "    Write-Host -NoNewline (\"$colors\" + \"Press ENTER to continue...\")\n"
                    + "    Read-Host\n"
                    + "}");
            this.makeShell(li);
        }
    }

    @Override
    public void genStart() throws IOException 
    {
        if (!start_ps.exists())
        {
            List<String> list = new ArrayList<>();
            list.add("param(\n"
                    + "[Parameter(Mandatory = $true)] $boot,\n"
                    + "[Parameter(Mandatory = $true)] $colors,\n"
                    + "[Parameter(Mandatory = $true)] $title,\n"
                    + "[Parameter(Mandatory = $true)] $java_home,\n"
                    + "[Parameter(Mandatory = $true)] $java_args,\n"
                    + "[Parameter(Mandatory = $true)] $pause\n"
                    + ")\n"
                    + "start-process powershell -ArgumentList '-ExecutionPolicy', 'Bypass', '-File', \"\"\"$boot\"\"\", '-colors', \"\"\"$colors\"\"\", '-title', \"\"\"$title\"\"\", '-java_home', \"\"\"$java_home\"\"\", '-java_args', \"\"\"$java_args\"\"\", '-pause', \"\"\"$pause\"\"\"");
            this.makeShell(list, start_ps);
        }
    }

	@Override
	public void cleanup()
	{
		this.shell.delete();
	}

	@Override
	public String getJVMFlags()
	{
		return "-Dot.w=false " + (this.app.pause ? "-Dot.p " : "") + super.getJVMFlags0();
	}

}
