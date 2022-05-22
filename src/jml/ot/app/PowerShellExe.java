package jml.ot.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OSUtil;
import jml.ot.OTConstants;
import jml.ot.TerminalApp;

public class PowerShellExe extends TerminalExe {

	public static final File start_ps = new File(OTConstants.start, "powershell.ps1");
	
	public PowerShellExe(TerminalApp app) throws IOException
	{
		super(app, new File(OTConstants.boot, "boot.ps1"));
	}

	@Override
	public void run() throws IOException 
	{
		super.run();
		String q = OSUtil.getQuote();
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"powershell",
			"-ExecutionPolicy",
			"Bypass",
			"-File",
			q + start_ps + q,
			"-boot",
			q + this.shell + q,
			"-java_home",
			OTConstants.java_home,
			"-java_args",
			OTConstants.args,
			"-title",
			this.app.getTitle()
		}).directory(OTConstants.userDir);
		pb.start();
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
			li.add("Start-Process -NoNewWindow $java_home -ArgumentList $java_args");
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
			list.add("start-process powershell -ArgumentList '-File', \"\"\"$boot\"\"\", '-title', \"\"\"$title\"\"\", '-java_home', \"\"\"$java_home\"\"\", '-java_args', \"\"\"$java_args\"\"\"");
			this.makeShell(list, start_ps);
		}
	}

}
