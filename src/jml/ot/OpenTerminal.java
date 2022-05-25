package jml.ot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.app.host.WTHost;
import jredfox.common.io.IOUtils;

public class OpenTerminal {
	
	public static final String console_host = "wt";
	public static final String terminal = "cmd";
	
	public static void open(TerminalApp app) throws IOException
	{
		//if open terminal has launched and failed printline and exit the application as it failed
		if(System.console() == null && System.getProperty("ot.l") != null)
		{
			System.err.println("System console boot failed report to https://github.com/jredfox/OpenTerminal/issues");
			System.exit(-1);
		}
		else if(System.console() != null && !app.force)
		{
			System.out.println("console is nonnull while forcing a new window isn't enabled!");
			return;
		}
		else if(System.getProperty("ot.bg") != null)
			return;
		
		boolean b = true;
		if(console_host != null && b)
		{
			switch(console_host)
			{
				case "wt":
				{
					new WTHost(app).run();
				}
				break;
			}
		}
		else if(OSUtil.isWindows())
		{
			app.getTerminal(terminal).run();
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
