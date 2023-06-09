package jml.ot.threads;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalUtil;
import jml.ot.colors.AnsiColors;
import jml.ot.terminal.MacBashExe;

public class OTDSPThread extends Thread {
	
	public TerminalApp app;
	
	public OTDSPThread(TerminalApp app)
	{
		this.app = app;
	}
	
	@Override
	public void run()
	{
		this.app.pause(false);
		//reset the console back to original profile settings
		System.out.print(app.colors.getHardReset() + AnsiColors.getSoftCls());
		
		//reset macOs's profile here
		if(this.app.defaultProfile != null && TerminalUtil.isMacTerm(this.app.terminal))
		{
			try
			{
				System.out.print("]0;" + this.app.sessionName + "");//clear the title so the CLI resets itself
				System.out.flush();
				System.out.println(this.app.defaultProfile + " " + this.app.sessionName);
				ProcessBuilder pb = new ProcessBuilder(new String[] {"osascript", MacBashExe.profileScpt.getPath(), this.app.defaultProfile, this.app.sessionName, OTConstants.home.getPath()});
				pb.start().waitFor();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		System.out.print("]0;" + "");//clear the title so the CLI resets itself
		System.out.flush();
	}

}
