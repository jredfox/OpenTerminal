package jml.ot.threads;

import jml.ot.TerminalApp;
import jredfox.common.utils.JREUtil;

/**
 * checks for end of the main thread without catching System#exit then pauses a TerminalApp
 */
public class OTSPThread extends OTDSPThread {

	public Thread main;
	public OTSPThread(TerminalApp app, Thread main) 
	{
		super(app);
		this.main = main;
	}
	
	@Override
	public void run()
	{
		while(main.isAlive())
		{
			JREUtil.sleep(50);
		}
		app.pause(true);
	}

}
