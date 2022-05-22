package jml.ot.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jredfox.common.os.OSUtil;

public class BatchExe extends TerminalExe {

	public BatchExe(TerminalApp app) throws IOException 
	{
		super(app, new File(OTConstants.boot, "boot.bat"));
	}

	@Override
	public void run() throws IOException
	{
		super.run();
		String q = OSUtil.getQuote();
		ProcessBuilder pb = null;
		if(this.app.getProfile() != null)
		{
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				OSUtil.getExeAndClose(),
				"start",
				"\"\"",//app name doesn't work with the batch boot shell for some reason
				"call",
				q + this.shell.getPath() + q,//path to the boot shell
				q + (this.app.getProfile() == null ? "-1" : (this.app.getProfile().bg + this.app.getProfile().fg)) + q,//color
				q + this.app.getTitle() + q,
				q + (OTConstants.java_home + " " + OTConstants.args).replaceAll("\"", ",") + q
			});
		}
		else
		{
			pb = new ProcessBuilder(new String[]
			{
				"cmd",
				OSUtil.getExeAndClose(),
				"start " + q + this.app.getTitle() + q + " " + OTConstants.java_home + " " + OTConstants.args
			});
		}
		pb.start();
	}

	@Override
	public void createShell() throws IOException 
	{
		if (!this.shell.exists()) {
			List<String> li = new ArrayList<>();
			li.add("@ECHO OFF");
			li.add("IF NOT \"%~1%\" == \"-1\" (");
			li.add("   color %~1%");
			li.add(")");
			li.add("title %~2%");
			li.add("set boot=%~3");
			li.add("set boot=%boot:,=^\"% ::REMAP the boot command to double quotes");
			li.add("call %boot%");
			this.makeShell(li);
		}
	}

	@Override
	public void genStart() throws IOException 
	{
		// nothing to do here the start command works just find using java
	}

}
