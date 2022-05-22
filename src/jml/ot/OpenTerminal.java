package jml.ot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.app.BatchExe;
import jml.ot.app.PowerShellExe;
import jredfox.common.io.IOUtils;

public class OpenTerminal {
	
	public static final String terminal = "powershell";
	
	public static void open(TerminalApp app) throws IOException
	{
		if(System.console() == null && System.getProperty("ot.l") != null)
		{
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			System.out.println(app.force);
			System.out.println("console is not null while forcing a new window isn't allowed!");
			return;
		}
		if(System.getProperty("ot.bg") != null)
		{
			System.out.println("background mode enabled");
			return;
		}
		
		if(OSUtil.isWindows())
		{
			switch(terminal)
			{
				case "cmd":
				{
					new BatchExe(app).run();
					break;
				}
				case "wt":
				{
					//--tabColor //custom profile for the exe
					runInTerminal("cmd", OSUtil.getExeAndClose(), "wt -w -1 new-tab -f --title \"" + app.getTitle() + "\"" +  " -p \"Command Prompt\" " + OTConstants.java_home + " " + OTConstants.args, new File("").getAbsoluteFile());
					break;
				}
				case "powershell":
					System.out.println("powershell is very buggy when it comes to the start-process command it's not recommended as a default terminal for your java application!");
					new PowerShellExe(app).run();
				break;
			}
		}
		else if(OSUtil.isMac())
		{
			
		}
        else if(OSUtil.isLinux())
        {
            File sh = new File(OTConstants.home, app.id + ".sh");
            List<String> cmds = new ArrayList<>();
            cmds.add("#!/bin/bash");
            cmds.add("set +v");//@Echo off
            cmds.add("echo -n -e \"\\033]0;" + app.getTitle() + "\\007\"");//Title
            cmds.add(OTConstants.java_home + " " + OTConstants.args);//actual command
            IOUtils.saveFileLines(cmds, sh, true);//save the file
            IOUtils.makeExe(sh);//make it executable
            Runtime.getRuntime().exec(terminal + " " + OSUtil.getLinuxNewWin() + " bash " + sh.getAbsolutePath());
        }
	}

    /**
     * enforces it to run in the command prompt terminal as sometimes it doesn't work without it
     */
    public static Process runInTerminal(String terminal, String flag, String command, File dir) throws IOException
    {
        return run(new String[]{terminal, flag, command}, dir);
    }
	
    public static Process run(String[] cmdarray, File dir) throws IOException
    {
        return new ProcessBuilder(cmdarray).directory(dir).start();
    }

}
