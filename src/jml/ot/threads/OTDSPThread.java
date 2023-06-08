package jml.ot.threads;

import jml.ot.TerminalApp;
import jml.ot.colors.AnsiColors;

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
		System.out.print("]0;" + "");//clear the title so the CLI resets itself
		System.out.flush();
	}

}
