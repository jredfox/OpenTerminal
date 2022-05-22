package jml.ot.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jredfox.common.io.IOUtils;

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
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"powershell",
			"-File",
			"\"" + start_ps + "\"",
			"\"" + this.shell + "\"",
			"-ExecutionPolicy",
			"Bypass"
		});
		pb.start();
	}
	
	@Override
	public void createShell() throws IOException
	{
		//create the shell script
		File home = this.app.getHome();
		File ps1 = new File(home, this.app.id + ".ps1");
		List<String> cmds = new ArrayList<>();
		cmds.add("$host.UI.RawUI.WindowTitle = \"" + this.app.getTitle()  + "\"");
		cmds.add("Start-Process '" + OTConstants.java_home + "' -ArgumentList '" + OTConstants.args + "' -NoNewWindow");
		IOUtils.saveFileLines(cmds, ps1, true);
		IOUtils.makeExe(ps1);
		this.shell = ps1;
	}

	@Override
	public void genStart() throws IOException 
	{
		if (!start_ps.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("$file=$args[0]");
			li.add("echo \"File var: $file\"");
			li.add("start-process powershell -ArgumentList '-File', \"\"\"$file\"\"\", '-ExecutionPolicy', 'Bypass'");
			IOUtils.saveFileLines(li, start_ps, true);
			IOUtils.makeExe(start_ps);
		}
	}

}
