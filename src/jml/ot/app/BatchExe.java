package jml.ot.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jredfox.common.os.OSUtil;

public class BatchExe extends TerminalExe {

	public BatchExe(TerminalApp app) throws IOException 
	{
		super(app, new File(OTConstants.boot, "boot.bat"));
	}

	@Override
	public void run() throws IOException
	{
		String q = OSUtil.getQuote();
		ProcessBuilder pb = null;
		Profile profile = this.app.getProfile();
		if(profile != null)
		{
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				OSUtil.getExeAndClose(),
				"start",
				"\"\"",//app name doesn't work with the batch boot shell for some reason
				"call",
				q + this.shell.getPath() + q,//path to the boot shell
				profile != null ? q + (profile.bg + profile.fg) + q : "-1",//color
				q + this.app.getTitle() + q,
				q + (OTConstants.java_home + " " + OTConstants.args).replaceAll("\"", ",") + q
			});
		}
		else
		{
			//more optimized version and contains the java icon on boot like it should always have
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				OSUtil.getExeAndClose(),
				"start " + q + this.app.getTitle() + q + " " + OTConstants.java_home + " " + OTConstants.args
			});
		}
		this.run(pb);
	}
	
	@Override
	public List<String> getBootCmd() throws IOException
	{
		String q = OSUtil.getQuote();
		Profile profile = this.app.getProfile();
		List<String> cmd = new ArrayList();
		cmd.add("cmd");
		cmd.add(OSUtil.getExeAndClose());
		cmd.add("call");
		cmd.add(q + this.shell.getPath() + q);//path to the boot shell
		cmd.add(profile != null ? q + (profile.bg + profile.fg) + q : "-1");//color
		cmd.add(q + this.app.getTitle() + q);
		cmd.add(q + (OTConstants.java_home + " " + OTConstants.args).replaceAll("\"", ",") + q);
		return cmd;
	}

	@Override
	public void createShell() throws IOException 
	{
		if (!this.shell.exists()) 
		{
			List<String> li = new ArrayList<>();
			li.add("@ECHO OFF");
			li.add("IF NOT \"%~1%\" == \"-1\" (");
			li.add("   color %~1%");
			li.add(")");
			li.add("title %~2%");
			li.add("set boot=%~3");
			li.add("set boot=%boot:,=^\"% ::RE-MAP the boot command to double quotes");
			li.add("call %boot%");
			li.add("exit ::Work around from a command prompt bug");
			this.makeShell(li);
		}
	}

	@Override
	public void genStart() throws IOException 
	{
		// nothing to do here the start command works just find using java
	}

}
