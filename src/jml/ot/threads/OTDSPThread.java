package jml.ot.threads;

import jml.ot.TerminalApp;

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
	}

}
