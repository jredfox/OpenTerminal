package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.TerminalUtil;
import jml.ot.OTConstants;
import jml.ot.TerminalApp;

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
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"powershell",
			"-ExecutionPolicy",
			"Bypass",
			"-File",
			q + start_ps + q,
			"-boot",
			q + this.shell + q,
			"-title",
			q + this.app.getTitle() + q,
			"-java_home",
			OTConstants.java_home,
			"-java_args",
			q + OTConstants.args.replaceAll(q, "'") + q,
			q + this.app.pause + q,
		});
		this.run(pb);
	}
	
	@Override
	public List<String> getBootCmd() 
	{
		String q = TerminalUtil.getQuote();
		List<String> cmd = new ArrayList<>();
		cmd.add("powershell");
		cmd.add("-ExecutionPolicy");
		cmd.add("Bypass");
		cmd.add("-File");
		cmd.add(q + this.shell + q);
		cmd.add("-title");
		cmd.add(q + this.app.getTitle() + q);
		cmd.add("-java_home");
		cmd.add(OTConstants.java_home);
		cmd.add("-java_args");
		cmd.add(q + OTConstants.args.replaceAll(q, "'") + q);
		cmd.add(q + this.app.pause + q);
		return cmd;
	}
	
	@Override
	public void createShell() throws IOException
	{
		if(!this.shell.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("param(");
			li.add("[Parameter(Mandatory = $true)] $title,");
			li.add("[Parameter(Mandatory = $true)] $java_home,");
			li.add("[Parameter(Mandatory = $true)] $java_args");
			li.add(")");
			li.add("$host.UI.RawUI.WindowTitle = \"$title\"");
			li.add("$java_args = $java_args.Replace(\"'\", \"\"\"\")");
			li.add("Start-Process -Wait -NoNewWindow $java_home -ArgumentList $java_args");
			this.makeShell(li);
		}
	}

	@Override
	public void genStart() throws IOException 
	{
		if (!start_ps.exists())
		{
			List<String> list = new ArrayList<>();
			list.add("param(");
			list.add("[Parameter(Mandatory = $true)] $boot,");
			list.add("[Parameter(Mandatory = $true)] $title,");
			list.add("[Parameter(Mandatory = $true)] $java_home,");
			list.add("[Parameter(Mandatory = $true)] $java_args");
			list.add(")");
			list.add("start-process powershell -ArgumentList '-ExecutionPolicy', 'Bypass', '-File', \"\"\"$boot\"\"\", '-title', \"\"\"$title\"\"\", '-java_home', \"\"\"$java_home\"\"\", '-java_args', \"\"\"$java_args\"\"\"");
			this.makeShell(list, start_ps);
		}
	}

	@Override
	public void cleanup()
	{
		this.shell.delete();
	}

}
