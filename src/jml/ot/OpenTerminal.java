package jml.ot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jredfox.common.file.FileUtils;
import jredfox.common.io.IOUtils;

public class OpenTerminal {
	
	public static final String terminal = "wt";
	
	public static void open(TerminalApp app) throws IOException
	{
		if(System.console() != null && (!app.force || app.id.equals("ot")))
		{
			System.out.println("returning");
			return;
		}
		String java_home = "\"" + System.getProperty("java.home") + "/bin/java\"";
		String java = "-cp " + System.getProperty("java.class.path") + " jml.ot.OTMain";
		File home = new File(System.getProperty("user.home"), "OpenTerminal/" + app.id + "/" + System.currentTimeMillis());
		if(OSUtil.isWindows())
		{
			switch(terminal)
			{
				case "cmd":
					runInTerminal(terminal, OSUtil.getExeAndClose(), "start " + "\"" + app.getAppName() + "\" " + java_home + " " + java, new File("").getAbsoluteFile());
				break;
				case "wt":
					runInTerminal("cmd", OSUtil.getExeAndClose(), "wt -w -1 new-tab -p \"Command Prompt\" " + java_home + " " + java, new File("").getAbsoluteFile());
				break;
				case "powershell":
					System.err.println("powershell has a known bug with spaces and the Start-Process command! So it won't execute if the path contains a space. Go spam them on their github!");
					File ps1 = new File(home, app.id + ".ps1");
					List<String> cmds = new ArrayList<>();
					cmds.add("Start-Process '" + java_home + "' -ArgumentList '" + java + "' -NoNewWindow");
					IOUtils.saveFileLines(cmds, ps1, true);
					IOUtils.makeExe(ps1);
					runInTerminal(terminal, OSUtil.getExeAndClose(), "start-process powershell -ArgumentList \"" + ps1.getAbsolutePath() + "\"", home);
				break;
			}
		}
		else if(OSUtil.isMac())
		{
			
		}
		else if(OSUtil.isLinux())
		{
			
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
